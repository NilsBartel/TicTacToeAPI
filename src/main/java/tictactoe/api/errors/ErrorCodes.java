package tictactoe.api.errors;

public enum ErrorCodes {
    NO_TOKEN(0,"No token provided"),
    TOKEN_INVALID(1, "provided Token is invalid");

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
