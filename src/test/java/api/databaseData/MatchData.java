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

    private static final Board board5 = new Board(List.of('o', 'x', ' ', ' ', 'o', ' ', ' ', 'x', 'o'));
    private static final Match match5 = new Match(board5, MatchStatus.PLAYER_WON, DifficultyState.EASY, true, 5, new Timestamp(1743585511010L), new Timestamp(1743585526010L));

    private static final Board board6 = new Board(List.of('o', 'x', 'o', ' ', 'o', 'x', ' ', 'x', 'o'));
    private static final Match match6 = new Match(board6, MatchStatus.PLAYER_WON, DifficultyState.MEDIUM, true, 6, new Timestamp(1743585717074L), new Timestamp(1743585732074L));

    private static final Board board7 = new Board(List.of('o', 'x', 'o', ' ', 'x', 'x', 'o', 'x', 'o'));
    private static final Match match7 = new Match(board7, MatchStatus.COMPUTER_WON, DifficultyState.MEDIUM, true, 7, new Timestamp(1743585789313L), new Timestamp(1743585804313L));

    private static final Board board8 = new Board(List.of('o', 'x', 'o', ' ', 'x', 'x', 'o', 'x', 'o'));
    private static final Match match8 = new Match(board8, MatchStatus.COMPUTER_WON, DifficultyState.IMPOSSIBLE, true, 8, new Timestamp(1743585839154L), new Timestamp(1743585854154L));

    private static final Board board9 = new Board(List.of('o', 'x', 'o', ' ', 'x', 'x', 'o', 'x', 'o'));
    private static final Match match9 = new Match(board9, MatchStatus.COMPUTER_WON, DifficultyState.IMPOSSIBLE, true, 9, new Timestamp(1743585912545L), new Timestamp(1743585927545L));

    private static final Board board10 = new Board(List.of('o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'x'));
    private static final Match match10 = new Match(board10, MatchStatus.DRAW, DifficultyState.IMPOSSIBLE, true, 10, new Timestamp(1743585983884L), new Timestamp(1743585998884L));

    private static final Board board11 = new Board(List.of('o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'x'));
    private static final Match match11 = new Match(board11, MatchStatus.DRAW, DifficultyState.MEDIUM, true, 11, new Timestamp(1743586036390L), new Timestamp(1743586051390L));

    private static final Board board12 = new Board(List.of('o', 'x', 'o', 'x', 'o', 'x', 'o', 'x', 'x'));
    private static final Match match12 = new Match(board12, MatchStatus.DRAW, DifficultyState.EASY, true, 12, new Timestamp(1743586088542L), new Timestamp(1743586103542L));

    private static final Board board13 = new Board(List.of('x', 'x', 'o', ' ', 'x', ' ', 'o', ' ', 'x'));
    private static final Match match13 = new Match(board13, MatchStatus.COMPUTER_WON, DifficultyState.EASY, true, 13, new Timestamp(1743586160180L), new Timestamp(1743586175180L));

    private static final Board board14 = new Board(List.of('x', 'x', 'o', ' ', 'x', ' ', 'o', ' ', 'x'));
    private static final Match match14 = new Match(board14, MatchStatus.COMPUTER_WON, DifficultyState.MEDIUM, true, 14, new Timestamp(1743586227413L), new Timestamp(1743586242413L));



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
    public static Match getMatch5() {
        return match5;
    }
    public static Match getMatch6() {
        return match6;
    }
    public static Match getMatch7() {
        return match7;
    }
    public static Match getMatch8() {
        return match8;
    }
    public static Match getMatch9() {
        return match9;
    }
    public static Match getMatch10() {
        return match10;
    }
    public static Match getMatch11() {
        return match11;
    }
    public static Match getMatch12() {
        return match12;
    }
    public static Match getMatch13() {
        return match13;
    }
    public static Match getMatch14() {
        return match14;
    }
}

