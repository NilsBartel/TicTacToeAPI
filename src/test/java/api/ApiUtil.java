package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.commons.io.IOUtils;
import tictactoe.api.account.LoginResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApiUtil {


    public static boolean tryLogIn(String userName, String password) throws IOException {
        String login = "{\"userName\":\"" +userName+ "\", \"password\":\"" +password+ "\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        return (httpResponse.getCode() == 200);
    }

    public static String getToken(String userName, String password) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String login = "{\"userName\":\"" +userName+ "\", \"password\":\"" +password+ "\"}";

        HttpUriRequest request = new HttpPost("http://localhost:8080/account/login");
        request.setEntity(new StringEntity(login));

        CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);


        String entitiystring = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        LoginResponse loginResponse = objectMapper.readValue(entitiystring, LoginResponse.class);
        return loginResponse.getToken();
    }
}
