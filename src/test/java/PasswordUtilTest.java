import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PasswordUtilTest {
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

        User user = new User("nils", "$2a$12$D/fzAEwEBVnby..yM4NKhOnXio/Vc4qJxHhZnuTxVsNfhvz.Abiri", "$2a$12$9IBdu7T9ee1pHTskkVRWGuiN8bELSW4V4MEy0P3vAEmGmeaQ25ItK", "$2a$12$kApWqxYSlzrp5kzF5EyIV.0BjFjkD74ZHPPmruYYS/8JKPDPNgsGS");
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
//        setUp();
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection()).thenReturn(getConnection());

        wipeTable();
    }

    private void wipeTable() {
        String sql = "delete from users";
        String sql2 = "ALTER SEQUENCE users_user_id_seq RESTART WITH 1";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
        ) {
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    private static Stream<Arguments> generatePasswordsForTest() {
        return Stream.of(
                Arguments.of("1!eeeQWrtt", true),
                Arguments.of("1234567!!Qü", true),
                Arguments.of("1234567!!Qä", true),
                Arguments.of("1234567!!Qö", true),
                Arguments.of("1234567!!Qß", true),
                Arguments.of("1234567!!aÜ", true),
                Arguments.of("1234567!!aÄ", true),
                Arguments.of("1234567!!aÖ", true),

                Arguments.of("", false),
                Arguments.of("1dQ!", false),
                Arguments.of("hallo123456", false),
                Arguments.of("hallo.....()(", false),
                Arguments.of("hallo123..-=", false),
                Arguments.of("HALLO11223..-?", false),
                Arguments.of("123.....jjjjjjjjj", false),
                Arguments.of("aaa", false),
                Arguments.of("AAA", false),
                Arguments.of("...", false),
                Arguments.of("111", false),
                Arguments.of("aaaaaaaaaaaaaaaaaasasasasasasasasasasasasasasasasasasa", false),
                Arguments.of("aaaaaaaaaaaaaaaaaasasasasasasasas.AA(()11122sasasasasasasasasasa", false)
        );
    }

    @ParameterizedTest
    @MethodSource("generatePasswordsForTest")
    void computerMove(String password, boolean expected) {

        boolean bool = PasswordUtil.isPasswordValid(password);

        assertEquals(expected, bool);
    }

    @Test
    void checkPasswordTestTrue(){
        String hashedPassword = HashService.hash("myNewPassword12_:");

        boolean bool = PasswordUtil.checkPassword("myNewPassword12_:", hashedPassword);

        assertTrue(bool);
    }

    @Test
    void checkPasswordTestFalse(){
        String hashedPassword = HashService.hash("not my password");

        boolean bool = PasswordUtil.checkPassword("myNewPassword12_:", hashedPassword);

        assertFalse(bool);
    }


    @Test
    void resetPasswordTestTrue(){
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());
        int userID = 1;

        String userName = "nils";
        String answer1 = "test";
        String answer2 = "test";
        String newPassword = "hallo123..ÄÖ";
        PlayerInput mockPlayerInput = mock(PlayerInput.class);
        LogInOutput mockLogInOutput = mock(LogInOutput.class);

        when(mockPlayerInput.askRecoveryQuestion1()).thenReturn(answer1);
        when(mockPlayerInput.askRecoveryQuestion2()).thenReturn(answer2);
        when(mockPlayerInput.crateNewPassword()).thenReturn(newPassword);


       boolean bool = PasswordUtil.resetPassword(userID, mockPlayerInput, mockLogInOutput, dataSource);

       assertTrue(bool);
       assertTrue(HashService.verify(newPassword, DB_User.getPassword(userName, dataSource)));
    }

    @Test
    void resetPasswordTestFalse(){
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection())
//                .thenReturn(getConnection());
        int userID = 1;

        String userName = "nils";
        String answer1 = "test";
        String answer2 = "wrong answer";
        String newPassword = "hallo123..ÄÖ";
        PlayerInput mockPlayerInput = mock(PlayerInput.class);
        LogInOutput mockLogInOutput = mock(LogInOutput.class);

        when(mockPlayerInput.askRecoveryQuestion1()).thenReturn(answer1);
        when(mockPlayerInput.askRecoveryQuestion2()).thenReturn(answer2);
        when(mockPlayerInput.crateNewPassword()).thenReturn(newPassword);


       boolean bool = PasswordUtil.resetPassword(userID, mockPlayerInput, mockLogInOutput, dataSource);

       assertFalse(bool);
       assertFalse(HashService.verify(newPassword, DB_User.getPassword(userName, dataSource)));
    }


}