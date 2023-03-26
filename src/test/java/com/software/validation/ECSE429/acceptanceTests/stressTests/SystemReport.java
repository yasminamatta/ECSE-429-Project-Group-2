package com.software.validation.ECSE429.acceptanceTests.stressTests;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SystemReport {
    public static void main(String[] args) {

    }

    public static void report() throws IOException {
        // Get system memory statistics
        String memstats = executeCommand("vmstat -s");
        String[] memlines = memstats.split("\n");

        // Get CPU usage statistics
        String cpustats = executeCommand("vmstat 1 5");
        String[] cpulines = cpustats.split("\n");

        // Get available memory statistics
        String availmem = executeCommand("free -h");
        String[] avmlines = availmem.split("\n");

        // Write the report to a CSV file
        PrintWriter writer = new PrintWriter(new FileWriter("report.csv", true));
        writer.println("Memory statistics:");
        for (String line : memlines) {
            String[] fields = line.split("\\s+");
            writer.println(String.join(",", fields));
        }
        writer.println();

        writer.println("CPU usage statistics:");
        for (String line : cpulines) {
            String[] fields = line.split("\\s+");
            writer.println(String.join(",", fields));
        }
        writer.println();

        writer.println("Available memory statistics:");
        for (String line : avmlines) {
            String[] fields = line.split("\\s+");
            writer.println(String.join(",", fields));
        }

        writer.close();

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
}
