package tictactoe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;
import tictactoe.api.account.Authentication;
import tictactoe.api.account.LoginResponse;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBUser;
import tictactoe.login.LogIn;
import tictactoe.user.User;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LoginHandler extends Handler {


    public static void handle(HttpExchange exchange) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        if (exchange.getRequestMethod().equals("POST")) {
            String lastUrlString = splitPath(exchange.getRequestURI().getPath()).getLast();
            String response = "";
            LoginResponse loginResponse = null;

            switch (lastUrlString) {
                case "login" -> {
                    String jsonString = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    User user = mapper.readValue(jsonString, User.class);

                    if (LogIn.getInstance().logInUser(user.getUserName(), user.getPassword(), ConnectionPool.getInstance().getDataSource())) {
                        String authToken = Authentication.getInstance().create(DBUser.getUserId(user.getUserName(), ConnectionPool.getInstance().getDataSource()));
                        loginResponse = new LoginResponse("logged in successfully", authToken);
                        //response = "logged in successfully, " + authToken + "\n";
                        //response = mapper.writeValueAsString(response);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        //response = "login failed";
                        loginResponse = new LoginResponse("login failed");
                        exchange.sendResponseHeaders(400, 0);
                    }
                }

                case "register" -> {
                    String jsonString = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    User user = mapper.readValue(jsonString, User.class);

                    if (LogIn.getInstance().createUser(user, ConnectionPool.getInstance().getDataSource())) {
                        response = "created user successfully";
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        response = "create failed";
                        exchange.sendResponseHeaders(400, 0);
                    }
                }

                case "reset" -> {
                    String jsonString = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    User user = mapper.readValue(jsonString, User.class);









                    response = "reset";
                    exchange.sendResponseHeaders(200, 0);
                }
                default -> exchange.sendResponseHeaders(404, 0);
            }

            byte[] responseByte = mapper.writeValueAsBytes(loginResponse);

            OutputStream output = exchange.getResponseBody();
            //output.write(loginResponse.toString().getBytes(StandardCharsets.UTF_8));
            output.write(responseByte);
            //output.write("hallo".getBytes(StandardCharsets.UTF_8));
            output.flush();

        } else {
            System.out.println("invalid request method");
            exchange.sendResponseHeaders(400, 0);
        }

        exchange.close();
        System.out.println(Authentication.getInstance().getAuthMap());
    }


//    private static List<String> splitPath(String path) {
//        String[] parts = path.split("/");
//        return new ArrayList<>(Arrays.asList(parts));
//    }

//    private static String buildString(InputStream inputStream) throws IOException {
//        InputStreamReader isr =  new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//        BufferedReader br = new BufferedReader(isr);
//        int b;
//        StringBuilder buf = new StringBuilder(512);
//        while ((b = br.read()) != -1) {
//            buf.append((char) b);
//        }
//        br.close();
//        isr.close();
//
//        return buf.toString();
//    }








}
