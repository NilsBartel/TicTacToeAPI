package tictactoe;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.api.Server;
import tictactoe.database.*;

import java.io.IOException;

public class Main {




    public static void main(String[] args) throws IOException {
        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());


        Server.start(pool.getDataSource());



    }

}
