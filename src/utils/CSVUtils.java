package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for reading, writing, appending, and updating CSV files
 * in the HDB BTO Management System. Handles CSV rows as string arrays, ensuring
 * proper comma separation and quote handling.
 *
 * @author SC2002Team
 */
public class CSVUtils {

    /**
     * Reads a CSV file and returns its content as a list of string arrays, where each
     * array represents a row split by commas (ignoring commas within quoted values).
     *
     * @param filepath The path to the CSV file.
     * @return A list of string arrays, each representing a CSV row.
     * @throws IllegalArgumentException If the filepath is invalid.
     * @throws RuntimeException If an error occurs while reading the file.
     */

    public static List<String[]> readCSV(String filepath) {
        validateFilePath(filepath);
        File file = new File(filepath);
        if (!file.exists() || !file.canRead()) {
            throw new RuntimeException("Cannot read CSV file: " + filepath + " (file does not exist or is not readable)");
        }

        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1); // Remove BOM
                }
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                data.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + filepath + " - " + e.getMessage());
        }

        return data;
    }

    /**
     * Writes a list of string arrays to a CSV file, overwriting existing content.
     * Each array represents a row, with elements joined by commas.
     *
     * @param filepath The path to the CSV file.
     * @param data The list of string arrays to write.
     * @throws IllegalArgumentException If the filepath or data is invalid.
     * @throws RuntimeException If an error occurs while writing the file.
     */
    public static void writeCSV(String filepath, List<String[]> data) {
        validateFilePath(filepath);
        validateData(data);
        File file = new File(filepath);
        if (file.exists() && !file.canWrite()) {
            throw new RuntimeException("Cannot write to CSV file: " + filepath + " (file is not writable)");
        }


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {

            for (String[] row : data) {
                validateRow(row, "writeCSV");
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to CSV file: " + filepath + " - " + e.getMessage());
        }
    }

    /**
     * Appends a single row to the end of a CSV file. Values containing commas are
     * quoted to preserve CSV format.
     *
     * @param filepath The path to the CSV file.
     * @param row The string array representing the row to append.
     * @throws IllegalArgumentException If the filepath or row is invalid.
     * @throws RuntimeException If an error occurs while appending to the file.
     */
    public static void appendToCSV(String filepath, String[] row) {
        validateFilePath(filepath);
        validateRow(row, "appendToCSV");
        File file = new File(filepath);
        if (file.exists() && !file.canWrite()) {
            throw new RuntimeException("Cannot append to CSV file: " + filepath + " (file is not writable)");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true))) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                String value = row[i];
                
                if (value.contains(",")) {

                    value = "\"" + value + "\"";
                }
                line.append(value);
                if (i < row.length - 1) {
                    line.append(",");
                }
            }
            bw.write(line.toString());
            bw.write(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Error appending to CSV file: " + filepath + " - " + e.getMessage());
        }
    }

    /**
     * Updates a row in a CSV file where the value at the specified key index matches
     * the provided key, replacing it with the new row.
     *
     * @param filepath The path to the CSV file.
     * @param key The value to match in the key index column.
     * @param keyIndex The index of the column to search for the key.
     * @param newRow The new row to replace the matching row.
     * @throws IllegalArgumentException If the filepath, key, keyIndex, or newRow is invalid.
     * @throws RuntimeException If an error occurs while reading or writing the file.
     */
    public static void updateCSV(String filepath, String key, int keyIndex, String[] newRow) {
        validateFilePath(filepath);
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid key: Cannot be null or empty");
        }
        if (keyIndex < 0) {
            throw new IllegalArgumentException("Invalid keyIndex: Must be non-negative");
        }
        validateRow(newRow, "updateCSV");

        List<String[]> data = readCSV(filepath);
        if (data.isEmpty()) {
            throw new RuntimeException("Cannot update CSV file: " + filepath + " is empty");
        }
        if (keyIndex >= data.get(0).length) {
            throw new IllegalArgumentException("Invalid keyIndex: " + keyIndex + " exceeds row length " + data.get(0).length);
        }
        if (newRow.length != data.get(0).length) {
            throw new IllegalArgumentException("Invalid newRow: Length " + newRow.length + " does not match CSV row length " + data.get(0).length);
        }

        boolean updated = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).length > keyIndex && data.get(i)[keyIndex] != null && data.get(i)[keyIndex].equals(key)) {
                data.set(i, newRow);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new RuntimeException("No row found with key '" + key + "' at index " + keyIndex + " in " + filepath);
        }

        writeCSV(filepath, data);
        System.out.println("Successfully updated row in " + filepath);
    }

    /**
     * Validates the file path for CSV operations.
     *
     * @param filepath The file path to validate.
     * @throws IllegalArgumentException If the filepath is invalid.
     */
    private static void validateFilePath(String filepath) {
        if (filepath == null || filepath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid filepath: Cannot be null or empty");
        }
        if (!filepath.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid filepath: Must be a .csv file");
        }
    }

    /**
     * Validates the data list for CSV writing.
     *
     * @param data The list of string arrays to validate.
     * @throws IllegalArgumentException If the data is invalid.
     */
    private static void validateData(List<String[]> data) {
        if (data == null) {
            throw new IllegalArgumentException("Invalid data: Cannot be null");
        }
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Invalid data: Cannot be empty");
        }
        int rowLength = data.get(0).length;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).length != rowLength) {
                throw new IllegalArgumentException("Invalid data: Row " + i + " length " + data.get(i).length + " does not match expected length " + rowLength);
            }
            validateRow(data.get(i), "validateData row " + i);
        }
    }

    /**
     * Validates a single row for CSV operations.
     *
     * @param row The string array to validate.
     * @param context The context for error messaging (e.g., "writeCSV").
     * @throws IllegalArgumentException If the row is invalid.
     */
    private static void validateRow(String[] row, String context) {
        if (row == null) {
            throw new IllegalArgumentException("Invalid row in " + context + ": Cannot be null");
        }
        if (row.length == 0) {
            throw new IllegalArgumentException("Invalid row in " + context + ": Cannot be empty");
        }
        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                throw new IllegalArgumentException("Invalid row in " + context + ": Element at index " + i + " cannot be null");
            }
        }
    }
}