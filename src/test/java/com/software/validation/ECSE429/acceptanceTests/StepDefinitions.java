package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepDefinitions extends CucumberFeaturesTestRunner {

    List<JSONObject> todosList = null;
    String error = null;

    @Given("the server is running")
    public void the_server_is_running() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            //System.out.println("Setting up environment");
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Server", "error");
        }
    }

    ////////////////// TODOS /////////////////////////

    @Given("atleast one todo exists in the system")
    public void atleast_one_todo_exists_in_the_system() {
        APICall ap = new APICall();
        Response response = ap.get("todos", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); // get todos as a response
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray) (json.get("todos"))).size();
        Assert.assertTrue(size >= 1); // API initially has atleast 1 todos loaded in as default.

        int code = response.code();
        Assert.assertEquals(200, code);
        System.out.println("GET todos -- TEST PASSED");

    }

    @When("the user makes a query to get an item by ID {string}")
    public void the_user_makes_a_query_to_get_an_item_by_ID(String todoId) {
        APICall ap = new APICall();
        Response response = ap.get("todos/" + todoId, "json"); // ID as path variable
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int code = response.code();

        if(code == 200) {
            todosList = new ArrayList<>();
            JSONArray todos = ((JSONArray) (json.get("todos")));
            for (int t = 0; t < todos.size(); t++) {
                todosList.add((JSONObject) todos.get(t));
            }
        } else {
            error = (String) ((JSONArray)(json.get("errorMessages"))).get(0);
        }
    }

    @Then("one todo item shall be returned")
    public void one_todo_item_shall_be_returned() {
        Assert.assertEquals(1, todosList.size());
    }

    @Then("no todo item shall be returned")
    public void no_todo_item_shall_be_returned() {
        Assert.assertNull(todosList);
    }

    @Then("an error message with content {string} shall be raised")
    public void an_error_message_with_content_shall_be_raised(String errorMessage) {
        Assert.assertEquals(error, errorMessage);
    }

    @Then("the todo shall have id {string}, title {string}, and doneStatus {string}")
    public void the_todo_shall_have_id_title_and_doneStatus(String todoId, String todoTitle, String todoDoneStatus) {
        for (JSONObject obj : todosList) {
            // get contents of the body in string format
            String id = (String) (obj.get("id"));
            String title = (String) (obj.get("title"));
            String doneStatus = (String) (obj.get("doneStatus"));

            // check if the body matches the query
            Assert.assertEquals(todoId, id);
            Assert.assertEquals(todoTitle, title);
            Assert.assertEquals(todoDoneStatus, doneStatus);

        }
    }


    ////////////////// TODOS /////////////////////////
}
