package tictactoe;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.api.match.MatchController;
import tictactoe.database.LiquibaseMigrationService;
import tictactoe.api.Server;
import tictactoe.database.*;
import tictactoe.login.HashService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {


    public static void main(String[] args) throws IOException {

        ch.qos.logback.classic.Logger hikariLogger =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);

        ch.qos.logback.classic.Logger testcontainerLogger =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.github.dockerjava");
        testcontainerLogger.setLevel(Level.ERROR);

        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());

        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration(pool.getDataSource());

        Server.start(pool.getDataSource());
    }

}
