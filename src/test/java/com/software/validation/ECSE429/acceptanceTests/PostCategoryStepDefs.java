package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;
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

public class PostCategoryStepDefs extends CucumberRunnerTest{
    List<JSONObject> categoryList = null;
    String error = null;
    int previousTotalCategories = 0;
    int latestTotalCategories = 0;

    @Given("the Todo Manager system runs well")
    public void the_TodoManager_system_runs_well() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Server", "error");
        }
    }

    @Given("more than one category exists in the system")
    public void more_than_one_category_exists_in_the_system() {
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

    @When("the user creates a category with title {string} and description {string}") // NORMAL & ALTERNATE FLOW
    public void the_user_posts_category_by_id(String categTitle, String categDescription){
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
        js.put("title", categTitle);
        js.put("description", categDescription);
        Response response = ap.post("categories", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int code = response.code();
        if (code == 200 | code == 201) {

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(responsePost);


                Response size = ap.get("categories/" + json.get("id"), "json");
                JSONParser parserResponse = new JSONParser();
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = (JSONObject) parserResponse.parse(size.body().string()); // parse body into created JSON object
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                Assert.assertEquals(201, response.code());


                categoryList = new ArrayList<>(); // initialising a new category list to make sure the list is updated and accurate
                JSONArray categories = ((JSONArray) (jsonResponse.get("categories")));
                for (int t = 0; t < categories.size(); t++) {
                    categoryList.add((JSONObject) categories.get(t));
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
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

    @Then("one category is created and returned")
    public void one_category_is_created(){
        Assert.assertEquals(1, latestTotalCategories - previousTotalCategories);
    }

    @And("the category shall have the title {string} and description {string}")
    public void category_has_title_and_description_provided(String categTitle, String categDescription){
        for (JSONObject c:categoryList){
            String title = (String) c.get("title");
            String description = (String) c.get("description");

            Assert.assertEquals(title, categTitle);
            Assert.assertEquals(description, categDescription);
        }
    }

    @Then("a category is not created") // ERROR FLOW
    public void a_category_is_not_created() {
        Assert.assertEquals(0, latestTotalCategories - previousTotalCategories);
    }

    @Then("the error message {string} is raised by the system") // ERROR FLOW
    public void the_error_message_is_raised_by_system(String errorMessage) {
        Assert.assertEquals(error, errorMessage);
    }

}
