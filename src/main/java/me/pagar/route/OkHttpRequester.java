package me.pagar.route;

import lombok.AllArgsConstructor;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class OkHttpRequester implements HttpRequester {

    private OkHttpClient client;
    private String authenticationUsername;
    private String authenticationPassword;

    public OkHttpRequester(OkHttpClient client, String username, String password) {
        this.client = client;
        this.authenticationPassword = password;
        this.authenticationUsername = username;
    }

    public OkHttpRequester(String username, String password) {
        this(new OkHttpClient(), username, password);
    }

    @Override
    public HttpResponse get(String url, String parameters, Map<String, String> headers) throws IOException {
        return doRequest("GET", url, parameters, headers);
    }

    @Override
    public HttpResponse post(String url, String parameters, Map<String, String> headers) throws IOException {
        return doRequest("POST", url, parameters, headers);
    }

    @Override
    public HttpResponse put(String url, String parameters, Map<String, String> headers) throws IOException {
        return doRequest("PUT", url, parameters, headers);
    }

    @Override
    public HttpResponse delete(String url, String parameters, Map<String, String> headers) throws IOException {
        return doRequest("DELETE", url, parameters, headers);
    }

    private HttpResponse doRequest(String method, String url, String body, Map<String, String> headers) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        String basicAuth = Credentials.basic(authenticationUsername, authenticationPassword, Charset.forName("utf-8"));
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .addHeader("Authorization", basicAuth);
        headers.forEach((key, value) -> {
            requestBuilder.addHeader(key, value);
        });

        Request request = requestBuilder.build();

        Response response = this.client.newCall(request).execute();

        String responseBody = response.body().string();
        Integer statusCode = response.code();

        return new OkHttpResponse(responseBody, new HashMap<>(), statusCode);
    }
}
