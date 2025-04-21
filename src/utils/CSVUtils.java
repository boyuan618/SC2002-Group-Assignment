package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    /**
     * Reads a CSV file and returns the content as a list of string arrays.
     * Each array represents one row (split by commas).
     */

    public static List<String[]> readCSV(String filepath) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                // Split on commas not inside quotes
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                data.add(row);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + filepath);
        }

        return data;
    }

    /**
     * Writes a list of string arrays to a CSV file, overwriting existing content.
     */
    public static void writeCSV(String filepath, List<String[]> data) {
        List<String[]> existingData = new ArrayList<>();

        // Read existing data from the file, keeping the first line (header)
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    existingData.add(line.split(",")); // Keep the first line (headers)
                    firstLine = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + filepath);
        }

        // Write the first line (header) back and overwrite the rest with new data
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            // Write the first line (header)
            for (String[] row : existingData) {
                bw.write(String.join(",", row)); // Write the header
                bw.newLine();
            }

            // Write the new data to overwrite the existing content
            for (String[] row : data) {
                bw.write(String.join(",", row)); // Write each new row
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + filepath);
        }
    }

    /**
     * Appends a new row to the end of a CSV file.
     */
    public static void appendToCSV(String filepath, String[] row) {
        if (row == null || row.length == 0)
            return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true))) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                String value = row[i];
                // Optionally wrap in quotes if value contains comma
                if (value.contains(",")) {
                    value = "\"" + value + "\"";
                }
                line.append(value);
                if (i < row.length - 1)
                    line.append(",");
            }
            bw.write(line.toString());
            bw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error appending to CSV file: " + filepath);
        }
    }

    /**
     * Update a line in a CSV file where a condition matches (e.g., NRIC).
     */
    public static void updateCSV(String filepath, String key, int keyIndex, String[] newRow) {
        List<String[]> data = readCSV(filepath);

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i)[keyIndex].equals(key)) {
                data.set(i, newRow);
                break;
            }
        }

        writeCSV(filepath, data);
    }

}
