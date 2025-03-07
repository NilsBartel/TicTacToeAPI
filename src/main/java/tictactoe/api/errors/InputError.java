package tictactoe.api.errors;

public class InputError extends RuntimeException {
    public InputError(String message) {
        super(message);
    }
}
