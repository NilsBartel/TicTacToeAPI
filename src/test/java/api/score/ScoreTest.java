package api.score;

import api.ApiUtil;
import api.databaseData.DatabaseUtil;
import api.databaseData.ScoreData;
import api.databaseData.UsersData;
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
import tictactoe.database.ConnectionPool;
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.game.Score;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ScoreTest {

    static HttpServer server;
    static ObjectMapper objectMapper;
    static HikariDataSource dataSource;
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
        postgres.close();
    }


    @Test
    void postRequestFalseTest() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/score/");
        request.setHeader("token", ApiUtil.getToken("test", "test"));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void getScoreTest() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/score/");
        request.setHeader("token", ApiUtil.getToken(UsersData.getUser1().getUserName(), UsersData.getUser1().getPassword()));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(200, httpResponse.getCode());

        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        Score score = objectMapper.readValue(entitiystring, Score.class);
        Assertions.assertEquals(score, ScoreData.getScore1());
    }

    @Test
    void wrongTokenTest() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/score/");
        request.setHeader("token", "laskdasdj0397");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void noTokenTest() throws IOException {
        HttpUriRequest request = new HttpGet("http://localhost:8080/score/");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }
}
