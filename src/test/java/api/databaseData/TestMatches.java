package api.databaseData;

import tictactoe.board.Board;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.sql.Timestamp;
import java.util.List;

public class TestMatches {
    private static final Board board1 = new Board(List.of(' ',' ',' ',' ',' ',' ',' ',' ',' '));
    private static final Match match1 = new Match(board1, MatchStatus.PLAYER_WON, DifficultyState.EASY, true, 1, new Timestamp(1741344934645L), new Timestamp(1741344954417L));


    public static Board getBoard1() {
        return board1;
    }

    public static Match getMatch1() {
        return match1;
    }
}
