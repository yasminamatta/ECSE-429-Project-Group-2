package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectTest {

    @BeforeEach
    public void setupEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            System.out.println("Setting up environment");
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void resetEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("fuser -k 4567/tcp"); // Resets the API environment once testing session is complete.
            System.out.println("Resetting environment");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
        Assert.assertEquals(1, size);
        System.out.println("GET projects -- TEST PASSED");

    }

    @Test
    public void headProject() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
        System.out.println("HEAD projects -- TEST PASSED");
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
        JSONObject jsonObjectPost = null;
        try{
            jsonObjectPost = (JSONObject) jsonParser.parse(responsePost);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("test", jsonObjectPost.get("title"));
        Assert.assertEquals("test", jsonObjectPost.get("description"));
        System.out.println("POST projects -- TEST PASSED");
    }

    @Test
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
        System.out.println("GET projects/:id -- TEST PASSED");
    }

    @Test
    public void headProjectById() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
        System.out.println("HEAD projects/:id -- TEST PASSED");
    }

    @Test
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
        System.out.println("POST projects/:id -- TEST PASSED");
    }

    @Test
    public void putProjectById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("title", "updated");
        jsonBody.put("description", "");
        HashMap tasks = new HashMap();
        tasks.put("id", "1");
        jsonBody.put("tasks", new JSONObject(tasks));
        Response response = apiCall.put("projects/1", "json", jsonBody);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = (JSONArray) jsonObject.get("tasks");
        assertEquals("updated", jsonObject.get("title"));
        assertEquals("", jsonObject.get("description"));
        assertEquals(jsonArray, jsonObject.get("tasks"));
        assertEquals(200, response.code());
        System.out.println("PUT projects/:id -- TEST PASSED");
    }

    @Test
    public void deleteProjectById() {
        
        APICall apiCall = new APICall();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "test");
        jsonObject.put("description", "test");
        Response response1 = apiCall.post("projects", "json", jsonObject);
        assertEquals(201, response1.code());
        JSONParser jsonParser2 = new JSONParser(); 
        JSONObject json = null;
        try{
            json = (JSONObject) jsonParser2.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String id = (String) json.get("id");

        Response response2 = apiCall.delete("projects/" + id + "", "json");
        assertEquals(200, response2.code());

        Response response3 = apiCall.get("projects/" + id + "", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response3.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String error = (String) ((((JSONArray) jsonObject1.get("errorMessages")).get(0)));
        assertEquals("Could not find an instance with projects/"+id, error);
        System.out.println("DELETE projects/:id -- TEST PASSED");
    }

    @Test
    public void getProjectCategoriesById() {
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
        System.out.println("GET projects/:id/categories -- TEST PASSED");
    }

    @Test
    public void headProjectCategoriesById() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1/categories", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
        System.out.println("HEAD projects/:id/categories -- TEST PASSED");
    }

    @Test
    public void postProjectCategoriesById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", "1");
        Response response = apiCall.post("projects/1/categories", "json", jsonBody);

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
        System.out.println("POST projects/:id/categories -- TEST PASSED");
    }

    @Test
    public void deleteProjectCategoriesById() {
        APICall apiCall = new APICall();
        Response response = apiCall.delete("projects/1/categories/1", "json");


        assertEquals(200, response.code());
        Response response1 = apiCall.get("projects/1/categories", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        assertEquals(0, ((JSONArray) jsonObject1.get("categories")).size());
        System.out.println("DELETE projects/:id/categories/:id -- TEST PASSED");
    }
    
    @Test
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
        System.out.println("GET projects/:id/tasks -- TEST PASSED");
    }

    @Test
    public void headProjectByIdTasks() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1/tasks", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code);
        System.out.println("HEAD projects/:id/tasks -- TEST PASSED");
    }

    @Test
    public void postProjectByIdTasks() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", "2");
        Response response = apiCall.post("projects/1/tasks", "json", jsonBody);

        assertEquals(201, response.code());
        Response response1 = apiCall.get("projects/1/tasks", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        assertEquals(2, ((JSONArray) jsonObject1.get("todos")).size());
        System.out.println("POST projects/:id/tasks -- TEST PASSED");
    }

    @Test
    public void deleteProjectByIdTasks() {
        APICall apiCall = new APICall();
        Response response = apiCall.delete("projects/1/tasks/2", "json");
        assertEquals(200, response.code());
        Response response1 = apiCall.get("projects/1/tasks", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try{
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, ((JSONArray) jsonObject1.get("todos")).size());
        System.out.println("DELETE projects/:id/tasks/:id -- TEST PASSED");
    }

    @Test
    public void postProjectJSONMalformed() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", "10");
        jsonBody.put("name", "test");
        jsonBody.put("description", "test");
        Response response = apiCall.post("projects", "json", jsonBody);
        assertEquals(400, response.code());

        Response response1 = apiCall.get("projects/10", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try{
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String error = (String) ((((JSONArray) jsonObject1.get("errorMessages")).get(0)));
        assertEquals("Could not find an instance with projects/10", error);
        System.out.println("POST projects/:id (JSON malformed) -- TEST PASSED");
    }

    @Test
    public void postProjectXML() {
        APICall apiCall = new APICall();
        String xml = "<project><title>ECSE 429</title><description>Software description> </description></project>";
        Response response = null;
        response = apiCall.postXML("projects", "xml", xml);
        assertEquals(201, response.code());
        JSONParser jsonParser2 = new JSONParser(); 
        JSONObject json = null;
        try{
            json = (JSONObject) jsonParser2.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String id = (String) json.get("id");
        Response response1 = apiCall.get("projects/"+id+"", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("ECSE 429", ((JSONObject) ((JSONArray) (jsonObject1.get("projects"))).get(0)).get("title"));
        System.out.println("POST projects (XML) -- TEST PASSED");
    }
    
    @Test
    public void postProjectXMLMalformed() {
        APICall apiCall = new APICall();
        String xml = "<project><id>15</id><name>test</name><description>test</description></project>";
        apiCall.postXML("/projects", "xml", xml);
        Response response = apiCall.postXML("/projects", "xml", xml);
        assertEquals(404, response.code());
        Response response1 = apiCall.get("projects/15", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String error = (String) ((((JSONArray) jsonObject1.get("errorMessages")).get(0)));
        assertEquals("Could not find an instance with projects/15", error);
        System.out.println("POST projects/:id (XML Malformed) -- TEST PASSED");

    }


}
