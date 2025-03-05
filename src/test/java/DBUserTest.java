//import com.zaxxer.hikari.HikariDataSource;
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//import tictactoe.database.ConnectionPool;
//import tictactoe.database.DBUser;
//import tictactoe.user.User;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//import static org.junit.Assert.*;
//
//class DBUserTest {
//
////    Connection connection;
//    static HikariDataSource dataSource;
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");
//
////    @BeforeAll
////    static void beforeAll() {
////        postgres.start();
////    }
//@BeforeAll
//static void beforeAll() {
//    LoggerConfig.disableLoggers();
//    postgres.start();
//
//    ConnectionPool pool = ConnectionPool.getInstance();
//    pool.initPool(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
//    dataSource = pool.getDataSource();
//}
//
//    @AfterAll
//    static void afterAll() {
//        postgres.stop();
//    }
//
////    @BeforeEach
////    void setUp() {
////        DatabaseConnectionForTests databaseConnectionForTests = new DatabaseConnectionForTests(
////                postgres.getJdbcUrl(),
////                postgres.getUsername(),
////                postgres.getPassword()
////        );
////        connection = databaseConnectionForTests.getConnection();
////
////    }
//
//    @AfterEach
//    void tearDown() {
////        setUp();
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        wipeUsersTable();
//    }
//
//
//    private void wipeUsersTable() {
//        String sql = "delete from users";
//        String sql2 = "ALTER SEQUENCE users_user_id_seq RESTART WITH 1";
//
//
//        try (Connection connection = dataSource.getConnection();
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2)
//        ) {
//            preparedStatement.executeUpdate();
//            preparedStatement2.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private User populateUserTable() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////
////
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        User user = new User("paul", "password", "answer1", "answer2");
//        DBUser.insertUser(user, dataSource);
//        return user;
//    }
//    private User populateUserTableWith(String userName, String password, String answer1, String answer2) {
//        //DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//
//
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        User user = new User(userName, password, answer1, answer2);
//        DBUser.insertUser(user, dataSource);
//        return user;
//    }
//
//
//    @Test
//    void userExistsTrue() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        User user = new User("nils", "password", "answer1", "answer2");
//        DBUser.insertUser(user, dataSource);
//
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        Assertions.assertTrue(DBUser.userExists("nils", dataSource));
//    }
//
//    @Test
//    void userExistsFalse() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        User user = new User("paul", "password", "answer1", "answer2");
//        DBUser.insertUser(user, dataSource);
//
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        assertTrue(DBUser.userExists("paul", dataSource));
//    }
//
//    @Test
//    void getPasswordTrue() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        User user = new User("paul", "password", "answer1", "answer2");
//        DBUser.insertUser(user, dataSource);
//
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        assertEquals(DBUser.getPassword("paul", dataSource), user.getPassword());
//    }
//
//    @Test
//    void updatePasswordTrue() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//
//        User user = new User("paul", "password", "answer1", "answer2");
//        DBUser.insertUser(user, dataSource);
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        DBUser.updatePassword(1, "newPassword", dataSource);
//
//
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertEquals("newPassword", DBUser.getPassword("paul", dataSource));
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertNotEquals("password", DBUser.getPassword("paul", dataSource));
//    }
//
//
//
//
//
//
//    @Test
//    void getUserID() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        User user = new User("nils", "password", "answer1", "answer2");
//        DBUser.insertUser(user, dataSource);
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        User user2 = new User("herbert", "password", "answer1", "answer2");
//        DBUser.insertUser(user2, dataSource);
//
//
//
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertEquals(1, DBUser.getUserId("nils", dataSource));
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertEquals(2, DBUser.getUserId("herbert", dataSource));
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertNotEquals(2, DBUser.getUserId("paul", dataSource));
//
//    }
//
//    @Test
//    void getAnswer1(){
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        User user = populateUserTableWith("paul", "password", "answer1", "answer2");
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertEquals(user.getAnswer1(), DBUser.getAnswer1(1, dataSource));
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertNotEquals("wrong", DBUser.getAnswer1(1, dataSource));
//    }
//
//    @Test
//    void getAnswer2(){
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        User user = populateUserTableWith("paul", "password", "answer1", "answer2");
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertEquals(user.getAnswer2(), DBUser.getAnswer2(1, dataSource));
//
////        setUp();
////        when(mockDBConHandler.getConnection()).thenReturn(connection);
//        assertNotEquals("hallo", DBUser.getAnswer2(1, dataSource));
//    }
//
//
//
//
//
//
//
//
//
//}