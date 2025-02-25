package tictactoe.api.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tictactoe.api.ErrorResponse;

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
        ErrorResponse response = new ErrorResponse(throwable.getMessage(), 400);
        exchange.sendResponseHeaders(400, 0);

        return response;

    }
}
