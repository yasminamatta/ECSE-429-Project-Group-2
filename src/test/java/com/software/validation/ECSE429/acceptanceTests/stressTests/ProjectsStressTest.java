package com.software.validation.ECSE429.acceptanceTests.stressTests;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Response;
import org.json.simple.JSONObject;

import java.io.IOException;

public class ProjectsStressTest {
    APICall apiCall = new APICall();

    public void test() {
        for (int i = 0; i < 1000000; i++) {
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Project number " + String.valueOf(i + 1));
            Response response = apiCall.post("projects", "json", js);

            if (response.code() != 200 && response.code() != 201) {
                throw new RuntimeException("Project POST failed");
            }

            if (i == 10) {

                // TODO POST
                // TODO MODIFY
                // TODO DELETE
            }

        }
    }
}
