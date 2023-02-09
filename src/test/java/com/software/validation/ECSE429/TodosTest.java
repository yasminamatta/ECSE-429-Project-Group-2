package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@SpringBootTest
public class TodosTest {

    Integer successCodes[] = {200, 201};
    int todos[] = {0, 0};


    @BeforeClass
    public static void setupEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            System.out.println("Setting up environment");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void resetEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("npx kill-port 4567");
            System.out.println("Resetting environment");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void get() {
        APICall ap = new APICall();
        Response response = ap.get("todos", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("todos"))).size();
        Assert.assertEquals(2, size);

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        System.out.println("GET todos -- TEST PASSED");

    }

    @Test
    public void head() {
        APICall api = new APICall();
        Response response = api.head("todos", "json");
        Headers headers = response.headers();

        Assert.assertEquals(4, headers.size());
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        System.out.println("HEAD todos -- TEST PASSED");

    }

    @Test
    public void post() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        JSONObject js = new JSONObject();
        js.put("title", "mcgill");
        js.put("description", "okhttp");
        Response response = ap.post("todos", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responsePost);


            Response size = ap.get("todos/"+json.get("id"), "json");
            JSONParser parserResponse = new JSONParser();
            JSONObject jsonResponse = null;
            try {
                jsonResponse = (JSONObject) parserResponse.parse(size.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }


            Assert.assertEquals("mcgill", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title"));
            Assert.assertEquals("okhttp", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description"));
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
                }
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(1, Math.abs(todos[1] - todos[0]));
        System.out.println("POST todos -- TEST PASSED");


    }


    @Test
    public void getId() {
        APICall ap = new APICall();
        Response response = ap.get("todos/2", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("todos"))).size();
        Assert.assertEquals(1, size);

        String id = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("id");
        String title = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("title");
        String doneStatus = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("doneStatus");
        String description = (String) ( (JSONObject) ((JSONArray)(json.get("todos"))).get(0)).get("description");

        Assert.assertEquals("2", id);
        Assert.assertEquals("file paperwork", title);
        Assert.assertEquals("false", doneStatus);
        Assert.assertEquals("", description);

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        System.out.println("GET todos/:id -- TEST PASSED");


    }

    @Test
    public void headId() {
        APICall api = new APICall();
        Response response = api.head("todos/2", "json");
        Headers headers = response.headers();

        Assert.assertEquals(4, headers.size());
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        System.out.println("HEAD todos/:id -- TEST PASSED");
    }



    @Test
    public void postId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        JSONObject js = new JSONObject();
        js.put("title", "scan homework");
        js.put("description", "scan every page");
        Response response = ap.post("todos/1", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
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
            }


            Assert.assertEquals("scan homework", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("title"));
            Assert.assertEquals("scan every page", ((JSONObject)(((JSONArray)jsonResponse.get("todos")).get(0))).get("description"));
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
                }
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("POST todos/:id -- TEST PASSED");

    }

    @Test
    public void putId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        JSONObject js = new JSONObject();
        js.put("title", "shred papers");
        js.put("description", "shred each paper into 100 pieces");
        js.put("doneStatus", false);
        JSONObject tasksof = new JSONObject();
        tasksof.put("id", "1");
        js.put("tasksof", tasksof);
        Response response = ap.put("todos/2", "json", js);

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
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
            }


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
                }
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("PUT todos/:id -- TEST PASSED");

    }

    @Test
    public void deleteId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        Response response = ap.delete("todos/1", "json");

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));



        Response retrieveDeleted = ap.get("todos/1", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(retrieveDeleted.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String error = (String) ((((JSONArray)json.get("errorMessages")).get(0)));
        Assert.assertEquals("Could not find an instance with todos/1", error);



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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }
        Assert.assertEquals(1, Math.abs(todos[1] - todos[0]));
        System.out.println("DELETE todos/:id -- TEST PASSED");

    }

    @Test
    public void getIdTasksOf() {

        int counter = 0;
        boolean related = false;


        APICall ap = new APICall();
        Response response = ap.get("todos/2/tasksof", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("projects"))).size();
        Assert.assertEquals(1, size);
        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));

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

        Assert.assertTrue(related);
        System.out.println("GET todos/:id/tasksof -- TEST PASSED");

    }

    @Test
    public void headIdTasksOf() {
        APICall api = new APICall();
        Response response = api.head("todos/1/tasksof", "json");
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size());
        //System.out.println("Size: " + headers.size() + " - TEST PASSED");
        Assert.assertEquals("application/json", headers.get("Content-Type").toString());
        //System.out.println("Content-Type: " + headers.get("Content-Type").toString() + " - TEST PASSED");

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));

        System.out.println("HEAD todos/:id/tasksof -- TEST PASSED");
    }


    @Test
    public void postIdTasksOf() {
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
                }

                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(responsePost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newTodoId[0] = (String) json.get("id");



            }
        });

        t3.start();
        try {
            t3.join();
        } catch (Exception e) {

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
                }
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject relationBody = new JSONObject();
                relationBody.put("id", "1");

                Response res = ap.post("todos/" + newTodoId[0] + "/tasksof", "json", relationBody);

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code));
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {

        }



        Response response = ap.get("todos/" + newTodoId[0] + "/tasksof", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        Assert.assertTrue(related);

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
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("POST todos/:id/tasksof -- TEST PASSED");

    }


    @Test
    public void deleteIdTasksOfId() {
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
                todos[0] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }


        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {

                Response res = ap.delete("todos/1/tasksof/1", "json");

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code));
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {

        }



        Response response = ap.get("todos/1/tasksof", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
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
                }
                todos[1] = ((JSONArray)(json.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("DELETE todos/:id/tasksof/:id -- TEST PASSED");

    }


    @Test
    public void getIdCategories() {
        APICall ap = new APICall();
        Response response = ap.get("todos/1/categories", "json");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));
        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(1, size);
        System.out.println("GET todos/:id/categories -- TEST PASSED");

    }


    @Test
    public void headIdCategories() {


        APICall ap = new APICall();
        Response response = ap.head("todos/1/categories", "json");
        Headers headers = response.headers();
        Assert.assertEquals(4, headers.size());

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));


        System.out.println("HEAD todos/:id/categories -- TEST PASSED");

    }


    @Test
    public void postIdCategories() {
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
                }
                Assert.assertEquals(0, ((JSONArray)(json.get("categories"))).size());

                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                todos[0] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }

        JSONObject js = new JSONObject();
        js.put("id", "1");
        Response response = ap.post("todos/2/categories", "json", js);

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));


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
                }
                Assert.assertEquals(1, ((JSONArray)(json.get("categories"))).size());


                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                todos[1] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("POST todos/:id/categories -- TEST PASSED");

    }


    @Test
    public void deleteIdCategories() {
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
                }
                Assert.assertEquals(1, ((JSONArray)(json.get("categories"))).size());


                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                todos[0] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }

        Response response = ap.delete("todos/1/categories/1", "json");

        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code));


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
                }
                Assert.assertEquals(0, ((JSONArray)(json.get("categories"))).size());


                Response responseOfTodos = ap.get("todos", "json");
                JSONParser parserOfTodos = new JSONParser();
                JSONObject jsonOfTodos = null;
                try {
                    jsonOfTodos = (JSONObject) parserOfTodos.parse(responseOfTodos.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                todos[1] = ((JSONArray)(jsonOfTodos.get("todos"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(0, Math.abs(todos[1] - todos[0]));
        System.out.println("DELETE todos/:id/categories -- TEST PASSED");

    }


}
