package tictactoe.database;

import tictactoe.game.Match;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.board.Position;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    public static void updateBoard(Match match, int userID, Position position, HikariDataSource dataSource) {
        String sql = "UPDATE field SET symbol = ? WHERE row_id = ( " + // --need char ( x or o or ' ')
                "SELECT row_id FROM row WHERE board_id = ( " +
                "SELECT board_id FROM board WHERE match_id = ( " +
                "SELECT match_id FROM match WHERE user_id = ? ORDER BY match_id DESC LIMIT 1 " + // --need user_id
                ") " +
                ") AND row = ? " + // -- need row ( 0 or 1 or 2)
                ") AND field = ?; "; //-- need field (0 or 1 or 2)

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sql)
        ) {

            char test = match.getBoard().getSymbol(position.getRow(), position.getColumn());
            //TODO: match.getSymbol ?

            prepStmt.setString(1, String.valueOf(test));
            prepStmt.setInt(2, userID);
            prepStmt.setInt(3, position.getRow());
            prepStmt.setInt(4, position.getColumn());
            prepStmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    public static void updateDB_Match(Match match, int userID, HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            int match_id = 0;
            String sql = "UPDATE match SET status = ?, isplayerturn = ? WHERE match_id = (select max(match_id) FROM match WHERE user_id = ?) returning match_id ";
            try (PreparedStatement prepStmt = connection.prepareStatement(sql)) {
                prepStmt.setString(1, match.getStatus().toString());
                prepStmt.setBoolean(2, match.isIsPlayerTurn());
                prepStmt.setInt(3, userID);
                prepStmt.execute();
                ResultSet resultSet = prepStmt.getResultSet();
                if (resultSet.next()) {
                    match_id = resultSet.getInt(1);
                }
            }

            String sql1 = "UPDATE time SET endtime = ? WHERE match_id = ?"; //SET starttime = ? AND
            try (PreparedStatement prepStmt = connection.prepareStatement(sql1)) {
                System.out.println(match.getStartTime());
                //prepStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                //prepStmt.setTimestamp(1, match.getStartTime());
                prepStmt.setTimestamp(1, match.getEndTime());
                prepStmt.setInt(2, match_id);
                prepStmt.executeUpdate();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
