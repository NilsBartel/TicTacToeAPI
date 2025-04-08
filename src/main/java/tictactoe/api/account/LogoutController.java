package tictactoe.api.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.vavr.control.Try;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.*;

import java.io.IOException;
import java.io.OutputStream;

public class LogoutController {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void endPoint(HttpServer server) {
        ErrorHandler errorHandler = new ErrorHandler();

        server.createContext("/account/logout", exchange ->
                Try.run(() -> handleLogout(exchange))
                        .onFailure(t -> {
                            errorHandler.handle(t, exchange);
                        })
        );
    }


    static void handleLogout(HttpExchange exchange) throws RequestIncompleteError, AuthenticationError, IOException, MethodNotAllowed {
        LoginResponse loginResponse = new LoginResponse();
        OutputStream responseBody = exchange.getResponseBody();
        String token;

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            try {
                token = exchange.getRequestHeaders().get("token").getFirst();
            } catch (Exception e) {
                throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
            }
            AuthenticationToken.getInstance().logout(token);
            loginResponse.setMessage("Logout successful");


            exchange.sendResponseHeaders(200, 0);
            responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        } else if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }


        responseBody.flush();
        responseBody.close();
    }


}
