package api.match;

import api.ApiUtil;
import api.databaseData.DatabaseUtil;
import api.databaseData.UsersData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
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
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.database.DBUser;
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


public class CreateMatchTest {

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
    void postRequestFalse() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/create");
        request.setHeader("token", ApiUtil.getToken("test", "test"));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void createEasyMatch() throws IOException {
        String json = "{\"difficulty\":\"EASY\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/create");
        request.setHeader("token", ApiUtil.getToken("test", "test"));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


        Assertions.assertEquals(200, httpResponse.getCode());

        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        Match match = objectMapper.readValue(entitiystring, Match.class);
        Assertions.assertEquals(DifficultyState.EASY, match.getDifficulty());
        Assertions.assertTrue(match.getBoard().isEmpty());
    }

    @Test
    void wrongJsonInput() throws IOException {
        String json = "{\"difficulty\":\"wrong\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/create");
        request.setHeader("token", ApiUtil.getToken("test", "test"));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(400, httpResponse.getCode());
    }


    @Test
    void noJsonInput() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/create");
        request.setHeader("token", ApiUtil.getToken("test", "test"));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(400, httpResponse.getCode());
    }

    @Test
    void newMatchGetsAddedToDB() throws IOException {
        String json = "{\"difficulty\":\"EASY\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/create");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser2().getUserName(), UsersData.getUser2().getPassword()));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        List<Match> matches = DBMatch.getLastNMatchesFromUser(2, 1, dataSource);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertTrue(matches.getFirst().getBoard().isEmpty());
    }

    @Test
    void noTokenProvidedTest() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/create");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void wrongTokenProvidedTest() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/create");
        request.setHeader("token", "wrongToken");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

















}
