//import com.zaxxer.hikari.HikariDataSource;
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class DB_FieldTest {
//    static HikariDataSource dataSource;
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername("postgres").withInitScript("init.sql");
//
////    @BeforeAll
////    static void beforeAll() {
////        postgres.start();
////    }
//    @BeforeAll
//    static void beforeAll() {
//        postgres.start();
//
//        ConnectionPool pool = ConnectionPool.getInstance();
//        pool.initPool(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
//        dataSource = pool.getDataSource();
//    }
//
//
//    @AfterAll
//    static void afterAll() {
//        postgres.stop();
//    }
//
//    @BeforeEach
//    void setUp() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(getConnection());
//
//        User user = new User("nils", "password", "answer1", "answer2");
//        //DB_User.insertUser(user, mockDBConHandler);
//        DB_User.insertUser(user, dataSource);
//    }
//
////    private Connection getConnection() {
////        DatabaseConnectionForTests databaseConnectionForTests = new DatabaseConnectionForTests(
////                postgres.getJdbcUrl(),
////                postgres.getUsername(),
////                postgres.getPassword()
////        );
////        return databaseConnectionForTests.getConnection();
////    }
//
//    @AfterEach
//    void tearDown() {
////        setUp();
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection()).thenReturn(getConnection());
//
//        wipeTable();
//    }
//
//    private void wipeTable() {
////        String sql = "delete from score";
////        String sql2 = "ALTER SEQUENCE score_score_id_seq RESTART WITH 1";
////        String sql3 = "delete from users";
////        String sql4 = "ALTER SEQUENCE users_user_id_seq RESTART WITH 1";
//
//        String sql = "delete from field";
//        String sql2 = "ALTER SEQUENCE field_field_id_seq RESTART WITH 1";
//
//
//
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql);
//             PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
////             PreparedStatement preparedStatement3 = connection.prepareStatement(sql3);
////             PreparedStatement preparedStatement4 = connection.prepareStatement(sql4)
//        ) {
//            preparedStatement.executeUpdate();
//            preparedStatement2.executeUpdate();
////            preparedStatement3.executeUpdate();
////            preparedStatement4.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
////    @Test
////    void insertField() {
////        Row row = new Row();
////        for(Field field : row.fields) {
////            field.setSymbol('x');
////        }
////
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection())
////                .thenReturn(getConnection())
////                .thenReturn(getConnection());
////
////        DB_Field.insertFields(row, mockDBConHandler);
////
////        assertEquals(row.getSymbol(0), DB_Field.getSymbol(0, 0, mockDBConHandler));
////
////    }
//
//    @Test
//    void updateField() {
//
//    }
//
//    @Test
//    void getSymbol() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection())
////                .thenReturn(getConnection())
////                .thenReturn(getConnection())
////                .thenReturn(getConnection());
//
//        Row row = new Row();
//        row.setSymbol(0,'x');
//        row.setSymbol(1,'x');
//        row.setSymbol(2,'x');
//
//        DB_Field.insertFields(row, dataSource);
//
//
//        System.out.println(DB_Field.getSymbol(1, 1, dataSource));
//        test(dataSource); //TODO: what does this do?
//
//
//    }
//
//
//
////    @Test
////    void testTest() {
////        DB_ConHandler mockDBConHandler = mock(DB_ConHandler.class);
////        when(mockDBConHandler.getConnection())
////                .thenReturn(getConnection())
////                .thenReturn(getConnection())
////                .thenReturn(getConnection());
////
//////        Row row = new Row();
//////        row.setSymbol(0,'x');
//////        row.setSymbol(1,'x');
//////        row.setSymbol(2,'x');
//////
//////        DB_Field.insertFields(row);
////        inserttest(mockDBConHandler);
////
////
////        System.out.println(DB_Field.getSymbol(1, 1, mockDBConHandler));
////        test(mockDBConHandler);
////
////
////    }
//
//
//
//    public void test(HikariDataSource dataSource) {
//        String sql = "select field.symbol from field where row_id = 1 AND field.field = 1;";
//
//        try(Connection connection = dataSource.getConnection();
//            PreparedStatement prepStatement = connection.prepareStatement(sql)
//        ){
//            prepStatement.execute();
//
//
//            ResultSet resultSet = prepStatement.getResultSet();
//            while(resultSet.next()) {
//                System.out.println(resultSet.getString(1));
//            }
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void inserttest(HikariDataSource dataSource) {
//        //String sql = "select field.symbol from field where row_id = 1 AND field.field = 1;";
//        String sql = "INSERT INTO field(field, symbol, row_id) VALUES (?,?,?)";
//
//        try(Connection connection = dataSource.getConnection();
//            PreparedStatement prepStatement = connection.prepareStatement(sql)
//        ){
//            prepStatement.setInt(1,0);
//            prepStatement.setString(2, String.valueOf('x'));
//            prepStatement.setInt(3, 1);
//
//            prepStatement.execute();
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
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
//
//
//}