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

import java.sql.DriverManager;
import java.sql.SQLException;

public class ScoreDataTest {

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
    void getScore1Test() {
        Score scoreDB = DBScore.getScore(1, dataSource);
        Score scoreData = ScoreData.getScore1();

        Assertions.assertEquals(scoreDB, scoreData);
    }

    @Test
    void getScore2Test() {
        Score scoreDB = DBScore.getScore(2, dataSource);
        Score scoreData = ScoreData.getScore2();

        Assertions.assertEquals(scoreDB, scoreData);
    }
}
