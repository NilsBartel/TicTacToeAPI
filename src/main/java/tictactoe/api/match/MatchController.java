package tictactoe.api.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.ErrorHandler;
import tictactoe.api.errors.MatchError;
import tictactoe.api.errors.MethodNotAllowed;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.database.DBScore;
import tictactoe.database.Database;
import tictactoe.game.DifficultyState;
import tictactoe.game.Game;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

public class MatchController {

    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void endPoint(HttpServer server) {

        ErrorHandler errorHandler = new ErrorHandler();
        server.createContext("/match/", exchange ->
                Try.run(() -> handleMatch(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

        server.createContext("/match/play", exchange ->
                Try.run(() -> handlePlay(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

        server.createContext("/match/create", exchange ->
                Try.run(() -> handleCreateMatch(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

        server.createContext("/match/start", exchange ->
                Try.run(() -> handleStart(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

        server.createContext("/match/matchHistory", exchange ->
                Try.run(() -> handleMatchHistory(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

    }






    public static void handleMatch(HttpExchange exchange) throws IOException, MethodNotAllowed, MatchError {

        String token = exchange.getRequestHeaders().get("token").getFirst();
        AuthenticationToken.getInstance().handleAuthentication(exchange, token);

        Match match;

        if (exchange.getRequestMethod().equals("GET")) {
            int userID = AuthenticationToken.getInstance().getUserID(token);

            //int matchId = getIDFromPath(exchange.getRequestURI().getPath());

            int matchId;
            try {
                matchId = getIDFromPath(exchange.getRequestURI().getPath());
            } catch (NumberFormatException e) {
                throw new MatchError("No number input found. Please input the matchID. (/match/20)");
            }

            match = DBMatch.getMatch(userID, matchId, ConnectionPool.getInstance().getDataSource());

            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(match));
        responseBody.flush();
        responseBody.close();
    }



    public static void handlePlay(HttpExchange exchange) throws IOException, MatchError, MethodNotAllowed {
        int userID;
        Match match;

        String token = exchange.getRequestHeaders().getFirst("token");
        AuthenticationToken.getInstance().handleAuthentication(exchange, token);


        if (exchange.getRequestMethod().equals("POST")) {
            userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            match = objectMapper.readValue(requestBody, Match.class);
            match.printBoard();

            if (ApiMatchUtil.validateMatch(userID, match)) {
                Database.updateDB_Match(match, userID, ConnectionPool.getInstance().getDataSource());
                Game.play(userID, match);
            }

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }


        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(DBMatch.getMatch(userID, match.getMatchID(), ConnectionPool.getInstance().getDataSource())));
        responseBody.flush();
        responseBody.close();
    }

    public static void handleStart(HttpExchange exchange) throws IOException, MethodNotAllowed, MatchError {
        int userID;
        Match match;

        String token = exchange.getRequestHeaders().getFirst("token");
        AuthenticationToken.getInstance().handleAuthentication(exchange, token);


        if (exchange.getRequestMethod().equals("GET")) {
            DifficultyState difficulty;
            userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            match = objectMapper.readValue(requestBody, Match.class);
            difficulty = match.getDifficulty();

            match = ApiMatchUtil.returnRunningOrNewMatch(difficulty, userID);

            if (!match.isIsPlayerTurn()) {
                Game.play(userID, match);
            }

            Database.updateDB_Match(match, userID, ConnectionPool.getInstance().getDataSource());

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(match));
        responseBody.flush();
        responseBody.close();
    }


    public static void handleMatchHistory(HttpExchange exchange) throws IOException, MethodNotAllowed, MatchError {
        int userID;
        List<Match> matchHistory;

        String token = exchange.getRequestHeaders().getFirst("token");
        AuthenticationToken.getInstance().handleAuthentication(exchange, token);

        if (exchange.getRequestMethod().equals("GET")) {
            int matchHistorySize;
            try {
                matchHistorySize = getIDFromPath(exchange.getRequestURI().getPath());
            } catch (NumberFormatException e) {
                throw new MatchError("No number input found. Please input the number of matches for the match history. (/match/matchHistory/20)");
            }

            userID = AuthenticationToken.getInstance().getUserID(token);
            matchHistory = DBMatch.getLastNMatchesFromUser(userID, matchHistorySize, ConnectionPool.getInstance().getDataSource());

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() + " not allowed for "+ exchange.getRequestURI());
        }

        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(matchHistory));
        responseBody.flush();
        responseBody.close();
    }

    public static void handleCreateMatch(HttpExchange exchange) throws IOException, MethodNotAllowed {
        int userID;
        Match match;

        String token = exchange.getRequestHeaders().getFirst("token");
        AuthenticationToken.getInstance().handleAuthentication(exchange, token);



        if (exchange.getRequestMethod().equals("GET")) {
            userID = AuthenticationToken.getInstance().getUserID(token);
            match = new Match();

            match.setStatus(MatchStatus.RUNNING);
            match.setDifficulty(DifficultyState.EASY);
            match.setStartTime(new Timestamp(System.currentTimeMillis()));

            match.setPlayerTurn(DBScore.getScore(userID, ConnectionPool.getInstance().getDataSource()));

            if (!match.isIsPlayerTurn()) {
                Game.play(userID, match);
            }

            int matchID = DBMatch.insertNewMatch(match, userID, ConnectionPool.getInstance().getDataSource());
            match.setMatchID(matchID);


        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(match));
        responseBody.flush();
        responseBody.close();
    }

























    private static int getIDFromPath(String path) {
        String[] parts = path.split("/");
        String id = parts[parts.length - 1];
        return Integer.parseInt(id);
    }












}
