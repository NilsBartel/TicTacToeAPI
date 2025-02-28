package tictactoe.game;

import ch.qos.logback.core.model.Model;
import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.database.ConnectionPool;
import tictactoe.database.Database;

import java.sql.Timestamp;

public class Game {





    public static void play(int userID, Match match) {

        char currentSymbol = Match.PLAYER_SYMBOL;
        System.out.println("player move:");
        match.printBoard();

        if (match.isGameOver(match.getBoard(), currentSymbol)){
//            System.out.println("Game Over");
//            match.setEndTime(new Timestamp(System.currentTimeMillis()));
//            Database.updateDB_Match(match, userID, ConnectionPool.getInstance().getDataSource());
//            return;
            endGame(userID, match);
            return;
        }
        match.setIsPlayerTurn(false);

        currentSymbol = Match.COMPUTER_SYMBOL;
        Position position = Difficulty.returnMove(match.getBoard(), match.getDifficulty());
        match.setSymbol(position.getRow(), position.getColumn(), currentSymbol);

        System.out.println();
        System.out.println("Computer move:");
        match.printBoard();

        if(match.isGameOver(match.getBoard(), currentSymbol)){
//            System.out.println("Game Over");
//            match.setEndTime(new Timestamp(System.currentTimeMillis()));
            endGame(userID, match);
            return;
        }
        match.setIsPlayerTurn(true);

        Database.updateDB_Match(match, userID, ConnectionPool.getInstance().getDataSource());
    }

    private static void endGame(int userID, Match match) {
        System.out.println("Game Over");
        match.setEndTime(new Timestamp(System.currentTimeMillis()));
        Database.updateDB_Match(match, userID, ConnectionPool.getInstance().getDataSource());
        Score.updateScore(match.getStatus(), userID);
    }

//    public boolean isGameOver(Board board, char currentSymbol) {
//
//        if (Winner.thereIsWinner(board, currentSymbol)) {
//            if (currentSymbol == COMPUTER_SYMBOL){
//                this.status = MatchStatus.COMPUTER_WON;
//            } else{
//                this.status = MatchStatus.PLAYER_WON;
//            }
//            return true;
//        }
//
//        if (board.isFull()) {
//            this.status = MatchStatus.DRAW;
//            return true;
//        }
//        return false;
//    }


    //TODO: can I move the game over if's out of this function?
    // where do I update the score

//    public void computerPlay(int userID) {
//        System.out.println("player move:");
//        board.print();
//
//        char currentSymbol = PLAYER_SYMBOL;
//        if (isGameOver(board, currentSymbol)){
//            System.out.println("Game Over");
//            endTime = new Timestamp(System.currentTimeMillis());
//            Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());
//            return;
//        }
//
//        Position position;
//        currentSymbol = COMPUTER_SYMBOL;
//
//        position = Difficulty.returnMove(board, difficulty);
//
//        board.setSymbol(position.getRow(), position.getColumn(), currentSymbol);
//
//        System.out.println();
//        System.out.println("Computer move:");
//        board.print();
//
//        Database.updateBoard(this, userID, ConnectionPool.getInstance().getDataSource());
//
//        if(isGameOver(board, currentSymbol)){
//            System.out.println("Game Over");
//            endTime = new Timestamp(System.currentTimeMillis());
//        }
//
//        Database.updateDB_Match(this, userID, ConnectionPool.getInstance().getDataSource());
//    }
}
