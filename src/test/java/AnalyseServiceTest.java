import ch.qos.logback.classic.Level;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.database.DBUser;
import tictactoe.game.AnalyseService;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;
import tictactoe.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalyseServiceTest {
    static HikariDataSource dataSource;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");


    //    @BeforeAll
    //    static void beforeAll() {
    //        postgres.start();
    //    }

    @BeforeEach
    void init() throws SQLException {

    }


    @BeforeAll
    static void beforeAll() {

        ch.qos.logback.classic.Logger dockerLogger1 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.github.dockerjava");
        dockerLogger1.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger dockerLogger2 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.wire");
        dockerLogger2.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger testContainerLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.testcontainers");
        testContainerLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger testContainerRyukLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("tc.testcontainers/ryuk:0.11.0");
        testContainerRyukLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger postgresLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("tc.postgres:16-alpine");
        postgresLogger.setLevel(Level.ERROR);



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
        DBUser.insertUser(user, dataSource);
    }


    @AfterEach
    void tearDown() {
        wipeTable();
    }

    private void wipeTable() { //TODO: put into one query? or at least less
        String sqlField = "delete from field";
        String sqlFieldSeq = "ALTER SEQUENCE field_field_id_seq RESTART WITH 1";
        String sqlRow = "delete from row";
        String sqlRowSeq = "ALTER SEQUENCE row_row_id_seq RESTART WITH 1";
        String sqlBoard = "delete from board";
        String sqlBoardSeq = "ALTER SEQUENCE board_board_id_seq RESTART WITH 1";
        String sqlTime = "delete from time";
        String sqlTimeSeq = "ALTER SEQUENCE time_time_id_seq RESTART WITH 1";
        String sqlScore = "delete from score";
        String sqlScoreSeq = "ALTER SEQUENCE score_score_id_seq RESTART WITH 1";
        String sqlMatch = "delete from match";
        String sqlMatchSeq = "ALTER SEQUENCE match_match_id_seq RESTART WITH 1";
        String sqlUser = "delete from users";
        String sqlUserSeq = "ALTER SEQUENCE users_user_id_seq RESTART WITH 1";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatementField = connection.prepareStatement(sqlField);
             PreparedStatement preparedStatementFieldSeq = connection.prepareStatement(sqlFieldSeq);

             PreparedStatement preparedStatementRow = connection.prepareStatement(sqlRow);
             PreparedStatement preparedStatementRowSeq = connection.prepareStatement(sqlRowSeq);

             PreparedStatement preparedStatementBoard = connection.prepareStatement(sqlBoard);
             PreparedStatement preparedStatementBoardSeq = connection.prepareStatement(sqlBoardSeq);

             PreparedStatement preparedStatementTime = connection.prepareStatement(sqlTime);
             PreparedStatement preparedStatementTimeSeq = connection.prepareStatement(sqlTimeSeq);
             PreparedStatement preparedStatementScore = connection.prepareStatement(sqlScore);
             PreparedStatement preparedStatementScoreSeq = connection.prepareStatement(sqlScoreSeq);
             PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
             PreparedStatement preparedStatementUserSeq = connection.prepareStatement(sqlUserSeq);
             PreparedStatement preparedStatementMatch = connection.prepareStatement(sqlMatch);
             PreparedStatement preparedStatementMatchSeq = connection.prepareStatement(sqlMatchSeq)

        ) {
            preparedStatementField.execute();
            preparedStatementFieldSeq.execute();
            preparedStatementRow.execute();
            preparedStatementRowSeq.execute();
            preparedStatementBoard.execute();
            preparedStatementBoardSeq.execute();
            preparedStatementTime.execute();
            preparedStatementTimeSeq.execute();
            preparedStatementScore.executeUpdate();
            preparedStatementScoreSeq.executeUpdate();
            preparedStatementMatch.executeUpdate();
            preparedStatementMatchSeq.executeUpdate();
            preparedStatementUser.executeUpdate();
            preparedStatementUserSeq.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }











    private Map<List<Position>, Integer> createMapForTest() {
        Map<List<Position>, Integer> map = new HashMap<>();
        map.put(List.of(new Position(1),new Position(5),new Position(9)),4);
        map.put(List.of(new Position(1),new Position(2),new Position(3)),4);
        map.put(List.of(new Position(3),new Position(6),new Position(9)),2);
        map.put(List.of(new Position(1),new Position(4),new Position(7)),2);
        map.put(List.of(new Position(4),new Position(5),new Position(6)),3);
        map.put(List.of(new Position(3),new Position(5),new Position(7)),1);
        return map;
    }


    private Map<List<Position>, Integer> createMapForTest2() {
        Map<List<Position>, Integer> map = new HashMap<>();
        map.put(List.of(new Position(1),new Position(2),new Position(3)),1);
        map.put(List.of(new Position(4),new Position(5),new Position(6)),1);
        map.put(List.of(new Position(7),new Position(8),new Position(9)),1);

//        map.put(List.of(new Position(1),new Position(4),new Position(7)),1);
//        map.put(List.of(new Position(2),new Position(5),new Position(8)),1);
//        map.put(List.of(new Position(3),new Position(6),new Position(9)),1);
//
//        map.put(List.of(new Position(1),new Position(5),new Position(9)),1);
//        map.put(List.of(new Position(3),new Position(5),new Position(7)),1);
        return map;
    }

    @Test
    void analyseTest2() {

        AnalyseService analyseService = AnalyseService.getInstance();

        insertMatches();
        Map<List<Position>, Integer> map = analyseService.findBestWinningLine(1, dataSource);


        assertEquals(createMapForTest2(), map);
    }

    private void insertMatches(){
        DBMatch.insertNewMatch(generateMatch(generateBoardForTest(new int[]{1,2,3}, new int[]{})), 1, dataSource);
        DBMatch.insertNewMatch(generateMatch(generateBoardForTest(new int[]{4,5,6}, new int[]{})), 1, dataSource);
        DBMatch.insertNewMatch(generateMatch(generateBoardForTest(new int[]{7,8,9}, new int[]{})), 1, dataSource);
    }

    private static Match generateMatch(Board board) {
        Match match = new Match();
        match.setBoard(board);
        match.setStatus(MatchStatus.PLAYER_WON);
        match.setDifficulty(DifficultyState.EASY);
        match.setIsPlayerTurn(true);
        match.setStartTime(new Timestamp(0));
        match.setEndTime(new Timestamp(0));
        //start/endTime
        return match;
    }

    private static Board generateBoardForTest(int[] player, int[] computer) {
        Board board = new Board();
        for (int j : player) {
            Position position = new Position(j);
            board.setSymbol(position.getRow(), position.getColumn(), Match.PLAYER_SYMBOL);
        }
        for (int j : computer) {
            Position position = new Position(j);
            board.setSymbol(position.getRow(), position.getColumn(), Match.COMPUTER_SYMBOL);
        }
        return board;
    }










}