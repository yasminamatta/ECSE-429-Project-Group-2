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

public class PostCategoryByIdStepDefs extends CucumberRunnerTest{

    List<JSONObject> categoryList = null;
    String error = null;
    Helper help = new Helper();

    @Given("the server is running")
    public void the_server_is_running() {
        help.the_server_is_running();
    }

    @After
    public void resetEnvironment() {
        help.resetEnvironment();
    }

    @Given("a minimum of one category exists in the system")
    public void a_minimum_of_one_category_exists_in_the_system() {
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

    @When("the user posts a category with ID {string}, title {string} and description {string}") // NORMAL FLOW
    public void the_user_posts_category_by_id(String id, String categTitle, String categDescription){
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("categories/" + id, "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
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
        Response response = ap.post("categories/" + id, "json", js);

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
                Assert.assertEquals(200, response.code());


                categoryList = new ArrayList<>();
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
                Response size2 = ap.get("categories/" + id, "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size2.body().close();
                }
                }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @When("the user posts a category with invalid ID {string}, title {string} and description {string}") // ERROR FLOW
    public void the_user_posts_category_using_invalid_id(String id, String categTitle, String categDescription){
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("categories/" + id, "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
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
        Response response = ap.post("categories/" + id, "json", js);

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
                Assert.assertEquals(200, response.code());


                categoryList = new ArrayList<>();
                JSONArray categories = ((JSONArray) (jsonResponse.get("categories")));
                for (int t = 0; t < categories.size(); t++) {
                    categoryList.add((JSONObject) categories.get(t));
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else { // Intended response, since error code should be 404
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
                Response size2 = ap.get("categories/" + id, "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size2.body().close();
                }
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @Then("the category shall be returned with ID {string}, title {string} and description {string}")
    public void the_category_has_the_updated_fields(String categId, String categTitle, String categDescription){
        for (JSONObject c:categoryList){
            String id = (String) c.get("id");
            String title = (String) c.get("title");
            String description = (String) c.get("description");

            Assert.assertEquals(id, categId);
            Assert.assertEquals(title, categTitle);
            Assert.assertEquals(description, categDescription);
        }
    }

    @Then("no category is returned") // ERROR FLOW
    public void no_category_is_returned() {
        Assert.assertNull(categoryList);
    }

    @Then("the error message {string} is raised") // ERROR FLOW
    public void the_error_message_is_raised(String errorMessage) {
        Assert.assertEquals(error, errorMessage);
    }
}
