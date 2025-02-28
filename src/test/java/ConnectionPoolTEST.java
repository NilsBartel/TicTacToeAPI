//import com.zaxxer.hikari.HikariDataSource;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
//public class ConnectionPoolTEST {
//    private static ConnectionPoolTEST instance;
//    private ConnectionPoolTEST() {}
//    public static ConnectionPoolTEST getInstance() {
//        if (instance == null) {
//            instance = new ConnectionPoolTEST();
//        }
//        return instance;
//    }
//
//    //private static HikariPool pool;
//    private static HikariDataSource dataSource;
//
//
//
//
//
//
//
//    public void initPool(String jdbcUrl, String username, String password) {
//        dataSource = new HikariDataSource();
//        //dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setJdbcUrl(jdbcUrl);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//    }
//
//    public void closePool() {
//        dataSource.close();
//    }
//
//
//
//    public Connection getConnection() throws SQLException {
//        return dataSource.getConnection();
//    }
//
//
//
//
//
//
//
//}
