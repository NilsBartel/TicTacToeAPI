package tictactoe.game;

import tictactoe.api.errors.MatchError;
import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.database.ConnectionPool;
import tictactoe.database.*;
import tictactoe.PlayerInput;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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



    public Match() {
        this.board = new Board();
        this.status = MatchStatus.NOT_STARTED;
    }


    public boolean validateMatch(int userID) throws MatchError {
        Match dbMatch = DBMatch.getMatch(userID, this.matchID, ConnectionPool.getInstance().getDataSource());

        if (!this.equalsWithoutBoard(dbMatch)) throw new MatchError("Something changed with the match that wasn't allowed");

        List<Position> positions = positionsThatChanged(dbMatch.getBoard());
        System.out.println(positions);


        if (positions.size() != 1) throw new MatchError("Wrong number of positions");
        if (board.getSymbol(positions.getFirst()) != PLAYER_SYMBOL) throw new MatchError("Wrong symbol, player symbol is: " + Match.PLAYER_SYMBOL);

        return true;
    }

    public List<Position> positionsThatChanged(Board board) throws MatchError {
        List<Position> positions = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            char oldSymbol = board.getSymbol(new Position(i));
            char newSymbol = this.board.getSymbol(new Position(i));
            if (oldSymbol != newSymbol) {
                positions.add(new Position(i));

                if (oldSymbol != EMPTY_SYMBOL) throw new MatchError("The field at position " + i + " was overwritten");
            }
        }

        return positions;
    }


    public void computerPlay(int userID) {



        board.print();
        System.out.println();




            char currentSymbol = PLAYER_SYMBOL;
        if (isGameOver(board, currentSymbol)){
            System.out.println("Game Over");
            System.out.println("Game Over");
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
            board.print();


            Database.updateBoard(this, userID, ConnectionPool.getInstance().getDataSource());

            //TODO: where do I get the position from? when player played
            if(isGameOver(board, currentSymbol)){
                //Database.updateBoard(this, userID, position, ConnectionPool.getInstance().getDataSource());
                System.out.println("Game Over");
                System.out.println("Game Over");
                System.out.println("Game Over");
                endTime = new Timestamp(System.currentTimeMillis());

                //break;
            }

            //isPlayerTurn = !isPlayerTurn;
            //Database.updateDatabase(this , userID);
            //Database.updateBoard(this, userID, position, ConnectionPool.getInstance().getDataSource());
            Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());



        //endTime = new Timestamp(System.currentTimeMillis());
        //DB_Time.updateTime(endTime, ConnectionPool.getInstance().getDataSource());
        Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());

        //Database.updateDatabase(this , userID);
//        Database.updateBoard(this, userID, position, ConnectionPool.getInstance().getDataSource());

    }




    public void playOLD(int userID) {
        //System.out.println("no playerinput?");
        //Database.writeToDatabase(this , userID);
        //Database.updateDB(this , userID);


        if (status == MatchStatus.NOT_STARTED) {
            setPlayerTurn(DBScore.getScore(userID, ConnectionPool.getInstance().getDataSource()));
        }

        this.status = MatchStatus.RUNNING;

        board.print();
        System.out.println();


        while (true){

            char currentSymbol;

            Position position;
            if(isPlayerTurn){
                currentSymbol = PLAYER_SYMBOL;

                position = playerMove(board, userID);
                if(position == null){
                    return;
                }

            } else{
                currentSymbol = COMPUTER_SYMBOL;
                position = Difficulty.returnMove(board, difficulty);
            }

            board.setSymbol(position.getRow(), position.getColumn(), currentSymbol);

            System.out.println();
            board.print();


            Database.updateBoard(this, userID, ConnectionPool.getInstance().getDataSource());

            if(isGameOver(board, currentSymbol)){
                //Database.updateBoard(this, userID, position, ConnectionPool.getInstance().getDataSource());
                break;
            }

            isPlayerTurn = !isPlayerTurn;
            //Database.updateDatabase(this , userID);
            //Database.updateBoard(this, userID, position, ConnectionPool.getInstance().getDataSource());
            Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());
        }


        endTime = new Timestamp(System.currentTimeMillis());
        //DB_Time.updateTime(endTime, ConnectionPool.getInstance().getDataSource());
        Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());

        //Database.updateDatabase(this , userID);
