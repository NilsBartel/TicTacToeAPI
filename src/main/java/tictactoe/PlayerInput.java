package tictactoe;

import tictactoe.board.*;
import tictactoe.database.*;
import tictactoe.game.*;
import tictactoe.user.User;
import tictactoe.login.*;


import java.util.List;
import java.util.Scanner;

public final class PlayerInput {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String INVALID_INPUT = "This is not a valid input! Please try again.";
    private static PlayerInput instance;

    private PlayerInput() {
    }
    public static PlayerInput getInstance() {
        if (instance == null) {
            instance = new PlayerInput();
        }
        return instance;
    }



    private String myScanner() {
        return SCANNER.nextLine();
    }

    public Position askForMove(Board board) {
        String move;
        int number;
        while(true){
            System.out.println("Please pick a field (1-9)");
            move = myScanner();

            if(isInteger(move) && board.isValid(Integer.parseInt(move))){
                number = Integer.parseInt(move);
                break;
            } else {
                System.out.println(INVALID_INPUT);
            }
        }

        return new Position(number);
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public boolean askPlayAgain(int userID) {
        String response;
        MatchHistory matchHistory = new MatchHistory();

        System.out.println();
        System.out.println("Do you want to play again? (Type y/n)");
        System.out.println("type (h) for history.");
        System.out.println("type (a) to analyse Match History.");
        System.out.println("type (q) to Log Out.");
        response = myScanner();

        while(! ("y".equals(response) || "n".equals(response) || "h".equals(response) || "a".equals(response) || "q".equals(response))) {
            System.out.println(INVALID_INPUT);
            response = myScanner();
        }

        if("h".equals(response)) {
            List<Match> matches = DBMatch.getLastNMatchesFromUser(userID, MatchHistory.MAX_HISTORY_SIZE, ConnectionPool.getInstance().getDataSource());
            MatchHistory.printMatchHistory(matches, PrintService.getInstance());
            return askPlayAgain(userID);
        }

        if("a".equals(response)) {
            PrintService.getInstance().printAnalysedWinPositions(AnalyseService.getInstance().findBestWinningLine(userID, ConnectionPool.getInstance().getDataSource()));
            return askPlayAgain(userID);
        }

        if("q".equals(response)) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("Logged out successfully!");
            System.out.println();
            StartMenu startMenu = new StartMenu();
            startMenu.openMenu();
        }


        return "y".equals(response);
    }

    public DifficultyState askForDifficulty() {
        String response;
        System.out.println();
        System.out.println("What difficulty would you like to play?   Easy, Medium, impossible?   (Type e/m/i)");
        response = myScanner();

        while(! ("e".equals(response) || "m".equals(response) || "i".equals(response))) {
            System.out.println(INVALID_INPUT);
            response = myScanner();
        }

        return switch (response) {
            case "e" -> DifficultyState.EASY;
            case "m" -> DifficultyState.MEDIUM;
            case "i" -> DifficultyState.IMPOSSIBLE;
            default -> throw new IllegalStateException("Unexpected value: " + response);
        };
    }

    @SuppressWarnings("PMD.UnusedLocalVariable")
    public boolean isInteger(String input) {
        if (input == null) {
            return false;
        }
        try {
            int num = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String askForUserName(){
        String response;
        System.out.println();
        System.out.println("What is your username?");
        response = myScanner();

        return response;
    }

    public String askForPassword(){
        String response;
        System.out.println();
        System.out.println("What is your password?");
        response = myScanner();

        return response;
    }

    public String createNewUserName() {
        String response;
        System.out.println();
        System.out.println("Please choose a Username.");
        response = myScanner();
        while (response.length() < LogIn.USERNAME_MIN_LENGTH){
            System.out.println("User name too short, please try again.");
            response = myScanner();
        }
        return response;
    }

    public String crateNewPassword() {
        String response;
        System.out.println();
        System.out.println("Please choose a Password.");
        System.out.println("Use at least 1 of each:");
        System.out.println("- upper case and lower case letters");
        System.out.println("- number");
        System.out.println("- special character");
        System.out.println("- 8 < ... > 32 characters");
        response = myScanner();
        while (!PasswordUtil.isPasswordValid(response)){
            System.out.println("Password not valid, please try again.");
            response = myScanner();
        }
        return response;
    }

    public String askRecoveryQuestion1() {
        String response;
        System.out.println();
        System.out.println(User.getQUESTION1());
        response = myScanner();
        return response;
    }
    public String askRecoveryQuestion2() {
        String response;
        System.out.println();
        System.out.println(User.getQUESTION2());
        response = myScanner();
        return response;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public boolean askPasswordReset() {
        String response;
        System.out.println();
        System.out.println("Do you want to reset your password? (Type y/n)");
        response = myScanner();
        while(! ("y".equals(response) || "n".equals(response))) {
            System.out.println(INVALID_INPUT);
            response = myScanner();
        }
        return "y".equals(response);
    }


}
