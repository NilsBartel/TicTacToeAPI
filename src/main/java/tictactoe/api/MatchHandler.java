package tictactoe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tictactoe.api.account.Authentication;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBMatch;
import tictactoe.game.Match;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MatchHandler extends Handler {


    public static void handle(HttpExchange exchange) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        MatchResponse response = new MatchResponse();
        if (exchange.getRequestMethod().equals("GET")) {




            String token = exchange.getRequestHeaders().get("token").getFirst();
            if (!Authentication.getInstance().authenticate(token)) {
                response = new MatchResponse("Invalid token");
                exchange.sendResponseHeaders(401, 0);

                byte[] responseByte = mapper.writeValueAsBytes(response);
                OutputStream output = exchange.getResponseBody();
                output.write(responseByte);
                output.flush();

                exchange.close();
                return;
            }

            System.out.println(exchange.getRequestURI().getPath().endsWith("/match"));

            String lastUrlString = splitPath(exchange.getRequestURI().getPath()).getLast();
            int userID = Authentication.getInstance().getUserID(token);


            switch (lastUrlString) {
                case "history" -> {
                    List<Match> history = DBMatch.getAllMatchesFromUser(userID, ConnectionPool.getInstance().getDataSource());
                    response.setMatches(history);
                }
            }






//            Headers headers = exchange.getRequestHeaders();
//            token = headers.get("token").getFirst();
            System.out.println(token);
            Boolean authentication = Authentication.getInstance().authenticate(token);
            System.out.println(authentication);

//            System.out.println(Authentication.getInstance().getAuthMap().get(token).getKey());
//            System.out.println(Authentication.getInstance().getUserID(token));
//            if (lastUrlString.equals("lastBoard")) {
//
//
//
//            }
            exchange.sendResponseHeaders(200, 0);


            response.setMessage("valid token");


        } else {
            System.out.println("invalid request method");
            response = new MatchResponse("invalid request method");
            exchange.sendResponseHeaders(400, 0);
        }

        byte[] responseByte = mapper.writeValueAsBytes(response);
        OutputStream output = exchange.getResponseBody();
        output.write(responseByte);
        output.flush();

        exchange.close();

    }






}
