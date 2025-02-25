package tictactoe.api.account;

import com.sun.net.httpserver.HttpExchange;
import io.vavr.control.Try;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.ErrorHandler;

public class AuthenticateToken {


    static ErrorHandler errorHandler = new ErrorHandler();
    public static boolean handle(HttpExchange exchange, String authToken) {
        Try.run(() -> tokenValid(authToken)).onFailure(t -> {errorHandler.handle(t, exchange);});
        return true;
    }

    private static void tokenValid(String authToken) {
        AuthenticationToken.getInstance().authenticate(authToken);
    }


}
