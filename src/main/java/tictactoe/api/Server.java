package tictactoe.api;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import tictactoe.api.account.LoginController;
import tictactoe.api.analyze.AnalyzeController;
import tictactoe.api.match.MatchController;
import tictactoe.api.score.ScoreController;

public class Server {


    public static HttpServer start(HikariDataSource dataSource) throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        LoginController.endPoint(server, dataSource);
        MatchController.endPoint(server, dataSource);
        ScoreController.endPoint(server, dataSource);
        AnalyzeController.endPoint(server, dataSource);

        server.setExecutor(null);
        server.start();
        return server;
    }

    public static void close(HttpServer server) throws InterruptedException {
        server.stop(0);
    }

}
