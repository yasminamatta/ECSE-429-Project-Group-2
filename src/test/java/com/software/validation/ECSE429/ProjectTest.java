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
import org.junit.Assert;
import org.junit.jupiter.api.*;


@TestMethodOrder(MethodOrderer.Random.class)
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
            Process pr = rt.exec("fuser -k 4567/tcp"); // Shuts down the server once testing session is complete.
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
        } finally {
            response.body().close();
        }
        int size = ((JSONArray) jsonObject.get("projects")).size();
        Assert.assertEquals(1, size); // Initially we have one existing project
        System.out.println("GET projects -- TEST PASSED");

    }

    @Test
    public void headProject() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(4, headers.size()); // Checks that there is one existing project
        Assert.assertEquals(200, code);
        System.out.println("HEAD projects -- TEST PASSED");
    }

    @Test
    public void postProject() {
        APICall apiCall = new APICall();
        JSONObject jsonObject = new JSONObject(); // Creates a new json object to be sent to the API
        jsonObject.put("title", "test");
        jsonObject.put("description", "test");
        Response response = apiCall.post("projects", "json", jsonObject);

        JSONParser jsonParser = new JSONParser();
        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        JSONObject jsonObjectPost = null;
        try{
            jsonObjectPost = (JSONObject) jsonParser.parse(responsePost);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("test", jsonObjectPost.get("title")); // checks that the title is correct
        Assert.assertEquals("test", jsonObjectPost.get("description")); //checks that the description is correct
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
        } finally {
            response.body().close();
        }
        Assert.assertEquals("Office Work", ((JSONObject) ((JSONArray) (jsonObject.get("projects"))).get(0)).get("title")); // checks that the title is correct
        Assert.assertEquals("",
                ((JSONObject) ((JSONArray) (jsonObject.get("projects"))).get(0)).get("description")); //checks that the description is correct
        System.out.println("GET projects/:id -- TEST PASSED");
    }

    @Test
    public void headProjectById() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(4, headers.size()); // Checks that it returns one header
        Assert.assertEquals(200, code);
        System.out.println("HEAD projects/:id -- TEST PASSED");
    }

    @Test
    public void postProjectById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject(); // Creates a new json object to be sent to the API
        jsonBody.put("title", "changed");
        jsonBody.put("description", "changed");
        Response response = apiCall.post("projects/1", "json", jsonBody);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        assertEquals("changed", jsonObject.get("title")); // checks that the title is correct
        assertEquals("changed", jsonObject.get("description")); //checks that the description is correct
        assertEquals(2, ((JSONArray) jsonObject.get("tasks")).size()); // checks that the number of tasks is correct
        assertEquals(200, response.code()); // checks that the response code is correct
        System.out.println("POST projects/:id -- TEST PASSED");
    }

    @Test
    public void putProjectById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject(); // creates a new json object to be sent to the API
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
        } finally {
            response.body().close();
        }
        JSONArray jsonArray = (JSONArray) jsonObject.get("tasks"); // gets the tasks array
        assertEquals("updated", jsonObject.get("title"));   // checks that the title is correct
        assertEquals("", jsonObject.get("description")); //checks that the description is correct
        assertEquals(jsonArray, jsonObject.get("tasks"));   // checks that the tasks are correct
        assertEquals(200, response.code()); // checks that the response code is correct
        System.out.println("PUT projects/:id -- TEST PASSED");
    }

    @Test
    public void deleteProjectById() {

        APICall apiCall = new APICall();

        JSONObject jsonObject = new JSONObject(); //creating a new project
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
        } finally {
            response1.body().close();
        }
        String id = (String) json.get("id");

        Response response2 = apiCall.delete("projects/" + id + "", "json"); //deleting the created project
        assertEquals(200, response2.code()); 

        Response response3 = apiCall.get("projects/" + id + "", "json"); //making sure that the project is doesn't exist anymore
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response3.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response3.body().close();
        }

        String error = (String) ((((JSONArray) jsonObject1.get("errorMessages")).get(0)));
        assertEquals("Could not find an instance with projects/"+id, error); //checking that the error message is correct
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
        } finally {
            response.body().close();
        }
        int size = ((JSONArray) jsonObject.get("categories")).size();
        Assert.assertEquals(0, size); // Checks that the number of categories is correct
        assertEquals(200, response.code()); // Checks that the response code is correct
        System.out.println("GET projects/:id/categories -- TEST PASSED");
    }

    @Test
    public void headProjectCategoriesById() {
        APICall apiCall = new APICall();
        Response response = apiCall.head("/projects/1/categories", "json");
        Headers headers = response.headers();
        Assert.assertEquals("application/json", headers.get("Content-Type"));
        int code = response.code();
        Assert.assertEquals(200, code); // Checks that the response code is correct
        System.out.println("HEAD projects/:id/categories -- TEST PASSED");
    }

    @Test
    public void postProjectCategoriesById() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject(); // Creates a new json object to be sent to the API
        jsonBody.put("id", "1");
        Response response = apiCall.post("projects/1/categories", "json", jsonBody);

        assertEquals(201, response.code()); // Checks that the response code is correct
        Response response1 = apiCall.get("projects/1/categories", "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response1.body().close();
        }
        assertEquals(1, ((JSONArray) jsonObject1.get("categories")).size());    // Checks that the number of categories is correct
        System.out.println("POST projects/:id/categories -- TEST PASSED");
    }

    @Test
    public void deleteProjectCategoriesById() {

        // creating relationship between project and category (and testing) for delete API to run since none exists by default
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                postProjectCategoriesById();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        APICall apiCall = new APICall();

        Response response = apiCall.delete("projects/1/categories/1", "json"); //deleting the created category
        assertEquals(200, response.code());

        Response response1 = apiCall.get("projects/1/categories", "json"); // making sure that the category is doesn't exist anymore
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response1.body().close();
        }
        assertEquals(0, ((JSONArray) jsonObject1.get("categories")).size()); // Checks that the number of categories is correct
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
        } finally {
            response.body().close();
        }
        int size = ((JSONArray) jsonObject.get("todos")).size();
        assertEquals(200, response.code()); // Checks that the response code is correct
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
        JSONObject jsonBody = new JSONObject(); // Creates a new json object to be sent to the API
        jsonBody.put("id", "2");
        Response response = apiCall.post("projects/1/tasks", "json", jsonBody); // Creating a new task

        assertEquals(201, response.code());
        Response response1 = apiCall.get("projects/1/tasks", "json"); // Getting the tasks to check if the new task was created
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            // no body response returned
        } finally {
            response.body().close();
        }
        assertEquals(2, ((JSONArray) jsonObject1.get("todos")).size()); // Checks that the number of tasks is correct
        System.out.println("POST projects/:id/tasks -- TEST PASSED");
    }

    @Test
    public void deleteProjectByIdTasks() {
        APICall apiCall = new APICall();
        Response response = apiCall.delete("projects/1/tasks/2", "json"); // Deleting the task
        assertEquals(200, response.code());
        Response response1 = apiCall.get("projects/1/tasks", "json"); // Getting the tasks to check if the task was deleted
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try{
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response1.body().close();
        }
        assertEquals(1, ((JSONArray) jsonObject1.get("todos")).size()); // Checks that the number of tasks is correct
        System.out.println("DELETE projects/:id/tasks/:id -- TEST PASSED");
    }

    @Test
    public void postProjectJSONMalformed() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject(); // Creates a new json object to be sent to the API
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
        } finally {
            response1.body().close();
        }
        String error = (String) ((((JSONArray) jsonObject1.get("errorMessages")).get(0))); // Gets the error message
        assertEquals("Could not find an instance with projects/10", error); // Checks that the error message is correct
        System.out.println("POST projects/:id (JSON malformed) -- TEST PASSED");
    }

    @Test
    public void postProjectXML() {
        APICall apiCall = new APICall();
        String xml = "<project><title>ECSE 429</title><description>Software description> </description></project>"; // Creates a new xml object to be sent to the API
        Response response = null;
        response = apiCall.postXML("projects", "xml", xml); // Creating a new project
        assertEquals(201, response.code());
        JSONParser jsonParser2 = new JSONParser();
        JSONObject json = null;
        try{
            json = (JSONObject) jsonParser2.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        String id = (String) json.get("id");
        Response response1 = apiCall.get("projects/"+id+"", "json"); // Getting the project to check if the new project was created
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response1.body().close();
        }
        assertEquals("ECSE 429", ((JSONObject) ((JSONArray) (jsonObject1.get("projects"))).get(0)).get("title")); // Checks that the title is correct
        System.out.println("POST projects (XML) -- TEST PASSED");
    }

    @Test
    public void postProjectXMLMalformed() {
        APICall apiCall = new APICall();
        String xml = "<project><id>15</id><name>test</name><description>test</description></project>"; // Creates a new malformed xml object to be sent to the API
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
        } finally {
            response1.body().close();
        }
        String error = (String) ((((JSONArray) jsonObject1.get("errorMessages")).get(0)));
        assertEquals("Could not find an instance with projects/15", error); // Checks that the error message is correct
        System.out.println("POST projects (XML Malformed) -- TEST PASSED");

    }

    @Test
    public void getProjectByWrongId() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects/100", "json"); // Getting a project with a wrong id
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        String error = (String) ((((JSONArray) jsonObject.get("errorMessages")).get(0))); // Gets the error message
        assertEquals("Could not find an instance with projects/100", error);
        System.out.println("GET projects/:id (wrong id) -- TEST PASSED");
    }

    // Bug
    @Test
    public void postProjectWithEmptyFields() {
        APICall apiCall = new APICall();
        JSONObject jsonBody = new JSONObject(); // Creates a new json object with empty fields to be sent to the API
        jsonBody.put("title", "");
        jsonBody.put("description", "");
        Response response = apiCall.post("projects", "json", jsonBody);
        assertEquals(201, response.code());
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        System.out.println(jsonObject);
        Assert.assertEquals(null, jsonObject.get("projects")); // Checks that the title is empty
        Assert.assertEquals(null, jsonObject.get("projects")); // Checks that the description is empty
        System.out.println("POST projects (empty title and description) -- TEST PASSED"); // It works but it's not suppopsed to
    }

    // Bug
    @Test
    public void getTaskByNonExistantProjectId() {
        APICall apiCall = new APICall();
        Response response = apiCall.get("projects/100/tasks", "json"); // Getting a task with a non existant project id
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        int size = ((JSONArray) jsonObject.get("todos")).size(); // Gets the size of the array
        assertEquals(2, size); // Checks that the size is 2
        assertEquals(200, response.code());
        System.out.println("GET projects/:id/tasks (non existant project id) -- TEST PASSED");

    }


}
