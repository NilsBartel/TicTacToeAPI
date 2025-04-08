package tictactoe.util;

import java.io.IOException;

public class MavenCommandExecutor {


    public static void mvnCleanCompile() {
        try {
            String command = "mvn clean compile";

            Process process = Runtime.getRuntime().exec(command, null, new java.io.File("/Users/nilsbartel/IdeaProjects/TicTacToeAPI"));
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
