package tictactoe.login;

import tictactoe.PlayerInput;
import tictactoe.database.*;

import com.zaxxer.hikari.HikariDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PasswordUtil {

    private final static int PASSWORD_MIN_LENGTH = 8;
    private final static int PASSWORD_MAX_LENGTH = 32;

    private PasswordUtil() {}



    public static boolean isPasswordValid(String password) {

        if(password.length() >= PASSWORD_MIN_LENGTH && password.length() < PASSWORD_MAX_LENGTH) {

            Pattern lowerCaseLetterPattern = Pattern.compile("[a-zäüöß]");
            Pattern upperCaseLetterPattern = Pattern.compile("[A-ZÄÖÜ]");
            Pattern digitPattern = Pattern.compile("[0-9]");
            Pattern specialPattern = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~\\-.,\"^°'´`]");


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


    public static boolean resetPassword(int userID, PlayerInput playerInput, LogInOutput logInOutput, HikariDataSource dataSource) {

        if (checkSecurityQuestions(userID, playerInput, logInOutput, dataSource)) {
            String newPassword = playerInput.crateNewPassword();


            DBUser.updatePassword(userID, HashService.hash(newPassword), dataSource);
            return true;

        } else {
            logInOutput.failedReset();
            return false;
        }
    }

    private static Boolean checkSecurityQuestions(int userID, PlayerInput playerInput, LogInOutput logInOutput, HikariDataSource dataSource) {

        boolean bool1 = false;
        boolean bool2 = false;

        String userAnswer1 = playerInput.askRecoveryQuestion1();
        if (HashService.verify(userAnswer1, DBUser.getAnswer1(userID, dataSource))) {
            logInOutput.correct();
            bool1 = true;
        } else {
            logInOutput.incorrect();
        }

        String userAnswer2 = playerInput.askRecoveryQuestion2();
        if (HashService.verify(userAnswer2, DBUser.getAnswer2(userID, dataSource))) {
            logInOutput.correct();
            bool2 = true;
        } else {
            logInOutput.incorrect();
        }
        return bool1 && bool2;
    }

}
