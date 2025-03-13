package tictactoe.login;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.api.errors.LoginError;
import tictactoe.database.DBUser;
import tictactoe.user.User;

public final class LogIn {

    public static final int USERNAME_MIN_LENGTH = 3;
    private static LogIn instance;

    private LogIn() {
    }

    public static LogIn getInstance() {
        if (instance == null) {
            instance = new LogIn();
        }
        return instance;
    }

    public void logInUser(String userName, String password, HikariDataSource dataSource) throws LoginError {

        if (!DBUser.userExists(userName, dataSource) || !PasswordUtil.checkPassword(
            password,
            DBUser.getPassword(userName, dataSource)
        )) {
            throw new LoginError("Username or Password is incorrect!");
        }

    }

    public void createUser(User user, HikariDataSource dataSource) throws LoginError {

        if (DBUser.userExists(user.getUserName(), dataSource)) {
            throw new LoginError("This user already exists");
        }

        if (user.getUserName().length() < USERNAME_MIN_LENGTH) {
            throw new LoginError("Username too short");
        }

        if (!PasswordUtil.isPasswordValid(user.getPassword())) {
            throw new LoginError("Password is not strong enough");
        }

        if (user.getAnswer1().isEmpty()) {
            throw new LoginError("Security answer1 is Empty");
        }

        if (user.getAnswer2().isEmpty()) {
            throw new LoginError("Security answer2 is Empty");
        }

        String userName = user.getUserName();
        String password = user.getPassword();
        String question1 = user.getAnswer1();
        String question2 = user.getAnswer2();

        user = new User(userName, HashService.hash(password), HashService.hash(question1), HashService.hash(question2));
        DBUser.insertUser(user, dataSource);
    }

}
