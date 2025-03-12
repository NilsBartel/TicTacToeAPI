package tictactoe.api.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ErrorHandler {





    public void handle(Throwable throwable, HttpExchange exchange) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            throwable.printStackTrace();
//            exchange.getResponseHeaders().set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
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
        if (throwable instanceof MatchError) {
            response = new ErrorResponse(throwable.getMessage(), 422);
            exchange.sendResponseHeaders(422, 0);
        } else if (throwable instanceof LoginError) {
            response = new ErrorResponse(throwable.getMessage(), 401);
            exchange.sendResponseHeaders(401, 0);
        } else if (throwable instanceof MethodNotAllowed) {
            response = new ErrorResponse(throwable.getMessage(), 405);
            exchange.sendResponseHeaders(405, 0);
        } else if (throwable instanceof NoTokenError) {
            response = new ErrorResponse(throwable.getMessage(), 401);
            exchange.sendResponseHeaders(401, 0);
        } else if (throwable instanceof InputError) {
            response = new ErrorResponse(throwable.getMessage(), 400);
            exchange.sendResponseHeaders(400, 0);
        } else if (throwable instanceof PasswordStrengthError) {
            response = new ErrorResponse(throwable.getMessage(), 403);
            exchange.sendResponseHeaders(403, 0);
        } else {
            response = new ErrorResponse(throwable.getMessage(), 500);
            exchange.sendResponseHeaders(500, 0);
        }
        return response;
    }

    // LoginError = 400
    // wrong token error = 405




}
