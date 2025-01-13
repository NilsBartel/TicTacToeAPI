package tictactoe.login;

import tictactoe.user.User;
import tictactoe.PlayerInput;
import tictactoe.database.*;

import com.zaxxer.hikari.HikariDataSource;

public final class LogIn {

    public static final int USERNAME_MIN_LENGTH = 3;
    private static LogIn instance;
    private LogIn() {}
    public static LogIn getInstance() {
        if (instance == null) {
            instance = new LogIn();
        }
        return instance;
    }


    public boolean logInUserOLD(String userName, PlayerInput playerInput, HikariDataSource dataSource) {
        String password;
        password = playerInput.askForPassword();

        return DBUser.userExists(userName, dataSource) && PasswordUtil.checkPassword(password, DBUser.getPassword(userName, dataSource));
    }

    public boolean logInUser(String userName, String password, HikariDataSource dataSource) {
//        String password;
//        password = playerInput.askForPassword();

        return DBUser.userExists(userName, dataSource) && PasswordUtil.checkPassword(password, DBUser.getPassword(userName, dataSource));
    }


    public String createUser(HikariDataSource dataSource) {
        PlayerInput playerInput = PlayerInput.getInstance();
        String userName;

        while(true) {
            userName = playerInput.createNewUserName();
            if (!DBUser.userExists(userName, dataSource)) {
                String password = playerInput.crateNewPassword();
                String question1 = playerInput.askRecoveryQuestion1();
                String question2 = playerInput.askRecoveryQuestion2();

                User user = new User(userName, HashService.hash(password), HashService.hash(question1), HashService.hash(question2));
                DBUser.insertUser(user, dataSource);

                break;
            }
        }

        LogInOutput.getInstance().createdNewUser(userName);
        return userName;
    }

}
