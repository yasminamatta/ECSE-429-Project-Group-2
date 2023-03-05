package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;
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

public class ProjectStepDefinitions extends CucumberRunnerTest {
    List<JSONObject> projectList = null;
    String error = null;
    int previousTotalProject = -1;
    int latestTotatProject = -1;

    @Given("the server is running")
    public void the_server_is_running() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be
                                                                               // tested
                                                                               // System.out.println("Setting up environment");
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Server", "error");
        }
    }
    
    @Given("at least one project exists")
    public void at_least_one_project_exists() {
        APICall ap = new APICall();
        Response response = ap.get("projects", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray) (json.get("projects"))).size();
        Assert.assertTrue(size >= 1); 

        int code = response.code();
        Assert.assertEquals(200, code);
    }

    @When("the user makes a GET request to /projects/{string}")
    public void the_user_makes_a_GET_request_to_projects(String id) {
        APICall ap = new APICall();
        Response response = ap.get("projects/" + id, "json");
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
        if (code == 200 | code == 201) {
            projectList = new ArrayList<>();
            JSONArray project = ((JSONArray) (json.get("projects")));
            for (int t = 0; t < project.size(); t++) {
                projectList.add((JSONObject) project.get(t));
            }
        } else {
            error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
        }
    }

    @Then("one project shall be returned")
    public void one_project_shall_be_returned() {
        Assert.assertEquals(1, projectList.size());
    }

    @Then("the project shall have the following properties:")
    public void the_project_shall_have_the_following_properties(String projectId, String projectTitle,
            String projectDescription, String completed, String active, JSONArray projectTodos) {
        for (JSONObject obj : projectList) {
            // get contents of the body in string format
            String id = (String) (obj.get("id"));
            String title = (String) (obj.get("title"));
            String description = (String) (obj.get("description"));
            String completedStatus = (String) (obj.get("completed"));
            String activeStatus = (String) (obj.get("active"));
            JSONArray todos = (JSONArray) (obj.get("todos"));

            // check if the body matches the query
            Assert.assertEquals(projectId, id);
            Assert.assertEquals(projectTitle, title);
            Assert.assertEquals(projectDescription, description);
            Assert.assertEquals(completed, completedStatus);
            Assert.assertEquals(active, activeStatus);
            Assert.assertEquals(projectTodos, todos);

        }
    }

    @Then("no project shall be returned")
    public void no_project_shall_be_returned() {
        Assert.assertEquals(0, projectList.size());
    }

    @Then("an error message with content {errorMessage} shall be returned")
    public void an_error_message_with_content_errorMessage_shall_be_returned(String errorMessage) {
        Assert.assertEquals(errorMessage, error);
    }

}




