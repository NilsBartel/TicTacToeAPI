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
import tictactoe.game.Match;

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
    void oneMatchMatchHistory() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory/1");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser3().getUserName(), UsersData.getUser3().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(MatchData.getMatch4(), myObjects.getFirst());
    }

    @Test
    void multipleMatchMatchHistory() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory/2");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser1().getUserName(), UsersData.getUser1().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        List<Match> myObjects = objectMapper.readValue(entitiystring, new TypeReference<List<Match>>(){});

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(MatchData.getMatch2(), myObjects.getFirst());
        Assertions.assertEquals(MatchData.getMatch1(), myObjects.get(1));
    }



    @Test
    void noMatchHistory() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/matchHistory/1");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser4().getUserName(), UsersData.getUser4().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(entitiystring, ErrorResponse.class);


        Assertions.assertEquals(422, httpResponse.getCode());
        Assertions.assertEquals(ErrorCodes.MATCH_HISTORY_EMPTY.getMessage(), errorResponse.getMessage());
    }






}

