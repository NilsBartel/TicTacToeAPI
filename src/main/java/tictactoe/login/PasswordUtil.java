package tictactoe.login;

import java.util.regex.Pattern;

import com.zaxxer.hikari.HikariDataSource;
import ch.qos.logback.core.util.StringUtil;
import tictactoe.api.errors.*;
import tictactoe.database.DBUser;
import tictactoe.user.User;

public final class PasswordUtil {

    private final static int PASSWORD_MIN_LENGTH = 8;

    private PasswordUtil() {
    }

    public static boolean isPasswordValid(String password) throws SignUpError {
        if (StringUtil.isNullOrEmpty(password)) {
            throw new SignUpError(ErrorCodes.PASSWORD_EMPTY);
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new SignUpError(ErrorCodes.PASSWORD_TOO_SHORT);
        }


        Pattern lowerCaseLetterPattern = Pattern.compile("[a-zäüöß]");
        Pattern upperCaseLetterPattern = Pattern.compile("[A-ZÄÖÜ]");
        Pattern digitPattern = Pattern.compile("[0-9]");
        Pattern specialPattern = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~\\-.,\"^°'´`]");

        if (!lowerCaseLetterPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_LOWERCASE);
        }
        if (!upperCaseLetterPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_UPPERCASE);
        }
        if (!digitPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_DIGIT);
        }
        if (!specialPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_SPECIAL);
        }

        return true;
    }

    public static boolean checkPassword(String inputPassword, String hashedPassword) {
        return HashService.verify(inputPassword, hashedPassword);
    }

    public static void resetPassword(int userID, User user, HikariDataSource dataSource) {
        String newPassword = user.getPassword();
        DBUser.updatePassword(userID, HashService.hash(newPassword), dataSource);
    }


    public static Boolean checkSecurityQuestions(int userID, User user, HikariDataSource dataSource) throws LoginError, RequestIncompleteError {
        if (StringUtil.isNullOrEmpty(user.getAnswer1()) || StringUtil.isNullOrEmpty(user.getAnswer2())) {
            throw new RequestIncompleteError(ErrorCodes.QUESTION_NOT_FOUND);
        }

        String userAnswer1 = user.getAnswer1();
        if (!HashService.verify(userAnswer1, DBUser.getAnswer1(userID, dataSource))) {
            throw new RequestIncompleteError(ErrorCodes.QUESTION_WRONG);
        }

        String userAnswer2 = user.getAnswer2();
        if (!HashService.verify(userAnswer2, DBUser.getAnswer2(userID, dataSource))) {
            throw new RequestIncompleteError(ErrorCodes.QUESTION_WRONG);
        }

        return true;
    }

}
