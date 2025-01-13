package tictactoe;

import tictactoe.api.Server;
import tictactoe.database.*;
import tictactoe.game.*;

import java.io.IOException;

public class Main {




    public static void main(String[] args) throws IOException {


        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Credentials.getURL(), Credentials.getUSERNAME(), Credentials.getPASSWORD());

        Server.start();



//        StartMenu startMenu = new StartMenu();
//        startMenu.openMenu();





    }

}
