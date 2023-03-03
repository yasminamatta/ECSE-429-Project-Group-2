package com.software.validation.ECSE429.acceptanceTests;

import com.software.validation.ECSE429.api.APICall;
import io.cucumber.java.After;
import io.cucumber.java.Before;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodosStepDefs extends CucumberRunnerTest {
    List<JSONObject> todosList = null;
    String error = null;
    int previousTotalTodos = -1;
    int latestTotalTodos = -1;

    @After
    public void resetEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("fuser -k 4567/tcp"); // Shuts down the server once testing session is complete.
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Reset", "Error");
        }
    }


    @Given("the server is running")
    public void the_server_is_running() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Running", "Error");
        }
    }


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

    }

    @Given("atleast one project exists in the system")
    public void atleast_one_project_exists_in_the_system() {
        APICall ap = new APICall();
        Response response = ap.get("projects", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); // get todos as a response
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray) (json.get("projects"))).size();
        Assert.assertTrue(size >= 1); // API initially has atleast 1 todos loaded in as default.

        int code = response.code();
        Assert.assertEquals(200, code);

    }

    @Given("atleast one category exists in the system")
    public void atleast_one_category_exists_in_the_system() {
        APICall ap = new APICall();
        Response response = ap.get("categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string()); // get todos as a response
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray) (json.get("categories"))).size();
        Assert.assertTrue(size >= 1); // API initially has atleast 1 todos loaded in as default.

        int code = response.code();
        Assert.assertEquals(200, code);

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


    @Given("todo with id {string} is assigned to a category in the system")
    public void todo_with_id_is_assigned_to_a_category_in_the_system(String todoId) {
        APICall ap = new APICall();
        Response response = ap.get("todos/" + todoId + "/categories", "json"); // Requesting all categories related to todos of ID=1
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
        Assert.assertEquals(200, code); // check if the HTML response code is a success or not
        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(1, size); // only one category is related to the todos by default
    }

    @When("the user makes a DELETE request to delete a category with id {string} of a todo with id {string}")
    public void the_user_makes_a_DELETE_request_to_delete_a_category_with_id_of_a_todo_with_id (String categoryId, String todoId) {
        APICall ap = new APICall();
        String[] newTodoId = {""};
        int counter = 0;
        boolean related = false;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    responseOfTodos.body().close();
                }
                previousTotalTodos = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.delete("todos/" + todoId + "/categories/" + categoryId, "json"); // deleting relationship category with id=1 and todos with id=1.

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

                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    responseOfTodos.body().close();
                }
                latestTotalTodos = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @Then("the relationship between the category with id {string} and the todo with id {string} shall not exist in the system")
    public void the_relationship_between_the_category_with_id_and_the_todo_with_id_shall_not_exist_in_the_system(String categoryId, String todoId) {
        APICall ap = new APICall();
        Response response = ap.get("todos/" + todoId + "/categories" + categoryId, "json");
        int code = response.code();
        Assert.assertEquals(404, code); // relationship deleted, hence cannot be found anymore
    }

    @Then("no todo item shall be created or deleted")
    public void no_todo_item_shall_be_created_or_deleted() {
        Assert.assertEquals(0, Math.abs(latestTotalTodos - previousTotalTodos)); // no new object created, only relationship modified
    }

    @Given("there exists todo with id {string} in the system that is not assigned project with id {string}")
    public void there_exists_todo_with_id_in_the_system_that_is_not_assigned_project_with_id (String todoId, String projectId) {
        APICall ap = new APICall();

        Thread t1 = new Thread(new Runnable() {
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
                previousTotalTodos = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                // deleting relationship tasksOf between todos with id=1 and projects with id=1.
                Response res = ap.delete("todos/" + todoId + "/tasksof/" + projectId, "json");

                int code = res.code();
                Assert.assertEquals(200, code); // check whether successful or not
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
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
                latestTotalTodos = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @When("the user makes a POST request to assign a todo with id {string} to a project with id {string}")
    public void the_user_makes_a_POST_request_to_assign_a_todo_with_id_to_a_project_with_id(String todoId, String projectId) {
        APICall ap = new APICall();

        Thread t1 = new Thread(new Runnable() {
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
                previousTotalTodos = ((JSONArray)(json.get("todos"))).size(); // getting the size of the categories before POST
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject relationBody = new JSONObject();
                relationBody.put("id", projectId);

                Response res = ap.post("todos/" + todoId + "/tasksof", "json", relationBody); // Establishing relationship using POST

                int code = res.code();
                if(code == 404) {
                    JSONParser parserOfTodos = new JSONParser();
                    JSONObject jsonOfTodos = null;
                    try {
                        jsonOfTodos = (JSONObject) parserOfTodos.parse(res.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        res.body().close();
                    }
                    error = (String) ((JSONArray) (jsonOfTodos.get("errorMessages"))).get(0);
                }
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
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
                latestTotalTodos = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @Then("a relationship named tasksof shall be created between project with id {string} and the todo with id {string}")
    public void a_relationship_named_tasksof_shall_be_created_between_project_with_id_and_the_todo_with_id (String projectId, String todoId) {
        boolean related = false;

        APICall ap = new APICall();
        Response response = ap.get("todos/" + todoId + "/tasksof", "json"); // Requesting all project related to todos of ID=1 by relationship tasksof
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
        Assert.assertEquals(1, size); // the only project relate to the todos with id=1 returned
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

    @Then("a relationship named tasksof shall exist between project with id {string} and the todo with id {string}")
    public void a_relationship_named_tasksof_shall_exist_between_project_with_id_and_the_todo_with_id (String todoId, String projectId) {
        int counter = 0;
        boolean related = false;

        APICall ap = new APICall();
        Response response = ap.get("todos/" + todoId + "/tasksof", "json"); // Requesting all project related to todos of ID=1 by relationship tasksof
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
        Assert.assertEquals(1, size); // the only project relate to the todos with id=1 returned
        int code = response.code();
        Assert.assertEquals(200, code); // check if the HTML response code is a success or not

        int mapSize = ((JSONArray)(json.get("projects"))).size();
        List<String> map = new ArrayList<>();
        for(int i = 0; i < mapSize; i++) {
            map.add("false");
        }

        for (Object projectArray : ((JSONArray)(json.get("projects")))) {

            JSONObject projectObject = (JSONObject) projectArray;

            JSONArray tasks = (JSONArray) projectObject.get("tasks");

            for (Object obj : tasks) {
                JSONObject JSONobj = (JSONObject) obj;
                String id = (String) JSONobj.get("id");
                if(id.equals(projectId)){
                    map.set(counter, "true");
                    counter++;
                    break;
                }
            }

        }

        for(String flag : map) {
            if(flag.equalsIgnoreCase("false")) {
                related = false;
                break;
            } else {
                related = true;
            }
        }

        Assert.assertTrue(related); // verifying the relationship from project side
    }

    @Given("there exists a tasksof relationship between todo with id {string} and project with id {string}")
    public void there_exists_a_tasksof_relationship_between_todo_with_id_and_project_with_id (String todoId, String projectId) {
        APICall ap = new APICall();

        Thread t1 = new Thread(new Runnable() {
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
                previousTotalTodos = ((JSONArray)(json.get("todos"))).size(); // getting the size of the categories before POST
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject relationBody = new JSONObject();
                relationBody.put("id", projectId);

                Response res = ap.post("todos/" + todoId + "/tasksof", "json", relationBody); // Establishing relationship using POST

                int code = res.code();
                Assert.assertEquals(201, code); // Checking that it was successfully created
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
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
                latestTotalTodos = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }

    @Given("there exists todo with id {string} in the system that is not assigned to any category")
    public void there_exists_todo_with_id_in_the_system_that_is_not_assigned_to_any_category(String todoId) {
        APICall ap = new APICall();
        Response response = ap.get("todos/"+ todoId + "/categories", "json"); // Requesting all categories related to todos of ID=1
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
        Assert.assertEquals(200, code); // check if the HTML response code is a success or not
        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(0, size); // no category is related to the todos by default
    }

    @When("the user makes a POST request to assign a todo with id {string} to a category with id {string}")
    public void the_user_makes_a_POST_request_to_assign_a_todo_with_id_to_a_category_with_id (String todoId, String categoryId) {
        APICall ap = new APICall();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    responseOfTodos.body().close();
                }
                previousTotalTodos = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject();
        js.put("id", categoryId);
        Response response = ap.post("todos/" + todoId + "/categories", "json", js); // establishing new relationship

        int code = response.code();
        if(code == 404) {
            JSONParser parser = new JSONParser();
            JSONObject json = null;
            try {
                json = (JSONObject) parser.parse(response.body().string());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
        }


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    responseOfTodos.body().close();
                }
                latestTotalTodos = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
    }
    @Then("a relationship named categories shall be created between category with id {string} and the todo with id {string}")
    public void a_relationship_named_categories_shall_be_created_between_category_with_id_and_the_todo_with_id(String categoryId, String todoId) {
        APICall ap = new APICall();
        Response size = ap.get("todos/"+todoId+"/categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(size.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            size.body().close();
        }
        Assert.assertTrue(((JSONArray)(json.get("categories"))).size() >= 1); // verify newly created relationship
        Assert.assertEquals(categoryId, ((JSONObject)(((JSONArray)(json.get("categories"))).get(0))).get("id"));
    }

    @Given("there exists a categories relationship between todo with id {string} and a category")
    public void there_exists_a_categories_relationship_between_todo_with_id_and_a_category(String todoId) {
        APICall ap = new APICall();
        Response size = ap.get("todos/"+todoId+"/categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(size.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            size.body().close();
        }
        Assert.assertTrue(((JSONArray)(json.get("categories"))).size() >= 1);
    }

    @Then("a relationship named categories shall exist between category with id {string} and the todo with id {string}")
    public void a_relationship_named_categories_exist_created_between_category_with_id_and_the_todo_with_id(String categoryId, String todoId) {
        APICall ap = new APICall();
        Response size = ap.get("todos/"+todoId+"/categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(size.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            size.body().close();
        }
        Assert.assertTrue(((JSONArray)(json.get("categories"))).size() >= 1); // verify newly created relationship
        Assert.assertEquals(categoryId, ((JSONObject)(((JSONArray)(json.get("categories"))).get(0))).get("id"));
    }


}
