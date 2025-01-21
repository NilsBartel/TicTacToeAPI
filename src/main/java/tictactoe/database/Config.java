package tictactoe.database;

public class Config {

    private final static String URL = "jdbc:postgresql://localhost:5432/tictactoe";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "mysecretpassword";


    public static String getURL() {
        return URL;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }
}
