package com.software.validation.ECSE429;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoriesTest {

    Integer successCodes[] = {200, 201}; // HTML success codes for OK and CREATE
    int categories[] = {0, 0}; // empty categories array to be used throughout the testing

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
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join(); // allows for GET to be completed first, before then doing the POST method
        } catch (Exception e) {

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
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size(); // add the new number of categories to array
            }
        });

        t2.start(); // second get method to check that a category was added
        try {
            t2.join();
        } catch (Exception e) {

        }

        Assert.assertEquals(1, Math.abs(categories[1] - categories[0])); // check that only 1 category was added
        System.out.println("POST categories - TEST PASSED");
    }

    @Test
    public void getCategoryWithId() {
        APICall ap = new APICall();
        Response response = ap.get("categories/2", "json"); // ID present in URL
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = ((JSONArray)(json.get("categories"))).size();
        Assert.assertEquals(1, size);

        // get contents of the body in string format
        String id = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("id");
        String title = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("title");
        String description = (String) ( (JSONObject) ((JSONArray)(json.get("categories"))).get(0)).get("description");

        // check if the body matches the query
        Assert.assertEquals("2", id);
        Assert.assertEquals("Home", title);
        Assert.assertEquals("", description);
        int code = response.code();
        Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // check if the HTML response code is a success or not
        System.out.println("GET categories/:id -- TEST PASSED");
    }

    @Test
    public void headCategoryWithId() {
        APICall api = new APICall();
        Response response = api.head("categories/2", "json"); // query done with ID in URL
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
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

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
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

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
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

        }

        JSONObject js = new JSONObject();
        js.put("title", "Yard work");
        js.put("description", "plant trees");
        Response response = ap.put("categories/3", "json", js); // using id = 3. Should completely replace the current entry with ID=3.

        String responsePost = null;
        try {
            responsePost = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
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
            }

            // check if the body matches with input
            Assert.assertEquals("Yard work", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("title"));
            Assert.assertEquals("plant trees", ((JSONObject)(((JSONArray)jsonResponse.get("categories")).get(0))).get("description"));
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
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

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
                }
                categories[0] = ((JSONArray)(json.get("categories"))).size(); // getting number of categories present before 1 is deleted
            }
        });

        t1.start();
        try {
            t1.join();
        } catch (Exception e) {

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
                }
                categories[1] = ((JSONArray)(json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

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
                js.put("title", "School work");
                js.put("description", "submit all assignments");
                Response response = ap.post("categories/1", "json", js); // creating a relationship between category 1 and project 1

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
                newCategoryId[0] = (String) json.get("id"); // saving the category ID for future use in cross-checking
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
                Response size = ap.get("categories", "json");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(size.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                categories[0] = ((JSONArray) (json.get("categories"))).size(); // getting the size of the categories before POST
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

                Response res = ap.post("categories/" + newCategoryId[0] + "/projects", "json", relationBody); // Establishing relationship using POST

                int code = res.code();
                Assert.assertTrue(Arrays.asList(successCodes).contains(code)); // Checking that it was successfully created
            }
        });

        t4.start();
        try {
            t4.join();
        } catch (Exception e) {

        }

        Response response = ap.get("categories/" + newCategoryId[0] + "/projects", "json"); // Getting the project assigned to category 1
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
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
                }
                categories[1] = ((JSONArray) (json.get("categories"))).size();
            }
        });

        t2.start();
        try {
            t2.join();
        } catch (Exception e) {

        }
        Assert.assertEquals(0, Math.abs(categories[1] - categories[0]));
        System.out.println("POST categories/:id/projects -- TEST PASSED");
    }



}



