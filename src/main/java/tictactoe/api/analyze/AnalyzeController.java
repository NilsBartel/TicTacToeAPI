package tictactoe.api.analyze;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.ErrorHandler;
import tictactoe.api.errors.LoginError;
import tictactoe.api.errors.MethodNotAllowed;
import tictactoe.board.Position;
import tictactoe.game.AnalyseService;

public class AnalyzeController {

    public static void endPoint(HttpServer server, HikariDataSource dataSource) {

        ErrorHandler errorHandler = new ErrorHandler();
        server.createContext("/analyze/", exchange ->
                Try.run(() -> handleAnalyze(exchange, dataSource))
                        .onFailure(t -> {
                            errorHandler.handle(t, exchange);
                        })
        );
    }
    private static void handleAnalyze(HttpExchange exchange, HikariDataSource dataSource) throws IOException, MethodNotAllowed, LoginError {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<List<Position>, Integer> positionMap;

        String token = exchange.getRequestHeaders().get("token").getFirst();
        AuthenticationToken.getInstance().authenticate(token);

        if (exchange.getRequestMethod().equals("GET")) {
            int userID = AuthenticationToken.getInstance().getUserID(token);

            positionMap =
                    AnalyseService.getInstance().findBestWinningLine(userID, dataSource);


            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(positionMap));
        responseBody.flush();
        responseBody.close();
    }
}
