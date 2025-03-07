package api.match;

import logger.LoggerConfig;
import api.MatchCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.Server;
import tictactoe.api.account.LoginResponse;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBUser;
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.game.Match;
import tictactoe.login.HashService;
import tictactoe.user.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ApiMatchTest {

        static ObjectMapper objectMapper;
        static HikariDataSource dataSource;
        static Server server;
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

            User user1 = new User("test", HashService.hash("test"), HashService.hash("answer1"), HashService.hash("answer2"));
            DBUser.insertUser(user1, dataSource);
            User user2 = new User("user2", HashService.hash("test"), HashService.hash("answer1"), HashService.hash("answer2"));
            DBUser.insertUser(user2, dataSource);
            MatchCreate.createMatch(dataSource);

            server = new Server();
            server.start(dataSource);
        }

        @AfterAll
        static void close() {
            dataSource.close();
            server.close();
        }



        @Test
        void getMatchTest() throws IOException {
            String userName = "test";
            String password = "test";
            String token = getToken(userName, password);


            HttpUriRequest request = new HttpGet("http://localhost:8080/match/1");
            request.addHeader("token", token);

            CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


            String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            Match match = objectMapper.readValue(entitiystring, Match.class);
            match.printBoard();

            Assertions.assertEquals(200, httpResponse.getCode());
        }

        @Test
        void getMatchFromWrongUserTest() throws IOException {
            String userName = "user2";
            String password = "test";
            String token = getToken(userName, password);


            HttpUriRequest request = new HttpGet("http://localhost:8080/match/1");
            request.addHeader("token", token);

            CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

            // no match found
            Assertions.assertEquals(422, httpResponse.getCode());
        }




        boolean tryLogIn(String userName, String password) throws IOException {
            String login = "{\"userName\":\"" +userName+ "\", \"password\":\"" +password+ "\"}";

            HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
            request.setEntity(new StringEntity(login));

            CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

            return (httpResponse.getCode() == 200);
        }

        String getToken(String userName, String password) throws IOException {
            String login = "{\"userName\":\"" +userName+ "\", \"password\":\"" +password+ "\"}";

            HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
            request.setEntity(new StringEntity(login));

            CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


            String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            LoginResponse loginResponse = objectMapper.readValue(entitiystring, LoginResponse.class);
            return loginResponse.getToken();
        }
}
