package old;

import api.databaseData.DatabaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.Server;
import tictactoe.api.account.LoginResponse;
import tictactoe.database.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestTest {



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
        void testMatch() {
            Assertions.assertTrue(DBUser.userExists("test", dataSource));
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

        void wipeDatabase(){
            String sql = "DROP SCHEMA public CASCADE;";
            String sql2 = "CREATE SCHEMA public;";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement prepStatement = connection.prepareStatement(sql);
                 PreparedStatement prepStatement2 = connection.prepareStatement(sql2)
            ) {
                prepStatement.executeUpdate();
                prepStatement2.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }



}
