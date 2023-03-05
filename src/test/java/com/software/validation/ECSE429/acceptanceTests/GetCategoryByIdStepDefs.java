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
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetCategoryByIdStepDefs extends CucumberRunnerTest {

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

    @Given("at least one category exists in the system")
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

    @When("the user initiates the query of the category with ID {string}")
    public void the_user_initiates_a_query_for_a_category_by_ID(String categoryId) {
        APICall ap = new APICall();
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

        if(code == 200) {
            categoryList = new ArrayList<>();
            JSONArray categories = ((JSONArray) (json.get("categories"))); // if the response is successful, get categories
            for (int c = 0; c < categories.size(); c++) {
                categoryList.add((JSONObject) categories.get(c));
            }
        } else {
            error = (String) ((JSONArray)(json.get("errorMessages"))).get(0);
        }
    }

    @Then("one category shall be returned")
    public void one_category_item_shall_be_returned() {
        Assert.assertEquals(1, categoryList.size());
    }

    @Then("the category shall have title {string} and description {string}") // NORMAL FLOW
    public void category_shall_have_title_and_description(String CategoryTitle, String CategoryDescription){
        for (JSONObject i:categoryList) {
            String title = (String) i.get("title");
            String description = (String) i.get("description");

            // Check if the query matches the body
            Assert.assertEquals(CategoryTitle, title);
            Assert.assertEquals(CategoryDescription, description);
        }
    }

    @Then("no category shall be returned") // ERROR FLOW
    public void no_category_shall_be_returned() {
        Assert.assertNull(categoryList);
    }

    @Then("the error message {string} shall be raised") // ERROR FLOW
    public void the_error_message_shall_be_raised(String errorMessage) {
        Assert.assertEquals(error, errorMessage);
    }
}
