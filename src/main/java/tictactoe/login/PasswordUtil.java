package tictactoe.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.api.errors.LoginError;
import tictactoe.database.DBUser;
import tictactoe.user.User;

public final class PasswordUtil {

    private final static int PASSWORD_MIN_LENGTH = 8;
    private final static int PASSWORD_MAX_LENGTH = 32;

    private PasswordUtil() {
    }

    public static boolean isPasswordValid(String password) {

        if (password.length() >= PASSWORD_MIN_LENGTH && password.length() < PASSWORD_MAX_LENGTH) {

            Pattern lowerCaseLetterPattern = Pattern.compile("[a-zäüöß]");
            Pattern upperCaseLetterPattern = Pattern.compile("[A-ZÄÖÜ]");
            Pattern digitPattern = Pattern.compile("[0-9]");
            Pattern specialPattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~\\-.,\"^°'´`]");

            Matcher lowerLetterMatcher = lowerCaseLetterPattern.matcher(password);
            Matcher upperLetterMatcher = upperCaseLetterPattern.matcher(password);
            Matcher digitMatcher = digitPattern.matcher(password);
            Matcher specialMatcher = specialPattern.matcher(password);

            return lowerLetterMatcher.find() && upperLetterMatcher.find() && digitMatcher.find() && specialMatcher.find();
        }
        return false;
    }

    public static boolean checkPassword(String inputPassword, String hashedPassword) {

        return HashService.verify(inputPassword, hashedPassword);
    }

    public static void resetPassword(int userID, User user, HikariDataSource dataSource) {

        String newPassword = user.getPassword();
        DBUser.updatePassword(userID, HashService.hash(newPassword), dataSource);
    }

    public static Boolean checkSecurityQuestions(int userID, User user, HikariDataSource dataSource) throws LoginError {

        String userAnswer1 = user.getAnswer1();
        if (!HashService.verify(userAnswer1, DBUser.getAnswer1(userID, dataSource))) {
            throw new LoginError("Security questions do not match!");
        }

        String userAnswer2 = user.getAnswer2();
        if (!HashService.verify(userAnswer2, DBUser.getAnswer2(userID, dataSource))) {
            throw new LoginError("Security questions do not match!");
        }

        return true;
    }

}
