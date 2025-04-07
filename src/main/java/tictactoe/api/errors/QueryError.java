package tictactoe.api.errors;

public class QueryError extends Exception {
  public QueryError(ErrorCodes error) {
    super(error.toString());
  }
}
