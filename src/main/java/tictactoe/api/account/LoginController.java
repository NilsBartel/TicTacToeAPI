package tictactoe.api.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariDataSource;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;
import tictactoe.api.AuthenticationToken;
import tictactoe.api.errors.ErrorHandler;
import tictactoe.api.errors.InputError;
import tictactoe.api.errors.LoginError;
import tictactoe.api.errors.MethodNotAllowed;
import tictactoe.database.DBUser;
import tictactoe.login.LogIn;
import tictactoe.login.PasswordUtil;
import tictactoe.user.User;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LoginController {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HikariDataSource dataSource;
    private final HttpServer server;

    public LoginController(HttpServer server, HikariDataSource dataSource) {
        this.server = server;
        this.dataSource = dataSource;
    }


    public void endPoint() {
        ErrorHandler errorHandler = new ErrorHandler();

        server.createContext("/account/login", exchange ->
                Try.run(() -> handleLogin(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

        server.createContext("/account/register", exchange ->
                Try.run(() -> handleRegister(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );

        server.createContext("/account/resetPassword", exchange ->
                Try.run(() -> handlePasswordReset(exchange))
                        .onFailure(t -> {errorHandler.handle(t, exchange);})
        );
    }


    private void handleLogin(HttpExchange exchange) throws IOException, MethodNotAllowed, LoginError {
        LoginResponse loginResponse = new LoginResponse();


        if (exchange.getRequestMethod().equals("POST")) {
            User user = null;
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                user = objectMapper.readValue(requestBody, User.class);
            } catch (JsonProcessingException e) {
                throw new InputError("Invalid request body");
            }

            LogIn.getInstance().logInUser(user.getUserName(), user.getPassword(), dataSource);
            String authToken = AuthenticationToken.getInstance().create(DBUser.getUserId(user.getUserName(), dataSource));
            loginResponse.setMessage("logged in successfully");
            loginResponse.setToken(authToken);

            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        responseBody.flush();
        responseBody.close();
    }


    private void handleRegister(HttpExchange exchange) throws IOException, MethodNotAllowed, LoginError {
        LoginResponse loginResponse = new LoginResponse();

        if (exchange.getRequestMethod().equals("POST")) {
            User user = null;
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                user = objectMapper.readValue(requestBody, User.class);
            } catch (JsonProcessingException e) {
                throw new LoginError("Invalid request body");
            }

            LogIn.getInstance().createUser(user, dataSource);

            loginResponse.setMessage("account creation successful");
            exchange.sendResponseHeaders(200, 0);
        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        responseBody.flush();
        responseBody.close();
    }

    private void handlePasswordReset(HttpExchange exchange) throws IOException, MethodNotAllowed, LoginError {
        LoginResponse loginResponse = new LoginResponse();

        if (exchange.getRequestMethod().equals("POST")) {
            User user = null;
            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            try {
                user = objectMapper.readValue(requestBody, User.class);
            } catch (JsonProcessingException e) {
                throw new InputError("Invalid request body");
            }
            int userID = DBUser.getUserId(user.getUserName(), dataSource);

            PasswordUtil.checkSecurityQuestions(userID, user, dataSource);
            if (PasswordUtil.isPasswordValid(user.getPassword())) {
                PasswordUtil.resetPassword(userID, user, dataSource);

                loginResponse.setMessage("password reset successful");
                exchange.sendResponseHeaders(200, 0);
            } else {
                throw new LoginError("password is too weak");
            }

        } else {
            throw new MethodNotAllowed("Method "+ exchange.getRequestMethod() +" not allowed for "+ exchange.getRequestURI());
        }

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(objectMapper.writeValueAsBytes(loginResponse));
        responseBody.flush();
        responseBody.close();
    }







}
