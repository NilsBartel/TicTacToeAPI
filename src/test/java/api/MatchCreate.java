package api;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.database.DBMatch;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.sql.Timestamp;

public class MatchCreate {




    public static void createMatch(HikariDataSource dataSource) {
        Match match = new Match();
        match.setStatus(MatchStatus.PLAYER_WON);
        match.setDifficulty(DifficultyState.EASY);
        match.setIsPlayerTurn(true);
        match.setMatchID(1);
        match.setStartTime(new Timestamp(System.currentTimeMillis()));
        match.setEndTime(new Timestamp(System.currentTimeMillis()));
        Board board = generateBoard(new int[]{1, 2, 3}, new int[]{});
        match.setBoard(board);

        DBMatch.insertNewMatch(match, 1, dataSource);
    }

    private static Board generateBoard(int[] player, int[] computer) {
        Board board = new Board();

        for (int j : player) {
            Position position = new Position(j);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setSymbol(Match.PLAYER_SYMBOL);
        }

        for (int j : computer) {
            Position position = new Position(j);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setSymbol(Match.COMPUTER_SYMBOL);
        }

        return board;
    }
}
