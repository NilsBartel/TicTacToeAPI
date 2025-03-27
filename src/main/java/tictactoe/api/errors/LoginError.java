package tictactoe.api.errors;

public class LoginError extends Exception {

    public LoginError(ErrorCodes error) {
        super(error.toString());
    }
}
