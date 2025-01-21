package tictactoe.api.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tictactoe.api.AuthenticationToken;
import tictactoe.database.DBMatch;

import java.io.IOException;
import java.io.OutputStream;

public class MatchController {

    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void endPoint(HttpServer server) {

        server.createContext("/match/", (MatchController::handleMatch));


    }




    public static void handleMatch(HttpExchange exchange) throws IOException {

        String token = exchange.getRequestHeaders().get("token").getFirst();
        MatchResponse matchResponse = new MatchResponse();

        if (!AuthenticationToken.getInstance().authenticate(token)) {

            matchResponse.setMessage("Invalid token");
            exchange.sendResponseHeaders(401, 0);

            OutputStream output = exchange.getResponseBody();
            output.write(objectMapper.writeValueAsBytes(matchResponse));
            output.flush();
            exchange.close();
            return;
        }

        if (exchange.getRequestMethod().equals("GET")) {
            int matchId = getIDFromPath(exchange.getRequestURI().getPath());
            int userID = AuthenticationToken.getInstance().getUserID(token);

            System.out.println("Match ID: " + matchId);
            System.out.println("User ID: " + userID);

            //TODO: cant get a specific match (need a function)
















            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();







        }


//        OutputStream os = exchange.getResponseBody();
//        os.close();
    }


    private static int getIDFromPath(String path) {
        String[] parts = path.split("/");
        String id = parts[parts.length - 1];
        return Integer.parseInt(id);
    }












}
