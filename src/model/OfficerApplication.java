package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.CSVUtils;

public class OfficerApplication {

    private String officerNRIC;
    private String project;
    private String status;
    private static final String OFFICERAPPLICATION_CSV = "data/OfficerApplication.csv";

    // Constructor to initialize the OfficerApplication object
    public OfficerApplication(String officerNRIC, String project, String status) {
        this.officerNRIC = officerNRIC;
        this.project = project;
        this.status = status;
    }

    // Getter and setter methods for officerNRIC
    public String getOfficerNRIC() {
        return officerNRIC;
    }

    public void setOfficerNRIC(String officerNRIC) {
        this.officerNRIC = officerNRIC;
    }

    // Getter and setter methods for project
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    // Getter and setter methods for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString method for easy printing of officer application details
    @Override
    public String toString() {
        return "OfficerApplication{" +
                "officerNRIC='" + officerNRIC + '\'' +
                ", project='" + project + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Static method to read officer applications from CSV
    public static List<OfficerApplication> readOfficerApplications() {
        List<OfficerApplication> officerApplications = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(OFFICERAPPLICATION_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    // Read officer application details from the CSV file
                    String officerNRIC = parts[0];
                    String project = parts[1];
                    String status = parts[2];

                    OfficerApplication officerApplication = new OfficerApplication(officerNRIC, project, status);
                    officerApplications.add(officerApplication);
                }
            }
        } catch (IOException e) {
        }

        return officerApplications;
    }

    // Static method to update officer application in the CSV
    public static void updateOfficerApplication(OfficerApplication updatedApplication) {
        List<OfficerApplication> officerApplications = readOfficerApplications();
        boolean applicationFound = false;

        // Open the file for writing the updated list
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(OFFICERAPPLICATION_CSV))) {
            for (OfficerApplication officerApp : officerApplications) {
                // If the officer application matches, update the status
                if (officerApp.getOfficerNRIC().equals(updatedApplication.getOfficerNRIC()) &&
                        officerApp.getProject().equals(updatedApplication.getProject())) {
                    officerApp.setStatus(updatedApplication.getStatus()); // Update the status
                    applicationFound = true; // Mark as updated
                }
                // Write each application (updated or not) back to the file
                bw.write(officerApp.getOfficerNRIC() + "," + officerApp.getProject() + "," + officerApp.getStatus());
                bw.newLine();
            }

            // Check if the application was found and updated
            if (applicationFound) {
                System.out.println("Officer application updated successfully.");
            } else {
                System.out.println("Officer application not found for the given NRIC and project.");
            }
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
        }

    }

    public static void createApplication(OfficerApplication newApp) {
        CSVUtils.appendToCSV(OFFICERAPPLICATION_CSV, newApp.toCSV());
    }

    public String[] toCSV() {
        return new String[] { officerNRIC, project, status };
    }

}
