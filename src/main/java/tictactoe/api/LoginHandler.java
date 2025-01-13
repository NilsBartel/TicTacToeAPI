package tictactoe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tictactoe.database.ConnectionPool;
import tictactoe.login.LogIn;
import tictactoe.user.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginHandler {


    public static void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equals("POST")) {
            String lastUrlString = splitPath(exchange.getRequestURI().getPath()).getLast();
            String response = "";

            if (lastUrlString.equals("login")) {
                response = "login";
                String jsonString = buildString(exchange.getRequestBody());
                ObjectMapper mapper = new ObjectMapper();
                User user = mapper.readValue(jsonString, User.class);
//                System.out.println("username = " + user.getUserName());
//                System.out.println("password = " + user.getPassword());

                if (LogIn.getInstance().logInUser(user.getUserName(), user.getPassword(), ConnectionPool.getInstance().getDataSource())){
                    response = "logged in successfully";
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    response = "login failed";
                    exchange.sendResponseHeaders(400, 0);
                }











                //exchange.sendResponseHeaders(200, 0);
            }else if (lastUrlString.equals("register")) {
                response = "register";
                exchange.sendResponseHeaders(200, 0);
            } else if (lastUrlString.equals("reset")) {
                response = "reset";
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(404, 0);
            }

            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.flush();

        } else {
            System.out.println("invalid request method");
            exchange.sendResponseHeaders(400, 0);
        }

        exchange.close();
    }


    private static List<String> splitPath(String path) {
        String[] parts = path.split("/");
        return new ArrayList<>(Arrays.asList(parts));
    }

    private static String buildString(InputStream inputStream) throws IOException {
        InputStreamReader isr =  new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        br.close();
        isr.close();

        return buf.toString();
    }






}
