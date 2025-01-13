package tictactoe.game;

import tictactoe.PlayerInput;
import tictactoe.database.*;
import tictactoe.login.*;

import java.util.Scanner;

public class StartMenu {
    private static final Scanner SCANNER = new Scanner(System.in);


    private String myScanner() {
        return SCANNER.nextLine();
    }


    public void openMenu(){
        System.out.println();
        System.out.println("Type (a) to Log in.");
        System.out.println("Type (r) to reset password.");
        System.out.println("Type (b) to create a new account.");
        System.out.println("Type (q) to quit game.");

        String response = myScanner();
        while(! ("a".equals(response) || "r".equals(response) || "b".equals(response) || "q".equals(response))) {
            System.out.println("invalid input");
            response = myScanner();
        }

        String userName;

        PlayerInput playerInput = PlayerInput.getInstance();

        switch(response) {
            case "a": {
                userName = playerInput.askForUserName();
                if(!LogIn.getInstance().logInUserOLD(userName, playerInput, ConnectionPool.getInstance().getDataSource())) {
                    System.out.println();
                    System.out.println("Wrong username or password");
                    openMenu();
                }
                break;
            }
            case "r": {
                userName = playerInput.askForUserName();
                int userID = DBUser.getUserId(userName, ConnectionPool.getInstance().getDataSource());
                while(!PasswordUtil.resetPassword(userID, PlayerInput.getInstance(), LogInOutput.getInstance(), ConnectionPool.getInstance().getDataSource())) {
                    System.out.println("failed to reset password");
                }
                break;
            }
            case "b": {
                userName = LogIn.getInstance().createUser(ConnectionPool.getInstance().getDataSource());
                break;
            }
            case "q": {
                System.exit(0);
            }
            default: {
                userName = null;
            }
        }

        int userID = DBUser.getUserId(userName, ConnectionPool.getInstance().getDataSource());
        StartGame startGame = new StartGame();
        startGame.start(userID);
    }


}
