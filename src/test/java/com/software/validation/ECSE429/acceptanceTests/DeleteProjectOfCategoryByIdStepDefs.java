package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteProjectOfCategoryByIdStepDefs extends CucumberRunnerTest {
    List<JSONObject> categoryList = null;
    List<JSONObject> projectList = null;
    String error = null;
    int previousTotalCategories = 0;
    int latestTotalCategories = 0;

    @Given("the server is running normally")
    public void the_TodoManager_system_runs_normally() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Running", "Error");
        }
    }

    @After
    public void resetEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("fuser -k 4567/tcp"); // Shuts down the server once testing session is complete.
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Reset", "Error");
        }
    }





    @Given("at least one category exists on the server")
    public void at_least_one_category_exists_in_the_system() {
        APICall ap = new APICall();
        Response response = ap.get("categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); // get categories as a response
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray) (json.get("categories"))).size();
        Assert.assertTrue(size >= 1); // API initially has at least 1 category loaded in as default.

        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @Given("at least one project exists on the server")
    public void at_least_one_project_exists_in_the_system() {
        APICall ap = new APICall();
        Response response = ap.get("projects", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); // get projects as a response
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray) (json.get("projects"))).size();
        Assert.assertTrue(size >= 1); // API initially has at least 1 project loaded in as default.

        int code = response.code();
        Assert.assertEquals(200, code); // expected response is OK (code 200)
    }

    @Given("project with id {string} is assigned to a category with id {string} in the system")
    public void project_is_assigned_to_a_category(String projectId, String categoryId){
        APICall ap = new APICall();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as projectID
        js.put("id", projectId);
        Response response = ap.post("categories/" + categoryId + "/projects", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int code = response.code();
        if (code != 200 && code != 201) {

            JSONParser parser = new JSONParser();
            JSONObject json = null;
            try {
                json = (JSONObject) parser.parse(responsePost);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
        }
    }

    @When("the user requests to delete a project with id {string} of a category with id {string}")
    public void the_user_request_to_DELETE_request_to_delete_a_project_related_to_category (String projectId, String categoryId) {
        APICall ap = new APICall();
        String[] newCategoryId = {""};
        int counter = 0;
        boolean related = false;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                Response responseOfTodos = ap.get("categories", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    responseOfTodos.body().close();
                }
                previousTotalCategories = ((JSONArray)(jsonOfTodos.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.delete("categories/" + categoryId + "/projects/" + projectId, "json"); // deleting relationship between category with id=1 and project with id=1.

        int code = response.code();
        if(code == 404) {
            JSONParser parserOfTodos = new JSONParser();
            JSONObject jsonOfTodos = null;
            try {
                jsonOfTodos = (JSONObject) parserOfTodos.parse(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                response.body().close();
            }
            error = (String) ((JSONArray) (jsonOfTodos.get("errorMessages"))).get(0);
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                Response responseOfTodos = ap.get("categories", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    responseOfTodos.body().close();
                }
                latestTotalCategories = ((JSONArray)(jsonOfTodos.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @Then("the relationship between the project with id {string} and the category with id {string} shall not exist in the system")
    public void the_relationship_between_the_project_and_category_shall_not_exist(String projectId, String categoryId) {
        APICall ap = new APICall();
        Response response = ap.get("todos/" + categoryId + "/categories" + projectId, "json");
        int code = response.code();
        Assert.assertEquals(404, code); // relationship deleted, hence cannot be found anymore
    }

    @And("no category shall be created nor deleted")
    public void no_category_shall_be_created_nor_deleted(){
        Assert.assertEquals(0, Math.abs(previousTotalCategories-latestTotalCategories));
    }

    @Then("an error with content {string} shall be raised")
    public void the_error_message_is_raised_by_system(String errorMessage) {
        Assert.assertEquals(error, errorMessage);
    }

}
