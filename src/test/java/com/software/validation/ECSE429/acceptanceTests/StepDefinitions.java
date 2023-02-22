package com.software.validation.ECSE429.acceptanceTests;

import io.cucumber.java.en.Given;
import org.junit.Assert;

public class StepDefinitions {

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
}
