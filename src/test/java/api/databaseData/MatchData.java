package api.databaseData;

import tictactoe.board.Board;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.sql.Timestamp;
import java.util.List;

public class MatchData {
    private static final Board board1 = new Board(List.of(' ',' ',' ',' ',' ',' ',' ',' ',' '));
    private static final Match match1 = new Match(board1, MatchStatus.PLAYER_WON, DifficultyState.EASY, true, 1, new Timestamp(1741344934645L), new Timestamp(1741344954417L));

    private static final Board board2 = new Board(List.of('o','x',' ',' ','o','x',' ',' ','o'));
    private static final Match match2 = new Match(board2, MatchStatus.PLAYER_WON, DifficultyState.EASY, true, 2, new Timestamp(1741355834611L), new Timestamp(1741355852265L));

    private static final Board board3 = new Board(List.of('o','x','o',' ','x','x',' ','x','o'));
    private static final Match match3 = new Match(board3, MatchStatus.COMPUTER_WON, DifficultyState.MEDIUM, false, 3, new Timestamp(1741364002831L), new Timestamp(1741364020486L));

    private static final Board board4 = new Board(List.of('o',' ',' ',' ','x',' ',' ','x','o'));
    private static final Match match4 = new Match(board4, MatchStatus.RUNNING, DifficultyState.MEDIUM, true, 4, new Timestamp(1741608125524L), new Timestamp(1741608143177L));






    public static Match getMatch1() {
        return match1;
    }
    public static Match getMatch2() {
        return match2;
    }
    public static Match getMatch3() {
        return match3;
    }
    public static Match getMatch4() {
        return match4;
    }
}
