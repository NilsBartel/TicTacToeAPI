package tictactoe.game;

import tictactoe.PrintService;

import java.util.List;

public class MatchHistory {
    public static final int MAX_HISTORY_SIZE = 10;


    public static void printMatchHistory(List<Match> matches, PrintService printService) {
        int counter = 1;
        //List<Match> matches = DB_Match.getLastNMatchesFromUser(userID, MAX_HISTORY_SIZE, DB_ConHandler.getInstance());

        for(Match match : matches){

            printService.printBoardNr(counter);
            printService.printBoard(match);
            printService.printSecondsElapsed(match.getStartTime(), match.getEndTime());
            printService.printDate(match.getStartTime());

            counter++;
        }
    }



}
