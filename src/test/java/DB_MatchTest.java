import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DB_MatchTest {

    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUsername("postgres")
            .withInitScript("init.sql");


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

//    private void printConnection() {
//        System.out.println(postgres.getPassword());
//        System.out.println(postgres.getJdbcUrl());
//        System.out.println(postgres.getUsername());
//        System.out.println();
//    }

    @AfterEach
    void tearDown() {
//        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
//        when(mockDBConHandler.getConnection()).thenReturn(getConnection());

        wipeTable();
    }

    private void wipeTable() {
        String sqlTime = "delete from time";
        String sqlTimeSeq = "ALTER SEQUENCE time_time_id_seq RESTART WITH 1";
        String sqlScore = "delete from score";
        String sqlScoreSeq = "ALTER SEQUENCE score_score_id_seq RESTART WITH 1";
        String sqlField = "delete from field";
        String sqlFieldSeq = "ALTER SEQUENCE field_field_id_seq RESTART WITH 1";
        String sqlRow = "delete from row";
        String sqlRowSeq = "ALTER SEQUENCE row_row_id_seq RESTART WITH 1";
        String sqlBoard = "delete from board";
        String sqlBoardSeq = "ALTER SEQUENCE board_board_id_seq RESTART WITH 1";
        String sqlMatch = "delete from match";
        String sqlMatchSeq = "ALTER SEQUENCE match_match_id_seq RESTART WITH 1";
        String sqlUser = "delete from users";
        String sqlUserSeq = "ALTER SEQUENCE users_user_id_seq RESTART WITH 1";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatementTime = connection.prepareStatement(sqlTime);
             PreparedStatement preparedStatementTimeSeq = connection.prepareStatement(sqlTimeSeq);

             PreparedStatement preparedStatementScore = connection.prepareStatement(sqlScore);
             PreparedStatement preparedStatementScoreSeq = connection.prepareStatement(sqlScoreSeq);

             PreparedStatement preparedStatementField = connection.prepareStatement(sqlField);
             PreparedStatement preparedStatementFieldSeq = connection.prepareStatement(sqlFieldSeq);

             PreparedStatement preparedStatementRow = connection.prepareStatement(sqlRow);
             PreparedStatement preparedStatementRowSeq = connection.prepareStatement(sqlRowSeq);

             PreparedStatement preparedStatementBoard = connection.prepareStatement(sqlBoard);
             PreparedStatement preparedStatementBoardSeq = connection.prepareStatement(sqlBoardSeq);

             PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
             PreparedStatement preparedStatementUserSeq = connection.prepareStatement(sqlUserSeq);

             PreparedStatement preparedStatementMatch = connection.prepareStatement(sqlMatch);
             PreparedStatement preparedStatementMatchSeq = connection.prepareStatement(sqlMatchSeq)

        ) {
            preparedStatementTime.execute();
            preparedStatementTimeSeq.execute();

            preparedStatementScore.executeUpdate();
            preparedStatementScoreSeq.executeUpdate();

            preparedStatementField.executeUpdate();
            preparedStatementFieldSeq.executeUpdate();

            preparedStatementRow.executeUpdate();
            preparedStatementRowSeq.executeUpdate();

            preparedStatementBoard.executeUpdate();
            preparedStatementBoardSeq.executeUpdate();

            preparedStatementMatch.executeUpdate();
            preparedStatementMatchSeq.executeUpdate();

            preparedStatementUser.executeUpdate();
            preparedStatementUserSeq.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: row and field delete

    private Match createRunningMatch() {
        Match match = new Match();
        Board board = new Board();
        match.setBoard(board);
        match.setDifficulty(DifficultyState.EASY);
        match.setStatus(MatchStatus.RUNNING);
        match.setIsPlayerTurn(true);

        return match;
    }

    private Match createWonMatch() {
        Match match = new Match();
        Board board = new Board();
        match.setBoard(board);
        match.setDifficulty(DifficultyState.EASY);
        match.setStatus(MatchStatus.PLAYER_WON);
        match.setIsPlayerTurn(true);

        return match;
    }
    private Match createDrawMatch() {
        Match match = new Match();
        Board board = new Board();
        match.setBoard(board);
        match.setDifficulty(DifficultyState.EASY);
        match.setStatus(MatchStatus.DRAW);
        match.setIsPlayerTurn(true);

        return match;
    }





    @Test
    void insertAndGetMatch() {

        int userId = 1;
        Match match = createRunningMatch();
        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
        match.setStartTime(startTimestamp);
        //DB_Match.insertMatch(match, userId, dataSource);
        DB_Match.insertNewMatch(match, userId, dataSource);


        //Match match1 = DB_Match.getLastMatchFromUser(1, dataSource);
        Match match1 = DB_Match.getLastNMatchesFromUser(userId, 1, dataSource).getFirst();
        match1.setBoard(match.getBoard());


        assertEquals(match, match1);
    }
    @Test
    void updateMatch() {

        int userId = 1;
        Match match = createRunningMatch();
        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
        match.setStartTime(startTimestamp);
        //DB_Match.insertMatch(match, userId, dataSource);
        DB_Match.insertNewMatch(match, userId, dataSource);

        Match newMatch = match;
        newMatch.setIsPlayerTurn(false);
        newMatch.setStatus(MatchStatus.COMPUTER_WON);

        Database.updateDB_Match(newMatch, userId, dataSource);

        //Match match1 = DB_Match.getMatchFromDB(1, dataSource);
        Match match1 = DB_Match.getLastNMatchesFromUser(userId, 1, dataSource).getFirst();
        match1.setBoard(match.getBoard());


        assertEquals(newMatch, match1);
    }

//    @Test
//    void userHasRunningMatch() {
//
//        int userId = 1;
//        Match match = createRunningMatch();
//        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
//        match.setStartTime(startTimestamp);
//        DB_Match.insertMatch(match, userId, dataSource);
//
//        assertTrue(DB_Match.userHasRunningMatch(userId, dataSource));
//    }

//    @Test
//    void userHasNoRunningMatch() {
//
//        int userId = 1;
//        Match match = createWonMatch();
//        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
//        match.setStartTime(startTimestamp);
//        DB_Match.insertMatch(match, userId, dataSource);
//
//        assertFalse(DB_Match.userHasRunningMatch(userId, dataSource));
//    }



    @Test
    void getLastMatchFromUser() {

        int userId = 1;
        Match matchUser = createWonMatch();
        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
        matchUser.setStartTime(startTimestamp);
        DB_Match.insertNewMatch(matchUser, userId, dataSource);


        int userId2 = 2;
        DB_User.insertUser(new User("test", "test", "test", "test"), dataSource);
        Match match2 = createWonMatch();
        match2.setStartTime(new Timestamp(System.currentTimeMillis()));
        DB_Match.insertNewMatch(match2, userId2, dataSource);
        Match match3 = createRunningMatch();
        match3.setStartTime(new Timestamp(System.currentTimeMillis()));
        DB_Match.insertNewMatch(match3, userId2, dataSource);

        //Match matchDB = DB_Match.getLastMatchFromUser(userId, dataSource);
        Match matchDB = DB_Match.getLastNMatchesFromUser(userId, 1, dataSource).getFirst();
        matchDB.setBoard(matchUser.getBoard());
        assertEquals(matchUser, matchDB);


    }





}