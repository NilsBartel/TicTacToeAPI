package tictactoe.game;

import tictactoe.PrintService;
import tictactoe.PlayerInput;
import tictactoe.database.*;

public class StartGame {


    public void start(int userID) {

        PrintService printService = PrintService.getInstance();
        PlayerInput playerInput = PlayerInput.getInstance();

        DifficultyState difficulty = null;
        do {
            Match match = StartGameUtil.returnRunningOrNewMatch( difficulty, userID);

            difficulty = match.getDifficulty();

            match.play(userID);

            Winner.printWhoWon(match.getStatus());
            Score.updateScore(match.getStatus(), userID);

            displayScore(DBScore.getScore(userID, ConnectionPool.getInstance().getDataSource()));

        } while (playerInput.askPlayAgain(userID));

        printService.printGameEndMessage();
    }


    private void displayScore(Score score) {
        PrintService.getInstance().printScore(score);
        PrintService.getInstance().printDrawCounter(score);
        PrintService.getInstance().printRoundCounter(score);
    }




}