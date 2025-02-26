package tictactoe.login;

import tictactoe.api.errors.LoginError;
import tictactoe.user.User;
//import tictactoe.PlayerInput;
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


//    public boolean logInUserOLD(String userName, PlayerInput playerInput, HikariDataSource dataSource) {
//        String password;
//        password = playerInput.askForPassword();
//
//        return DBUser.userExists(userName, dataSource) && PasswordUtil.checkPassword(password, DBUser.getPassword(userName, dataSource));
//    }

    public boolean logInUser(String userName, String password, HikariDataSource dataSource) throws LoginError {

        if (!DBUser.userExists(userName, dataSource) || !PasswordUtil.checkPassword(password, DBUser.getPassword(userName, dataSource))) {
            throw new LoginError("Username or Password is incorrect!");
        }
//        String password;
//        password = playerInput.askForPassword();
        return true;

        //return DBUser.userExists(userName, dataSource) && PasswordUtil.checkPassword(password, DBUser.getPassword(userName, dataSource));
    }


//    public String createUserOLD(HikariDataSource dataSource) {
//        PlayerInput playerInput = PlayerInput.getInstance();
//        String userName;
//
//        while(true) {
//            userName = playerInput.createNewUserName();
//            if (!DBUser.userExists(userName, dataSource)) {
//                String password = playerInput.crateNewPassword();
//                String question1 = playerInput.askRecoveryQuestion1();
//                String question2 = playerInput.askRecoveryQuestion2();
//
//                User user = new User(userName, HashService.hash(password), HashService.hash(question1), HashService.hash(question2));
//                DBUser.insertUser(user, dataSource);
//
//                break;
//            }
//        }
//
//        LogInOutput.getInstance().createdNewUser(userName);
//        return userName;
//    }

    public Boolean createUser(User user, HikariDataSource dataSource) throws LoginError {

        if (DBUser.userExists(user.getUserName(), dataSource)) {
            throw new LoginError("This user already exists");
        }

        if (!PasswordUtil.isPasswordValid(user.getPassword())) {
            throw new LoginError("Password is not strong enough");
        }

        String userName = user.getUserName();
        String password = user.getPassword();
        String question1 = user.getAnswer1();
        String question2 = user.getAnswer2();

        user = new User(userName, HashService.hash(password), HashService.hash(question1), HashService.hash(question2));
        DBUser.insertUser(user, dataSource);

//        if (!DBUser.userExists(user.getUserName(), dataSource)) {
//            String userName = user.getUserName();
//            String password = user.getPassword();
//            String question1 = user.getAnswer1();
//            String question2 = user.getAnswer2();
//
//            user = new User(userName, HashService.hash(password), HashService.hash(question1), HashService.hash(question2));
//            DBUser.insertUser(user, dataSource);
//
//            return true;
//        }
//        return false;
        return true;
    }



}
