package tictactoe.api.errors;

public enum ErrorCodes {

    NO_TOKEN(0,"No Token provided"),
    TOKEN_INVALID(1, "Provided Token is invalid"),

    INVALID_REQUEST_BODY(2, "Request Body Incomplete"),

    USERNAME_NOT_FOUND(4, "Username does not Exist"),
    USERNAME_OR_PASSWORD_INCORRECT(5, "Username or Password is incorrect"),
    USERNAME_ALREADY_EXISTS(6, "Username already exists"),
    USERNAME_TOO_SHORT(7, "Username too short"),

    PASSWORD_WEAK(3, "Password too Weak"),
    PASSWORD_EMPTY(8, "Password is empty"),
    PASSWORD_TOO_SHORT(9, "Password too short"),
    PASSWORD_NO_LOWERCASE(10, "Password needs lowercase letter"),
    PASSWORD_NO_UPPERCASE(11, "Password needs uppercase letter"),
    PASSWORD_NO_DIGIT(12, "Password needs digit"),
    PASSWORD_NO_SPECIAL(13, "Password needs special character"),

    QUESTION_NOT_FOUND(14, "Security Questions Empty"),
    QUESTION_WRONG(15, "Security Question is wrong"),

    MATCH_HISTORY_EMPTY(16, "Match History is Empty."),
    MATCH_NOT_FOUND(25, "Match Not Found"),

    QUERY_TIME_NEGATIVE(17, "Parameter seconds can't be zero or negative."),
    QUERY_AMOUNT_NEGATIVE(18, "Parameter amount can't be zero or negative.."),
    QUERY_TIME_NOT_LONG(19, "Parameter seconds can only be a Long"),
    QUERY_AMOUNT_NOT_INT(20, "Parameter amount can only be an Int"),
    QUERY_TIME_TO_LARGE(21, "Parameter seconds can only be 209.999.999.999 max"),
    QUERY_AMOUNT_TO_LARGE(22, "Parameter amount can only be 1.000 max"),
    QUERY_STATUS_INVALID(23, "This Status does not exist."),
    QUERY_DIFFICULTY_INVALID(24, "This Difficulty does not exist.");




    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + ":" + message;
    }

    public static String decodeMessage(String throwableMessage) {
        return throwableMessage.split(":")[1];
    }

    public static int decodeCode(String throwableMessage) {
        return Integer.parseInt(throwableMessage.split(":")[0]);
    }

}
