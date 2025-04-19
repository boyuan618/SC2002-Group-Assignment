package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.CSVUtils;

public class BTOApplication {
    final private Applicant applicant;
    final private BTOProject project;
    private String status;
    private String flatType;

    private static final String APPLICATIONS_CSV = "data/FlatApplications.csv";

    // Constructor with full object references
    public BTOApplication(Applicant applicant, BTOProject project, String flatType, String status) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = status;
    }

    public void updateStatus(String newStatus) {
        if (isValidStatus(newStatus)) {
            this.status = newStatus;
        }
    }

    public void setFlatType(String flatType) {
        if (flatType.equals("2-Room") || flatType.equals("3-Room")) {
            this.flatType = flatType;
        }
    }

    private boolean isValidStatus(String newStatus) {
        return newStatus.equals("Pending") ||
                newStatus.equals("Successful") ||
                newStatus.equals("Unsuccessful") ||
                newStatus.equals("Booked") ||
                newStatus.equals("Withdrawn");
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProjectObject() {
        return project;
    }

    public String getStatus() {
        return status;
    }

    public String getFlatType() {
        return flatType;
    }

    public String getApplicantNRIC() {
        return applicant != null ? applicant.getNric() : null;
    }

    public String getProjectName() {
        return project != null ? project.getProjectName() : null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Convert BTOApplication to CSV Row
    public String[] toCSVRow() {
        return new String[] { getApplicantNRIC(), getProjectName(), flatType, status };
    }

    // Method to apply for a flat
    public static void applyForFlat(Applicant applicant, BTOProject project, String flatType) {
        BTOApplication application = new BTOApplication(applicant, project, flatType, "Pending");
        CSVUtils.appendToCSV(APPLICATIONS_CSV, application.toCSVRow());
    }

    // Method to update application in CSV
    public static void updateBTOApplication(BTOApplication updatedApplication) {
        List<String[]> rows = CSVUtils.readCSV(APPLICATIONS_CSV);
        boolean applicationFound = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPLICATIONS_CSV))) {
            for (String[] row : rows) {
                if (row.length >= 4 &&
                        row[0].equals(updatedApplication.getApplicantNRIC()) &&
                        row[1].equals(updatedApplication.getProjectName()) &&
                        row[2].equals(updatedApplication.getFlatType())) {
                    row[3] = updatedApplication.getStatus();
                    applicationFound = true;
                }
                bw.write(String.join(",", row));
                bw.newLine();
            }

            if (applicationFound) {
                System.out.println("Flat application updated successfully.");
            } else {
                System.out.println("Flat application not found for the given NRIC, project, and flat type.");
            }
        } catch (IOException e) {
            System.out.println("Error updating application: " + e.getMessage());
        }
    }

    // Method to apply for a flat
    public static void applyForFlat(String applicantNRIC, String project, String flatType) {
        BTOApplication application = new BTOApplication(Applicant.getApplicantByNRIC(applicantNRIC),
                BTOProject.getProjectByName(project), flatType,
                "Pending");
        CSVUtils.appendToCSV(APPLICATIONS_CSV, application.toCSVRow());
    }

    // Method to view applications by a specific applicant (filtered by NRIC)
    public static List<BTOApplication> viewApplications(String applicantNRIC) {
        List<BTOApplication> applications = getApplications();
        List<BTOApplication> applicantApps = new ArrayList<>();
        for (BTOApplication app : applications) {
            if (app.getApplicantNRIC().equals(applicantNRIC)) {
                applicantApps.add(app);
            }
        }
        return applicantApps;
    }

    // Helper method to get all applications from CSV
    public static List<BTOApplication> getApplications() {
        List<String[]> raw = CSVUtils.readCSV(APPLICATIONS_CSV);
        List<BTOApplication> applications = new ArrayList<>();
        for (String[] row : raw) {
            if (row.length >= 4)
                applications.add(fromCSVRow(row));
        }
        return applications;
    }

    public static BTOApplication getApplicationByNRIC(String nric) {
        List<BTOApplication> applications = getApplications();
        for (BTOApplication app : applications) {
            if (app.getApplicantNRIC().equals(nric)) {
                return app;
            }
        }
        return null;
    }

    // Helper method to write applications back to the CSV file
    public static void writeApplications(List<BTOApplication> applications) {
        List<String[]> rows = new ArrayList<>();
        for (BTOApplication app : applications) {
            rows.add(app.toCSVRow());
        }
        CSVUtils.writeCSV(APPLICATIONS_CSV, rows);
    }

    // Convert a CSV row to a BTOApplication object
    public static BTOApplication fromCSVRow(String[] row) {
        return new BTOApplication(Applicant.getApplicantByNRIC(row[0]), BTOProject.getProjectByName(row[1]), row[2],
                row[3]);
    }

    public static boolean deleteApplicationByNRIC(String nric) {
        List<BTOApplication> applications = getApplications();
        boolean removed = applications.removeIf(app -> app.getApplicantNRIC().equals(nric));
        if (removed) {
            writeApplications(applications);
        }
        return removed;
    }
}
