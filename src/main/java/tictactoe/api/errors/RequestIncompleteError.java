package tictactoe.api.errors;

public class RequestIncompleteError extends Exception {

    public RequestIncompleteError(ErrorCodes error) {
        super(error.toString());
    }
}
