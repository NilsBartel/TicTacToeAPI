package tictactoe;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.database.LiquibaseMigrationService;
import tictactoe.api.Server;
import tictactoe.database.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{
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
