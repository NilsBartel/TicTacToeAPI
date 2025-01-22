package tictactoe;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.api.Server;
import tictactoe.database.*;

import java.io.IOException;

public class Main {




    public static void main(String[] args) throws IOException, InterruptedException {

        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);




//        String token =  AuthenticationToken.getInstance().create(11);

        //AuthenticationToken.getInstance().create(12);

//        System.out.println(AuthenticationToken.getInstance().getAuthMap());
//
//
////        Thread.sleep(10000);
////        System.out.println(AuthenticationToken.getInstance().authenticate(token) + " 10 sec");
//
//        Thread.sleep(30000);
//        System.out.println(AuthenticationToken.getInstance().authenticate(token) + " 30 sec");
//
//        Thread.sleep(25000);
//        System.out.println(AuthenticationToken.getInstance().authenticate(token) + " 55 sec");
//
//        Thread.sleep(10000);
//        System.out.println(AuthenticationToken.getInstance().authenticate(token) + " 65 sec");
//
//        Thread.sleep(25000);
//        System.out.println(AuthenticationToken.getInstance().authenticate(token) + " 180 sec");


        //System.out.println(AuthenticationToken.getInstance().checkTimestamp(new Timestamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))));


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());
        //DBMatch.getMatch(10, 358, ConnectionPool.getInstance().getDataSource()).printBoard();


        Server.start();



//        StartMenu startMenu = new StartMenu();
//        startMenu.openMenu();





    }

}
