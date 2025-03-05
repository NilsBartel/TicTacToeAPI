import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ProtocolException;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
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
import tictactoe.login.PasswordUtil;
import tictactoe.user.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TEST {

    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");

    @BeforeAll
    static void init() throws SQLException {
        LoggerConfig.disableLoggers();

        postgres.start();

        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        dataSource = pool.getDataSource();
    }

    @AfterAll
    static void close() throws SQLException {
        dataSource.close();
    }

    @BeforeEach
    void setUp() {
        User user = new User("nils", "password", "answer1", "answer2");

        User user2 = new User("test", HashService.hash("test"), "answer3", "answer4");
        DBUser.insertUser(user, dataSource);
        DBUser.insertUser(user2, dataSource);
        MatchCreate.createMatch(dataSource);
    }


    @Test
    void loginTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Server.start(dataSource);
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
    void test() throws SQLException, IOException {
        HttpUriRequest request = new HttpGet( "http://localhost:8080/match/1" );
        request.addHeader("token", "safouhauhauh398rz");
        HttpUriRequest request2 = new HttpPost("http://localhost:8080/match/1");

        Server.start(dataSource);

        //HttpResponse response = HttpClientBuilder.create().build().execute( request );
        //HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        //CloseableHttpResponse httpResponse = httpClient.execute(request);


//        URL url = new URL("http://localhost:8080/match/1");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        System.out.println("test");

        //assertThat(httpResponse.getCode() == 200);

//        System.out.println(con.getResponseCode());
//        System.out.println("test");
//        System.out.println("test");
        System.out.println(httpResponse.getCode());
        Assertions.assertEquals(401, httpResponse.getCode());
    }



    @Test
    void test2() throws SQLException, IOException {
        HttpUriRequest request = new HttpGet( "http://localhost:8080/match/1" );
        Server.start(dataSource);

        //HttpResponse response = HttpClientBuilder.create().build().execute( request );
        //HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

        //CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        URL url = new URL("http://localhost:8080/match/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        System.out.println("test");

        //assertThat(httpResponse.getCode() == 200);

        System.out.println(con.getResponseCode());
        System.out.println("test");
//        System.out.println("test");
//        System.out.println(httpResponse.getCode());
        Assertions.assertEquals(401, con.getResponseCode());
    }
















}
