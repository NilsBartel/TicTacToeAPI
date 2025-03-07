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


        long time = System.currentTimeMillis();


        System.out.println(time);
        System.out.println(new Timestamp(time));

        Thread.sleep(19760);
        long time2 = System.currentTimeMillis();
        System.out.println(time2);
        System.out.println(new Timestamp(time2));


//        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
//        hikariLogger.setLevel(Level.ERROR);
//
//
//        ConnectionPool pool = ConnectionPool.getInstance();
//        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());
//
//
//        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
//        migrationService.runMigration(pool.getDataSource());
//
//        Server server = new Server();
//        server.start(pool.getDataSource());
    }

}
