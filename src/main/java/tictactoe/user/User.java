package tictactoe.user;

public class User {


    private String userName;
    private String password;
    private String answer1;
    private String answer2;
    private static final String QUESTION1 = "Name of your first Pet?";
    private static final String QUESTION2 = "City your were born in?";


    public User(String userName, String password, String answer1, String answer2) {
        this.userName = userName;
        this.password = password;
        this.answer1 = answer1;
        this.answer2 = answer2;
    }
    public User(){
    }
    





    public static String getQUESTION1() {
        return QUESTION1;
    }

    public static String getQUESTION2() {
        return QUESTION2;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }
}
