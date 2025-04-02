package tictactoe.api.errors;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

public class ErrorHandler {

    public void handle(Throwable throwable, HttpExchange exchange) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            throwable.printStackTrace();
            ErrorResponse response = getErrorResponse(throwable, exchange);
            OutputStream responseBody = exchange.getResponseBody();

            responseBody.write(mapper.writeValueAsBytes(response));
            responseBody.flush();
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ErrorResponse getErrorResponse(Throwable throwable, HttpExchange exchange) throws IOException {
        ErrorResponse response;

        switch (throwable) {
            case AuthenticationError authenticationError -> {
                response = new ErrorResponse(ErrorCodes.decodeMessage(throwable.getMessage()), ErrorCodes.decodeCode(throwable.getMessage()));
                exchange.sendResponseHeaders(401, 0);
            }
            case RequestIncompleteError requestIncompleteError -> {
                response = new ErrorResponse(ErrorCodes.decodeMessage(throwable.getMessage()), ErrorCodes.decodeCode(throwable.getMessage()));
                exchange.sendResponseHeaders(422, 0);
            }
            case SignUpError signUpError -> {
                response = new ErrorResponse(ErrorCodes.decodeMessage(throwable.getMessage()), ErrorCodes.decodeCode(throwable.getMessage()));
                exchange.sendResponseHeaders(409, 0);
            }
            case LoginError loginError -> {
                response = new ErrorResponse(ErrorCodes.decodeMessage(throwable.getMessage()), ErrorCodes.decodeCode(throwable.getMessage()));
                exchange.sendResponseHeaders(401, 0);
            }
            case MethodNotAllowed methodNotAllowed -> {
                response = new ErrorResponse(throwable.getMessage(), -1);
                exchange.sendResponseHeaders(405, 0);
            }
            case MatchError matchError -> {
                response = new ErrorResponse(throwable.getMessage(), 422);
                exchange.sendResponseHeaders(422, 0);
            }
            case QueryError queryError -> {
                response = new ErrorResponse(ErrorCodes.decodeMessage(throwable.getMessage()), ErrorCodes.decodeCode(throwable.getMessage()));
                exchange.sendResponseHeaders(422, 0);
            }
            default -> {
                response = new ErrorResponse("Internal server Error", -1);
                exchange.sendResponseHeaders(500, 0);
            }
        }
        return response;
    }

}
