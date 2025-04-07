package tictactoe.api.errors;

public class NotFoundError extends Exception {
    public NotFoundError(ErrorCodes error) {
        super(error.toString());
    }
}

