package utils;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Applicant;
import model.BTOApplication;
import model.BTOProject;

import controller.ProjectController;

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

    public static List<BTOApplication> getApplications(String filepath) {
        List<BTOApplication> applications = new ArrayList<>();
        List<String[]> rows = readCSV(filepath);

        for (String[] row : rows) {
            if (row.length >= 16) {
                try {
                    String nric = row[0];
                    String projectName = row[1];
                    String status = row[2];
                    String flatType = row[3];

                    // ApplicantNRIC,Project,FlatType,Status
                    BTOProject project = ProjectController.findProjectByName(projectName);

                    Applicant applicant = Applicant.getApplicantByNRIC(nric);
                    if (applicant == null) {
                        System.out.println("⚠️ Applicant with NRIC " + nric + " not found.");
                        continue;
                    }

                    BTOApplication application = new BTOApplication(applicant, project, status);
                    application.setFlatType(flatType);

                    applications.add(application);
                } catch (Exception e) {
                    System.out.println("❌ Error parsing row: " + Arrays.toString(row));
                }
            }
        }

        return applications;
    }

    public static void writeApplications(String filepath, List<BTOApplication> applications) {
        List<String[]> data = new ArrayList<>();

        for (BTOApplication app : applications) {
            String[] row = new String[] {
                    app.getApplicant().getNric(),
                    app.getProject().getProjectName(),
                    app.getStatus(),
                    app.getFlatType() == null ? "" : app.getFlatType()
            };
            data.add(row);
        }

        writeCSV(filepath, data);
    }

}
