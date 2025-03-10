package api.databaseData;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zaxxer.hikari.HikariDataSource;
import logger.LoggerConfig;
import org.junit.jupiter.api.*;

import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.database.*;
import tictactoe.game.Match;
import tictactoe.game.Score;
import tictactoe.user.User;


import java.sql.DriverManager;
import java.sql.SQLException;

public class MatchDataTest {



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
        //DatabaseUtil.setSeq(dataSource, postgres);


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
    void getMatch1Test() {
        Match match = DBMatch.getMatch(1,1, dataSource);
        Match match2 = MatchData.getMatch1();

        Assertions.assertEquals(match, match2);
    }

    @Test
    void getMatch2Test() {
        Match match = DBMatch.getMatch(1,2, dataSource);
        Match match2 = MatchData.getMatch2();

        Assertions.assertEquals(match, match2);
    }

    @Test
    void getMatch3Test() {
        Match match = DBMatch.getMatch(2,3, dataSource);
        Match match3 = MatchData.getMatch3();

        Assertions.assertEquals(match, match3);
    }

    @Test
    void getMatch4Test() {
        Match match = DBMatch.getMatch(3,4, dataSource);
        Match match4 = MatchData.getMatch4();

        Assertions.assertEquals(match, match4);
    }




    @Test
    void changeDBTest() throws SQLException {
        Assertions.assertFalse(DBUser.userExists("hallo", dataSource));

        DBUser.insertUser(new User("hallo", "zest", "test", "test"), dataSource);


        Assertions.assertTrue(DBUser.userExists("hallo", dataSource));
        DatabaseUtil.recreateDatabase(postgres, dataSource);
        Assertions.assertFalse(DBUser.userExists("hallo", dataSource));
    }







    @Test
    void thoroughMatchTest() {
        Match match = DBMatch.getMatch(2,3, dataSource);
        Match match2 = MatchData.getMatch3();

        Assertions.assertEquals(match.getBoard(), match2.getBoard());

        Assertions.assertEquals(match.getStatus(), match2.getStatus());
        Assertions.assertEquals(match.getDifficulty(), match2.getDifficulty());
        Assertions.assertEquals(match.isIsPlayerTurn(), match2.isIsPlayerTurn());
        Assertions.assertEquals(match.getMatchID(), match2.getMatchID());
        Assertions.assertEquals(match.getStartTime(), match2.getStartTime());
        Assertions.assertEquals(match.getEndTime(), match2.getEndTime());
    }










}
