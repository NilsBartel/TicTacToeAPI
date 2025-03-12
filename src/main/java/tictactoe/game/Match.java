package tictactoe.game;

import tictactoe.board.Board;

import java.sql.Timestamp;
import java.util.Objects;

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

    public Match(Board board, MatchStatus status, DifficultyState difficulty, boolean isPlayerTurn, int matchID, Timestamp startTime, Timestamp endTime) {
        this.board = board;
        this.status = status;
        this.difficulty = difficulty;
        this.isPlayerTurn = isPlayerTurn;
        this.matchID = matchID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Match() {
        this.board = new Board();
        this.status = MatchStatus.NOT_STARTED;
    }

    public boolean isGameOver(Board board, char currentSymbol) {

        if (Winner.thereIsWinner(board, currentSymbol)) {
            if (currentSymbol == COMPUTER_SYMBOL){
                this.status = MatchStatus.COMPUTER_WON;
            } else{
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

    public boolean isStatusEqual(MatchStatus newStatus) {
        return this.status == newStatus;
    }

    public static char getOpponentsSymbol(char symbol) {
        if (symbol == COMPUTER_SYMBOL){
            return PLAYER_SYMBOL;
        } else if (symbol == PLAYER_SYMBOL){
            return COMPUTER_SYMBOL;
        }
        return symbol;
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

    public void setStatus(MatchStatus status) {
        this.status = status;
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
        if (!(o instanceof Match match)) return false;
        return isPlayerTurn == match.isPlayerTurn && matchID == match.matchID &&  status == match.status && difficulty == match.difficulty && Objects.equals(startTime, match.startTime) && Objects.equals(endTime, match.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Match match)) return false;
        return isPlayerTurn == match.isPlayerTurn && matchID == match.matchID && Objects.equals(board, match.board) && status == match.status && difficulty == match.difficulty && Objects.equals(startTime, match.startTime) && Objects.equals(endTime, match.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, status, difficulty, isPlayerTurn, matchID, startTime, endTime);
    }
}
