package tictactoe.api.errors;

public class MatchError extends Exception {

    public MatchError(String message) {
        super(message);
    }
    public MatchError(Throwable cause) {
        super(cause);
    }
    public MatchError(String message, Throwable cause) {
        super(message, cause);
    }

}
