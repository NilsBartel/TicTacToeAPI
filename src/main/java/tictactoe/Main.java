package tictactoe;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

import liquibase.exception.LiquibaseException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.database.LiquibaseMigrationService;
import tictactoe.api.Server;
import tictactoe.database.*;
import tictactoe.login.HashService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Main {




    public static void main(String[] args) throws IOException, SQLException, LiquibaseException, InterruptedException {

//        long startTime = System.currentTimeMillis();
//        System.out.println(startTime);
//        System.out.println(new Timestamp(startTime));
//        Thread.sleep(17643);
//        long endTime = System.currentTimeMillis();
//        System.out.println(endTime);
//        System.out.println(new Timestamp(endTime));

//        System.out.println(HashService.hash("hamburg"));
//        System.out.println(HashService.hash("bruno"));




        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());


        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration(pool.getDataSource());

        Server server = new Server();
        server.start(pool.getDataSource());
    }

}
