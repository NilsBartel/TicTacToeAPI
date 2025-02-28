import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class newTest {

    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        dataSource = pool.getDataSource();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {

        User user = new User("nils", "password", "answer1", "answer2");
        DB_User.insertUserTEST(user, dataSource);

        User user2 = new User("lala", "password", "answer1", "answer2");
        DB_User.insertUserTEST(user2, dataSource);
    }

    @AfterEach
    void tearDown() {
        wipeTable();
    }

    private void wipeTable() {
        String sql = "delete from score";
        String sql2 = "ALTER SEQUENCE score_score_id_seq RESTART WITH 1";
        String sql3 = "delete from users";
        String sql4 = "ALTER SEQUENCE users_user_id_seq RESTART WITH 1";


        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
             PreparedStatement preparedStatement3 = connection.prepareStatement(sql3);
             PreparedStatement preparedStatement4 = connection.prepareStatement(sql4)
        ) {
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();
            preparedStatement3.executeUpdate();
            preparedStatement4.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    @Test
    void getScore() {

        DB_Score.insertEmptyScore(1, dataSource);

        assertEquals(new Score(0,0,0), DB_Score.getScore(1, dataSource));
    }








}
