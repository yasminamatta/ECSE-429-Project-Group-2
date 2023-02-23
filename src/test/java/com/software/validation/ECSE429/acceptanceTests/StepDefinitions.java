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

public class StepDefinitions extends CucumberRunnerTest {
    //////////////////// TODOS ///////////////////////////
    List<JSONObject> todosList = null;
    String error = null;
    int previousTotalTodos = -1;
    int latestTotalTodos = -1;

    //////////////////// TODOS ///////////////////////////

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
        //System.out.println("GET todos -- TEST PASSED");

    }

    @When("the user makes a query to get a todo item by ID {string}")
    public void the_user_makes_a_query_to_get_a_todo_item_by_ID(String todoId) {
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

        if (code == 200 | code == 201) {
            todosList = new ArrayList<>();
            JSONArray todos = ((JSONArray) (json.get("todos")));
            for (int t = 0; t < todos.size(); t++) {
                todosList.add((JSONObject) todos.get(t));
            }
        } else {
            error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
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

    @When("the user makes a query to create a todo item with title {string} and description {string}")
    public void the_user_makes_a_query_to_create_a_todo_item_with_title_and_description(String todoTitle, String todoDescription) {

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("todos", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                previousTotalTodos = ((JSONArray) (json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", todoTitle);
        js.put("description", todoDescription);
        Response response = ap.post("todos", "json", js);

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


                Response size = ap.get("todos/" + json.get("id"), "json");
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


                todosList = new ArrayList<>();
                JSONArray todos = ((JSONArray) (jsonResponse.get("todos")));
                for (int t = 0; t < todos.size(); t++) {
                    todosList.add((JSONObject) todos.get(t));
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
                Response size2 = ap.get("todos", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size2.body().close();
                }
                latestTotalTodos = ((JSONArray) (json.get("todos"))).size(); // add the new size of todos to array
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        // System.out.println("POST todos -- TEST PASSED");

    }

    @Then("one todo item shall be created and returned")
    public void one_todo_item_shall_be_created_and_returned() {
        Assert.assertEquals(1, latestTotalTodos - previousTotalTodos);
    }

    @Then("no todo item shall be created")
    public void no_todo_item_shall_be_created() {
        Assert.assertEquals(0, latestTotalTodos - previousTotalTodos);
    }



    @When("the user makes a query to create a todo item with only title {string}")
    public void the_user_makes_a_query_to_create_a_todo_item_with_only_title(String todoTitle) {

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("todos", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                previousTotalTodos = ((JSONArray) (json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", todoTitle);
        Response response = ap.post("todos", "json", js);

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


                Response size = ap.get("todos/" + json.get("id"), "json");
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


                todosList = new ArrayList<>();
                JSONArray todos = ((JSONArray) (jsonResponse.get("todos")));
                for (int t = 0; t < todos.size(); t++) {
                    todosList.add((JSONObject) todos.get(t));
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
                Response size2 = ap.get("todos", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size2.body().close();
                }
                latestTotalTodos = ((JSONArray) (json.get("todos"))).size(); // add the new size of todos to array
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        // System.out.println("POST todos -- TEST PASSED");

    }


    @When("description shall be set to {string} and doneStatus to {string}")
    public void description_shall_be_set_to_and_doneStatus_to(String todoDescription, String todoDoneStatus) {
        for(JSONObject obj: todosList) {
            Assert.assertEquals(todoDescription, obj.get("description"));
            Assert.assertEquals(todoDoneStatus, obj.get("doneStatus"));
        }
    }
    ////////////////// TODOS /////////////////////////
}
