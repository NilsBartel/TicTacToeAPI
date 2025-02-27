package tictactoe.api;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.api.errors.MatchError;
import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.database.DBScore;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ApiMatchUtil{



    //TODO: start -> running or new match?


    public static boolean validateMatch(int userID, Match match) throws MatchError {
        Match dbMatch = DBMatch.getMatch(userID, match.getMatchID(), ConnectionPool.getInstance().getDataSource());

        if (!match.equalsWithoutBoard(dbMatch)) throw new MatchError("Something changed with the match that wasn't allowed");

        List<Position> positions = positionsThatChanged(dbMatch.getBoard(), match.getBoard());
        System.out.println(positions);


        if (positions.isEmpty()) throw new MatchError("No new input found");
        if (positions.size() != 1) throw new MatchError("Wrong number of positions");
        if (match.getBoard().getSymbol(positions.getFirst()) != Match.PLAYER_SYMBOL) throw new MatchError("Wrong symbol, player symbol is: " + Match.PLAYER_SYMBOL);

        return true;
    }

    private static List<Position> positionsThatChanged(Board DBBoard, Board playerBoard) throws MatchError {
        List<Position> positions = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            char oldSymbol = DBBoard.getSymbol(new Position(i));
            char newSymbol = playerBoard.getSymbol(new Position(i));
            if (oldSymbol != newSymbol) {
                positions.add(new Position(i));

                if (oldSymbol != Match.EMPTY_SYMBOL) throw new MatchError("The field at position " + i + " was overwritten");
            }
        }

        return positions;
    }

    public static Match returnRunningOrNewMatch(DifficultyState difficulty, int userID) throws MatchError {
        Match match;

        if (difficulty != DifficultyState.EASY && difficulty != DifficultyState.MEDIUM && difficulty != DifficultyState.IMPOSSIBLE) {
            throw new MatchError("Wrong difficulty");
        }

        if (userHasRunningMatch(userID, ConnectionPool.getInstance().getDataSource())) {
            match = DBMatch.getLastNMatchesFromUser(userID, 1, ConnectionPool.getInstance().getDataSource()).getFirst();
        } else {
            match = new Match();
            match.setStatus(MatchStatus.RUNNING);
            match.setDifficulty(difficulty);
            match.setStartTime(new Timestamp(System.currentTimeMillis()));
            match.setPlayerTurn(DBScore.getScore(userID, ConnectionPool.getInstance().getDataSource()));

            int matchID = DBMatch.insertNewMatch(match, userID, ConnectionPool.getInstance().getDataSource());
            match.setMatchID(matchID);
        }


        return match;
    }

//        public static Match returnRunningOrNewMatch(DifficultyState difficulty, int userID) {
//
//        Match match;
//
//        //if (DB_Match.userHasRunningMatch(userID, ConnectionPool.getInstance().getDataSource())) {
//        if (userHasRunningMatch(userID, ConnectionPool.getInstance().getDataSource())) {
//            System.out.println("loaded a match");
//            //match = DB_Match.getLastMatchFromUser(userID, ConnectionPool.getInstance().getDataSource());
//            match = DBMatch.getLastNMatchesFromUser(userID, 1, ConnectionPool.getInstance().getDataSource()).getFirst();
//        } else {
//            System.out.println("Creating a new match");
//            match = new Match(); //TODO combine into insert match method
//            match.setDifficulty(difficulty);
//            if (difficulty == null) {
//                match.setDifficulty(PlayerInput.getInstance().askForDifficulty());
//            }
//
//
////            Database.writeToDatabase(match, userID);
//            System.out.println("wrote to DB");
//            match.setStartTime(new Timestamp(System.currentTimeMillis()));
//            //DB_Match.insertMatch(match, userID, ConnectionPool.getInstance().getDataSource());
//            int matchID = DBMatch.insertNewMatch(match, userID, ConnectionPool.getInstance().getDataSource());
//            match.setMatchID(matchID);
//
//
//        }
//
//        return match;
//    }


    private static boolean userHasRunningMatch(int userID, HikariDataSource dataSource) {
        List<Match> match = DBMatch.getLastNMatchesFromUser(userID, 1, dataSource);
        if (match.isEmpty()) {
            return false;
        }
        return match.getFirst().getStatus() == MatchStatus.RUNNING || match.getLast().getStatus() == MatchStatus.NOT_STARTED;
    }


}
