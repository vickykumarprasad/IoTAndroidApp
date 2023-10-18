package com.urjalabs.smartcontroller.network;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** basic auth handler
 * Created by tarun on 01-11-2017.
 */

public class BasicAuthInterceptor implements Interceptor {
    private String credentials;

    public BasicAuthInterceptor(String user, String password) {
        this.credentials = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // If the request already have an authorization (eg. Basic auth), do nothing
        if (request.header("Authorization") == null) {
            request = request.newBuilder()
                    .addHeader("Authorization", credentials)
                    .build();
        }
        return chain.proceed(request);
    }
}
