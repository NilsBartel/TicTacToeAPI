package tictactoe.api.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import tictactoe.api.LoginHandler;
import tictactoe.database.ConnectionPool;
import tictactoe.database.DBUser;
import tictactoe.login.LogIn;
import tictactoe.login.PasswordUtil;
import tictactoe.user.User;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LoginController {
    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void endPoint(HttpServer server) {

        server.createContext("/account/login", (LoginController::handleLogin));
        server.createContext("/account/register", (LoginController::handleRegister));
        server.createContext("/account/resetPassword", (LoginController::handlePasswordReset));
    }


    private static void handleLogin(HttpExchange exchange) throws IOException {
        LoginResponse loginResponse = new LoginResponse();


        if (exchange.getRequestMethod().equals("POST")) {
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            User user = objectMapper.readValue(requestBody, User.class);

            if (LogIn.getInstance().logInUser(user.getUserName(), user.getPassword(), ConnectionPool.getInstance().getDataSource())) {
                String authToken = Authentication.getInstance().create(DBUser.getUserId(user.getUserName(), ConnectionPool.getInstance().getDataSource()));
                loginResponse.setMessage("logged in successfully");
                loginResponse.setToken(authToken);

                exchange.sendResponseHeaders(200, 0);
            } else {
                loginResponse.setMessage("Invalid username or password");
                exchange.sendResponseHeaders(400, 0);
            }

        } else {
            loginResponse.setMessage("Invalid request method");
            exchange.sendResponseHeaders(400, 0);
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        responseBody.flush();
        responseBody.close();
    }


    private static void handleRegister(HttpExchange exchange) throws IOException {
        LoginResponse loginResponse = new LoginResponse();

        if (exchange.getRequestMethod().equals("POST")) {
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            User user = objectMapper.readValue(requestBody, User.class);

            if (LogIn.getInstance().createUser(user, ConnectionPool.getInstance().getDataSource())) {
                loginResponse.setMessage("account creation successful");

                exchange.sendResponseHeaders(200, 0);
            } else {
                loginResponse.setMessage("account creation failed");
                exchange.sendResponseHeaders(400, 0);
            }

        } else {
            loginResponse.setMessage("Invalid request method");
            exchange.sendResponseHeaders(400, 0);
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        responseBody.flush();
        responseBody.close();
    }

    private static void handlePasswordReset(HttpExchange exchange) throws IOException {
        LoginResponse loginResponse = new LoginResponse();

        if (exchange.getRequestMethod().equals("POST")) {
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            User user = objectMapper.readValue(requestBody, User.class);
            int userID = DBUser.getUserId(user.getUserName(), ConnectionPool.getInstance().getDataSource());


            if (PasswordUtil.checkSecurityQuestions(userID, user, ConnectionPool.getInstance().getDataSource())) {

                if (PasswordUtil.isPasswordValid(user.getPassword())) {
                    PasswordUtil.resetPassword(userID, user, ConnectionPool.getInstance().getDataSource());
                    loginResponse.setMessage("password reset successful");
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    loginResponse.setMessage("password is invalid (check it has all the needed characters)");
                    exchange.sendResponseHeaders(400, 0);
                }
            } else {
                loginResponse.setMessage("security questions do not match");
                exchange.sendResponseHeaders(400, 0);
            }

        } else {
            loginResponse.setMessage("Invalid request method");
            exchange.sendResponseHeaders(400, 0);
        }
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        responseBody.flush();
        responseBody.close();

    }







}
