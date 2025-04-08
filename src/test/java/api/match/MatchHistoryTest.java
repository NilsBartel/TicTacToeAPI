package api.match;

import api.ApiUtil;
import api.databaseData.DatabaseUtil;
import api.databaseData.MatchData;
import api.databaseData.UsersData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.Server;
import tictactoe.api.errors.ErrorCodes;
import tictactoe.api.errors.ErrorResponse;
import tictactoe.database.ConnectionPool;
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

public class MatchHistoryTest {

    static final long YEAR_MILLIS = 31536000000L;
    static final long MONTH_MILLIS = 2628000000L;
    static final long WEEK_MILLIS = 604800000L;
    static final long DAY_MILLIS = 86400000L;
    static final long HOUR_MILLIS = 3600000L;
    static final long MINUTE_MILLIS = 60000L;

    static final long YEAR_SECONDS = 31536000L;
    static final long MONTH_SECONDS = 2628000L;
    static final long WEEK_SECONDS = 604800L;
    static final long DAY_SECONDS = 86400L;
    static final long HOUR_SECONDS = 3600L;
    static final long MINUTE_SECONDS = 60L;

    static ObjectMapper objectMapper;
    static HikariDataSource dataSource;
    static HttpServer server;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres");

    @BeforeAll
    static void init() throws IOException, InterruptedException, SQLException {
        LoggerConfig.disableLoggers();
        objectMapper = new ObjectMapper();

        postgres.start();


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        dataSource = pool.getDataSource();


        while (true) {
            try {
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
                break;
            } catch (SQLException e){
                Thread.sleep(500);
            }
        }

        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration(dataSource);

        DatabaseUtil.populateDatabase(postgres, dataSource);


        editStartTimes(dataSource);


        server = Server.start(dataSource);
    }

    @AfterAll
    static void close() throws InterruptedException {
        dataSource.close();
        Server.close(server);
    }

    public static void editStartTimes(HikariDataSource dataSource) {
        int userID = 5;
        int matchID = 5; //first match till id = 14


        long currentTimeMillis = System.currentTimeMillis();

        updateTime(5, new Timestamp(currentTimeMillis-(2*YEAR_MILLIS)));
        updateTime(6, new Timestamp(currentTimeMillis-YEAR_MILLIS));
        updateTime(7, new Timestamp(currentTimeMillis-(3*MONTH_MILLIS)));
        updateTime(8, new Timestamp(currentTimeMillis-MONTH_MILLIS));
        updateTime(9, new Timestamp(currentTimeMillis-WEEK_MILLIS));
        updateTime(10, new Timestamp(currentTimeMillis-(2*DAY_MILLIS)));
        updateTime(11, new Timestamp(currentTimeMillis-DAY_MILLIS));
        updateTime(12, new Timestamp(currentTimeMillis-(2*HOUR_MILLIS)));
        updateTime(13, new Timestamp(currentTimeMillis-HOUR_MILLIS));
        updateTime(14, new Timestamp(currentTimeMillis-MINUTE_MILLIS));

    }

    private static void updateTime(int matchID, Timestamp startTime) {
        try (Connection connection = dataSource.getConnection()) {
            String sql1 = "UPDATE time SET starttime = ? WHERE match_id = ?";

            try (PreparedStatement prepStmt = connection.prepareStatement(sql1)) {
                prepStmt.setTimestamp(1, startTime);
                prepStmt.setInt(2, matchID);
                prepStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }










    @Test
    void PostRequestFalse() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/matchHistory/1");
        request.setHeader("token", ApiUtil.getToken("test", "test"));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void noToken() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory/1");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.NO_TOKEN.getCode(), errorResponse.getCode());
    }

    @Test
    void wrongToken() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory/1");
        request.setHeader("token", "wrongToken");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(401, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.TOKEN_INVALID.getCode(), errorResponse.getCode());
    }

