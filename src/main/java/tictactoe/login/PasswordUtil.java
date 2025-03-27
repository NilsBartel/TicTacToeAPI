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
            //throw new PasswordStrengthError("password is empty or null");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new SignUpError(ErrorCodes.PASSWORD_TOO_SHORT);
            //throw new PasswordStrengthError("password is too short");
        }


        Pattern lowerCaseLetterPattern = Pattern.compile("[a-zäüöß]");
        Pattern upperCaseLetterPattern = Pattern.compile("[A-ZÄÖÜ]");
        Pattern digitPattern = Pattern.compile("[0-9]");
        Pattern specialPattern = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~\\-.,\"^°'´`]");

        if (!lowerCaseLetterPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_LOWERCASE);
            //throw new PasswordStrengthError("password needs lower case letter");
        }
        if (!upperCaseLetterPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_UPPERCASE);
            //throw new PasswordStrengthError("password needs upper case letter");
        }
        if (!digitPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_DIGIT);
            //throw new PasswordStrengthError("password needs digit");
        }
        if (!specialPattern.matcher(password).find()) {
            throw new SignUpError(ErrorCodes.PASSWORD_NO_SPECIAL);
            //throw new PasswordStrengthError("password needs special character");
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
            //throw new InputError("Security questions required");
        }

        String userAnswer1 = user.getAnswer1();
        if (!HashService.verify(userAnswer1, DBUser.getAnswer1(userID, dataSource))) {
            throw new RequestIncompleteError(ErrorCodes.QUESTION_WRONG);
            //throw new LoginError("Security questions do not match!");
        }

        String userAnswer2 = user.getAnswer2();
        if (!HashService.verify(userAnswer2, DBUser.getAnswer2(userID, dataSource))) {
            throw new RequestIncompleteError(ErrorCodes.QUESTION_WRONG);
            //throw new LoginError("Security questions do not match!");
        }

        return true;
    }

}
