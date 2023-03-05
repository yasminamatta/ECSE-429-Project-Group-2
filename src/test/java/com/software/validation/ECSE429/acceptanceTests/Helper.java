package com.software.validation.ECSE429.acceptanceTests;

import org.junit.Assert;

public class Helper {

    public void the_server_is_running() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Running", "Error");
        }
    }

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
}
