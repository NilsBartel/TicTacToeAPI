package tictactoe.api.documentation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import tictactoe.api.errors.ErrorHandler;
import tictactoe.api.errors.MethodNotAllowed;
import tictactoe.util.MavenCommandExecutor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocumentationController {

    public static void endPoint(HttpServer server, HikariDataSource dataSource) {
        ErrorHandler errorHandler = new ErrorHandler();

        server.createContext("/documentation", exchange ->
                Try.run(() -> handleDocumentation(exchange, dataSource))
                        .onFailure(t -> {
                            errorHandler.handle(t, exchange);
                        })
        );
    }



    static void handleDocumentation(HttpExchange exchange, HikariDataSource dataSource) throws MethodNotAllowed, IOException {
        Path documentationPath = Path.of("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/target/openapi-docs/index.html");

        if (!Files.exists(documentationPath)) {
            MavenCommandExecutor.mvnCleanCompile();
        }

        if (exchange.getRequestMethod().equals("GET")) {
            byte[] htmlContent = Files.readAllBytes(Paths.get(documentationPath.toString()));

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, 0);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(htmlContent);
            responseBody.flush();
            responseBody.close();

        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method " + exchange.getRequestMethod() + " not allowed for " + exchange.getRequestURI());
        }
    }



}
