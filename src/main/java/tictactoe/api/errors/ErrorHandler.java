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
            case MatchError matchError -> {
                response = new ErrorResponse(throwable.getMessage(), 422);
                exchange.sendResponseHeaders(422, 0);
            }
            case LoginError loginError -> {
                response = new ErrorResponse(throwable.getMessage(), 401);
                exchange.sendResponseHeaders(401, 0);
            }
            case MethodNotAllowed methodNotAllowed -> {
                response = new ErrorResponse(throwable.getMessage(), 405);
                exchange.sendResponseHeaders(405, 0);
            }
            case NoTokenError noTokenError -> {
                response = new ErrorResponse(throwable.getMessage(), 401);
                exchange.sendResponseHeaders(401, 0);
            }
            case InputError inputError -> {
                response = new ErrorResponse(throwable.getMessage(), 400);
                exchange.sendResponseHeaders(400, 0);
            }
            case PasswordStrengthError passwordStrengthError -> {
                response = new ErrorResponse(throwable.getMessage(), 403);
                exchange.sendResponseHeaders(403, 0);
            }
            case null, default -> {
                response = new ErrorResponse(throwable.getMessage(), 500);
                exchange.sendResponseHeaders(500, 0);
            }
        }
        return response;
    }

}
