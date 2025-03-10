package api.databaseData;

import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import tictactoe.database.LiquibaseMigrationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DatabaseUtil {


    public static void setSeq(HikariDataSource dataSource) {

        String sql = ("BEGIN; " +
                            "SELECT setval('users_user_id_seq', max(user_id)) FROM users;" +
                            "SELECT setval('score_score_id_seq', max(score_id)) FROM score;" +
                            "SELECT setval('match_match_id_seq', max(match_id)) FROM match;" +
                            "SELECT setval('time_time_id_seq', max(time_id)) FROM time;" +
                            "SELECT setval('board_board_id_seq', max(board_id)) FROM board;" +
                            "SELECT setval('row_row_id_seq', max(row_id)) FROM row;" +
                            "SELECT setval('field_field_id_seq', max(field_id)) FROM field;" +
                        "END;"
        );


        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(sql)
        ) {
            prepStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void populateDatabase(PostgreSQLContainer postgres, HikariDataSource dataSource) throws SQLException {

        System.out.println("populating database " + new Timestamp(System.currentTimeMillis()));
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            new CopyManager((BaseConnection) conn)
                    .copyIn(
                            "COPY users FROM STDIN (FORMAT csv, HEADER);",
                            new BufferedReader(new FileReader("/Users/nilsbartel/IdeaProjects/TicTacToeAPI/src/test/java/api/databaseData/UsersTableData.csv"))
                    );
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
        setSeq(dataSource);
        System.out.println("finished populating database " + new Timestamp(System.currentTimeMillis()));

    }

    public static void recreateDatabase(PostgreSQLContainer postgres, HikariDataSource dataSource) throws SQLException {
        wipeDatabase(dataSource);

        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration(dataSource);

        populateDatabase(postgres, dataSource);
    }

    public static void wipeDatabase(HikariDataSource dataSource) {
        String sql = "DROP SCHEMA public CASCADE;";
        String sql2 = "CREATE SCHEMA public;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(sql);
             PreparedStatement prepStatement2 = connection.prepareStatement(sql2)
        ) {
            prepStatement.executeUpdate();
            prepStatement2.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
