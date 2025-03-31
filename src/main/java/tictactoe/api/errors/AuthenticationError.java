package tictactoe.api.errors;

public class AuthenticationError extends Exception {

    public AuthenticationError(ErrorCodes error) {
        super(error.toString());
    }

}
