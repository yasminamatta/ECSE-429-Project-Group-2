package com.software.validation.ECSE429.acceptanceTests.stressTests;

import com.software.validation.ECSE429.api.APICall;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TodosStressTest {
    APICall ap = new APICall();
    SystemReport sr = new SystemReport();


    public static void main(String[] args) {
        SystemReport.initExcel();
        TodosStressTest test = new TodosStressTest();
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
        for(int i=0; i < 1000; i++) {
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

            try {
                SystemReport.report(sheet, workbook);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
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