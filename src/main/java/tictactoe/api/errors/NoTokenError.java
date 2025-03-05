package tictactoe.api.errors;

public class NoTokenError extends RuntimeException {
  public NoTokenError(String message) {
    super(message);
  }
}
