package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.Random.class)
public class CategoriesTest {
    Integer successCodes[] = {200, 201}; // HTML success codes for OK and CREATE
    int categories[] = {0, 0}; // empty categories array to be used throughout the testing

    @BeforeEach
    public void setupEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            System.out.println("Setting up environment");
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void resetEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("fuser -k 4567/tcp"); // Shuts down the server once testing session is complete.
            System.out.println("Resetting environment");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getCategory() {
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

        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(2, size); // API initially has 2 categories loaded in as default.
        System.out.println("GET categories - TEST PASSED");
    }

    @Test
    public void headCategory() {
        APICall api = new APICall();
        Response response = api.head("categories", "json");
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // 4 headers exist. Check that 4 are returned as response
        Assert.assertEquals("application/json", headers.get("Content-Type").toString()); // checks that output is of JSON format
        System.out.println("HEAD categories - TEST PASSED");
    }

    @Test
    public void postCategory() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() { //Allows for the called GET and POST methods to run in a sequence
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
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", "School");
        js.put("description", "xyz");
        Response response = ap.post("categories", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);

            Response size = ap.get("categories/"+json.get("id"), "json");
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(size.body().string()); // parse body into created JSON object
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                size.body().close();
            }

            // check that the id and the body of the created post both match
            Assert.assertEquals("School", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("title"));
            Assert.assertEquals("xyz", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("description"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check that status code is either OK (200) or CREATED (201)

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error");
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
                categories[1] = ((JSONArray)(json.get("categories"))).size(); // add the new size of categories to array
            }
        });

        t2.start(); // second get method to check that a category was added
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(1, Math.abs(categories[1] - categories[0])); // check that only 1 category was added
        System.out.println("POST categories - TEST PASSED");
    }

    @Test
    public void getCategoryWithId() {
        APICall ap = new APICall();
        Response response = ap.get("categories/1", "json"); // ID present in URL
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(1, size);

        // get contents of the body in string format
        String id = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("id");
        String title = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("title");
        String description = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("description");

        // check if the body matches the query
        Assert.assertEquals("1", id);
        Assert.assertEquals("Office", title);
        Assert.assertEquals("", description);
        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        System.out.println("GET categories/:id -- TEST PASSED");
    }

    @Test
    public void headCategoryWithId() {
        APICall api = new APICall();
        Response response = api.head("categories/1", "json"); // query done with ID in URL
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok

        System.out.println("HEAD categories/:id -- TEST PASSED");
    }

    @Test
    public void postCategoryWithId() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject(); // creating new JSON object with updated body
        js.put("title", "School");
        js.put("description", "updated category");
        Response response = ap.post("categories/1", "json", js); // posting to already existing category with ID=1.

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);

            Response res = ap.get("categories/"+json.get("id"), "json"); // getting the created category again to see if POST worked
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(res.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                res.body().close();
            }

            // check if the new body is now with the category with the given ID
            Assert.assertEquals("School", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("title"));
            Assert.assertEquals("updated category", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("description"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML response is ok

        } catch (ParseException e) {
            e.printStackTrace();
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
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        Assert.assertEquals(0, Math.abs(categories[1] - categories[0])); // check that no new category was created. Body of one category should've been overwritten
        System.out.println("POST categories/:id -- TEST PASSED");
    }

    @Test
    public void putCategoryWithId() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject();
        js.put("title", "Office");
        js.put("description", "");
        Response response = ap.put("categories/1", "json", js); // using id = 1. Should completely replace the current entry with ID=1.

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);

            Response res = ap.get("categories/"+json.get("id"), "json");
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(res.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                res.body().close();
            }

            // check if the body matches with input
            Assert.assertEquals("Office", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("title"));
            Assert.assertEquals("", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("description"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code));

        } catch (ParseException e) {
            e.printStackTrace();
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
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(categories[1] - categories[0])); // no new entry should be created. Only the body should completely change.
        System.out.println("PUT categories/:id -- TEST PASSED");
    }

    @Test
    public void deleteCategoryWithId() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray)(json.get("categories"))).size(); // getting number of categories present before 1 is deleted
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.delete("categories/2", "json"); // delete category with ID=2

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // Ensure HTML response is ok

        Response retrieveDeleted = ap.get("categories/2", "json"); // now try to get the same category of ID=2, although it's deleted.
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(retrieveDeleted.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retrieveDeleted.body().close();
        }

        String error = (String) ((((JSONArray)json.get("errorMessages")).get(0)));
        Assert.assertEquals("Could not find an instance with categories/2", error);

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
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        Assert.assertEquals(1, Math.abs(categories[1] - categories[0])); // the difference should be 1, between what the number of initial categories and the current number
        System.out.println("DELETE categories/:id -- TEST PASSED");
    }

    @Test
    public void getProjectsRelatedToCategory() {
        APICall ap = new APICall();
        Response response = ap.get("categories/1/projects", "json"); // Requesting all projects related to category of ID=1
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        var contents = ((JSONArray)(json.get("categories")));
        Assert.assertEquals(null, contents); // as the API initially has no relationships between projects and categories, the result is a null empty set.

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        System.out.println("GET categories/:id/projects -- TEST PASSED");
    }

    @Test
    public void headProjectsRelatedToCategory(){
        APICall api = new APICall();
        Response response = api.head("categories/1/projects", "json"); // querying projects related to category with ID=1.
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok

        System.out.println("HEAD categories/:id/projects -- TEST PASSED");
    }

    @Test
    public void postProjectsRelatedToCategory(){
        APICall ap = new APICall();
        String[] newCategoryId = {""};
        int counter = 0;
        boolean related = false;

        // create dummy object first
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject js = new JSONObject();
                js.put("title", "Office");
                js.put("description", "");
                Response response = ap.post("categories/1", "json", js); // creating a relationship between category 1 and project 1

                String responsePost = null;
                try {
                    responsePost = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close();
                }

                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(responsePost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newCategoryId[0] = (String) json.get("id"); // saving the category ID for future use in cross-checking
            }
        });

        t3.start();
        try {
            t3.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray) (json.get("categories"))).size(); // getting the size of the categories before POST
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
                relationBody.put("id", "1");

                Response res = ap.post("categories/" + newCategoryId[0] + "/projects", "json", relationBody); // Establishing relationship using POST

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // Checking that it was successfully created
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.get("categories/" + newCategoryId[0] + "/projects", "json"); // Getting the project assigned to category 1
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int mapSize = ((JSONArray) (json.get("projects"))).size(); // Making a map to add the projects to
        List<String> map = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            map.add("false");
        }

        for (Object projectArray : ((JSONArray) (json.get("projects")))) { // Iterating through all fields of the project

            JSONObject projectObject = (JSONObject) projectArray;

            String id = (String) projectObject.get("id"); // Getting the ID of the project which was assigned

            if(id.equals(newCategoryId[0])) { // Checking that the project ID and the Category ID match. Since in this case both are 1
                map.set(counter, "true");
                counter++;
                break;
            }
        }

        for (String flag : map) {
            if (flag.equalsIgnoreCase("false")) {
                related = false;
                break;
            } else {
                related = true;
            }
        }
        Assert.assertTrue(related); // Ensures that relation exists

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
                categories[1] = ((JSONArray) (json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        Assert.assertEquals(0, Math.abs(categories[1] - categories[0]));
        System.out.println("POST categories/:id/projects -- TEST PASSED");
    }

    @Test
    public void deleteProjectRelatedToCategory(){

        postProjectsRelatedToCategory(); // POST relationship so that the relationship exists to be deleted

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray)(json.get("categories"))).size();
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

                Response res = ap.delete("categories/1/projects/1", "json"); // deleting project with id=1, which was assigned to category with id=1.

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check whether successful or not
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.get("categories/1/projects", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        for(Object allRelatedProjects : ((JSONArray)(json.get("projects")))) {

            JSONObject eachProject = (JSONObject) allRelatedProjects;
            String projectId = (String) eachProject.get("id");

            Assert.assertNotEquals("1", projectId);
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
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(categories[1] - categories[0]));// Confirmation that no new categories were deleted
        System.out.println("DELETE categories/:id/projects/:id -- TEST PASSED");
    }

    @Test
    public void getTodosRelatedToCategory(){
        APICall ap = new APICall();
        Response response = ap.get("categories/2/todos", "json"); // Requesting all projects related to category of ID=2
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        var contents = ((JSONArray)(json.get("todos"))).size();
        Assert.assertEquals(0, contents); // as the API initially has no relationships between projects and categories, the result is a null empty set.

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        System.out.println("GET categories/:id/todos -- TEST PASSED");
    }

    @Test
    public void headTodosRelatedToCategory(){
        APICall api = new APICall();
        Response response = api.head("categories/1/todos", "json"); // querying projects related to category with ID=1.
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok

        System.out.println("HEAD categories/:id/todos -- TEST PASSED");
    }

    @Test
    public void postTodosRelatedToCategory(){
        APICall ap = new APICall();
        String[] newCategoryId = {""};
        int counter = 0;
        boolean related = false;

        // create dummy object first
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject js = new JSONObject();
                js.put("title", "Office");
                js.put("description", "");
                Response response = ap.post("categories/1", "json", js); // creating a relationship between category 1 and todo 1

                String responsePost = null;
                try {
                    responsePost = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close();
                }

                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(responsePost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newCategoryId[0] = (String) json.get("id"); // saving the category ID for future use in cross-checking
            }
        });

        t3.start();
        try {
            t3.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray) (json.get("categories"))).size(); // getting the size of the categories before POST
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
                relationBody.put("id", "1");

                Response res = ap.post("categories/" + newCategoryId[0] + "/todos", "json", relationBody); // Establishing relationship using POST

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // Checking that it was successfully created
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.get("categories/" + newCategoryId[0] + "/todos", "json"); // Getting the todo assigned to category 1
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int mapSize = ((JSONArray) (json.get("todos"))).size(); // Making a map to add the todos to
        List<String> map = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            map.add("false");
        }

        for (Object projectArray : ((JSONArray) (json.get("todos")))) { // Iterating through all fields of the todo

            JSONObject projectObject = (JSONObject) projectArray;

            String id = (String) projectObject.get("id"); // Getting the ID of the todo which was assigned

            if(id.equals(newCategoryId[0])) { // Checking that the project ID and the Category ID match. Since in this case both are 1
                map.set(counter, "true");
                counter++;
                break;
            }
        }

        for (String flag : map) {
            if (flag.equalsIgnoreCase("false")) {
                related = false;
                break;
            } else {
                related = true;
            }
        }
        Assert.assertTrue(related); // Ensures that relation exists

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
                categories[1] = ((JSONArray) (json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        Assert.assertEquals(0, Math.abs(categories[1] - categories[0]));
        System.out.println("POST categories/:id/todos -- TEST PASSED");
    }

    @Test
    public void deleteTodosRelatedToCategory(){

        postTodosRelatedToCategory(); // POST relationship so that the relationship exists to be deleted

        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
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
                categories[0] = ((JSONArray)(json.get("categories"))).size();
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

                Response res = ap.delete("categories/1/todos/1", "json"); // deleting project with id=1, which was assigned to category with id=1.

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check whether successful or not
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.get("categories/1/todos", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        for(Object allRelatedProjects : ((JSONArray)(json.get("todos")))) {

            JSONObject eachProject = (JSONObject) allRelatedProjects;
            String projectId = (String) eachProject.get("id");

            Assert.assertNotEquals("1", projectId);
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
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(categories[1] - categories[0]));
        System.out.println("DELETE categories/:id/projects/:id -- TEST PASSED");
    }

    @Test
    public void postCategoriesJSONMalformed() {
        APICall ap = new APICall();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    size.body().close();
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject();
        js.put("description", "okhttp");
        Response response = ap.post("categories", "json", js); // malformed JSON as no title field is provided
        Assert.assertEquals(400, response.code()); // expect error

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        JSONObject json = null;
        try {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(responsePost);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("title : field is mandatory", ((JSONArray) json.get("errorMessages")).get(0)); // verify error message

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
                }
                finally {
                    size2.body().close();
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(categories[1] - categories[0])); // verify no new object created
        System.out.println("POST categories (JSON Malformed) -- TEST PASSED");
    }

    @Test
    public void postCategoryXML() {
        APICall apiCall = new APICall();
        String xml = "<todo><title>ECSE 429</title><description>Software Validation</description></todo>";
        Response response = apiCall.postXML("categories", "xml", xml); // request body as XML
        assertTrue(Arrays.asList(successCodes).contains(response.code()));
        JSONParser jsonParser2 = new JSONParser();
        JSONObject json = null;
        try{
            json = (JSONObject) jsonParser2.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        String id = (String) json.get("id");
        Response response1 = apiCall.get("categories/"+id, "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response1.body().close();
        }
        assertEquals("ECSE 429", ((JSONObject) ((JSONArray) (jsonObject1.get("categories"))).get(0)).get("title")); // verify fields of newly created category
        System.out.println("POST categories (XML) -- TEST PASSED");
    }

    @Test
    public void postCategoryXMLMalformed() {
        APICall apiCall = new APICall();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = apiCall.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    size.body().close();
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        String xml = "<todo><description>test</description></todo>";
        apiCall.postXML("/todos", "xml", xml); // malformed XML, no title provided
        Response response = apiCall.postXML("/categories", "xml", xml);
        assertEquals(404, response.code()); // expect error

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = apiCall.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    size2.body().close();
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(categories[1] - categories[0])); // no new object created
        System.out.println("POST categories (XML Malformed) -- TEST PASSED");
    }

}



