package tictactoe.api.match;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.*;
import tictactoe.database.DBMatch;
import tictactoe.database.Database;
import tictactoe.game.DifficultyState;
import tictactoe.game.Game;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

public class MatchController {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int DEFAULT_MATCH_HISTORY_SIZE = 10;

    public static void endPoint(HttpServer server, HikariDataSource dataSource) {

        ErrorHandler errorHandler = new ErrorHandler();
        server.createContext("/match/", exchange ->
            Try.run(() -> handleMatch(exchange, dataSource))
                .onFailure(t -> {
                    errorHandler.handle(t, exchange);
                })
        );

        server.createContext("/match/play", exchange ->
            Try.run(() -> handlePlay(exchange, dataSource))
                .onFailure(t -> {
                    errorHandler.handle(t, exchange);
                })
        );

        server.createContext("/match/start", exchange ->
            Try.run(() -> handleStart(exchange, dataSource))
                .onFailure(t -> {
                    errorHandler.handle(t, exchange);
                })
        );

        server.createContext("/match/matchHistory", exchange ->
            Try.run(() -> matchHistory(exchange, dataSource))
                .onFailure(t -> {
                    errorHandler.handle(t, exchange);
                })
        );

    }


    public static void matchHistory(HttpExchange exchange, HikariDataSource dataSource) throws RequestIncompleteError, AuthenticationError, MatchError, IOException, MethodNotAllowed {
        int userID;
        List<Match> matchHistory;
        String token;
        OutputStream responseBody = exchange.getResponseBody();


        if (exchange.getRequestMethod().equals("GET")) {
            int matchHistorySize = DEFAULT_MATCH_HISTORY_SIZE;

            try {
                token = exchange.getRequestHeaders().get("token").getFirst();
            } catch (Exception e) {
                throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
            }
            AuthenticationToken.getInstance().authenticate(token);

            String query = exchange.getRequestURI().getQuery();


            userID = AuthenticationToken.getInstance().getUserID(token);
            matchHistory = getMatchHistory(query, userID, dataSource);

            if (matchHistory.isEmpty()) {
                throw new MatchError(ErrorCodes.MATCH_HISTORY_EMPTY);
            }

            exchange.sendResponseHeaders(200, 0);
            responseBody.write(objectMapper.writeValueAsBytes(matchHistory));

        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","origin, X-PINGOTHER, access-control-allow-origin, content-type, accept, authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST , OPTIONS");
            exchange.sendResponseHeaders(200, 0);

        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }

        responseBody.flush();
        responseBody.close();
    }



    private static List<Match> getMatchHistory(String query, int userID, HikariDataSource dataSource) {

        Map<String, String> queryMap = splitParams(query);
        validateParameters(queryMap);


        return DBMatch.getMatchHistoryWithParams(userID, queryMap, dataSource);
    }

    private static void validateParameters(Map<String, String> queryMap) throws QueryError {

        System.out.println(queryMap);



        if (queryMap.containsKey("amount")) {
                try {
                if (Integer.parseInt(queryMap.get("amount")) <= 0) {
                    throw new QueryError(ErrorCodes.QUERY_AMOUNT_NEGATIVE);
                }
                if (Integer.parseInt(queryMap.get("amount")) > 1000) {
                    throw new QueryError(ErrorCodes.QUERY_AMOUNT_TO_LARGE);
                }
            } catch (NumberFormatException e) {
                throw new QueryError(ErrorCodes.QUERY_AMOUNT_NOT_INT);
            }
        }

        if (queryMap.containsKey("seconds")) {
            try {
                if (Long.parseLong(queryMap.get("seconds")) <= 0) {
                    throw new QueryError(ErrorCodes.QUERY_TIME_NEGATIVE);
                }
                if (Long.parseLong(queryMap.get("seconds")) > 209999999999L) {
                    throw new QueryError(ErrorCodes.QUERY_TIME_TO_LARGE);
                }
            } catch (NumberFormatException e) {
                throw new QueryError(ErrorCodes.QUERY_TIME_NOT_LONG);
            }
        }



        if (queryMap.containsKey("status")) {
            String status = queryMap.get("status");
            if (!status.equals(MatchStatus.PLAYER_WON.toString()) &&
                    !status.equals(MatchStatus.COMPUTER_WON.toString()) &&
                    !status.equals(MatchStatus.DRAW.toString())
            ) {
                throw new QueryError(ErrorCodes.QUERY_STATUS_INVALID);
            }
        }

        if (queryMap.containsKey("difficulty")) {
            String difficulty = queryMap.get("difficulty");
            if (!difficulty.equals(DifficultyState.EASY.toString()) &&
                    !difficulty.equals(DifficultyState.MEDIUM.toString()) &&
                    !difficulty.equals(DifficultyState.IMPOSSIBLE.toString())
            ) {
                throw new QueryError(ErrorCodes.QUERY_DIFFICULTY_INVALID);
            }
        }











    }

