import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DB_ScoreTest {
    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");

//    @BeforeAll
//    static void beforeAll() {
//        postgres.start();
//    }
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
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection()).thenReturn(getConnection());

        User user = new User("nils", "password", "answer1", "answer2");
        DB_User.insertUser(user, dataSource);
    }

//    private Connection getConnection() {
//        DatabaseConnectionForTests databaseConnectionForTests = new DatabaseConnectionForTests(
//                postgres.getJdbcUrl(),
//                postgres.getUsername(),
//                postgres.getPassword()
//        );
//        return databaseConnectionForTests.getConnection();
//    }

    @AfterEach
    void tearDown() {
//        //setUp();
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection()).thenReturn(getConnection());

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
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());

        DB_Score.insertEmptyScore(1, dataSource);

        assertEquals(new Score(0,0,0), DB_Score.getScore(1, dataSource));
        assertNotEquals(new Score(5,1,0), new Score(0,0,0));
    }


    @Test
    void updateScoreIfNotExists() {
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());

        DB_Score.updateScore("player", 1, dataSource);
        assertEquals(new Score(1,0,0), DB_Score.getScore(1, dataSource));
    }


    @Test
    void updatePlayerScore() {
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());
        DB_Score.insertEmptyScore(1, dataSource);

        DB_Score.updateScore("player", 1, dataSource);

        assertEquals(new Score(1,0,0), DB_Score.getScore(1, dataSource));
    }

    @Test
    void updateComputerScore() {
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());
        DB_Score.insertEmptyScore(1, dataSource);

        DB_Score.updateScore("computer", 1, dataSource);

        assertEquals(new Score(0,1,0), DB_Score.getScore(1, dataSource));
    }

    @Test
    void updateDrawScore() {
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());
        DB_Score.insertEmptyScore(1, dataSource);

        DB_Score.updateScore("draw", 1, dataSource);

        assertEquals(new Score(0, 0, 1), DB_Score.getScore(1, dataSource));
    }













}