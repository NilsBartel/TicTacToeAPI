package tictactoe.api.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.*;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class MatchController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpServer server;
    private final HikariDataSource dataSource;
    
    public MatchController(HttpServer server, HikariDataSource datasource) {
        this.server = server;
        this.dataSource = datasource;
    }



    public void endPoint() {

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






    public void handleMatch(HttpExchange exchange) throws IOException, MethodNotAllowed, MatchError, LoginError {
        String token;
        Match match;

        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new NoTokenError("No token provided");
        }
        AuthenticationToken.getInstance().authenticate(token);

        if (exchange.getRequestMethod().equals("GET")) {
            int userID = AuthenticationToken.getInstance().getUserID(token);

            int matchId;
            try {
                matchId = getIDFromPath(exchange.getRequestURI().getPath());
            } catch (NumberFormatException e) {
                throw new MatchError("No number input found. Please input the matchID. (/match/20)");
            }

            match = DBMatch.getMatch(userID, matchId, dataSource);
            if (match == null) {
                throw new MatchError("No match found.");
            }

            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(match));
        responseBody.flush();
        responseBody.close();
    }



    public void handlePlay(HttpExchange exchange) throws IOException, MatchError, MethodNotAllowed, LoginError {
        int userID;
        Match match;
        String token;

        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new NoTokenError("No token provided");
        }
        AuthenticationToken.getInstance().authenticate(token);

        if (exchange.getRequestMethod().equals("POST")) {
            userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            try {
                match = objectMapper.readValue(requestBody, Match.class);
            } catch (JsonProcessingException e) {
                throw new InputError("No request body provided");
            }
            match.printBoard();

            if (ApiMatchUtil.validateMatch(userID, match, dataSource)) {
                Database.updateDB_Match(match, userID, dataSource);
                Game.play(userID, match);
            }

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }


        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(DBMatch.getMatch(userID, match.getMatchID(), dataSource)));
        responseBody.flush();
        responseBody.close();
    }

    public void handleStart(HttpExchange exchange) throws IOException, MethodNotAllowed, MatchError, SQLException, LoginError {
        int userID;
        Match match;
        String token;

        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new NoTokenError("No token provided");
        }
        AuthenticationToken.getInstance().authenticate(token);

        if (exchange.getRequestMethod().equals("GET")) {
            DifficultyState difficulty;
            userID = AuthenticationToken.getInstance().getUserID(token);
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                match = objectMapper.readValue(requestBody, Match.class);
            } catch (JsonProcessingException e) {
                    throw new InputError("Invalid request body provided. Need a difficulty. {\"difficulty\": \"EASY\"}");
            }

            difficulty = match.getDifficulty();
            match = ApiMatchUtil.returnRunningOrNewMatch(difficulty, userID, dataSource);

            if (!match.isIsPlayerTurn()) {
                Game.play(userID, match);
            }

            Database.updateDB_Match(match, userID, dataSource);

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(match));
        responseBody.flush();
        responseBody.close();
    }


    public void handleMatchHistory(HttpExchange exchange) throws IOException, MethodNotAllowed, MatchError, LoginError {
        int userID;
        List<Match> matchHistory;
        String token;

        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new NoTokenError("No token provided");
        }
        AuthenticationToken.getInstance().authenticate(token);
        if (exchange.getRequestMethod().equals("GET")) {
            int matchHistorySize;
            try {
                matchHistorySize = getIDFromPath(exchange.getRequestURI().getPath());
            } catch (NumberFormatException e) {
                throw new MatchError("No number input found. Please input the number of matches for the match history. (/match/matchHistory/20)");
            }

            userID = AuthenticationToken.getInstance().getUserID(token);
            matchHistory = DBMatch.getLastNMatchesFromUser(userID, matchHistorySize, dataSource);

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() + " not allowed for "+ exchange.getRequestURI());
        }

        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(matchHistory));
        responseBody.flush();
        responseBody.close();
    }

    public void handleCreateMatch(HttpExchange exchange) throws IOException, MethodNotAllowed, LoginError {
        int userID;
        Match match;
        DifficultyState difficulty;
        String token;

        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new NoTokenError("No token provided");
        }
        AuthenticationToken.getInstance().authenticate(token);

        if (exchange.getRequestMethod().equals("GET")) {
            userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                match = objectMapper.readValue(requestBody, Match.class);
            } catch (JsonProcessingException e) {
                throw new InputError("Wrong or no request body provided. Need a difficulty. {\"difficulty\": \"EASY\"}");
            }
            difficulty = match.getDifficulty();

            match = new Match();
            match.setStatus(MatchStatus.RUNNING);
            match.setDifficulty(difficulty);
            match.setStartTime(new Timestamp(System.currentTimeMillis()));
            match.setPlayerTurn(DBScore.getScore(userID, dataSource));
            if (!match.isIsPlayerTurn()) {
                Game.play(userID, match);
            }

            int matchID = DBMatch.insertNewMatch(match, userID, dataSource);
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
