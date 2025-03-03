package tictactoe.game;

import java.sql.Timestamp;

import tictactoe.board.Position;
import tictactoe.database.ConnectionPool;
import tictactoe.database.Database;

public class Game {

    public static void play(int userID, Match match) {

        char currentSymbol = Match.PLAYER_SYMBOL;
        System.out.println("player move:");
        match.printBoard();

        if (match.isGameOver(match.getBoard(), currentSymbol)) {

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

        if (match.isGameOver(match.getBoard(), currentSymbol)) {
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

}
