package tictactoe.api.errors;

public class SignUpError extends Exception {

    public SignUpError(ErrorCodes error) {
        super(error.toString());
    }
}
