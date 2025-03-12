package tictactoe.api.errors;

public class PasswordStrengthError extends RuntimeException {
    public PasswordStrengthError(String message) {
        super(message);
    }
}
