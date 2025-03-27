package tictactoe.api.errors;

public class AuthorisationError extends Exception {

    public AuthorisationError(ErrorCodes error) {
        super(error.toString());
    }

}
