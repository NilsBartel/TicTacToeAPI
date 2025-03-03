import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.ClientProtocolException;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import tictactoe.api.Server;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBUser;
import tictactoe.user.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TEST {

    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");

    @BeforeAll
    static void init() throws SQLException {
        Logger.disableLoggers();

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
        DBUser.insertUser(user, dataSource);
        MatchCreate.createMatch(dataSource);
    }


    @Test
    void loginTest() throws IOException, InterruptedException {
        Server.start(dataSource);
        String login = "{\"userName\":\"nils\", \"password\":\"password\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        System.out.println(httpResponse.getCode());
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
