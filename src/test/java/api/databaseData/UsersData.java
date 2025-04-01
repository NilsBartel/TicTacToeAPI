package api.databaseData;

import tictactoe.user.User;


public class UsersData {

    private static final User user1 = new User("test", "test", "test", "test");
    private static final User user2 = new User("nils", "Password123.", "hamburg", "bruno");
    private static final User user3 = new User("robert", "RobertsPassword345!", "berlin", "WauWau");
    private static final User user4 = new User("bruno", "Password123!", "berlin", "hundi");
    private static final User user5 = new User("history", "Password123!", "berlin", "hundi");




    public static User getUser1() {
        return user1;
    }

    public static User getUser2() {
        return user2;
    }

    public static User getUser3() {
        return user3;
    }

    public static User getUser4() {
        return user4;
    }

    public static User getUser5() { return user5; }
}

