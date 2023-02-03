package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;

public class TodosTest {

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
        Response response = ap.post("http://localhost:4567/todos", "json", js);

        String res = null;

        try {
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            System.out.println(res);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(res);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }
}
