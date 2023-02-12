package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.Random.class)
public class TodosTest {

    Integer successCodes[] = {200, 201}; // HTML success codes for OK and CREATE
    int todos[] = {0, 0};


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
    public void getTodos() {
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

        int size = ((JSONArray)(json.get("todos"))).size();
        Assert.assertEquals(2, size); // API initially has 2 todos loaded in as default.

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        System.out.println("GET todos -- TEST PASSED");

    }

    @Test
    public void headTodos() {
        APICall api = new APICall();
        Response response = api.head("todos", "json");
        Headers headers = response.headers();

        Assert.assertEquals(4, headers.size()); // 4 headers exist. Check that 4 are returned as response
        Assert.assertEquals("application/json", headers.get("Content-Type").toString()); // checks that output is of JSON format

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        System.out.println("HEAD todos -- TEST PASSED");

    }

    @Test
    public void postTodo() {
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
                }
                finally {
                    size.body().close();
                }
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", "mcgill");
        js.put("description", "okhttp");
        Response response = ap.post("todos", "json", js);

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


            Response size = ap.get("todos/"+json.get("id"), "json");
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(size.body().string()); // parse body into created JSON object
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                size.body().close();
            }


            // check that the id and the body of the created post both match
            Assert.assertEquals("mcgill", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title"));
            Assert.assertEquals("okhttp", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description"));
            Assert.assertEquals("false", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("doneStatus"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("id"));

            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check that status code is either OK (200) or CREATED (201)

        } catch (ParseException e) {
            e.printStackTrace();
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
                }
                finally {
                    size2.body().close();
                }
                todos[1] = ((JSONArray)(json.get("todos"))).size(); // add the new size of todos to array
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(1, Math.abs(todos[1] - todos[0])); // check that only 1 todos-object was added
        System.out.println("POST todos -- TEST PASSED");
    }

    @Test
    public void getTodoById() {
        APICall ap = new APICall();
        Response response = ap.get("todos/2", "json"); // ID as path variable
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int size = ((JSONArray)(json.get("todos"))).size();
        Assert.assertEquals(1, size);

        // get contents of the body in string format
        String id = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("id");
        String title = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("title");
        String doneStatus = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("doneStatus");
        String description = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("description");

        // check if the body matches the query
        Assert.assertEquals("2", id);
        Assert.assertEquals("file paperwork", title);
        Assert.assertEquals("false", doneStatus);
        Assert.assertEquals("", description);

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        System.out.println("GET todos/:id -- TEST PASSED");


    }

    @Test
    public void getTodoByWrongId() {
        APICall ap = new APICall();
        Response response = ap.get("todos/2000", "json"); // wrong ID as path variable
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        String error = (String) ((JSONArray)(json.get("errorMessages"))).get(0);
        Assert.assertEquals("Could not find an instance with todos/2000", error);

        int code = response.code();
        Assert.assertFalse(Arrays.asList(successCodes).contains(code)); // expect error
        System.out.println("GET todos/:id (Wrong ID) -- TEST PASSED");
    }

    @Test
    public void headTodoById() {
        APICall api = new APICall();
        Response response = api.head("todos/2", "json"); // query done with ID as path variable
        Headers headers = response.headers();

        Assert.assertEquals(4, headers.size()); // expect 4 headers
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok
        System.out.println("HEAD todos/:id -- TEST PASSED");
    }

    @Test
    public void headTodoByWrongId() {
        APICall api = new APICall();
        Response response = api.head("todos/2000", "json"); // query done with ID as path variable
        Headers headers = response.headers();

        Assert.assertEquals(4, headers.size()); // expect 4 headers
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertFalse(Arrays.asList(successCodes).contains(code)); // expect error
        System.out.println("HEAD todos/:id (Wrong ID) -- TEST PASSED");
    }

    @Test
    public void postTodoByWrongId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject();
        js.put("title", "scan homework");
        js.put("description", "scan every page");
        Response response = ap.post("todos/2000", "json", js); // posting to todos with ID that does not exist in system

        JSONObject responsePost = null;
        JSONParser parser = new JSONParser();
        try {
            responsePost = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }


        String error = (String) ((JSONArray)(responsePost.get("errorMessages"))).get(0);
        Assert.assertEquals("No such todo entity instance with GUID or ID 2000 found", error);


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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        int code = response.code();
        Assert.assertFalse(Arrays.asList(successCodes).contains(code)); // expect error

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // check that no new todos was created
        System.out.println("POST todos/:id (Wrong ID) -- TEST PASSED");

    }

    @Test
    public void postTodoById() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject();
        js.put("title", "scan homework");
        js.put("description", "scan every page");
        Response response = ap.post("todos/1", "json", js); // posting to already existing todos with ID.

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


            Response res = ap.get("todos/"+json.get("id"), "json"); // getting the created todos again to see if POST worked
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(res.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                res.body().close();
            }

            // check if the new body is now with the todos object with the given ID
            Assert.assertEquals("scan homework", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title"));
            Assert.assertEquals("scan every page", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description"));
            Assert.assertEquals("false", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("doneStatus"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML response is ok

        } catch (ParseException e) {
            e.printStackTrace();
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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // check that no new todos was created. Body of one todos should've been overwritten
        System.out.println("POST todos/:id -- TEST PASSED");

    }

    @Test
    public void putTodoById() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject();
        js.put("title", "shred papers");
        js.put("description", "shred each paper into 100 pieces");
        js.put("doneStatus", false);
        JSONObject tasksof = new JSONObject();
        tasksof.put("id", "1");
        js.put("tasksof", tasksof);
        Response response = ap.put("todos/2", "json", js); // using id = 2. Should completely replace the current entry with ID=2.

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


            Response res = ap.get("todos/"+json.get("id"), "json");
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
            Assert.assertEquals("shred papers", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title"));
            Assert.assertEquals("shred each paper into 100 pieces", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description"));
            Assert.assertEquals("false", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("doneStatus"));
            Assert.assertEquals(json.get("id"), ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("id"));
            int code = response.code();
            Assert.assertTrue(Arrays.asList(successCodes).contains(code));

        } catch (ParseException e) {
            e.printStackTrace();
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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // no new entry should be created. Only the body of an existing todos should completely change.
        System.out.println("PUT todos/:id -- TEST PASSED");

    }

    @Test
    public void putTodoByWrongId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject();
        js.put("title", "shred papers");
        js.put("description", "shred each paper into 100 pieces");
        js.put("doneStatus", false);
        JSONObject tasksof = new JSONObject();
        tasksof.put("id", "1");
        js.put("tasksof", tasksof);
        Response response = ap.put("todos/2000", "json", js); // using id = 2. Should completely replace the current entry with ID=2.

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        int code = response.code();
        Assert.assertFalse(Arrays.asList(successCodes).contains(code)); // expect error
        JSONObject json = null;
        try {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(responsePost);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        String error = (String) ((JSONArray)(json.get("errorMessages"))).get(0);
        Assert.assertEquals("Invalid GUID for 2000 entity todo", error);


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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // no new entry should be created. Only the body of an existing todos should completely change.
        System.out.println("PUT todos/:id (Wrong ID) -- TEST PASSED");

    }

    @Test
    public void deleteTodoById() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size(); // getting number of categories present before 1 entry is deleted
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        Response response = ap.delete("todos/1", "json"); // delete todos with ID=1

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // Ensure HTML response is ok



        Response retrieveDeleted = ap.get("todos/1", "json"); // now try to get the same todos of ID=1, although it's deleted.
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
        Assert.assertEquals("Could not find an instance with todos/1", error); // returning error as expected



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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        Assert.assertEquals(1, Math.abs(todos[1] - todos[0])); // the difference should be 1, between what the total number of initial todos and the current number
        System.out.println("DELETE todos/:id -- TEST PASSED");

    }

    @Test
    public void deleteTodoByWrongId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size(); // getting number of categories present before 1 entry is deleted
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        Response response = ap.delete("todos/2000", "json"); // delete todos with ID=1

        int code = response.code();
        Assert.assertFalse(Arrays.asList(successCodes).contains(code)); // Ensure HTML response is not ok


        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        String error = (String) ((((JSONArray)json.get("errorMessages")).get(0)));
        Assert.assertEquals("Could not find any instances with todos/2000", error); // returning error as expected



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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }
        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // the difference should be 0, between what the total number of initial todos and the current number
        System.out.println("DELETE todos/:id (Wrong ID) -- TEST PASSED");

    }

    @Test
    public void getTasksOfTodoById() {

        int counter = 0;
        boolean related = false;


        APICall ap = new APICall();
        Response response = ap.get("todos/2/tasksof", "json"); // Requesting all project related to todos of ID=1 by relationship tasksof
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
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not

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
                if(id.equals("2")){
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
        System.out.println("GET todos/:id/tasksof -- TEST PASSED");

    }

    @Test
    public void getTasksOfTodoByBug() {

        APICall ap = new APICall();
        Response response = ap.get("todos/2000/tasksof", "json"); // Requesting all project related to todos of ID=1 by relationship tasksof
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }

        Assert.assertEquals(2, ((JSONArray)json.get("projects")).size()); // no project should be related to todos 2000 as object does not exist

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // status code should not be ok
        System.out.println("GET todos/:id/tasksof (BUG) -- DEMONSTRATED");

    }

    @Test
    public void headTasksOfTodoById() {
        APICall api = new APICall();
        Response response = api.head("todos/1/tasksof", "json"); // querying projects related to todos with ID=1.
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok

        System.out.println("HEAD todos/:id/tasksof -- TEST PASSED");
    }

    @Test
    public void postTasksOfTodoById() {
        APICall ap = new APICall();
        String[] newTodoId = {""};
        int counter = 0;
        boolean related = false;



        // create dummy object first
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject js = new JSONObject();
                js.put("title", "scan lab");
                js.put("description", "scan every lab");
                Response response = ap.post("todos", "json", js);

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
                newTodoId[0] = (String) json.get("id"); // saving the category ID for reference



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
                todos[0] = ((JSONArray)(json.get("todos"))).size(); // getting the size of the categories before POST
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

                Response res = ap.post("todos/" + newTodoId[0] + "/tasksof", "json", relationBody); // Establishing relationship using POST

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



        Response response = ap.get("todos/" + newTodoId[0] + "/tasksof", "json"); // Getting the projects assigned to the new todos
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }


        int mapSize = ((JSONArray)(json.get("projects"))).size();
        List<String> map = new ArrayList<>();
        for(int i = 0; i < mapSize; i++) {
            map.add("false");
        }

        for (Object projectArray : ((JSONArray)(json.get("projects")))) { // Iterating through all fields of the project to verify relationship

            JSONObject projectObject = (JSONObject) projectArray;

            JSONArray tasks = (JSONArray) projectObject.get("tasks");

            for (Object obj : tasks) {
                JSONObject JSONobj = (JSONObject) obj;
                String id = (String) JSONobj.get("id");
                if(id.equals(newTodoId[0])){
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
        Assert.assertTrue(related); // Verify that relation exists

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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("POST todos/:id/tasksof -- TEST PASSED");

    }

    @Test
    public void deleteTasksOfTodoById() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
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

                Response res = ap.delete("todos/1/tasksof/1", "json"); // deleting relationship tasksOf between todos with id=1 and projects with id=1.

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



        Response response = ap.get("todos/1/tasksof", "json");
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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // Confirmation that only relationship was modified, no objects deleted
        System.out.println("DELETE todos/:id/tasksof/:id -- TEST PASSED");

    }

    @Test
    public void getCategoriesOfTodoById() {
        APICall ap = new APICall();
        Response response = ap.get("todos/1/categories", "json"); // Requesting all categories related to todos of ID=1
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
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(1, size); // only one category is related to the todos by default
        System.out.println("GET todos/:id/categories -- TEST PASSED");

    }

    @Test
    public void headCategoriesOfTodoById() {


        APICall ap = new APICall();
        Response response = ap.head("todos/1/categories", "json"); // querying categories related to todos with ID=1
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());
        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if HTML code is ok
        System.out.println("HEAD todos/:id/categories -- TEST PASSED");
    }

    @Test
    public void headCategoriesOfTodoByWrongIdBug() {
        APICall ap = new APICall();
        Response response = ap.head("todos/200/categories", "json"); // querying categories related to todos with ID=1
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size()); // expect 4 headers regardless
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());
        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // status code should not be OK
        System.out.println("HEAD todos/:id/categories (BUG) -- DEMONSTRATED");
    }

    @Test
    public void postCategoriesOfTodoById() {
        APICall ap = new APICall();
        String[] newTodoId = {""};
        int counter = 0;
        boolean related = false;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("todos/2/categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                Assert.assertEquals(0, ((JSONArray)(json.get("categories"))).size()); // no categories assigned to the todos initially

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
                todos[0] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        JSONObject js = new JSONObject();
        js.put("id", "1");
        Response response = ap.post("todos/2/categories", "json", js); // establishing new relationship

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if operation was successful


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("todos/2/categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                Assert.assertEquals(1, ((JSONArray)(json.get("categories"))).size()); // verify newly created relationship


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
                todos[1] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // no new object created, only relationship established
        System.out.println("POST todos/:id/categories -- TEST PASSED");

    }

    @Test
    public void deleteCategoriesOfTodoById() {
        APICall ap = new APICall();
        String[] newTodoId = {""};
        int counter = 0;
        boolean related = false;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("todos/1/categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                Assert.assertEquals(1, ((JSONArray)(json.get("categories"))).size());


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
                todos[0] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Response response = ap.delete("todos/1/categories/1", "json"); // deleting relationship category with id=1 and todos with id=1.

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check whether successful or not


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = ap.get("todos/1/categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    size.body().close();
                }
                Assert.assertEquals(0, ((JSONArray)(json.get("categories"))).size()); // relationship deleted


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
                todos[1] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // no new object created, only relationship modified
        System.out.println("DELETE todos/:id/categories -- TEST PASSED");

    }



    @Test
    public void postTodoJSONMalformed() {
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
                }
                finally {
                    size.body().close();
                }
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }


        JSONObject js = new JSONObject();
        js.put("doneStatus", false);
        js.put("description", "okhttp");
        Response response = ap.post("todos", "json", js); // malformed json as no title field is provided
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
                Response size2 = ap.get("todos", "json");
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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // verify no new object created
        System.out.println("POST todos (JSON Malformed) -- TEST PASSED");


    }

    @Test
    public void postTodoXML() {
        APICall apiCall = new APICall();
        String xml = "<todo><title>ECSE 429</title><description>Software Validation</description></todo>";
        Response response = apiCall.postXML("todos", "xml", xml); // request body as XML
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
        Response response1 = apiCall.get("todos/"+id, "json");
        JSONParser jsonParser1 = new JSONParser();
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonParser1.parse(response1.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response1.body().close();
        }
        assertEquals("ECSE 429", ((JSONObject) ((JSONArray) (jsonObject1.get("todos"))).get(0)).get("title")); // verify fields of newly created object
        System.out.println("POST todos (XML) -- TEST PASSED");
    }

    @Test
    public void postTodoXMLMalformed() {
        APICall apiCall = new APICall();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size = apiCall.get("todos", "json");
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
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
        Response response = apiCall.postXML("/todos", "xml", xml);
        assertEquals(404, response.code()); // expect error


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response size2 = apiCall.get("todos", "json");
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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {
            assertEquals("Thread join failed", "Thread join successful");
        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0])); // no new object created
        System.out.println("POST todos (XML Malformed) -- TEST PASSED");
    }

}
