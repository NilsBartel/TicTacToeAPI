package tictactoe.api.account;

import net.bytebuddy.utility.RandomString;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Authentication {

    private static Authentication instance;
    private Authentication() {}
    public static Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    private final int TOKEN_LENGTH = 32;
    private final int TOKEN_LIFETIME = 60;  // in minutes
    private final Map<String, Map.Entry<Integer, Timestamp>> authMap = new HashMap<>();








    public String create(int userID){
        String token = RandomString.make(TOKEN_LENGTH);
        authMap.put(token, Map.entry(userID, new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(TOKEN_LIFETIME))));
        return token;
    }


    public boolean authenticate(String authToken){
        return authMap.containsKey(authToken) && timestampValid(authMap.get(authToken).getValue());
        //return authMap.containsKey(authToken) && authMap.get(authToken).getValue().before(new Timestamp(System.currentTimeMillis()));

    }

    private boolean timestampValid(Timestamp timestamp){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now.before(timestamp);
    }

    //TODO: clean up map






    public Map<String, Map.Entry<Integer, Timestamp>> getAuthMap() {
        return authMap;
    }

    public int getUserID(String authToken){
        return authMap.get(authToken).getKey();
    }
}
