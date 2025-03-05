package tictactoe.api;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import tictactoe.database.Config;

public class LiquibaseMigrationService {
    private static final String JDBC_URL = Config.getURL();
    private static final String USERNAME = Config.getUSERNAME();
    private static final String PASSWORD = Config.getPASSWORD();

    public void runMigration(HikariDataSource dataSource) {
        try (Connection connection = openConnection(dataSource)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("liquibase-outputChangeLog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
            System.out.println("Datenbankmigration abgeschlossen.");
        } catch (SQLException | liquibase.exception.LiquibaseException e) {
            System.err.println("Fehler bei der Datenbankmigration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Connection openConnection(HikariDataSource dataSource) throws SQLException {
        //return dataSource.getConnection();
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