//        Database.updateBoard(this, userID, position, ConnectionPool.getInstance().getDataSource());

    }

    private Position playerMove(Board board, int userID) { //TODO a lot!! the DB method calls are really bad!!!

        //tictactoe.game.Match tempMatch = FileWriteRead.getInstance().readFromHistoryFile(FileUtil.getInstance().getFileMatchHistory()).getMatches().getLast();
        //tictactoe.game.Match tempMatch = DB_Match.getMatchFromDB(DB_Match.getLastMatchIDFromUser(userID, DB_ConHandler.getInstance()), DB_ConHandler.getInstance());

        //tictactoe.game.Match tempMatch = DB_Match.getLastMatchFromUser(userID, ConnectionPool.getInstance().getDataSource());
        Match tempMatch = DBMatch.getLastNMatchesFromUser(userID, 1, ConnectionPool.getInstance().getDataSource()).getFirst();

        Position position = PlayerInput.getInstance().askForMove(board);
        //Position position = new Position(4);

//        if (!tempMatch.equals(DB_Match.getMatchFromDB(DB_Match.getLastMatchIDFromUser(userID)))) {     //FileWriteRead.getInstance().readFromHistoryFile(FileUtil.getInstance().getFileMatchHistory()).getMatches().getLast())
//            do {
//                //if (!FileWriteRead.getInstance().compareToLastMatchState(MatchStatus.RUNNING)){
//                if (!){
//
//                    System.out.println();
//                    System.out.println("The game you were playing has already been finished! too slow");
//                    this.status = MatchStatus.MATCH_ALREADY_FINISHED;
//                    return null;
//                }
//
//                if (!FileWriteRead.getInstance().compareToLastMatchStartTime(this.startTimeOLD)) {
//                    System.out.println();
//                    System.out.println("The game you were playing has already been finished! And a new one has started");
//                    this.status = MatchStatus.MATCH_ALREADY_FINISHED;
//                    return null;
//                }
//
//                System.out.println();
//                System.out.println("The Board has changed!");
//                System.out.println("New Board:");
//                //this.board = FileWriteRead.getInstance().getLastBoard();
//                this.board = DB_Board.getBoard(DB_Board.getBoardId(DB_Match.getLastMatchIDFromUser(userID))); //TODO a LOT!!!!! make the calls easier, all over this method !!!!!!!!!!
//                this.board.print();
//
//                //tempMatch = FileWriteRead.getInstance().readFromHistoryFile(FileUtil.getInstance().getFileMatchHistory()).getMatches().getLast();
//                tempMatch = DB_Match.getMatchFromDB(DB_Match.getLastMatchIDFromUser(userID));
//                position = PlayerInput.getInstance().askForMove(board);
//            } while (!tempMatch.equals(DB_Match.getMatchFromDB(DB_Match.getLastMatchIDFromUser(userID))));    //FileWriteRead.getInstance().readFromHistoryFile(FileUtil.getInstance().getFileMatchHistory()).getMatches().getLast()
//        }

        return position;
    }

    private boolean dfdf(Board board, Position position, char currentSymbol) {
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

    private boolean isGameOver(Board board, char currentSymbol) {

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

    private boolean isGameOverOLD(Board board, Position position, char currentSymbol) {

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

//    @Override
//    public boolean equals(Object object) {
//        if (this == object) {
//            return true;
//        }
//        if (!(object instanceof tictactoe.game.Match match)) {
//            return false;
//        }
//        return isPlayerTurn == match.isPlayerTurn && startTime == match.startTime && endTime == match.endTime && board.equals(match.board) && status == match.status && difficulty == match.difficulty;
//    }

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
