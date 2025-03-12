package api.databaseData;

import tictactoe.game.Score;

public class ScoreData {

    private static final Score score1 = new Score(5,2,1);
    private static final Score score2 = new Score(1,1,0);


    public static Score getScore1() {
        return score1;
    }
    public static Score getScore2() {
        return score2;
    }
}
