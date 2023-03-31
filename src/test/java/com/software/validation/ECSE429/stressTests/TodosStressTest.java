package com.software.validation.ECSE429.stressTests;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TodosStressTest {
    APICall ap = new APICall();
    SystemReport sr = new SystemReport();


    public static void main(String[] args) {
        SystemReport.initExcel("todos_interval.xlsx");
        //SystemReport.initExcel("todos_polling.xlsx");
        TodosStressTest test = new TodosStressTest();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("todos_interval.xlsx");
            //inputStream = new FileInputStream("todos_polling.xlsx");

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            test.testInterval(sheet, workbook, "todos_interval.xlsx");
            //test.testPolling(sheet, workbook, "todos_polling.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testInterval(Sheet sheet, Workbook workbook, String filename) {
        for(int i=0; i < 10002; i++) {

            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Todo number " + String.valueOf(i+1));
            Response response = ap.post("todos", "json", js);
            if(response.code() != 200 && response.code() != 201){
                throw new RuntimeException("Todo POST failed for " + i);
            }


            if(i == 10) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 20) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 50) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }

            else if(i == 100) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 500) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 1000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }

            else if(i == 2000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 3000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 4000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 5000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            else if(i == 10000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }

        }
        writeClose(workbook, filename);
    }

    public void testPolling(Sheet sheet, Workbook workbook, String filename) {
        for(int i=0; i < 1000; i++) {
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Todo number " + String.valueOf(i+1));
            Response response = ap.post("todos", "json", js);
            if(response.code() != 200 && response.code() != 201){
                throw new RuntimeException("Todo POST failed for " + i);
            }

            takeReadingPost(sheet, workbook, i);
            takeReadingModify(sheet, workbook, i);
            takeReadingDelete(sheet, workbook, i);

            System.out.println(i);
        }
        writeClose(workbook, filename);
    }


    public void takeReadingPost(Sheet sheet, Workbook workbook, int i) {
        final long start = System.currentTimeMillis();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", "mcgill");
        js.put("description", "Todo number " + String.valueOf(i+1));
        Response response = ap.post("todos", "json", js);
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Todo POST failed for " + i);
        }
        final long end = System.currentTimeMillis();

        try {
            Row row = SystemReport.report(workbook, start, end, "Post");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeReadingDelete(Sheet sheet, Workbook workbook, int i) {
        final long start = System.currentTimeMillis();

        Response response = ap.delete("todos/" + String.valueOf(i+1), "json");
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Todo DELETE failed for " + i);
        }
        final long end = System.currentTimeMillis();

        try {
            Row row = SystemReport.report(workbook, start, end, "Delete");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeReadingModify(Sheet sheet, Workbook workbook, int i) {
        final long start = System.currentTimeMillis();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("description", "Todo number modified " + String.valueOf(i+1));
        Response response = ap.post("todos/" + String.valueOf(i+1), "json", js);
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Todo MODIFY failed for " + i);
        }
        final long end = System.currentTimeMillis();

        try {
            Row row = SystemReport.report(workbook, start, end, "Modify");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeClose(Workbook workbook, String filename) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}