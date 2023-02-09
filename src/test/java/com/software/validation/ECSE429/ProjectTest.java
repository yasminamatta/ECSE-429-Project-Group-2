package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.jupiter.api.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectTest {



    @Test
    @Order(1)
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
        Assert.assertEquals(1, size);
    }

    @Test
    @Order(2)
    public void headProject() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @Test
    @Order(3)
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
    @Order(4)
    public void getProjectById() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects/1", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals("Office Work", ((JSONObject) ((JSONArray) (jsonObject.get("projects"))).get(0)).get("title"));
        Assert.assertEquals("",
                ((JSONObject) ((JSONArray) (jsonObject.get("projects"))).get(0)).get("description"));
    }

    @Test
    @Order(5)
    public void headProjectById() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @Test
    @Order(6)
    public void postProjectById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("title", "changed");
        jsonBody.put("description", "changed");
        Response response = apiCall.post("projects/1", "json", jsonBody);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("changed", jsonObject.get("title"));
        assertEquals("changed", jsonObject.get("description"));
        assertEquals(2, ((JSONArray) jsonObject.get("tasks")).size());
        assertEquals(200, response.code());
    }

    @Test
    @Order(7)
    public void putProjectById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("title", "updated");
        Response response = apiCall.put("projects/1", "json", jsonBody);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("updated", jsonObject.get("title"));
        assertEquals("", jsonObject.get("description"));
        assertEquals(200, response.code());
    }

    @Test
    @Order(8)
    public void deleteProjectById() {
        APICall apiCall = new APICall();
        Response response = apiCall.delete("projects/2", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(200, response.code());
        Response response1 = apiCall.get("projects", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try{
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        }catch (Exception e){
            e.printStackTrace();
        }
        assertEquals(1, ((JSONArray) jsonObject1.get("projects")).size());
    }

    @Test
    @Order(9)
    public void testJ_getProjectCategoriesById() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects/1/categories", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int size = ((JSONArray) jsonObject.get("categories")).size();
        Assert.assertEquals(0, size);
        assertEquals(200, response.code());
    }

    @Test
    @Order(10)
    public void testK_headProjectCategoriesById() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1/categories", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @Test
    @Order(11)
    public void testL_postProjectCategoriesById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", "1");
        Response response = apiCall.post("projects/1/categories", "json", jsonBody);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }


        assertEquals(201, response.code());
        // assertEquals("1", ((JSONObject)((JSONArray) jsonObject.get("categories")).get(0)).get("id"));
        Response response1 = apiCall.get("projects/1/categories", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, ((JSONArray) jsonObject1.get("categories")).size());
    }

    @Test
    @Order(12)
    public void deleteProjectCategoriesById() {
        APICall apiCall = new APICall();
        Response response = apiCall.delete("projects/1/categories/1", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(200, response.code());
        Response response1 = apiCall.get("projects/1/categories", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try{
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        }catch (Exception e){
            e.printStackTrace();
        }
        assertEquals(0, ((JSONArray) jsonObject1.get("categories")).size());
    }

    @Test
    @Order(13)
    public void postProjectByIdTasks() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", "2");
        Response response = apiCall.post("projects/1/tasks", "json", jsonBody);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(201, response.code());
        Response response1 = apiCall.get("projects/1/tasks", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, ((JSONArray) jsonObject1.get("todos")).size());
    }

    @Test
    @Order(14)
    public void getProjectByIdTasks() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects/1/tasks", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int size = ((JSONArray) jsonObject.get("todos")).size();
        Assert.assertEquals(1, size);
        assertEquals(200, response.code());
    }

    @Test
    @Order(15)
    public void headProjectByIdTasks() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1/tasks", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @Test
    @Order(16)
    public void deleteProjectByIdTasks() {
        APICall apiCall = new APICall();
        Response response = apiCall.delete("projects/1/tasks/2", "json");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(200, response.code());
        Response response1 = apiCall.get("projects/1/tasks", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try{
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        }catch (Exception e){
            e.printStackTrace();
        }
        assertEquals(0, ((JSONArray) jsonObject1.get("todos")).size());
    }


}
