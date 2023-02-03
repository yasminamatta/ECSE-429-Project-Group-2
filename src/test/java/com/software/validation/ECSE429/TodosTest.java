package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
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

    public static void main(String[] args) {

        TodosTest td = new TodosTest();
        td.test();
    }

    @Test
    public void test() {
        APICall ap = new APICall();

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
            Assert.assertEquals("mcgill", json.get("title"));
            System.out.println("title == mcgill - TEST PASSED");
            Assert.assertEquals("okhttp", json.get("description"));
            System.out.println("description == okhttp - TEST PASSED");
            Assert.assertTrue(Arrays.asList(successCodes).contains(response.code()));
            System.out.println("status code == 200 - TEST PASSED");

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error");
        }

    }
}
