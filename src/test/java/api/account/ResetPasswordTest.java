package api.account;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.Server;
import tictactoe.database.ConnectionPool;
import tictactoe.database.LiquibaseMigrationService;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ResetPasswordTest {

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
    void getRequestFalseTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\""+ UsersData.getUser1().getPassword() +"\"}";

        HttpUriRequest request = new HttpGet("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void resetSuccessfulTest() throws IOException {
        String newPassword = "tEst123456!";
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                         "\"password\":\""+ newPassword +"\", " +
                         "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                         "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertTrue(ApiUtil.tryLogIn(UsersData.getUser2().getUserName(), newPassword));
    }

    @Test
    void userNameDoesNotExistTest() throws IOException {
        String newPassword = "tEst123456!";
        String login = "{\"userName\":\" randomName\", " +
                         "\"password\":\""+ newPassword +"\", " +
                         "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                         "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void passwordTooWeakTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                         "\"password\":\"weakPassword\", " +
                         "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                         "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(403, httpResponse.getCode());
    }

    @Test
    void passwordTooShortTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                         "\"password\":\"\", " +
                         "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                         "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(403, httpResponse.getCode());
    }

    @Test
    void requestBodyAnswerMissingTest() throws IOException {
        String newPassword = "tEst123456!";
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                         "\"password\":\""+ newPassword +"\", " +
                         "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(400, httpResponse.getCode());
    }

    @Test
    void requestBodyPasswordTest() throws IOException {

        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +

                        "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                        "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(403, httpResponse.getCode());
    }

    @Test
    void emptyPasswordTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                "\"password\":\"\", " +
                "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(403, httpResponse.getCode());
    }

    @Test
    void noRequestBodyTest() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(400, httpResponse.getCode());
    }

    @Test
    void Answer1WrongTest() throws IOException {
        String newPassword = "tEst123456!";
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                "\"password\":\""+ newPassword +"\", " +
                "\"answer1\":\"WrongAnswer\", " +
                "\"answer2\":\""+ UsersData.getUser2().getAnswer2() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void Answer2WrongTest() throws IOException {
        String newPassword = "tEst123456!";
        String login = "{\"userName\":\""+ UsersData.getUser2().getUserName() +"\", " +
                "\"password\":\""+ newPassword +"\", " +
                "\"answer1\":\""+ UsersData.getUser2().getAnswer1() +"\", " +
                "\"answer2\":\"wrongAnswer\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/resetPassword");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }






    // answers wrong


}
