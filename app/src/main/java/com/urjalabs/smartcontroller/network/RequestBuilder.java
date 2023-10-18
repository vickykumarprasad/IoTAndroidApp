package com.urjalabs.smartcontroller.network;

import com.urjalabs.smartcontroller.util.Constants;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

/**
 * Request builder
 * Created by tarun on 01-11-2017.
 */

public class RequestBuilder {
    //Login request body
    public static RequestBody LoginBody(String username, String password, String token) {
        return new FormBody.Builder()
                .add("action", "login")
                .add("format", "json")
                .add("username", username)
                .add("password", password)
                .add("logintoken", token)
                .build();
    }

    public static HttpUrl buildURL(String scheme, Map<String, String> queryParams, String pathSegment) {

        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(scheme) //http or https
                .host(Constants.HOST_NAME)
                .addPathSegment(pathSegment);//adds "/pathSegment" at the end of hostname
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());//add query parameters to the URL
            }
        }
        //.addEncodedQueryParameter("encodedName", "encodedValue")//add encoded query parameters to the URL
        return builder.build();
        /**
         * The return URL:
         *  https://www.somehostname.com/pathSegment?param1=value1&encodedName=encodedValue
         */
    }

}
