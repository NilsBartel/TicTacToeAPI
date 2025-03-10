package api.databaseData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.api.errors.LoginError;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBUser;
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.login.PasswordUtil;
import tictactoe.user.User;

import java.sql.DriverManager;
import java.sql.SQLException;

class UsersDataTest {

    static ObjectMapper objectMapper;
    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres");

    @BeforeAll
    static void init() throws InterruptedException, SQLException {
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
    }


    @AfterAll
    static void close() {
        dataSource.close();
    }


    @Test
    void user1Test() throws LoginError {
        User user = UsersData.getUser1();

        Assertions.assertTrue(DBUser.userExists(user.getUserName(), dataSource));
        Assertions.assertTrue(PasswordUtil.checkPassword(user.getPassword(), DBUser.getPassword(user.getUserName(), dataSource)));
        Assertions.assertTrue(PasswordUtil.checkSecurityQuestions(1, user, dataSource));
    }

    @Test
    void user2Test() throws LoginError {
        User user = UsersData.getUser2();

        Assertions.assertTrue(DBUser.userExists(user.getUserName(), dataSource));
        Assertions.assertTrue(PasswordUtil.checkPassword(user.getPassword(), DBUser.getPassword(user.getUserName(), dataSource)));
        Assertions.assertTrue(PasswordUtil.checkSecurityQuestions(2, user, dataSource));
    }

    @Test
    void user3Test() throws LoginError {
        User user = UsersData.getUser3();

        Assertions.assertTrue(DBUser.userExists(user.getUserName(), dataSource));
        Assertions.assertTrue(PasswordUtil.checkPassword(user.getPassword(), DBUser.getPassword(user.getUserName(), dataSource)));
        Assertions.assertTrue(PasswordUtil.checkSecurityQuestions(3, user, dataSource));
    }



}