package com.software.validation.ECSE429.stressTests;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SystemReport {

    public static Row report(Workbook workbook, long start, long end, String type) throws IOException {

        Sheet sheet = null;
        if(type.equals("Post")) {
            sheet = workbook.getSheetAt(0);
        } else if(type.equals("Modify")) {
            sheet = workbook.getSheetAt(1);
        } else {
            sheet = workbook.getSheetAt(2);
        }

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

        String[] fields = avmlines[1].split("\\s+"); // total, used, free, shared, buff/cache, available



        // ------------------------------- DURATION ----------------------------------
        // Find the first available row in column
        int rowNumTotalMem = sheet.getLastRowNum() + 1;

        // Write the value to the first available cell in column
        Row rowTotalMem = sheet.createRow(rowNumTotalMem);
        Cell cellTotalMem = rowTotalMem.createCell(0);

        long duration = (end-start);

        cellTotalMem.setCellValue(duration);

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

        Cell cellCurrentTime = rowTotalMem.createCell(4);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        cellCurrentTime.setCellValue(dtf.format(now));


        return rowTotalMem;
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

    public static void initExcel(String filename) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream out = new FileOutputStream(new File(filename));

            // Create a new sheet in the workbook
            workbook.createSheet("Post");
            workbook.createSheet("Modify");
            workbook.createSheet("Delete");
            Row row = workbook.getSheet("Post").createRow(0);

            // Add titles to the first three columns
            Cell cell = row.createCell(0);
            cell.setCellValue("Time Elapsed");
            cell = row.createCell(1);
            cell.setCellValue("Used Memory (Mi)");
            cell = row.createCell(2);
            cell.setCellValue("Free Memory (Mi)");
            cell = row.createCell(3);
            cell.setCellValue("CPU Usage");
            cell = row.createCell(4);
            cell.setCellValue("Current Time");

            Row rowModify = workbook.getSheet("Modify").createRow(0);

            // Add titles to the first three columns
            Cell cellModify = rowModify.createCell(0);
            cellModify.setCellValue("Time Elapsed");
            cellModify = rowModify.createCell(1);
            cellModify.setCellValue("Used Memory (Mi)");
            cellModify = rowModify.createCell(2);
            cellModify.setCellValue("Free Memory (Mi)");
            cellModify = rowModify.createCell(3);
            cellModify.setCellValue("CPU Usage");
            cellModify = rowModify.createCell(4);
            cellModify.setCellValue("Current Time");


            Row rowDelete = workbook.getSheet("Delete").createRow(0);

            // Add titles to the first three columns
            Cell cellDelete = rowDelete.createCell(0);
            cellDelete.setCellValue("Time Elapsed");
            cellDelete = rowDelete.createCell(1);
            cellDelete.setCellValue("Used Memory (Mi)");
            cellDelete = rowDelete.createCell(2);
            cellDelete.setCellValue("Free Memory (Mi)");
            cellDelete = rowDelete.createCell(3);
            cellDelete.setCellValue("CPU Usage");
            cellDelete = rowDelete.createCell(4);
            cellDelete.setCellValue("Current Time");

            // Write the workbook to the output stream
            workbook.write(out);
            System.out.println("Excel file created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}