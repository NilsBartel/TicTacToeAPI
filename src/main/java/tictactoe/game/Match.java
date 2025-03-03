package tictactoe.game;

import java.sql.Timestamp;
import java.util.Objects;

import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.database.ConnectionPool;
import tictactoe.database.Database;

public class Match {

    public static final char PLAYER_SYMBOL = 'o';
    public static final char COMPUTER_SYMBOL = 'x';
    public static final char EMPTY_SYMBOL = ' ';

    private Board board; //final
    private MatchStatus status;
    private DifficultyState difficulty;
    private boolean isPlayerTurn;
    private int matchID;
    private Timestamp startTime;
    private Timestamp endTime;

    public Match() {
        this.board = new Board();
        this.status = MatchStatus.NOT_STARTED;
    }

    //TODO: can I move the game over if's out of this function?
    // where do I update the score

    public static char getOpponentsSymbol(char symbol) {
        if (symbol == COMPUTER_SYMBOL) {
            return PLAYER_SYMBOL;
        } else if (symbol == PLAYER_SYMBOL) {
            return COMPUTER_SYMBOL;
        }
        return symbol;
    }

    public void computerPlay(int userID) {
        System.out.println("player move:");
        board.print();

        char currentSymbol = PLAYER_SYMBOL;
        if (isGameOver(board, currentSymbol)) {
            System.out.println("Game Over");
            endTime = new Timestamp(System.currentTimeMillis());
            Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());
            return;
        }

        Position position;
        currentSymbol = COMPUTER_SYMBOL;

        position = Difficulty.returnMove(board, difficulty);

        board.setSymbol(position.getRow(), position.getColumn(), currentSymbol);

        System.out.println();
        System.out.println("Computer move:");
        board.print();

        Database.updateBoard(this, userID, ConnectionPool.getInstance().getDataSource());

        if (isGameOver(board, currentSymbol)) {
            System.out.println("Game Over");
            endTime = new Timestamp(System.currentTimeMillis());
        }

        Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());
    }

    public boolean isGameOver(Board board, char currentSymbol) {

        if (Winner.thereIsWinner(board, currentSymbol)) {
            if (currentSymbol == COMPUTER_SYMBOL) {
                this.status = MatchStatus.COMPUTER_WON;
            } else {
                this.status = MatchStatus.PLAYER_WON;
            }
            return true;
        }

        if (board.isFull()) {
            this.status = MatchStatus.DRAW;
            return true;
        }
        return false;
    }

    public void setPlayerTurn(Score score) {
        this.isPlayerTurn = score.getRoundCounter() % 2 == 0;
    }

    public void printBoard() {
        board.print();
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public boolean isStatusEqual(MatchStatus newStatus) {
        return this.status == newStatus;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setSymbol(int row, int column, char symbol) {
        board.setSymbol(row, column, symbol);
    }

    public boolean isIsPlayerTurn() {
        return isPlayerTurn;
    }

    public void setIsPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
    }

    public DifficultyState getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyState difficulty) {
        this.difficulty = difficulty;
    }

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public boolean equalsWithoutBoard(Object o) {
        if (!(o instanceof Match match)) {
            return false;
        }
        return isPlayerTurn == match.isPlayerTurn && matchID == match.matchID && status == match.status && difficulty == match.difficulty && Objects.equals(
            startTime,
            match.startTime
        ) && Objects.equals(endTime, match.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Match match)) {
            return false;
        }
        return isPlayerTurn == match.isPlayerTurn && matchID == match.matchID && Objects.equals(
            board,
            match.board
        ) && status == match.status && difficulty == match.difficulty && Objects.equals(
            startTime,
            match.startTime
        ) && Objects.equals(endTime, match.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, status, difficulty, isPlayerTurn, matchID, startTime, endTime);
    }
}
