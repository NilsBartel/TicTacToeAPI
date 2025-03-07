package api.databaseData;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.database.*;
import tictactoe.game.Match;
import tictactoe.game.Score;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestMatchesTest {



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
    void getScoreTest() {
        Score score = DBScore.getScore(1,dataSource);
        System.out.println(score);
        System.out.println(score.getPlayerScore() + " = 5?");
        System.out.println(score.getComputerScore() + " = 3?");

        Assertions.assertTrue(DBUser.userExists("test", dataSource));
    }

    @Test
    void getUserTest() {
        Assertions.assertTrue(DBUser.userExists("test", dataSource));
    }

    @Test
    void getMatchTest() {
        Match match = DBMatch.getMatch(1,1, dataSource);
        Match match2 = TestMatches.getMatch1();

        Assertions.assertEquals(match, match2);
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
