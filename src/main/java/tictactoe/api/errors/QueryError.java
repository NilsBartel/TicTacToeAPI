package tictactoe.api.errors;

public class QueryError extends RuntimeException {
  public QueryError(ErrorCodes error) {
    super(error.toString());
  }
}
