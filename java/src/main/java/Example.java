import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.Arrays;

public class Example {

    public static void main(String[] args) throws IOException {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        String urlBase = "http://linkurious.example.org:3000";
        String username = "user@example.org";
        String password = "password123";

        // get the status (public API)
        HttpGet getStatus = new HttpGet(urlBase + "/api/status");
        CloseableHttpResponse statusResponse = client.execute(getStatus);
        printResponse("Status", statusResponse);

        // get the current user (will fail: requires authentication)
        HttpGet getUser = new HttpGet(urlBase + "/api/auth/me");
        CloseableHttpResponse userResponse = client.execute(getUser);
        printResponse("Current user (1)", userResponse);

        // authenticate
        HttpPost postLogin = new HttpPost(urlBase + "/api/auth/login");
        postLogin.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("usernameOrEmail", username),
                new BasicNameValuePair("password", password)
        )));
        CloseableHttpResponse loginResponse = client.execute(postLogin);
        printResponse("Login", loginResponse);

        // get the current user again (check that authentication actually worked)
        HttpGet getUser2 = new HttpGet(urlBase + "/api/auth/me");
        CloseableHttpResponse userResponse2 = client.execute(getUser2);
        printResponse("Current user (2)", userResponse2);

        client.close();
    }

    private static void printResponse(String title, HttpResponse r) throws IOException {
        System.out.println(
                title + ": [" + r.getStatusLine().getStatusCode() + "] " + EntityUtils.toString(r.getEntity())
        );
    }
}
