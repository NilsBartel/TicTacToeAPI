package tictactoe.database;

import tictactoe.board.*;

import com.zaxxer.hikari.HikariDataSource;
import tictactoe.board.Board;
//import tictactoe.game.Match;
import tictactoe.game.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBMatch {

    public static void insertNewMatch(Match match, int userID, HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {

            String sql = "INSERT INTO match(difficulty, status, isplayerturn, user_id) VALUES(?, ?, ?, ?) returning match_id";
            int match_id = 0;
            try(PreparedStatement prepStmt = connection.prepareStatement(sql)) {

                prepStmt.setString(1, match.getDifficulty().toString());
                prepStmt.setString(2, match.getStatus().toString());
                prepStmt.setString(2, match.getStatus().toString());
                prepStmt.setBoolean(3, match.isIsPlayerTurn());
                prepStmt.setInt(4, userID);
                prepStmt.execute();

                ResultSet resultSet = prepStmt.getResultSet();
                if (resultSet.next()) {
                    match_id = resultSet.getInt(1);
                }
            }

            sql = "INSERT INTO board(match_id) VALUES(?) returning board_id";
            int board_id = 0;
            try(PreparedStatement prepStmt = connection.prepareStatement(sql)) {
                prepStmt.setInt(1, match_id);
                prepStmt.execute();

                ResultSet resultSet = prepStmt.getResultSet();
                if (resultSet.next()) {
                    board_id = resultSet.getInt(1);
                }
            }

            sql = "INSERT INTO row(row, board_id) VALUES(?, ?) returning row_id";
            int[] rows = {0, 0, 0};
            for(int i = 0; i < 3; i++){
                try(PreparedStatement prepStmt = connection.prepareStatement(sql)) {
                    prepStmt.setInt(1, i);
                    prepStmt.setInt(2, board_id);
                    prepStmt.execute();

                    ResultSet resultSet = prepStmt.getResultSet();
                    if (resultSet.next()) {
                        rows[i] = resultSet.getInt(1);
                    }
                }
            }

            sql = "INSERT INTO field(field, symbol, row_id) VALUES(?, ?, ?)";
            char symbol = ' ';
            Board board = match.getBoard();


            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    try(PreparedStatement prepStmt = connection.prepareStatement(sql)) {
                        prepStmt.setInt(1, j);
                        //prepStmt.setString(2, String.valueOf(symbol));
                        prepStmt.setString(2, String.valueOf(board.getSymbol(i, j)));
                        prepStmt.setInt(3, rows[i]);
                        prepStmt.executeUpdate();
                    }
                }
            }

            sql = "INSERT INTO time(starttime, endtime, match_id) values(?, ?, ?)";
            try(PreparedStatement prepStmt = connection.prepareStatement(sql)) {
                prepStmt.setTimestamp(1, match.getStartTime());
                prepStmt.setTimestamp(2, match.getEndTime());
                prepStmt.setInt(3, match_id);
                prepStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static List<Match> getAllMatchesFromUser(int userID, HikariDataSource dataSource) {
        List<Match> matches = new ArrayList<>();

        String sql = "SELECT * FROM match LEFT JOIN time ON match.match_id = time.match_id WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(sql)
        ) {
            prepStatement.setInt(1, userID);
            ResultSet resultSet = prepStatement.executeQuery();
            while (resultSet.next()) {
                Match match = new Match();
                int matchID = resultSet.getInt("match_id");
                match.setDifficulty(DifficultyState.valueOf(resultSet.getString("difficulty")));
                match.setStatus(MatchStatus.valueOf(resultSet.getString("status")));
                match.setIsPlayerTurn(resultSet.getBoolean("isplayerturn"));
                match.setStartTime(resultSet.getTimestamp("starttime"));
                match.setEndTime(resultSet.getTimestamp("endtime"));

                match.setBoard(getBoard(matchID, dataSource));

                matches.add(match);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return matches;
    }



    public static List<Match> getLastNMatchesFromUser(int userID, int n, HikariDataSource dataSource) {
        List<Match> matches = new ArrayList<>();

        String sql = "SELECT * FROM match LEFT JOIN time ON match.match_id = time.match_id WHERE match.user_id = ? ORDER BY match.match_id DESC LIMIT ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(sql)
        ) {
            prepStatement.setInt(1, userID);
            prepStatement.setInt(2, n);
            ResultSet resultSet = prepStatement.executeQuery();
            while (resultSet.next()) {
                Match match = new Match();
                int matchID = resultSet.getInt("match_id");
                match.setDifficulty(DifficultyState.valueOf(resultSet.getString("difficulty")));
                match.setStatus(MatchStatus.valueOf(resultSet.getString("status")));
                match.setIsPlayerTurn(resultSet.getBoolean("isplayerturn"));
                match.setStartTime(resultSet.getTimestamp("starttime"));
                match.setEndTime(resultSet.getTimestamp("endtime"));

                match.setBoard(getBoard(matchID, dataSource));

                matches.add(match);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return matches;
    }




    public static Board getBoard(int matchID, HikariDataSource dataSource) {
        String sql = "select * from board left join public.row r on board.board_id = r.board_id left join public.field f on r.row_id = f.row_id WHERE board.match_id = ?";
        Board board = new Board();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sql)
        ) {
            prepStmt.setInt(1, matchID);
            ResultSet resultSet = prepStmt.executeQuery();
            while (resultSet.next()) {

                char Symbol = resultSet.getString("symbol").charAt(0);
                int row = resultSet.getInt("row");
                int column = resultSet.getInt("field");
                board.setSymbol(row, column, Symbol);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return board;
    }
}
