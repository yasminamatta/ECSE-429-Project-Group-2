package com.software.validation.ECSE429.stressTests;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Response;
import org.json.simple.JSONObject;

import java.io.IOException;

public class TodosStressTest {
    APICall ap = new APICall();


    public void test() {
        for(int i=0; i < 1000000; i++) {
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Todo number " + String.valueOf(i+1));
            Response response = ap.post("todos", "json", js);

            if(response.code() != 200 && response.code() != 201){
                throw new RuntimeException("Todo POST failed");
            }

            if(i == 10){

                // TODO POST
                // TODO MODIFY
                // TODO DELETE
            }

        }

    }
}
