package tictactoe.api.match;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.*;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.database.DBScore;
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

        server.createContext("/match/create", exchange ->
            Try.run(() -> handleCreateMatch(exchange, dataSource))
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
            Try.run(() -> handleMatchHistory(exchange, dataSource))
                .onFailure(t -> {
                    errorHandler.handle(t, exchange);
                })
        );


        server.createContext("/test/matchHistory", exchange ->
            Try.run(() -> testMatchHistory(exchange, dataSource))
                .onFailure(t -> {
                    errorHandler.handle(t, exchange);
                })
        );

    }


    public static void testMatchHistory(HttpExchange exchange, HikariDataSource dataSource) throws RequestIncompleteError, AuthenticationError, MatchError, IOException, MethodNotAllowed {
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

            //TODO: check which parameters
            // if none -> default 10 matches no filters

            //TODO: ? after "normal" url and & as separator between parameters
            // -> check for ? then separate &
            // have a class for the parameters?

            String query = exchange.getRequestURI().getQuery();
            System.out.println(query);





//            try {
//                matchHistorySize = getIDFromPath(exchange.getRequestURI().getPath());
//            } catch (NumberFormatException e) {
//                throw new MatchError(
//                        "No number input found. Please input the number of matches for the match history. " +
//                                "(/match/matchHistory/20)");
//            }







            userID = AuthenticationToken.getInstance().getUserID(token);
            matchHistory = handleQuery(query, userID);
            //matchHistory = DBMatch.getLastNMatchesFromUser(userID, matchHistorySize, dataSource);
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



    public static List<Match> handleQuery(String query, int userID) {

        Map<String, String> queryMap = splitParams(query);
        Map<String, String> mapWithSeconds = replaceTimeWithSeconds(queryMap);
        System.out.println(mapWithSeconds);



        //TODO: get userID
        return DBMatch.getMatchHistoryWithParams(userID, mapWithSeconds, ConnectionPool.getInstance().getDataSource());
    }

    public static Map<String, String> splitParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) {
            return map;
        }
        if (query.isBlank()) {
            return map;
        }
        //TODO: try catch if some weired things get entered
        List<String> list = Arrays.stream(query.split("&")).toList();
        for (String s : list) {
            String[] splitString = s.split("=");
            map.put(splitString[0], splitString[1]);
        }
        System.out.println("query: " + map);
        return map;
    }

    public static Map<String, String> replaceTimeWithSeconds(Map<String, String> map) {
        long seconds = 0;

        //TODO: if-else or just multiple if's? Now only smallest one is taken
        if (map.containsKey("minutes")) {
            seconds += TimeUnit.MINUTES.toSeconds(Long.parseLong(map.get("minutes")));
            map.remove("minutes");
        } else if (map.containsKey("hours")) {
            seconds += TimeUnit.HOURS.toSeconds(Long.parseLong(map.get("hours")));
            map.remove("hours");
        } else if (map.containsKey("days")) {
            seconds += TimeUnit.DAYS.toSeconds(Long.parseLong(map.get("days")));
            map.remove("days");
        } else if (map.containsKey("weeks")) {
            seconds += TimeUnit.DAYS.toSeconds(Integer.parseInt(map.get("weeks"))* 7L);
            map.remove("weeks");
        } else if (map.containsKey("months")) {
            seconds += TimeUnit.DAYS.toSeconds(Integer.parseInt(map.get("months"))*30L);
            map.remove("months");
        } else if (map.containsKey("years")) {
            seconds += TimeUnit.DAYS.toSeconds(Integer.parseInt(map.get("years"))*365L);
            map.remove("years");
        }
        map.put("seconds", String.valueOf(seconds));
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

    public static void handleMatchHistory(HttpExchange exchange, HikariDataSource dataSource) throws IOException, MethodNotAllowed, MatchError, AuthenticationError, RequestIncompleteError {
        int userID;
        List<Match> matchHistory;
        String token;
        OutputStream responseBody = exchange.getResponseBody();


        if (exchange.getRequestMethod().equals("GET")) {
            int matchHistorySize;

            try {
                token = exchange.getRequestHeaders().get("token").getFirst();
            } catch (Exception e) {
                throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
            }
            AuthenticationToken.getInstance().authenticate(token);

            try {
                matchHistorySize = getIDFromPath(exchange.getRequestURI().getPath());
            } catch (NumberFormatException e) {
                throw new MatchError(
                    "No number input found. Please input the number of matches for the match history. " +
                        "(/match/matchHistory/20)");
            }

            userID = AuthenticationToken.getInstance().getUserID(token);
            matchHistory =
                DBMatch.getLastNMatchesFromUser(userID, matchHistorySize, dataSource);
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

    public static void handleCreateMatch(HttpExchange exchange, HikariDataSource dataSource) throws IOException, MethodNotAllowed, AuthenticationError, RequestIncompleteError {
        int userID;
        Match match;
        DifficultyState difficulty;
        String token;

        try {
            token = exchange.getRequestHeaders().get("token").getFirst();
        } catch (Exception e) {
            throw new RequestIncompleteError(ErrorCodes.NO_TOKEN);
        }
        AuthenticationToken.getInstance().authenticate(token);

        if (exchange.getRequestMethod().equals("GET")) {
            userID = AuthenticationToken.getInstance().getUserID(token);

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                match = objectMapper.readValue(requestBody, Match.class);
            } catch (JsonProcessingException e) {
                throw new RequestIncompleteError(ErrorCodes.INVALID_REQUEST_BODY);
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
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
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
