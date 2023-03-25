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
    int previousTotalProjects = -1;
    int latestTotalProjects = -1;
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

    @When("the user makes GET request to get all project items")
    public void the_user_makes_GET_request_to_get_all_project_items() {
        APICall ap = new APICall();
        Response response = ap.get("projects", "json"); // ID as path variable
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


    @When("the user makes POST request to create a project item with title {string} and description {string}")
    public void the_user_makes_POST_request_to_create_a_project_item_with_title_and_description(String projectTitle, String projectDescription) {

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("projects", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                previousTotalProjects = ((JSONArray) (json.get("projects"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", projectTitle);
        js.put("description", projectDescription);
        Response response = ap.post("projects", "json", js);

        String responsePost = null;
        try {
            if(response == null) {
                responseCode = "null";
            } else {
                responsePost = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(response != null) {
                response.body().close();
            }
        }

        int code = 0;
        if(response != null){
            code = response.code();
        }
        if (code == 200 | code == 201) {

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(responsePost);


                Response size = ap.get("projects/" + json.get("id"), "json");
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


                projectList = new ArrayList<>();
                JSONArray todos = ((JSONArray) (jsonResponse.get("projects")));
                for (int t = 0; t < todos.size(); t++) {
                    projectList.add((JSONObject) todos.get(t));
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
                Response size2 = ap.get("projects", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size2.body().close();
                }
                latestTotalProjects = ((JSONArray) (json.get("projects"))).size(); // add the new size of todos to array
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

    }

    @Then("one project item shall be created and returned")
    public void one_project_item_shall_be_created_and_returned() {
        Assert.assertEquals(1, latestTotalProjects - previousTotalProjects);
    }

    @When("the user makes POST request to create a project item with only title {string}")
    public void the_user_makes_POST_request_to_create_a_project_item_with_only_title(String projectTitle) {

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("projects", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    if(size != null) {
                        json = (JSONObject) parser.parse(size.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(size != null) {
                        size.body().close();
                    }
                }
                if(json != null) {
                    previousTotalProjects = ((JSONArray) (json.get("projects"))).size();
                }
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", projectTitle);
        Response response = ap.post("projects", "json", js);

        String responsePost = null;
        try {
            if(response == null) {
                responseCode = "null";
            } else {
                responsePost = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(response != null) {
                response.body().close();
            }
        }

        int code = 0;
        if(response != null){
            code = response.code();
        }
        if (code == 200 | code == 201) {

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(responsePost);


                Response size = ap.get("projects/" + json.get("id"), "json");
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


                projectList = new ArrayList<>();
                JSONArray todos = ((JSONArray) (jsonResponse.get("projects")));
                for (int t = 0; t < todos.size(); t++) {
                    projectList.add((JSONObject) todos.get(t));
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            JSONParser parser = new JSONParser();
            JSONObject json = null;
            try {
                if(responsePost != null) {
                    json = (JSONObject) parser.parse(responsePost);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = ap.get("projects", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    if(size2 != null) {
                        json = (JSONObject) parser.parse(size2.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(size2 != null) {
                        size2.body().close();
                    }
                }
                if(json != null) {
                    latestTotalProjects = ((JSONArray) (json.get("projects"))).size(); // add the new size of todos to array
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

    @When("the user makes POST request to create a project item with id {string}")
    public void the_user_makes_POST_request_to_create_a_project_item_with_id(String id) {

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
            @Override
            public void run() {
                Response size = ap.get("projects", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    if(size != null) {
                        json = (JSONObject) parser.parse(size.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(size != null) {
                        size.body().close();
                    }
                }
                if(json != null) {
                    previousTotalProjects = ((JSONArray) (json.get("projects"))).size();
                }
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("id", id);
        Response response = ap.post("projects", "json", js);

        String responsePost = null;
        try {
            if(response == null) {
                responseCode = "null";
            } else {
                responsePost = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(response != null) {
                response.body().close();
            }
        }

        int code = 0;
        if(response != null){
            code = response.code();
        }
        if (code == 200 | code == 201) {

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(responsePost);


                Response size = ap.get("projects/" + json.get("id"), "json");
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


                projectList = new ArrayList<>();
                JSONArray todos = ((JSONArray) (jsonResponse.get("projects")));
                for (int t = 0; t < todos.size(); t++) {
                    projectList.add((JSONObject) todos.get(t));
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            JSONParser parser = new JSONParser();
            JSONObject json = null;
            try {
                if(responsePost != null) {
                    json = (JSONObject) parser.parse(responsePost);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = ap.get("projects", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    if(size2 != null) {
                        json = (JSONObject) parser.parse(size2.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(size2 != null) {
                        size2.body().close();
                    }
                }
                if(json != null) {
                    latestTotalProjects = ((JSONArray) (json.get("projects"))).size(); // add the new size of todos to array
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

    @When("description shall be set to {string}, completed to {string}, active to {string}")
    public void description_shall_be_set_to_completed_to_active_to(String todoDescription, String completed, String active) {
        for(JSONObject obj: projectList) {
            Assert.assertEquals(todoDescription, obj.get("description"));
            Assert.assertEquals(completed, obj.get("completed"));
            Assert.assertEquals(active, obj.get("active"));
        }
    }

    @Given("there exists zero projects in the system")
    public void there_exists_zero_projects_in_the_system(){
        APICall ap = new APICall();
        Response res = ap.delete("projects/1", "json");
        Assert.assertEquals(200, res.code());
    }

    @When("the user makes a DELETE request for project with id {string}")
    public void the_user_makes_a_DELETE_request_for_project_with_id (String id) {
        APICall ap = new APICall();
        Response res = ap.delete("projects/" + id, "json");
        Assert.assertEquals(200, res.code());
    }

    @When("the user makes a DELETE request for project with non-existent id {string}")
    public void the_user_makes_a_DELETE_request_for_project_with_non_existent_id (String id) {
        APICall ap = new APICall();
        Response response = ap.delete("projects/" + id, "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
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

        if (code == 404) {
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }
    }

    @When("the user makes a DELETE request for project with invalid id {string}")
    public void the_user_makes_a_DELETE_request_for_project_with_invalid_id (String id) {
        APICall ap = new APICall();
        Response response = ap.delete("projects/" + id, "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
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

        if (code == 404) {
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }
    }

    @Then("the project with id {string} shall not exist")
    public void the_project_with_id_shall_not_exist(String id) {
        APICall ap = new APICall();
        Response response = ap.get("projects" + id, "json");

        int code = response.code();
        Assert.assertEquals(404, code);

    }

    @Then("zero project item shall be returned")
    public void zero_project_item_shall_be_returned() {
        Assert.assertEquals(0, projectList.size());
    }

    @Then("two tasks shall be returned")
    public void two_tasks_shall_be_returned() {
        Assert.assertEquals(2, projectList.size());
    }


    @Given("at least one project exists")
    public void at_least_one_project_exists() {
        APICall ap = new APICall();
        Response response = ap.get("projects", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            if(response != null) {
                json = (JSONObject) parser.parse(response.body().string()); // get todos as a response
            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        } finally {
            if(response != null) {
                response.body().close();
            }
        }

        int size = 0;
        if(json != null) {
            size = ((JSONArray) (json.get("projects"))).size();
        }
        Assert.assertTrue(size >= 1); // API initially has atleast 1 todos loaded in as default.

        int code = response.code();
        Assert.assertEquals(200, code);

    }

    @When("the user makes a GET request for tasks of project with id {string}")
    public void the_user_makes_a_GET_request_for_tasks_of_project_with_id(String id) {
        APICall ap = new APICall();
        Response response = ap.get("projects/" + id + "/tasks", "json"); // ID as path variable
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
            JSONArray todos = ((JSONArray) (json.get("todos")));
            for (int t = 0; t < todos.size(); t++) {
                projectList.add((JSONObject) todos.get(t));
            }
        } else {
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }
    }

    @When("the user makes a GET request for tasks of project with non-existent id {string}")
    public void the_user_makes_a_GET_request_for_tasks_of_project_with_non_existent_id(String id) {
        APICall ap = new APICall();
        Response response = ap.get("projects/" + id, "json"); // ID as path variable
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
            JSONArray todos = ((JSONArray) (json.get("todos")));
            for (int t = 0; t < todos.size(); t++) {
                projectList.add((JSONObject) todos.get(t));
            }
        } else {
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }
    }

    @When("the user makes a GET request for tasks of project with invalid id {string}")
    public void the_user_makes_a_GET_request_for_tasks_of_project_with_invalid_id(String id) {
        APICall ap = new APICall();
        Response response = ap.get("projects/" + id, "json"); // ID as path variable
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
            JSONArray todos = ((JSONArray) (json.get("todos")));
            for (int t = 0; t < todos.size(); t++) {
                projectList.add((JSONObject) todos.get(t));
            }
        } else {
            if(json != null) {
                error = (String) ((JSONArray) (json.get("errorMessages"))).get(0);
            }
        }
    }

    @Then("the API call should not be successful")
    public void the_API_call_should_not_successful() {
        APICall ap = new APICall();
        Response res = ap.get("projects/", "json");
        Assert.assertEquals(null, res);
    }


}




