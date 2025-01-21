package tictactoe.api;

import com.sun.net.httpserver.HttpServer;
import tictactoe.api.account.LoginController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {


    public static void start() throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);


        //server.createContext("/api/match")
        //server.createContext("/api/login", (LoginHandler::handle));
        server.createContext("/api/match", (MatchHandler::handle));

        //new LoginController(server);
        //loginController.endPoint();
        LoginController.endPoint(server);




        server.setExecutor(null);
        server.start();
    }




}
