package api.match;

import api.ApiUtil;
import api.databaseData.DatabaseUtil;
import api.databaseData.MatchData;
import api.databaseData.UsersData;
import com.fasterxml.jackson.core.JsonParser;
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class MatchHistoryTest {

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

        server = Server.start(dataSource);
    }

    @AfterAll
    static void close() throws InterruptedException {
        dataSource.close();
        Server.close(server);
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


        Assertions.assertEquals(422, httpResponse.getCode());
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
        Assertions.assertEquals(MatchData.getMatch14(), myObjects.getFirst());
        Assertions.assertEquals(MatchData.getMatch13(), myObjects.get(1));
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
        Assertions.assertEquals(MatchData.getMatch14(), myObjects.get(0));
        Assertions.assertEquals(MatchData.getMatch13(), myObjects.get(1));
        Assertions.assertEquals(MatchData.getMatch12(), myObjects.get(2));
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
        Assertions.assertEquals(MatchData.getMatch14(), myObjects.getFirst());
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
        Assertions.assertEquals(MatchData.getMatch5(), myObjects.getFirst());
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












}

