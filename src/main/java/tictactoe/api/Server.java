package tictactoe.api;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import tictactoe.api.account.LoginController;
import tictactoe.api.analyze.AnalyzeController;
import tictactoe.api.match.MatchController;
import tictactoe.api.score.ScoreController;

public class Server {

    public static void start() throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        LoginController.endPoint(server);
        MatchController.endPoint(server);
        ScoreController.endPoint(server);
        AnalyzeController.endPoint(server);

        server.setExecutor(null);
        server.start();
    }

}
