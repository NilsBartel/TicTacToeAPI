package tictactoe.api.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.ErrorHandler;
import tictactoe.api.errors.MatchError;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.database.DBScore;
import tictactoe.database.Database;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class MatchController {

    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void endPoint(HttpServer server) {


        server.createContext("/match/", (MatchController::handleMatch));
        //server.createContext("/match/play", (MatchController::handlePlay));
        server.createContext("/match/play", (MatchController::handle));

        server.createContext("/match/create", (MatchController::handleCreateMatch));


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

    //static MatchError testError = new MatchError();
    static ErrorHandler errorHandler = new ErrorHandler();
    public static void handle(HttpExchange exchange) {
        Try.run(() -> handlePlay(exchange)).onFailure(t -> {errorHandler.handle(t, exchange);});
    }


    public static void handlePlay(HttpExchange exchange) throws IOException, MatchError {
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




            if (match.validateMatch(userID)) {
                Database.updateDB_Match(match, userID, ConnectionPool.getInstance().getDataSource());
                match.computerPlay(userID);
            }


            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(DBMatch.getMatch(userID, match.getMatchID(), ConnectionPool.getInstance().getDataSource())));
            responseBody.flush();
            responseBody.close();




        }



    }public static void handleCreateMatch(HttpExchange exchange) throws IOException {
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


        if (exchange.getRequestMethod().equals("GET")) {
            int userID = AuthenticationToken.getInstance().getUserID(token);

//            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
//            Match match = objectMapper.readValue(requestBody, Match.class);
//            match.printBoard();
//
//
//            System.out.println(match.validateMatch(userID) + " - match equals");
            Match match = new Match();
            //TODO
            //TODO: needs to come from the person somehow
            match.setStatus(MatchStatus.RUNNING);
            match.setDifficulty(DifficultyState.EASY);
            //match.setIsPlayerTurn(true);
            match.setStartTime(new Timestamp(System.currentTimeMillis()));

            match.setPlayerTurn(DBScore.getScore(userID, ConnectionPool.getInstance().getDataSource()));

            if (!match.isIsPlayerTurn()) {
                //TODO: play
                match.computerPlay(userID);

            }


            //match.setIsPlayerTurn(match.setPlayerTurn(DBScore.getScore(userID, ConnectionPool.getInstance().getDataSource())));


            //TODO
            System.out.println("test1");
            int matchID = DBMatch.insertNewMatch(match, userID, ConnectionPool.getInstance().getDataSource());
            System.out.println("test");
            match.setMatchID(matchID);


            //TODO: start game







            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(DBMatch.getMatch(userID, match.getMatchID(), ConnectionPool.getInstance().getDataSource())));
            responseBody.flush();
            responseBody.close();

        }



    }

























    private static int getIDFromPath(String path) {
        String[] parts = path.split("/");
        String id = parts[parts.length - 1];
        return Integer.parseInt(id);
    }












}
