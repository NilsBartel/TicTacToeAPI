package api.match;

import api.MatchCreate;
import api.databaseData.DatabaseUtil;
import api.databaseData.TestMatches;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.Server;
import tictactoe.api.account.LoginResponse;
import tictactoe.database.*;
import tictactoe.game.Match;
import tictactoe.game.Score;
import tictactoe.login.HashService;
import tictactoe.user.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

            //User user1 = new User("test", "test", "answer1", "answer2");
            //DBUser.insertUser(user1, dataSource);
            //insertTable();
            DatabaseUtil.populateDatabase(postgres, dataSource);
//            User user2 = new User("user2", "test", "answer1", "answer2");
//            DBUser.insertUser(user2, dataSource);

//            User user1 = new User("test", HashService.hash("test"), HashService.hash("answer1"), HashService.hash("answer2"));
//            DBUser.insertUser(user1, dataSource);
//            User user2 = new User("user2", HashService.hash("test"), HashService.hash("answer1"), HashService.hash("answer2"));
//            DBUser.insertUser(user2, dataSource);
            //MatchCreate.createMatch(dataSource);



            server = new Server();
            server.start(dataSource);
        }


        void setUp() throws SQLException {
            LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
            migrationService.runMigration(dataSource);

//            User user1 = new User("test", "test", "answer1", "answer2");
//            DBUser.insertUser(user1, dataSource);
//            User user2 = new User("user2", "test", "answer1", "answer2");
//            DBUser.insertUser(user2, dataSource);

//            User user1 = new User("test", HashService.hash("test"), HashService.hash("answer1"), HashService.hash("answer2"));
//            DBUser.insertUser(user1, dataSource);
//            User user2 = new User("user2", HashService.hash("test"), HashService.hash("answer1"), HashService.hash("answer2"));
//            DBUser.insertUser(user2, dataSource);
            insertTable();
            MatchCreate.createMatch(dataSource);
        }

        @AfterAll
        static void close() {
            dataSource.close();
            server.close();
        }


        void tearDown() {
            wipeDatabase();
        }



        @Test
        void getScoreTest() throws IOException {
            Score score = DBScore.getScore(1,dataSource);
            System.out.println(score);
            System.out.println(score.getPlayerScore() + " = 5?");
            System.out.println(score.getComputerScore() + " = 3?");

            Assertions.assertTrue(DBUser.userExists("test", dataSource));
        }

        @Test
        void getUserTest() throws IOException {
            Assertions.assertTrue(DBUser.userExists("test", dataSource));

        }

        @Test
        void getMatchTest() throws IOException {
            Match match = DBMatch.getMatch(1,1, dataSource);
            Match match2 = TestMatches.getMatch1();

            Assertions.assertEquals(match, match2);
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

        static void insertTable() throws SQLException {
//            String sql = "COPY users FROM 'src/test/java/api/UsersTableData.csv' ( FORMAT CSV, DELIMITER'/' );";
//            try (Connection connection = dataSource.getConnection();
//                 PreparedStatement prepStatement = connection.prepareStatement(sql);
//            ) {
//                prepStatement.executeUpdate();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }

            try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
                long rowsInserted = new CopyManager((BaseConnection) conn)
                        .copyIn(
                                "COPY users FROM STDIN (FORMAT csv, HEADER)",
                                new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/UsersTableData.csv"))
                        );
                System.out.printf("%d row(s) inserted%n", rowsInserted);
                new CopyManager((BaseConnection) conn)
                        .copyIn(
                                "COPY score FROM STDIN (FORMAT csv, HEADER)",
                                new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/match/ScoreTableData.csv"))
                        );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

}
