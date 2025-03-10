package api.databaseData;

import tictactoe.user.User;


public class UsersData {

    private static final User user1 = new User("test", "test", "test", "test");
    private static final User user2 = new User("nils", "Password123.", "hamburg", "bruno");
    private static final User user3 = new User("robert", "RobertsPassword345!", "berlin", "WauWau");



    public static User getUser1() {
        return user1;
    }

    public static User getUser2() {
        return user2;
    }

    public static User getUser3() {
        return user3;
    }
}
