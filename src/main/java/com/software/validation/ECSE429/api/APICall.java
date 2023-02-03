package com.software.validation.ECSE429.api;

import okhttp3.*;
import org.json.simple.JSONObject;

import java.io.IOException;

public class APICall {


    public static void main(String[] args) {
        APICall ap = new APICall();


    }

    public Response post(String url, String contentType, JSONObject jsonBody) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        url = "http://localhost:4567/" + url;

        MediaType JSON = MediaType.parse("application/" + contentType + "; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/" + contentType + "; charset=utf-8")
                .post(body)
                .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return null;
        }


        return response;
    }

    public Response get(String url, String contentType) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        url = "http://localhost:4567/" + url;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/" + contentType + "; charset=utf-8")
                .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return null;
        }


        return response;
    }


}