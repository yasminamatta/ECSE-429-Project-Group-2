package com.software.validation.ECSE429.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APICall {


    public static void main(String[] args) {
        APICall ap = new APICall();
    }

    public Response post(String url) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        Request request = new Request.Builder()
                .url(url)
                .build();


        return response;
    }

}