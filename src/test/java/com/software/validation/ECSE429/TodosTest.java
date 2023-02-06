package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TodosTest {

    Integer successCodes[] = {200, 201};
    int todos[] = {0, 0};

    public static void main(String[] args) {

        TodosTest td = new TodosTest();
        td.post();
    }

    @Test
    public void get() {
        APICall ap = new APICall();
        Response response = ap.get("todos", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("todos"))).size();
        Assert.assertEquals(2, size);
        System.out.println("Size: " + size + " - TEST PASSED");

    }

    @Test
    public void head() {
        APICall api = new APICall();
        Response response = api.head("todos", "json");
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size());
        System.out.println("Size: " + headers.size() + " - TEST PASSED");
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());
        System.out.println("Content-Type: " + headers.get("Content-Type").toString() + " - TEST PASSED");

    }

    @Test
    public void post() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("todos", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        JSONObject js = new JSONObject();
        js.put("title", "mcgill");
        js.put("description", "okhttp");
        Response response = ap.post("todos", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);


            Response size = ap.get("todos/"+json.get("id"), "json");
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(size.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }


            Assert.assertEquals("mcgill", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title"));
            System.out.println("title == " + ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title") + " - TEST PASSED");
            Assert.assertEquals("okhttp", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description"));
            System.out.println("description == " + ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description") + " - TEST PASSED");
            Assert.assertEquals("false", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("doneStatus"));
            System.out.println("doneStatus == " + ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("doneStatus") + " - TEST PASSED");
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("id"));
            System.out.println("id == " + ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("id") + " - TEST PASSED");
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code));
            System.out.println("status code == " + code + " - TEST PASSED");

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error");
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = ap.get("todos", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(1, Math.abs(todos[1] - todos[0]));
        System.out.println("only " + Math.abs(todos[1] - todos[0]) + " todo created - TEST PASSED");

    }
}
