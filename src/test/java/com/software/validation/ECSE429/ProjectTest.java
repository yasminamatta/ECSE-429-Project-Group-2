package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.boot.test.context.SpringBootTest;

public class ProjectTest {
    
    @Test
    public void getProject() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int size = ((JSONArray) jsonObject.get("projects")).size();
        Assert.assertEquals(2, size);
    }
    
    @Test
    public void headProject() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @Test
    public void postProject() {
        APICall apiCall = new APICall();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "test");
        jsonObject.put("description", "test");
        Response response = apiCall.post("projects", "json", jsonObject);
       
        JSONParser jsonParser = new JSONParser();
        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            JSONObject jsonObjectPost = (JSONObject) jsonParser.parse(responsePost);
            Assert.assertEquals("test", jsonObjectPost.get("title"));
            Assert.assertEquals("test", jsonObjectPost.get("description"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getProjectById() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects/6", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals("test", ((JSONObject) ((JSONArray)(jsonObject.get("projects"))).get(0)).get("title"));
        Assert.assertEquals("test", ((JSONObject) ((JSONArray) (jsonObject.get("projects"))).get(0)).get("description"));
    }



}
