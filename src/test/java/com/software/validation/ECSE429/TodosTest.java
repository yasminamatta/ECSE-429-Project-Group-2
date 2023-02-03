package com.software.validation.ECSE429;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TodosTest {



    public void test() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse("");
            // System.out.println(json.get(""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
