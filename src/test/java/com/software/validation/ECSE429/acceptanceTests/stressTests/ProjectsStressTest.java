package com.software.validation.ECSE429.acceptanceTests.stressTests;

import com.software.validation.ECSE429.api.APICall;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
    long startTime = 0;
    long endTime = 0;
    long elapsedTime = 0;

    public static void main(String[] args) {
        SystemReport.initExcel();
        ProjectsStressTest test = new ProjectsStressTest();
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
        for (int i = 0; i < 10000; i++) {
            JSONObject js = new JSONObject(); // Create new JSON object with system selected ID, and input body as
                                              // fields
            js.put("title", "mcgill");
            js.put("description", "Project number " + String.valueOf(i + 1));
            Response response = ap.post("projects", "json", js);

            if (response.code() != 200 && response.code() != 201) {
                throw new RuntimeException("Project POST failed");
            }
            String id = "";
            if (i == 10) {
                startTime = System.currentTimeMillis();

                // PROJECT POST
                JSONObject js2 = new JSONObject();
                js2.put("title", "mcgill");
                js2.put("description", "Project number 10 " + String.valueOf(i + 1));
                Response response2 = ap.post("projects", "json", js2);
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try{
                    json = (JSONObject) parser.parse(response2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                id = json.get("id").toString();

                if (response2.code() != 200 && response2.code() != 201) {
                    throw new RuntimeException("Project POST failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 10 projects: " + elapsedTime + " ms");
                              
                // PROJECT MODIFY
                startTime = System.currentTimeMillis();
                JSONObject js3 = new JSONObject();
                js3.put("title", "mcgill");
                js3.put("description", "Project number 10 update " + String.valueOf(i + 1));
                Response response3 = ap.put("projects/" + id, "json", js3);
                if (response3.code() != 200 && response3.code() != 201) {
                    throw new RuntimeException("Project PUT failed");
                }

                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to update 10 projects: " + elapsedTime + " ms");

                // PROJECT DELETE
                startTime = System.currentTimeMillis();
                Response response4 = ap.delete("projects/" + id, "json");
                if (response4.code() != 200 && response4.code() != 201) {
                    throw new RuntimeException("Project DELETE failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to delete 10 projects: " + elapsedTime + " ms");

            }

            else if (i == 20) {
                // PROJECT POST
                startTime = System.currentTimeMillis();
                JSONObject js2 = new JSONObject();
                js2.put("title", "mcgill");
                js2.put("description", "Project number 20 " + String.valueOf(i + 1));
                Response response2 = ap.post("projects", "json", js2);
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try {
                    json = (JSONObject) parser.parse(response2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                id = json.get("id").toString();

                if (response2.code() != 200 && response2.code() != 201) {
                    throw new RuntimeException("Project POST failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 20 projects: " + elapsedTime + " ms");

                // PROJECT MODIFY
                startTime = System.currentTimeMillis();
                JSONObject js3 = new JSONObject();
                js3.put("title", "mcgill");
                js3.put("description", "Project number 20 update " + String.valueOf(i + 1));
                Response response3 = ap.put("projects/" + id, "json", js3);
                if (response3.code() != 200 && response3.code() != 201) {
                    throw new RuntimeException("Project PUT failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to update 20 projects: " + elapsedTime + " ms");

                // PROJECT DELETE
                startTime = System.currentTimeMillis();
                Response response4 = ap.delete("projects/" + id, "json");
                if (response4.code() != 200 && response4.code() != 201) {
                    throw new RuntimeException("Project DELETE failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to delete 20 projects: " + elapsedTime + " ms");

            }

            else if (i == 100) {
                // PROJECT POST
                startTime = System.currentTimeMillis();
                JSONObject js2 = new JSONObject();
                js2.put("title", "mcgill");
                js2.put("description", "Project number 100 " + String.valueOf(i + 1));
                Response response2 = ap.post("projects", "json", js2);
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try {
                    json = (JSONObject) parser.parse(response2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                id = json.get("id").toString();

                if (response2.code() != 200 && response2.code() != 201) {
                    throw new RuntimeException("Project POST failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 100 projects: " + elapsedTime + " ms");

                // PROJECT MODIFY
                startTime = System.currentTimeMillis();
                JSONObject js3 = new JSONObject();
                js3.put("title", "mcgill");
                js3.put("description", "Project number 100 update " + String.valueOf(i + 1));
                Response response3 = ap.put("projects/" + id, "json", js3);
                if (response3.code() != 200 && response3.code() != 201) {
                    throw new RuntimeException("Project PUT failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to update 100 projects: " + elapsedTime + " ms");

                // PROJECT DELETE
                startTime = System.currentTimeMillis();
                Response response4 = ap.delete("projects/" + id, "json");
                if (response4.code() != 200 && response4.code() != 201) {
                    throw new RuntimeException("Project DELETE failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to delete 100 projects: " + elapsedTime + " ms");
            }

            else if (i == 1000) {
                // PROJECT POST
                startTime = System.currentTimeMillis();
                JSONObject js2 = new JSONObject();
                js2.put("title", "mcgill");
                js2.put("description", "Project number 1000 " + String.valueOf(i + 1));
                Response response2 = ap.post("projects", "json", js2);
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try {
                    json = (JSONObject) parser.parse(response2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                id = json.get("id").toString();

                if (response2.code() != 200 && response2.code() != 201) {
                    throw new RuntimeException("Project POST failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 1000 projects: " + elapsedTime + " ms");

                // PROJECT MODIFY
                startTime = System.currentTimeMillis();
                JSONObject js3 = new JSONObject();
                js3.put("title", "mcgill");
                js3.put("description", "Project number 1000 update " + String.valueOf(i + 1));
                Response response3 = ap.put("projects/" + id, "json", js3);
                if (response3.code() != 200 && response3.code() != 201) {
                    throw new RuntimeException("Project PUT failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to update 1000 projects: " + elapsedTime + " ms");

                // PROJECT DELETE
                startTime = System.currentTimeMillis();
                Response response4 = ap.delete("projects/" + id, "json");
                if (response4.code() != 200 && response4.code() != 201) {
                    throw new RuntimeException("Project DELETE failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to delete 1000 projects: " + elapsedTime + " ms");
            }
            
            else if (i == 10000) {
                // PROJECT POST
                startTime = System.currentTimeMillis();
                JSONObject js2 = new JSONObject();
                js2.put("title", "mcgill");
                js2.put("description", "Project number 10000 " + String.valueOf(i + 1));
                Response response2 = ap.post("projects", "json", js2);
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try {
                    json = (JSONObject) parser.parse(response2.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                id = json.get("id").toString();

                if (response2.code() != 200 && response2.code() != 201) {
                    throw new RuntimeException("Project POST failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 10000 projects: " + elapsedTime + " ms");

                // PROJECT MODIFY
                startTime = System.currentTimeMillis();
                JSONObject js3 = new JSONObject();
                js3.put("title", "mcgill");
                js3.put("description", "Project number 10000 update " + String.valueOf(i + 1));
                Response response3 = ap.put("projects/" + id, "json", js3);
                if (response3.code() != 200 && response3.code() != 201) {
                    throw new RuntimeException("Project PUT failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 10000 projects: " + elapsedTime + " ms");

                // PROJECT DELETE
                startTime = System.currentTimeMillis();
                Response response4 = ap.delete("projects/" + id, "json");
                if (response4.code() != 200 && response4.code() != 201) {
                    throw new RuntimeException("Project DELETE failed");
                }
                endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;
                System.out.println("Time taken to create 10000 projects: " + elapsedTime + " ms");
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
