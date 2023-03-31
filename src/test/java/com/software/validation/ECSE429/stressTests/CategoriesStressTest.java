package com.software.validation.ECSE429.stressTests;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CategoriesStressTest {

    APICall ap = new APICall();
    SystemReport sr = new SystemReport();
    int deleteCounter = 1;

    public static void main(String[] args) {
        SystemReport.initExcel();
        CategoriesStressTest test = new CategoriesStressTest();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("report.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            test.test(sheet, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test(Sheet sheet, Workbook workbook) {
        for(int i=0; i < 10002; i++) {
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
            js.put("title", "mcgill");
            js.put("description", "Category number " + String.valueOf(i+1));
            Response response = ap.post("categories", "json", js);

            if(response.code() != 200 && response.code() != 201){
                throw new RuntimeException("Category POST failed");
            }

            if(i == 10) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 20) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 50) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }

            if(i == 100) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 500) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 1000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }

            if(i == 2000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 3000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 4000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 5000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }
            if(i == 10000) {
                takeReadingPost(sheet, workbook, i);
                takeReadingModify(sheet, workbook, i);
                takeReadingDelete(sheet, workbook, i);
            }

        }
        writeClose(workbook);
    }

    public void takeReadingPost(Sheet sheet, Workbook workbook, int i) {
        final long start = System.currentTimeMillis();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("title", "mcgill");
        js.put("description", "Category number " + String.valueOf(i+1));
        Response response = ap.post("categories", "json", js);
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Category POST failed");
        }
        final long end = System.currentTimeMillis();

        try {
            Row row = SystemReport.report(sheet, workbook, start, end);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeReadingDelete(Sheet sheet, Workbook workbook, int i) {
        final long start = System.currentTimeMillis();

        Response response = ap.delete("categories/" +String.valueOf(i-5), "json");
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Category DELETE failed");
        }
        final long end = System.currentTimeMillis();

        try {
            Row row = SystemReport.report(sheet, workbook, start, end);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeReadingModify(Sheet sheet, Workbook workbook, int i) {
        final long start = System.currentTimeMillis();
        JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as fields
        js.put("description", "Category number modified " + String.valueOf(i+1));
        Response response = ap.post("categories/" + String.valueOf(i-3), "json", js);
        if(response.code() != 200 && response.code() != 201){
            throw new RuntimeException("Category MODIFY failed");
        }
        final long end = System.currentTimeMillis();

        try {
            Row row = SystemReport.report(sheet, workbook, start, end);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeClose(Workbook workbook) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("report.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
