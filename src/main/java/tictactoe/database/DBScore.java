package tictactoe.database;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.game.Score;

import java.sql.*;

public class DBScore {
    public static void insertEmptyScore(int userID, HikariDataSource dataSource) {
        String sql = "INSERT INTO score (player, computer, draw, user_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(sql)
        ) {
            prepStatement.setInt(1, 0);
            prepStatement.setInt(2, 0);
            prepStatement.setInt(3, 0);
            prepStatement.setInt(4, userID);
            prepStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateScore(String column, int userID, HikariDataSource dataSource) {

        if (!scoreExists(userID, dataSource)) {
            insertEmptyScore(userID, dataSource);
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            String sql = "UPDATE score SET "+column+" = "+column+" +1 WHERE user_id = "+userID+";";

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Score getScore(int userID, HikariDataSource dataSource) {
        int player = 0;
        int computer = 0;
        int draw = 0;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT player, computer, draw FROM score WHERE user_id = "+ userID +" ")
        ) {
            while (resultSet.next()) {
                player = resultSet.getInt("player");
                computer = resultSet.getInt("computer");
                draw = resultSet.getInt("draw");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Score(player, computer, draw);
    }

    private static boolean scoreExists(int userID, HikariDataSource dataSource) {
        boolean bool = false;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT exists(SELECT 1 FROM score WHERE user_id = '"+ userID +"') AS exists ")
        ) {
            while (resultSet.next()) {
                bool = resultSet.getBoolean("exists");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bool;
    }

}
