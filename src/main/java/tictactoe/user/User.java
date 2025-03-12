package tictactoe.user;

public class User {
    private String userName;
    private String password;
    private String answer1;
    private String answer2;

    public User(String userName, String password, String answer1, String answer2) {
        this.userName = userName;
        this.password = password;
        this.answer1 = answer1;
        this.answer2 = answer2;
    }
    public User(){
    }
    

    public String getUserName() {
        return userName;
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

    public String getAnswer2() {
        return answer2;
    }
}
