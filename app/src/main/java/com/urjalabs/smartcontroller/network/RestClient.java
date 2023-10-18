package com.urjalabs.smartcontroller.network;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tarun on 01-11-2017.
 */

public class RestClient {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    //    final static String basic =
//    "Basic " + Base64.encodeToString(Constants.DUMMY_CREDENTIALS[0].getBytes(), Base64.NO_WRAP);
    private static volatile OkHttpClient httpClient;

    //GET network request object
    public static Request Request(OkHttpClient client, HttpUrl url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                //.header("Authorization", basic)
                .build();
        return request;
    }

    //POST network request object
    public static Request POST(HttpUrl url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        return new Request.Builder()
                .url(url)
                //.header("Authorization", basic)
                .post(body)
                .build();
    }
    //POST network request object
    public static Request createDeleteRequest(HttpUrl url) throws IOException {
        return new Request.Builder()
                .url(url)
                .delete()
                .build();
    }
    //POST network request object
    public static Request createLoginRequest(HttpUrl url, String username, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        return new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
    }

    public static OkHttpClient getAuthenticatedClient() {
        //String credential[] = Constants.DUMMY_CREDENTIALS[0].split(":");
        //String username = credential[0];
        //String password = credential[1];
        if (httpClient == null) {
            synchronized (RestClient.class) {
                // build client with authentication information.
                if (httpClient == null) {
                    CookieManager cookieManager = new CookieManager();
                    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                    httpClient = new OkHttpClient.Builder()
                            .cookieJar(new JavaNetCookieJar(cookieManager))
                            .build();
                    //.Builder()
                    //.addInterceptor(new BasicAuthInterceptor(username, password)).build();
                }
            }
        }
        return httpClient;
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
