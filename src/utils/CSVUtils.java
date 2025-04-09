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
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + filepath);
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Writes a list of string arrays to a CSV file, overwriting existing content.
     */
    public static void writeCSV(String filepath, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + filepath);
            e.printStackTrace();
        }
    }

    /**
     * Appends a new row to the end of a CSV file.
     */
    public static void appendToCSV(String filepath, String[] row) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true))) {
            bw.write(String.join(",", row));
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error appending to CSV file: " + filepath);
            e.printStackTrace();
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
