package com.software.validation.ECSE429.acceptanceTests.stressTests;

import java.io.*;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SystemReport {
    public static void main(String[] args) {
        try {
            //initExcel();
            for(int i = 0; i < 10; i++ ) {
                FileInputStream inputStream = new FileInputStream("report.xlsx");
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);
                report(sheet, workbook);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void report(Sheet sheet, Workbook workbook) throws IOException {

        // Get available memory statistics
        String availmem = executeCommand("free -h");
        String[] avmlines = availmem.split("\n");

        //writer.println("CPU usage statistics:");
        String cpustats = executeCommand("vmstat -n 1 1");
        String[] lines = cpustats.split("\n");
        String[] headers = lines[1].trim().split("\\s+");
        String[] values = lines[2].trim().split("\\s+");
        int usIndex = Arrays.asList(headers).indexOf("us");
        int syIndex = Arrays.asList(headers).indexOf("sy");

        double cpuUsage = Double.parseDouble(values[usIndex]) + Double.parseDouble(values[syIndex]);
        System.out.println("CPU usage: " + cpuUsage + "%");




        //writer.println("Available memory statistics:");
        String[] fields = avmlines[1].split("\\s+"); // total, used, free, shared, buff/cache, available
        //writer.println(String.join(",", fields));


        // ------------------------------- TOTAL MEMORY ----------------------------------
        // Find the first available row in column
        int rowNumTotalMem = sheet.getLastRowNum() + 1;

        // Write the value to the first available cell in column
        Row rowTotalMem = sheet.createRow(rowNumTotalMem);
        Cell cellTotalMem = rowTotalMem.createCell(0);
        double totalMem = 0;
        if(fields[1].contains("Gi")) {
            fields[1] = fields[1].substring(0, fields[1].length() - 2);
            totalMem = Double.valueOf(fields[1]) * 1024;
        } else {
            fields[1] = fields[1].substring(0, fields[1].length() - 2);
            totalMem = Double.valueOf(fields[1]);
        }
        cellTotalMem.setCellValue(totalMem);


        // ------------------------------- USED MEMORY ----------------------------------
        // Write the value to the first available cell in column
        Cell cellUsedMem = rowTotalMem.createCell(1);
        double usedMem = 0;
        if(fields[2].contains("Gi")) {
            fields[2] = fields[2].substring(0, fields[2].length() - 2);
            usedMem = Double.valueOf(fields[2]) * 1024;
        } else {
            fields[2] = fields[2].substring(0, fields[2].length() - 2);
            usedMem = Double.valueOf(fields[2]);
        }
        cellUsedMem.setCellValue(usedMem);


        // ------------------------------- FREE MEMORY ----------------------------------
        // Write the value to the first available cell in column
        Cell cellFreeMem = rowTotalMem.createCell(2);
        double freeMem = 0;
        if(fields[3].contains("Gi")) {
            fields[3] = fields[3].substring(0, fields[3].length() - 2);
            freeMem = Double.valueOf(fields[3]) * 1024;
        } else {
            fields[3] = fields[3].substring(0, fields[3].length() - 2);
            freeMem = Double.valueOf(fields[3]);
        }
        cellFreeMem.setCellValue(freeMem);



        // ------------------------------- CPU ----------------------------------
        // Write the value to the first available cell in column
        Cell cellCPU = rowTotalMem.createCell(3);
        cellCPU.setCellValue(cpuUsage);






        // Save the changes to the Excel file
//        FileOutputStream outputStream = new FileOutputStream("report.xlsx");
//        workbook.write(outputStream);
//        workbook.close();

        //writer.close();

        // Display the report on the terminal
        System.out.println("System resource report saved to report.csv");
    }

    private static String executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        StringBuilder output = new StringBuilder();

        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exitCode == 0) {
            // Read the output of the command
            java.io.InputStream inputStream = process.getInputStream();
            java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
            output.append(scanner.hasNext() ? scanner.next() : "");
            scanner.close();
        } else {
            // Handle errors
            java.io.InputStream errorStream = process.getErrorStream();
            java.util.Scanner scanner = new java.util.Scanner(errorStream).useDelimiter("\\A");
            output.append(scanner.hasNext() ? scanner.next() : "");
            scanner.close();
            throw new RuntimeException(output.toString());
        }

        return output.toString();
    }

    public static void initExcel() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream out = new FileOutputStream(new File("report.xlsx"));

            // Create a new sheet in the workbook
            workbook.createSheet("Sheet1");
            Row row = workbook.getSheet("Sheet1").createRow(0);

            // Add titles to the first three columns
            Cell cell = row.createCell(0);
            cell.setCellValue("Total Memory (Mi)");
            cell = row.createCell(1);
            cell.setCellValue("Used Memory (Mi)");
            cell = row.createCell(2);
            cell.setCellValue("Free Memory (Mi)");
            cell = row.createCell(3);
            cell.setCellValue("CPU Usage %");

            // Write the workbook to the output stream
            workbook.write(out);
            System.out.println("Excel file created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}