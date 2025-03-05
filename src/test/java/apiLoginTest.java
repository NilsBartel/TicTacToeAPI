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
import tictactoe.login.HashService;
import tictactoe.user.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;


public class apiLoginTest {
    static ObjectMapper objectMapper;
    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");

    @BeforeAll
    static void init() throws SQLException, IOException {
        LoggerConfig.disableLoggers();
        objectMapper = new ObjectMapper();

        postgres.start();

        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        dataSource = pool.getDataSource();

        User user2 = new User("test", HashService.hash("test"), "answer3", "answer4");
        DBUser.insertUser(user2, dataSource);
        MatchCreate.createMatch(dataSource);

        Server.start(dataSource);
    }

//    @BeforeEach
//    void setUp() {
//        User user = new User("nils", "password", "answer1", "answer2");
//
//        User user2 = new User("test", HashService.hash("test"), "answer3", "answer4");
//        DBUser.insertUser(user, dataSource);
//        DBUser.insertUser(user2, dataSource);
//        MatchCreate.createMatch(dataSource);
//    }

    @AfterAll
    static void close() throws SQLException {
        dataSource.close();
    }

//    @AfterEach
//    void tearDown() {
//
//    }


    @Test
    void loginTest() throws IOException {
        //Server.start(dataSource);
        String login = "{\"userName\":\"test\", \"password\":\"test\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


        System.out.println(httpResponse.getCode());

        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        System.out.println(entitiystring);
        LoginResponse loginResponse = objectMapper.readValue(entitiystring, LoginResponse.class);
        System.out.println(loginResponse.getToken());

        Assertions.assertEquals(200, httpResponse.getCode());

    }


    @Test
    void getRequestFalse() throws IOException {
        //Server.start(dataSource);
        String login = "{\"userName\":\"test\", \"password\":\"test\"}";

        HttpUriRequest request = new HttpGet("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));


        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        //MethodNotAllowed error
        Assertions.assertEquals(405, httpResponse.getCode());
    }

    @Test
    void loginSuccessfulTest() throws IOException {
        //Server.start(dataSource);
        String login = "{\"userName\":\"test\", \"password\":\"test\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(200, httpResponse.getCode());
    }

    @Test
    void loginFailedTest() throws IOException {
        //Server.start(dataSource);
        String login = "{\"userName\":\"test\", \"password\":\"wrongPassword\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(401, httpResponse.getCode());
    }

    @Test
    void tokenCreatedTest() throws IOException {
        //Server.start(dataSource);
        String login = "{\"userName\":\"test\", \"password\":\"test\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);



        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        LoginResponse loginResponse = objectMapper.readValue(entitiystring, LoginResponse.class);
        System.out.println(loginResponse.getToken());

        Assertions.assertNotNull(loginResponse.getToken());
    }

    @Test
    void accountCreationSuccessfulTest() throws IOException {
        String userName = "newAccountTest";
        String password = "Test1234!";
        String newAccount = "{\"userName\":\"" +userName+ "\", \"password\":\"" +password+ "\", \"answer1\":\"bruno\", \"answer2\":\"hamburg\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/register");
        request.setEntity(new StringEntity(newAccount));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assertions.assertEquals(200, httpResponse.getCode());
        Assertions.assertTrue(tryLogIn(userName, password));
        System.out.println(getToken(userName, password));

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
