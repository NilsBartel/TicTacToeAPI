package api.databaseData;

import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {




    public static void populateDatabase(PostgreSQLContainer postgres, HikariDataSource dataSource) throws SQLException {
//            String sql = "COPY users FROM 'src/test/java/api/UsersTableData.csv' ( FORMAT CSV, DELIMITER'/' );";
//            try (Connection connection = dataSource.getConnection();
//                 PreparedStatement prepStatement = connection.prepareStatement(sql);
//            ) {
//                prepStatement.executeUpdate();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }

        //TODO: can i use my HikariPool connection?
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            long rowsInserted = new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY users FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/UsersTableData.csv"))
                    );
            System.out.printf("%d row(s) inserted%n", rowsInserted);
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY score FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/ScoreTableData.csv"))
                    );
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY match FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/MatchTableData.csv"))
                    );
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY time FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/TimeTableData.csv"))
                    );
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY board FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/BoardTableData.csv"))
                    );
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY row FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/RowTableData.csv"))
                    );
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY field FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/FieldTableData.csv"))
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
