package api.account;

import api.databaseData.DatabaseUtil;
import api.databaseData.UsersData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.Server;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBUser;
import tictactoe.database.LiquibaseMigrationService;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RegisterTest {

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

        DatabaseUtil.populateDatabase(postgres, dataSource);

        server = new Server();
        server.start(dataSource);
    }

    @AfterAll
    static void close() {
        dataSource.close();
        server.close();
    }






    @Test
    void getRequestFalseTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\""+ UsersData.getUser1().getPassword() +"\"}";

        HttpUriRequest request = new HttpGet("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void wrongRequestBodyTest() throws IOException {
        String login = "{\"WrongJson\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\""+ UsersData.getUser1().getPassword() +"\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void noRequestBodyTest() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void creationSuccessfulTest() throws IOException {
        String json = "{\"userName\":\"hallo\", \"password\":\"Test1234!\", \"answer1\":\"bruno\", \"answer2\":\"hamburg\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertTrue(DBUser.userExists("hallo", dataSource));
    }


    @Test
    void accountAlreadyExistsTest() throws IOException {
        String json = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\"Test1234!\", \"answer1\":\"bruno\", \"answer2\":\"hamburg\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void passwordTooWeakTest() throws IOException {
        String json = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\"Test1234\", \"answer1\":\"bruno\", \"answer2\":\"hamburg\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void UserNameEmptyTest() throws IOException {
        String json = "{\"userName\":\"\", \"password\":\"Test1234!\", \"answer1\":\"bruno\", \"answer2\":\"hamburg\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void Answer1EmptyTest() throws IOException {
        String json = "{\"userName\":\"Username\", \"password\":\"Test1234!\", \"answer1\":\"\", \"answer2\":\"hamburg\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void Answer2EmptyTest() throws IOException {
        String json = "{\"userName\":\"Username\", \"password\":\"Test1234!\", \"answer1\":\"Bruno\", \"answer2\":\"\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(json));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }






}
