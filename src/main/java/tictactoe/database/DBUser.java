package tictactoe.database;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.api.errors.LoginError;
import tictactoe.user.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUser {


    public static void insertUser(User user, HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            String sql = "INSERT INTO users(username, password, answer1, answer2) " +
                              "VALUES ('"+user.getUserName()+"', '"+ user.getPassword() +"', '"+ user.getAnswer1() +"', '"+ user.getAnswer2() +"')";
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userExists(String username, HikariDataSource dataSource) {
        boolean bool = false;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT exists(SELECT 1 FROM users WHERE username = '"+ username +"') AS exists ")
        ) {
            while (resultSet.next()) {
                bool = resultSet.getBoolean("exists");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bool;
    }

    public static String getPassword(String username, HikariDataSource dataSource) {
        String password = "";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT password FROM users WHERE username = '"+ username +"'")
        ) {
            while (resultSet.next()) {
                password = resultSet.getString("password");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return password;
    }

    public static void updatePassword(int userID, String HashedPassword, HikariDataSource dataSource) {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            for (int i = 0; i < 3; i++) {
                String sql =
                        "UPDATE users " +
                                "SET password = '" + HashedPassword + "' " +
                                "WHERE user_id = " + userID + " ";
                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getUserId(String username, HikariDataSource dataSource) throws LoginError {
        int userID = 0;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT users.user_id FROM users WHERE username = '"+ username +"'")

        ) {
            while (resultSet.next()) {
                userID = resultSet.getInt("user_id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (userID == 0) {
            throw new LoginError("Username does not exist");
        }
        return userID;
    }

    public static String getAnswer1(int userID, HikariDataSource dataSource) {
        String answer1 = "";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT answer1 FROM users WHERE user_id = "+ userID +" ")
        ) {
            while (resultSet.next()) {
                answer1 = resultSet.getString("answer1");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return answer1;
    }

    public static String getAnswer2(int userID, HikariDataSource dataSource) {
        String answer2 = "";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT answer2 FROM users WHERE user_id = "+ userID +" ")
        ) {
            while (resultSet.next()) {
                answer2 = resultSet.getString("answer2");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return answer2;
    }

}