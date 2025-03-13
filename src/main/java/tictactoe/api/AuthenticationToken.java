package tictactoe.api;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.bytebuddy.utility.RandomString;
import tictactoe.api.errors.LoginError;

public final class AuthenticationToken {

    private final int TOKEN_LENGTH = 32;
    private final int TOKEN_LIFETIME = 60;  // in minutes
    private final Map<String, Map.Entry<Integer, Timestamp>> authMap = new HashMap<>();
    private static AuthenticationToken instance;
    private AuthenticationToken() {

    }
    public static AuthenticationToken getInstance() {
        if (instance == null) {
            instance = new AuthenticationToken();
        }
        return instance;
    }

    public String create(int userID) {
        String token = RandomString.make(TOKEN_LENGTH);
        authMap.put(
            token,
            Map.entry(userID, new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(TOKEN_LIFETIME)))
        );
        return token;
    }


    public void authenticate(String authToken) throws LoginError {
        if (!authMap.containsKey(authToken) || !timestampValid(authMap.get(authToken).getValue())) {
            throw new LoginError("Invalid or expired token");
        }
    }

    private boolean timestampValid(Timestamp timestamp) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now.before(timestamp);
    }

    //TODO: clean up map after certain time (things where the time ha ran out)


    public int getUserID(String authToken){
        return authMap.get(authToken).getKey();
    }
}
