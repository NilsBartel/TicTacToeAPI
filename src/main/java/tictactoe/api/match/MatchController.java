package tictactoe.api.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;
import tictactoe.api.AuthenticationToken;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.game.Match;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MatchController {

    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void endPoint(HttpServer server) {

        server.createContext("/match/", (MatchController::handleMatch));
        server.createContext("/match/play", (MatchController::playHandler));


    }




    public static void handleMatch(HttpExchange exchange) throws IOException {

        String token = exchange.getRequestHeaders().get("token").getFirst();
        MatchResponse matchResponse = new MatchResponse();

        if (!AuthenticationToken.getInstance().authenticate(token)) {

            matchResponse.setMessage("Invalid token");
            exchange.sendResponseHeaders(401, 0);

            OutputStream output = exchange.getResponseBody();
            output.write(objectMapper.writeValueAsBytes(matchResponse));
            output.flush();
            exchange.close();
            return;
        }

        Match match = new Match();

        if (exchange.getRequestMethod().equals("GET")) {
            int matchId = getIDFromPath(exchange.getRequestURI().getPath());
            int userID = AuthenticationToken.getInstance().getUserID(token);



            match = DBMatch.getMatch(userID, matchId, ConnectionPool.getInstance().getDataSource());

            //TODO: cant get a specific match (need a function)



            exchange.sendResponseHeaders(200, 0);
            //exchange.getResponseBody().close();

        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(match));
        responseBody.flush();
        responseBody.close();

    }


    public static void playHandler(HttpExchange exchange) throws IOException {
        String token = exchange.getRequestHeaders().getFirst("token");
        MatchResponse matchResponse = new MatchResponse();

        if (!AuthenticationToken.getInstance().authenticate(token)) {

            matchResponse.setMessage("Invalid token");
            exchange.sendResponseHeaders(401, 0);

            OutputStream output = exchange.getResponseBody();
            output.write(objectMapper.writeValueAsBytes(matchResponse));
            output.flush();
            exchange.close();
            return;
        }


        if (exchange.getRequestMethod().equals("POST")) {
            int userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Match match = objectMapper.readValue(requestBody, Match.class);
            match.printBoard();


            System.out.println(match.validateMatch(userID) + " - match equals");


            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(DBMatch.getMatch(userID, match.getMatchID(), ConnectionPool.getInstance().getDataSource())));
            responseBody.flush();
            responseBody.close();







            //exchange.close();

        }



    }




















    private static int getIDFromPath(String path) {
        String[] parts = path.split("/");
        String id = parts[parts.length - 1];
        return Integer.parseInt(id);
    }












}
