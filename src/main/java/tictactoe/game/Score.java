package tictactoe.game;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;

import tictactoe.database.ConnectionPool;
import tictactoe.database.DBScore;

public class Score {

    private int playerScore;
    private int computerScore;
    private int drawCount;

    public Score() {
    }

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

        }
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getComputerScore() {
        return computerScore;
    }

    public int getDrawCount() {
        return drawCount;
    }

    @JsonIgnore
    public int getRoundCounter(){
        return playerScore + computerScore + drawCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Score score)) {
            return false;
        }
        return playerScore == score.playerScore && computerScore == score.computerScore && drawCount == score.drawCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerScore, computerScore, drawCount);
    }
}
