package com.software.validation.ECSE429.stressTests;

import com.software.validation.ECSE429.api.APICall;

import org.apache.poi.ss.usermodel.Row;
import org.json.simple.parser.JSONParser;
import okhttp3.Response;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProjectsStressTest {
    APICall ap = new APICall();
    SystemReport sr = new SystemReport();


    public static void main(String[] args) {
        setupEnvironment();
        SystemReport.initExcel("projects_interval.xlsx");
        //SystemReport.initExcel("projects_polling.xlsx");
        ProjectsStressTest test = new ProjectsStressTest();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("projects_interval.xlsx");
            //inputStream = new FileInputStream("projects_polling.xlsx");

            Workbook workbook = new XSSFWorkbook(inputStream);
            test.testInterval(workbook, "projects_interval.xlsx");
            //test.testPolling(workbook, "projects_polling.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetEnvironment();
    }

    public void testInterval(Workbook workbook, String filename) {
        for(int i=0; i < 10002; i++) {

            // create new object at every iteration
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Project number " + String.valueOf(i+1));
            Response response = ap.post("projects", "json", js);
            if(response.code() != 200 && response.code() != 201){
                throw new RuntimeException("Project POST failed for " + i);
            }

            // take reading at certain interval
            if(i == 10) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 20) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 50) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }

            else if(i == 100) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 500) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 1000) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }

            else if(i == 2000) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 3000) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 4000) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 5000) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }
            else if(i == 10000) {
                takeReadingPost(workbook, i);
                takeReadingModify(workbook, i);
                takeReadingDelete(workbook, i);
            }

        }
        writeClose(workbook, filename);
    }

    public void testPolling(Workbook workbook, String filename) {
        for(int i=0; i < 1000; i++) {

            // create new object at every iteration
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Project number " + String.valueOf(i+1));
            Response response = ap.post("projects", "json", js);
            if(response.code() != 200 && response.code() != 201){
                throw new RuntimeException("Project POST failed for " + i);
            }

            // take reading at every iteration
            takeReadingPost(workbook, i);
            takeReadingModify(workbook, i);
            takeReadingDelete(workbook, i);

            System.out.println(i);
        }
        writeClose(workbook, filename);
    }


    public void takeReadingPost(Workbook workbook, int i) {
        final long start = System.currentTimeMillis();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", "mcgill");
        js.put("description", "Project number " + String.valueOf(i+1));
        Response response = ap.post("projects", "json", js);
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Project POST failed for " + i);
        }
        final long end = System.currentTimeMillis();

        // write to excel file
        try {
            Row row = SystemReport.report(workbook, start, end, "Post");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeReadingDelete(Workbook workbook, int i) {
        final long start = System.currentTimeMillis();

        Response response = ap.delete("projects/" + String.valueOf(i+1), "json");
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Project DELETE failed for " + i);
        }
        final long end = System.currentTimeMillis();

        // write to excel file
        try {
            Row row = SystemReport.report(workbook, start, end, "Delete");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeReadingModify(Workbook workbook, int i) {
        final long start = System.currentTimeMillis();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("description", "Project number modified " + String.valueOf(i+1));
        Response response = ap.post("projects/" + String.valueOf(i+1), "json", js);
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Project MODIFY failed for " + i);
        }
        final long end = System.currentTimeMillis();

        // write to excel file
        try {
            Row row = SystemReport.report(workbook, start, end, "Modify");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeClose(Workbook workbook, String filename) {
        // finalize workbook
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // setup environment
    public static void setupEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar"); // Ensures that the API is ready to be tested
            System.out.println("Setting up environment");
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // reset environment
    public static void resetEnvironment() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("fuser -k 4567/tcp"); // Shuts down the server once testing session is complete.
            System.out.println("Resetting environment");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
