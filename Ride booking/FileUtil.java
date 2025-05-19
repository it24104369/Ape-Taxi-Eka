package com.ridebooking.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * Reads all lines from a file.
     *
     * @param filePath Path to the file
     * @return List containing all lines in the file
     * @throws IOException If an I/O error occurs
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        // Make sure the file exists
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            return new ArrayList<>();
        }

        try {
            System.out.println("Reading from file: " + filePath);
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            System.out.println("Read " + lines.size() + " lines from file");
            return lines;
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Writes all lines to a file.
     *
     * @param filePath Path to the file
     * @param lines    List of lines to write
     * @throws IOException If an I/O error occurs
     */
    public static void writeAllLines(String filePath, List<String> lines) throws IOException {
        try {
            // Make sure parent directories exist
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            System.out.println("Writing " + lines.size() + " lines to file: " + filePath);
            Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
            System.out.println("Successfully wrote to file");
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Appends a line to a file.
     *
     * @param filePath Path to the file
     * @param line     Line to append
     * @throws IOException If an I/O error occurs
     */
    public static void appendLine(String filePath, String line) throws IOException {
        try {
            // Make sure parent directories exist
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Create file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            System.out.println("Appending line to file: " + filePath);
            Files.write(Paths.get(filePath), (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND);
            System.out.println("Successfully appended to file");
        } catch (IOException e) {
            System.err.println("Error appending to file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}