package tictactoe.api.errors;

public class TokenError extends RuntimeException {
  public TokenError(String message) {
    super(message);
  }
}
