package tictactoe;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import tictactoe.api.Server;
import tictactoe.api.errors.MatchError;
import tictactoe.database.*;

import java.io.IOException;

public class Main {




    public static void main(String[] args) throws IOException, InterruptedException {

        ch.qos.logback.classic.Logger hikariLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.ERROR);



        ConnectionPool pool = ConnectionPool.getInstance();
        pool.initPool(Config.getURL(), Config.getUSERNAME(), Config.getPASSWORD());

//        char p = 'o';
//        char c = 'x';
//        char e = ' ';
//
//        Board board = new Board();
//        board.setSymbol(0,0, c);
//        board.setSymbol(0,1, p);
//        board.setSymbol(0,2, 'o');
//
//        board.setSymbol(1,0, c);
//        board.setSymbol(1,1, c);
//        board.setSymbol(1,2,  e);
//
//        board.setSymbol(2,0, e);
//        board.setSymbol(2,1, p);
//        board.setSymbol(2,2, c);
//
//        System.out.println(Winner.thereIsWinner(board, c));

        MatchError matchError = new MatchError("MatchError");



        Server.start();



//        StartMenu startMenu = new StartMenu();
//        startMenu.openMenu();





    }

}
