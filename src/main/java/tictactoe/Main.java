package tictactoe;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

import tictactoe.api.Server;
import tictactoe.database.*;

import java.io.IOException;

public class Main {




    public static void main(String[] args) throws IOException, InterruptedException {
//        String token =  Authentication.getInstance().create(11);

        //Authentication.getInstance().create(12);

//        System.out.println(Authentication.getInstance().getAuthMap());
//
//
////        Thread.sleep(10000);
////        System.out.println(Authentication.getInstance().authenticate(token) + " 10 sec");
//
//        Thread.sleep(30000);
//        System.out.println(Authentication.getInstance().authenticate(token) + " 30 sec");
//
//        Thread.sleep(25000);
//        System.out.println(Authentication.getInstance().authenticate(token) + " 55 sec");
//
//        Thread.sleep(10000);
//        System.out.println(Authentication.getInstance().authenticate(token) + " 65 sec");
//
//        Thread.sleep(25000);
//        System.out.println(Authentication.getInstance().authenticate(token) + " 180 sec");


        //System.out.println(Authentication.getInstance().checkTimestamp(new Timestamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))));


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());

        Server.start();



//        StartMenu startMenu = new StartMenu();
//        startMenu.openMenu();





    }

}
