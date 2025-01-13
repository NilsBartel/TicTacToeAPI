package tictactoe.login;

public final class LogInOutput {
    private static LogInOutput instance;
    private LogInOutput() {}
    public static LogInOutput getInstance() {
        if (instance == null) {
            instance = new LogInOutput();
        }
        return instance;
    }




    public void printUserNotFound(String userName) {
        System.out.println("User " + userName + " doesn't exist!");
        System.out.println();
    }

    public void createdNewUser(String userName) {
        System.out.println("User " + userName + " has been created!");
    }

    public void printTooManyInvalidTries() {
        System.out.println();
        System.out.println();
        System.out.println("Too many invalid tries!");
    }

    public void triesLeft(int tries) {
        System.out.println("Password does not match, please try again! (tries left: " + tries + ")");
    }

    public void failedReset() {
        System.out.println();
        System.out.println("Failed to reset password!");
    }

    public void correct() {
        System.out.println("answer correct");
    }

    public void incorrect() {
        System.out.println("answer incorrect");
    }




}
