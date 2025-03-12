package tictactoe.api.score;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.ErrorHandler;
import tictactoe.api.errors.LoginError;
import tictactoe.api.errors.MethodNotAllowed;
import tictactoe.api.errors.NoTokenError;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBScore;
import tictactoe.game.Score;

import java.io.IOException;
import java.io.OutputStream;

public class ScoreController {
    
    private final HttpServer server;
    private final HikariDataSource dataSource;
    
    public ScoreController(HttpServer server, HikariDataSource dataSource) {
        this.server = server;
        this.dataSource = dataSource;
    }



    public void endPoint() {

        ErrorHandler errorHandler = new ErrorHandler();
        server.createContext("/score/", exchange ->
                Try.run(() -> handeScore(exchange))
                        .onFailure(t -> {
                            errorHandler.handle(t, exchange);
                        })
        );
    }
    private void handeScore(HttpExchange exchange) throws IOException, MethodNotAllowed, LoginError {
        ObjectMapper objectMapper = new ObjectMapper();
        Score score;

        String token = null;
        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new NoTokenError("No token provided");
        }
        AuthenticationToken.getInstance().authenticate(token);
        if (exchange.getRequestMethod().equals("GET")) {
            int userID = AuthenticationToken.getInstance().getUserID(token);

            score = DBScore.getScore(userID, dataSource);




            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(score));
        responseBody.flush();
        responseBody.close();
    }
}
