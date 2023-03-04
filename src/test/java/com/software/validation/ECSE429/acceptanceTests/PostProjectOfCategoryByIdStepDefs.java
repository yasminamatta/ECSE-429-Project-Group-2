package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
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

public class PostProjectOfCategoryByIdStepDefs extends CucumberRunnerTest{
    List<JSONObject> categoryList = null;
    List<JSONObject> projectList = null;
    String error = null;
    int previousTotalCategories = 0;
    int latestTotalCategories = 0;

    @Given("the Todo Manager system is running normally")
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

    @Given("at least one category exists in the management system")
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

    @Given("at least one project exists in the management system")
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

    @Given("there exists a project with id {string} in the system that is not assigned to a category with id {string}")
    public void project_exists_which_is_not_assigned_to_a_category(String projectId, String categoryId) {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = ap.get("categories/" + categoryId, "json"); // ID added as a path variable
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

                if (code == 200) {
                    categoryList = new ArrayList<>();
                    JSONArray categories = ((JSONArray) (json.get("categories"))); // if the response is successful, get categories
                    for (int c = 0; c < categories.size(); c++) {
                        categoryList.add((JSONObject) categories.get(c));
                    }
                } else {
                    error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
                }
                Assert.assertTrue(categoryList.size() >= 1); // make sure at least 1 category exists
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before executing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response response2 = ap.get("projects/" + projectId, "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(response2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    response2.body().close();
                }
                int code = response2.code();

                if (code == 200) {
                    projectList = new ArrayList<>();
                    JSONArray projects = ((JSONArray) (json.get("projects"))); // if the response is successful, get projects
                    for (int p = 0; p < projects.size(); p++) {
                        projectList.add((JSONObject) projects.get(p));
                    }
                } else {
                    error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
                }
                Assert.assertTrue(projectList.size() >= 1); // make sure at least 1 project exists
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        for (var c : categoryList){
            JSONArray projects = ((JSONArray) (c.get("projects")));
            Assert.assertNull(projects); // ensure no relationship exists between project and category
        }
    }

    @Given("there is already a relationship between category with id {string} and project with id {string}")
    public void project_exists_which_is_assigned_to_a_category(String projectId, String categoryId){
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

    @When("the user makes a POST request to assign a project with id {string} to a category with id {string}")
    public void when_user_posts_project_relationship_to_category(String projectId, String categoryId){
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                previousTotalCategories = ((JSONArray) (json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before executing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
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
                } finally {
                    size2.body().close();
                }
                latestTotalCategories = ((JSONArray) (json.get("categories"))).size(); // add the new size of categories to array
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @Then("a relationship shall exist between project with id {string} and the category with id {string}")
    public void then_a_relationship_exists_between_project_and_category(String projectId, String categoryId){
        boolean related = false;

        APICall ap = new APICall();
        Response response = ap.get("categories/" + categoryId + "/projects", "json"); // Requesting all project related to category with ID=1
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray)(json.get("projects"))).size();
        Assert.assertEquals(1, size); // the only project relate to the category with id=1 returned
        int code = response.code();
        Assert.assertEquals(200, code); // check if the HTML response code is a success or not

        int mapSize = ((JSONArray)(json.get("projects"))).size();
        List<String> map = new ArrayList<>();
        for(int i = 0; i < mapSize; i++) {
            map.add("false");
        }

        for (Object projectArray : ((JSONArray)(json.get("projects")))) {

            JSONObject projectObject = (JSONObject) projectArray;

            String projId = (String) projectObject.get("id");
            if(projId.equalsIgnoreCase(projectId)) {
                Assert.assertTrue(projId.equalsIgnoreCase(projectId));
                return;
            }
        }

        Assert.assertTrue(related); // verifying the relationship from project side
    }

    @And("no category shall be created or deleted")
    public void no_category_shall_be_created_or_deleted(){
        Assert.assertEquals(0, Math.abs(previousTotalCategories-latestTotalCategories));
    }

    @Then("an error message with content {string} shall be raised by the system")
    public void the_error_message_is_raised_by_system(String errorMessage) {
        Assert.assertEquals(error, errorMessage);
    }
}
