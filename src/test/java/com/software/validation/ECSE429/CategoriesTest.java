package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class CategoriesTest {

    Integer successCodes[] = {200, 201}; // HTML success codes for OK and CREATE
    int categories[] = {0, 0}; // empty categories array to be used throughout

    @Test
    public void getCategory() {
        APICall ap = new APICall();
        Response response = ap.get("categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); // get categories as a response
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(2, size); // API initially has 2 categories loaded in as default.
        System.out.println("GET categories - TEST PASSED");
    }

    @Test
    public void headCategory() {
        APICall api = new APICall();
        Response response = api.head("categories", "json");
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // 4 headers exist. Check that 4 are returned as response
        Assert.assertEquals("application/json", headers.get("Content-Type").toString()); // checks that output is of JSON format
        System.out.println("HEAD categories - TEST PASSED");
    }

    @Test
    public void postCategory() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {

        }

        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", "School");
        js.put("description", "xyz");
        Response response = ap.post("categories", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);

            Response size = ap.get("categories/"+json.get("id"), "json");
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(size.body().string()); // parse body into created JSON object
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check that the id and the body of the created post both match
            Assert.assertEquals("School", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("title"));
            Assert.assertEquals("xyz", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("description"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check that status code is either OK (200) or CREATED (201)

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error");
        }
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size(); // add the new number of categories to array
            }
        });

        t2.start(); // second get method to check that a category was added
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(1, Math.abs(categories[1] - categories[0])); // check that only 1 category was added
        System.out.println("POST categories - TEST PASSED");
    }

    @Test
    public void getCategoryWithId() {
        APICall ap = new APICall();
        Response response = ap.get("categories/1", "json"); // ID present in URL
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(1, size);

        // get contents of the body in string format
        String id = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("id");
        String title = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("title");
        String description = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("description");

        // check if the body matches the query
        Assert.assertEquals("1", id);
        Assert.assertEquals("Office", title);
        Assert.assertEquals("", description);
        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        System.out.println("GET categories/:id -- TEST PASSED");
    }

    @Test
    public void headCategoryWithId() {
        APICall api = new APICall();
        Response response = api.head("categories/2", "json"); // query done with ID in URL
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok

        System.out.println("HEAD categories/:id -- TEST PASSED");
    }

    @Test
    public void postCategoryWithId() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }

        JSONObject js = new JSONObject(); // creating new JSON object with updated body
        js.put("title", "School");
        js.put("description", "updated category");
        Response response = ap.post("categories/1", "json", js); // posting to already existing category with ID=1.

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);

            Response res = ap.get("categories/"+json.get("id"), "json"); // getting all the categories again to see if POST worked
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(res.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check if the new body is now with the category with the given ID
            Assert.assertEquals("School", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("title"));
            Assert.assertEquals("updated category", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("description"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML response is ok

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }
        Assert.assertEquals(0, Math.abs(categories[1] - categories[0])); // check that no new category was created. Body of one category should've been overwritten
        System.out.println("POST categories/:id -- TEST PASSED");
    }




}



