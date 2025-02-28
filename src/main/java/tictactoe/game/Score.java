package tictactoe.game;

import tictactoe.database.*;

import java.util.Objects;

public class Score {

    private int playerScore;
    private int computerScore;
    private int drawCount;

    public Score() {}

    public Score(int playerScore, int computerScore, int drawCount) {
        this.playerScore = playerScore;
        this.computerScore = computerScore;
        this.drawCount = drawCount;
    }

    public static void updateScore(MatchStatus status, int userID) {
        System.out.println("Score");
        switch (status) {
            case PLAYER_WON -> {
                System.out.println("Player won");
                DBScore.updateScore("player", userID, ConnectionPool.getInstance().getDataSource());
            }
            case COMPUTER_WON -> DBScore.updateScore("computer", userID, ConnectionPool.getInstance().getDataSource());
            case DRAW -> DBScore.updateScore("draw", userID, ConnectionPool.getInstance().getDataSource());
            case NOT_STARTED, RUNNING, MATCH_ALREADY_FINISHED -> System.out.println("Match not finished!");
            default -> {
                System.out.println("Wrong score!");
            }

//            default -> PrintService.getInstance().printInvalidStatus();
        }
    }


    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getComputerScore() {
        return computerScore;
    }

    public void setComputerScore(int computerScore) {
        this.computerScore = computerScore;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public int getRoundCounter(){
        return playerScore + computerScore + drawCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Score score)) return false;
        return playerScore == score.playerScore && computerScore == score.computerScore && drawCount == score.drawCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerScore, computerScore, drawCount);
    }
}
