package tictactoe.api.errors;



public class LoginError extends Exception {
//    private final String description;
//    private final int code;

    public LoginError(ErrorCodes error) {
        super(error.toString());

//        this.description = description;
//        this.code = code;
    }

    public LoginError(String message) {
        super(message);
    }


//    @Override
//    public String getMessage() {
//        return message;
//    }


//    public String getDescription() {
//        return description;
//    }
//
//    public int getCode() {
//        return code;
//    }
}
