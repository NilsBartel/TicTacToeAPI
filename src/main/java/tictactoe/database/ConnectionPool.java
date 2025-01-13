package tictactoe.database;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {
    private static ConnectionPool instance;
    private ConnectionPool() {}
    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private static HikariDataSource dataSource;




    public void initPool(String jdbcUrl, String username, String password) {
        dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

    public void closePool() {
        dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

}