    private static Map<String, String> splitParams(String query) {
        Map<String, String> map = new HashMap<>();
        try {
            List<String> list = Arrays.stream(query.split("&")).toList();
            for (String s : list) {
                String[] splitString = s.split("=");
                map.put(splitString[0], splitString[1]);
            }
        } catch (Exception e) {
            return map;
        }

        return map;
    }












    public static void handleMatch(HttpExchange exchange, HikariDataSource dataSource) throws IOException, MethodNotAllowed, MatchError, AuthenticationError, RequestIncompleteError {
        String token;
        Match match;
        OutputStream responseBody = exchange.getResponseBody();


        if (exchange.getRequestMethod().equals("GET")) {
            int matchId;

            try {
                token = exchange.getRequestHeaders().get("token").getFirst();
            } catch (Exception e) {
                throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
            }
            AuthenticationToken.getInstance().authenticate(token);
            int userID = AuthenticationToken.getInstance().getUserID(token);

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
            responseBody.write(objectMapper.writeValueAsBytes(match));
        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","origin, X-PINGOTHER, access-control-allow-origin, content-type, accept, authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET , OPTIONS");
            exchange.sendResponseHeaders(200, 0);

        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }


        responseBody.flush();
        responseBody.close();
    }

    public static void handlePlay(HttpExchange exchange, HikariDataSource dataSource) throws IOException, MatchError, MethodNotAllowed, AuthenticationError, RequestIncompleteError {
        int userID;
        Match match;
        String token;
        OutputStream responseBody = exchange.getResponseBody();



        if (exchange.getRequestMethod().equals("POST")) {
            try {
                token = exchange.getRequestHeaders().get("token").getFirst();
            } catch (Exception e) {
                throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
            }
            AuthenticationToken.getInstance().authenticate(token);
            userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            System.out.println("requestBody " + requestBody);
            try {
                match = objectMapper.readValue(requestBody, Match.class);
            } catch (JsonProcessingException e) {
                throw new RequestIncompleteError(ErrorCodes.INVALID_REQUEST_BODY);
            }
            match.printBoard();

            if (ApiMatchUtil.validateMatch(userID, match, dataSource)) {
                Database.updateDB_Match(match, userID, dataSource);
                Game.play(userID, match);
            }

            exchange.sendResponseHeaders(200, 0);
            responseBody.write(objectMapper.writeValueAsBytes(DBMatch.getMatch(
                    userID,
                    match.getMatchID(),
                    dataSource
            )));


        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","origin, X-PINGOTHER, access-control-allow-origin, content-type, accept, authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST , OPTIONS");
            exchange.sendResponseHeaders(200, 0);

        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }


        responseBody.flush();
        responseBody.close();
    }

    public static void handleStart(HttpExchange exchange, HikariDataSource dataSource) throws IOException, MethodNotAllowed, MatchError, AuthenticationError, RequestIncompleteError {
        int userID;
        Match match;
        String token;
        OutputStream responseBody = exchange.getResponseBody();



        if (exchange.getRequestMethod().equals("POST")) {
            DifficultyState difficulty;

            try {
                token = exchange.getRequestHeaders().get("token").getFirst();
            } catch (Exception e) {
                throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
            }
            AuthenticationToken.getInstance().authenticate(token);

            userID = AuthenticationToken.getInstance().getUserID(token);
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                match = objectMapper.readValue(requestBody, Match.class);
            } catch (JsonProcessingException e) {
                throw new RequestIncompleteError(ErrorCodes.INVALID_REQUEST_BODY);
            }

            difficulty = match.getDifficulty();
            match = ApiMatchUtil.returnRunningOrNewMatch(difficulty, userID, dataSource);

            if (!match.isIsPlayerTurn()) {
                Game.play(userID, match);
            }

            Database.updateDB_Match(match, userID, dataSource);

            exchange.sendResponseHeaders(200, 0);
            responseBody.write(objectMapper.writeValueAsBytes(match));

        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","origin, X-PINGOTHER, access-control-allow-origin, content-type, accept, authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST , OPTIONS");
            exchange.sendResponseHeaders(200, 0);

        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }


        responseBody.flush();
        responseBody.close();
    }

    private static int getIDFromPath(String path) {
        String[] parts = path.split("/");
        String id = parts[parts.length - 1];
        return Integer.parseInt(id);
    }

}
