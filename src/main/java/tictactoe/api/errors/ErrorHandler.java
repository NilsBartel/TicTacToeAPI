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
        ErrorResponse response;
        if (throwable instanceof MatchError) {
            response = new ErrorResponse(throwable.getMessage(), 422);
            exchange.sendResponseHeaders(422, 0);
        } else if (throwable instanceof LoginError) {
            response = new ErrorResponse(throwable.getMessage(), 401);
            exchange.sendResponseHeaders(401, 0);
        }else {
            response = new ErrorResponse(throwable.getMessage(), 500);
            exchange.sendResponseHeaders(500, 0);
        }
        return response;



//        ErrorResponse response = new ErrorResponse(throwable.getMessage(), 422);
//        exchange.sendResponseHeaders(422, 0);
//
//        return response;

    }
}
