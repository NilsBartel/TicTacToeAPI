package tictactoe.api;

import com.sun.net.httpserver.HttpServer;
import tictactoe.api.account.LoginController;
import tictactoe.api.match.MatchController;
import tictactoe.api.score.ScoreController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {


    public static void start() throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);








        LoginController.endPoint(server);
        MatchController.endPoint(server);
        ScoreController.endPoint(server);




        server.setExecutor(null);
        server.start();
    }




}
