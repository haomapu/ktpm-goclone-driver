package com.example.ktpm_goclone_driver;

import okhttp3.*;
import java.io.IOException;

public class ApiCaller {
    private String BASE_URL = "https://ktpm-goride.onrender.com";
    private static ApiCaller instance;
    private OkHttpClient client;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    // Private constructor to prevent direct instantiation
    private ApiCaller() {
        client = new OkHttpClient();
    }

    // Static method to get the singleton instance
    public static synchronized ApiCaller getInstance() {
        if (instance == null) {
            instance = new ApiCaller();
        }
        return instance;
    }

    public void post(String path, String req, Callback callback, String header) {
        RequestBody body = RequestBody.create(JSON, req);
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + path)
                .post(body);

        if (!header.equalsIgnoreCase("None")) {
            requestBuilder.addHeader("Authorization", "Bearer " + header);
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public void get(String path, Callback callback, String header) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + path)
                .get();

        if (!header.equalsIgnoreCase("None")) {
            requestBuilder.addHeader("Authorization", "Bearer " + header);
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void patch(String path, String req, Callback callback, String header) {
        RequestBody body = RequestBody.create(JSON, req);

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + path)
                .patch(body);

        if (!header.equalsIgnoreCase("None")) {
            requestBuilder.addHeader("Authorization", "Bearer " + header);
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}

