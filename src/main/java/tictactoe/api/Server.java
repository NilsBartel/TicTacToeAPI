package tictactoe.api;

import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import tictactoe.api.account.LoginController;
import tictactoe.api.analyze.AnalyzeController;
import tictactoe.api.match.MatchController;
import tictactoe.api.score.ScoreController;
import tictactoe.database.ConnectionPool;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private HttpServer server;

    public Server() {
    }

    public void start(HikariDataSource datasource) throws IOException {
        int serverPort = 8080;
        this.server = HttpServer.create(new InetSocketAddress(serverPort), 0);


        LoginController loginController = new LoginController(server, datasource);
        MatchController matchController = new MatchController(server, datasource);
        ScoreController scoreController = new ScoreController(server, datasource);
        AnalyzeController analyzeController = new AnalyzeController(server, datasource);

        loginController.endPoint();
        matchController.endPoint();
        scoreController.endPoint();
        analyzeController.endPoint();



        server.setExecutor(null);
        server.start();
    }

    public void close() {
        this.server.stop(1);


    }




}
