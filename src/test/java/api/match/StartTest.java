package api.match;

import api.ApiUtil;
import api.databaseData.DatabaseUtil;
import api.databaseData.MatchData;
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
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.game.Match;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class StartTest {

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
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/start");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser1().getUserName(), UsersData.getUser1().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }


    @Test
    void returnsRunningMatch() throws IOException {
        String json = "{\"difficulty\":\"EASY\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/start");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser3().getUserName(), UsersData.getUser3().getPassword()));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        Match match = objectMapper.readValue(entitiystring, Match.class);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertEquals(match, MatchData.getMatch4());
    }

    @Test
    void returnsEmptyMatch() throws IOException {
        String json = "{\"difficulty\":\"EASY\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/start");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser2().getUserName(), UsersData.getUser2().getPassword()));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        Match match = objectMapper.readValue(entitiystring, Match.class);


        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertTrue(match.getBoard().isEmpty());
    }

    @Test
    void newEmptyMatchGetsAddedToDB() throws IOException {
        String json = "{\"difficulty\":\"EASY\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/match/start");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser2().getUserName(), UsersData.getUser2().getPassword()));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        List<Match> matches = DBMatch.getLastNMatchesFromUser(2, 1, dataSource);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertTrue(matches.getFirst().getBoard().isEmpty());
    }



    @Test
    void noRequestBody() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/start");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser3().getUserName(), UsersData.getUser3().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void wrongRequestBody() throws IOException {
        String json = "{\"difficulty\":\"easy\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/start");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser3().getUserName(), UsersData.getUser3().getPassword()));
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void noTokenProvidedTest() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/start");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void wrongTokenProvidedTest() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/match/start");
        request.setHeader("token", "wrongToken");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }


}
