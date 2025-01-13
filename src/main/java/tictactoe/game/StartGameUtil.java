package tictactoe.game;

import tictactoe.database.*;
import tictactoe.PlayerInput;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Timestamp;
import java.util.List;

public final class StartGameUtil {
    private StartGameUtil() {
    }


    public static Match returnRunningOrNewMatch(DifficultyState difficulty, int userID) {

        Match match;

        //if (DB_Match.userHasRunningMatch(userID, ConnectionPool.getInstance().getDataSource())) {
        if (userHasRunningMatch(userID, ConnectionPool.getInstance().getDataSource())) {
            System.out.println("loaded a match");
            //match = DB_Match.getLastMatchFromUser(userID, ConnectionPool.getInstance().getDataSource());
            match = DBMatch.getLastNMatchesFromUser(userID, 1, ConnectionPool.getInstance().getDataSource()).getFirst();
        } else {
            System.out.println("Creating a new match");
            match = new Match(); //TODO: combine into insert match method
            match.setDifficulty(difficulty);
            if (difficulty == null) {
                match.setDifficulty(PlayerInput.getInstance().askForDifficulty());
            }


//            Database.writeToDatabase(match, userID);
            System.out.println("wrote to DB");
            match.setStartTime(new Timestamp(System.currentTimeMillis()));
            //DB_Match.insertMatch(match, userID, ConnectionPool.getInstance().getDataSource());
            DBMatch.insertNewMatch(match, userID, ConnectionPool.getInstance().getDataSource());


        }

        return match;
    }


    private static boolean userHasRunningMatch(int userID, HikariDataSource dataSource) {
        List<Match> match = DBMatch.getLastNMatchesFromUser(userID, 1, dataSource);
        return match.getFirst().getStatus() == MatchStatus.RUNNING || match.getLast().getStatus() == MatchStatus.NOT_STARTED;
    }



}
