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
import tictactoe.api.account.LoginResponse;
import tictactoe.database.ConnectionPool;
import tictactoe.database.LiquibaseMigrationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginTest {

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
    void getRequestFalse() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\""+ UsersData.getUser1().getPassword() +"\"}";
        HttpUriRequest request = new HttpGet("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }


    @Test
    void loginSuccessfulTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\""+ UsersData.getUser1().getPassword() +"\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        LoginResponse loginResponse = objectMapper.readValue(entitiystring, LoginResponse.class);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertNotNull(loginResponse.getToken());
    }

    @Test
    void loginFailedWrongPasswordTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"password\":\"WrongPassword\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void loginFailedWrongUserNameTest() throws IOException {
        String login = "{\"userName\":\"WrongName\", \"password\":\""+ UsersData.getUser1().getPassword() +"\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


        Assertions.assertEquals(401, httpResponse.getCode());
    }


    @Test
    void requestBodyWrongTest() throws IOException {
        String login = "{\"userName\":\""+ UsersData.getUser1().getUserName() +"\", \"Wrong\":\""+ UsersData.getUser1().getPassword() +"\"}";
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


        Assertions.assertEquals(400, httpResponse.getCode());
    }

    @Test
    void noRequestBodyTest() throws IOException {
        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(400, httpResponse.getCode());
    }





}