    @Test
    void noMatchHistory() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser4().getUserName(), UsersData.getUser4().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);


        Assertions.assertEquals(404, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.MATCH_HISTORY_EMPTY.getMessage(), errorResponse.getMessage());
    }


    @Test
    void getMatchHistoryDefault() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(10, myObjects.size());
    }



    @Test
    void getMatchHistoryPLayerWon() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=PLAYER_WON");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(2, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(MatchStatus.PLAYER_WON, match.getStatus());
        }
    }

    @Test
    void getMatchHistoryComputerWon() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=COMPUTER_WON");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(5, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(MatchStatus.COMPUTER_WON, match.getStatus());
        }
    }

    @Test
    void getMatchHistoryDraw() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=DRAW");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(3, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(MatchStatus.DRAW, match.getStatus());
        }
    }


    @Test
    void getMatchHistoryEasy() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?difficulty=EASY");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(3, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.EASY, match.getDifficulty());
        }
    }

    @Test
    void getMatchHistoryMedium() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?difficulty=MEDIUM");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(4, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.MEDIUM, match.getDifficulty());
        }
    }

    @Test
    void getMatchHistoryImpossible() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?difficulty=IMPOSSIBLE");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(3, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.IMPOSSIBLE, match.getDifficulty());
        }
    }

    @Test
    void getMatchHistoryAmount3() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?amount=3");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(3, myObjects.size());
    }

    @Test
    void getMatchHistoryAmount1() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?amount=1");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(1, myObjects.size());
    }

    @Test
    void getMatchHistoryPlayerWonAndEasy() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=PLAYER_WON&difficulty=EASY");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(1, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.EASY, match.getDifficulty());
            Assertions.assertEquals(MatchStatus.PLAYER_WON, match.getStatus());
        }
    }


    @Test
    void getMatchHistoryComputerWonAndImpossible() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=COMPUTER_WON&difficulty=IMPOSSIBLE");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(2, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.IMPOSSIBLE, match.getDifficulty());
            Assertions.assertEquals(MatchStatus.COMPUTER_WON, match.getStatus());
        }
    }

    @Test
    void getMatchHistoryComputerWonAndImpossibleAndAmount1() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=COMPUTER_WON&difficulty=IMPOSSIBLE&amount=1");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(1, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.IMPOSSIBLE, match.getDifficulty());
            Assertions.assertEquals(MatchStatus.COMPUTER_WON, match.getStatus());
        }
    }

    @Test
    void getMatchHistoryDrawAndImpossible() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=DRAW&difficulty=IMPOSSIBLE");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(1, myObjects.size());
        for (Match match : myObjects) {
            Assertions.assertEquals(DifficultyState.IMPOSSIBLE, match.getDifficulty());
            Assertions.assertEquals(MatchStatus.DRAW, match.getStatus());
        }
    }


    @Test
    void getMatchHistoryRandomInput() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?hours=10");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(10, myObjects.size());
    }

    @Test
    void getMatchHistoryRandomInput2() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?hours=10&asdasd");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(10, myObjects.size());
    }

    @Test
    void secondsInputLetters() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=hdhd");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_TIME_NOT_LONG.getCode(), errorResponse.getCode());
    }

    @Test
    void secondsInputDecimal() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=8.7");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_TIME_NOT_LONG.getCode(), errorResponse.getCode());
    }

    @Test
    void secondsInputNegative() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=-1");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_TIME_NEGATIVE.getCode(), errorResponse.getCode());
    }

    @Test
    void secondsInputZero() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=0");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_TIME_NEGATIVE.getCode(), errorResponse.getCode());
    }


    @Test
    void secondsInputToLong() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=1000000000001");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_TIME_TO_LARGE.getCode(), errorResponse.getCode());
    }


    @Test
    void amountInputLetter() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?amount=ajksdh");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_AMOUNT_NOT_INT.getCode(), errorResponse.getCode());
    }

    @Test
    void amountInputDecimal() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?amount=9.9");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_AMOUNT_NOT_INT.getCode(), errorResponse.getCode());
    }

    @Test
    void amountInputNegative() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?amount=-9");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_AMOUNT_NEGATIVE.getCode(), errorResponse.getCode());
    }

    @Test
    void amountInputToLarge() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?amount=2000");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_AMOUNT_TO_LARGE.getCode(), errorResponse.getCode());
    }


    @Test
    void statusInputWrong() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=wrong");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_STATUS_INVALID.getCode(), errorResponse.getCode());
    }

    @Test
    void statusInputNumber() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?status=23");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_STATUS_INVALID.getCode(), errorResponse.getCode());
    }

    @Test
    void difficultyInputWrong() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?difficulty=wrong");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_DIFFICULTY_INVALID.getCode(), errorResponse.getCode());
    }

    @Test
    void difficultyInputNumber() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?difficulty=25");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);

        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.QUERY_DIFFICULTY_INVALID.getCode(), errorResponse.getCode());
    }








    @Test
    void timeTest3min() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (3 * MINUTE_SECONDS));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(1, myObjects.size());
    }

    @Test
    void timeTest1hour() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (HOUR_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(2, myObjects.size());
    }

    @Test
    void timeTest3hour() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (3 * HOUR_SECONDS));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(3, myObjects.size());
    }


    @Test
    void timeTest1day() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (DAY_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(4, myObjects.size());
    }

    @Test
    void timeTest3days() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (3 * DAY_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(5, myObjects.size());
    }

    @Test
    void timeTest1week() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (WEEK_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(6, myObjects.size());
    }

    @Test
    void timeTest1month() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (MONTH_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(7, myObjects.size());
    }

    @Test
    void timeTest4month() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (4 * MONTH_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(8, myObjects.size());
    }


    @Test
    void timeTest1year() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (YEAR_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(9, myObjects.size());
    }


    @Test
    void timeTest2years() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory?seconds=" + (2 * YEAR_SECONDS + (3*MINUTE_SECONDS)));
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser5().getUserName(), UsersData.getUser5().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(10, myObjects.size());
    }





















}

