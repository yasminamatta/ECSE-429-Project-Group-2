package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;

import io.cucumber.java.After;
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
    String responseCode;


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


    @When("the user makes GET request to get a project item by ID {string}")
    public void the_user_makes_GET_request_to_get_a_project_item_by_ID(String projectId) {
        APICall ap = new APICall();
        Response response = ap.get("projects/" + projectId, "json"); // ID as path variable
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            if(response == null) {
                responseCode = "null";
            } else {
                json = (JSONObject) parser.parse(response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(response != null) {
                response.body().close();
            }
        }

        int code = 0;
        if(response != null) {
            code = response.code();
        }

        if (code == 200 || code == 201) {
            projectList = new ArrayList<>();
            JSONArray todos = ((JSONArray) (json.get("projects")));
            for (int t = 0; t < todos.size(); t++) {
                projectList.add((JSONObject) todos.get(t));
            }
        } else {
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }
    }

    @Then("an error message for project with content {string} shall be raised")
    public void an_error_message_with_content_shall_be_raised(String errorMessageOfProject) {
        Assert.assertEquals(error, errorMessageOfProject);
    }

    @Then("one project item shall be returned")
    public void one_project_item_shall_be_returned() {
        Assert.assertEquals(1, projectList.size());
    }

    @Then("the project shall have id {string}, title {string}, completed {string}, active {string}")
    public void the_project_shall_have_id_title_and_doneStatus(String projectId, String projectTitle, String completed, String active) {
        for (JSONObject obj : projectList) {
            // get contents of the body in string format
            String id = (String) (obj.get("id"));
            String title = (String) (obj.get("title"));
            String complete = (String) (obj.get("completed"));
            String isActive = (String) (obj.get("active"));

            // check if the body matches the query
            Assert.assertEquals(projectId, id);
            Assert.assertEquals(projectTitle, title);
            Assert.assertEquals(completed, complete);
            Assert.assertEquals(isActive, active);

        }
    }


}




