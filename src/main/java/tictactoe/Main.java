package tictactoe;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

import liquibase.exception.LiquibaseException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.database.LiquibaseMigrationService;
import tictactoe.api.Server;
import tictactoe.database.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {




    public static void main(String[] args) throws IOException, SQLException, LiquibaseException {
        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());


        Connection connection = pool.getDataSource().getConnection();

        //java.sql.Connection connection = openConnection(); //your openConnection logic here
//        java.sql.Connection connection = pool.getDataSource().getConnection();
//
//        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
//
//        Liquibase liquibase = new liquibase.Liquibase("src/main/resources/liquibase-outputChangeLog.xml", new ClassLoaderResourceAccessor(), (DatabaseConnection) database);
//
//        liquibase.update(new Contexts(), new LabelExpression());

        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration(pool.getDataSource());


        //Server.start(pool.getDataSource());
        Server server = new Server();
        server.start(pool.getDataSource());



    }

}
