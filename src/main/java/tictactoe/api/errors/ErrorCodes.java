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

    MATCH_HISTORY_EMPTY(16, "Match History is Empty.");




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
