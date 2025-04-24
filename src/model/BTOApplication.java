package model;

import java.io.*;

import java.util.*;

import utils.CSVUtils;
import utils.Validator;

/**
 * Represents a BTO application for a flat in the HDB BTO Management System.
 * Manages application details, status updates, and CSV storage.
 *
 * @author SC2002Team
 */
public class BTOApplication {
    final private Applicant applicant;
    final private BTOProject project;
    private String status;
    private String flatType;

    private static final String APPLICATIONS_CSV = "data/FlatApplications.csv";

    /**
     * Constructs a BTOApplication with the specified details.
     *
     * @param applicant The applicant submitting the application.
     * @param project The BTO project being applied for.
     * @param flatType The type of flat (e.g., "2-Room", "3-Room").
     * @param status The status of the application (e.g., "Pending", "Successful").
     * @throws IllegalArgumentException If any input is invalid.
     */
    public BTOApplication(Applicant applicant, BTOProject project, String flatType, String status) {
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant cannot be null.");
        }
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be '2-Room' or '3-Room'.");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: Must be 'Pending', 'Successful', 'Unsuccessful', 'Booked', or 'Withdrawn'.");
        }
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = status;
    }

    /**
     * Updates the status of the application.
     *
     * @param newStatus The new status to set.
     * @throws IllegalArgumentException If the status is invalid.
     */
    public void updateStatus(String newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        if (isValidStatus(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalArgumentException("Invalid status: Must be 'Pending', 'Successful', 'Unsuccessful', 'Booked', or 'Withdrawn'.");
        }
    }

    /**
     * Sets the flat type of the application.
     *
     * @param flatType The new flat type.
     * @throws IllegalArgumentException If the flat type is invalid.
     */
    public void setFlatType(String flatType) {
        if (flatType == null) {
            throw new IllegalArgumentException("Flat type cannot be null.");
        }
        if (Validator.isValidFlatType(flatType)) {
            this.flatType = flatType;
        } else {
            throw new IllegalArgumentException("Invalid flat type: Must be '2-Room' or '3-Room'.");
        }
    }

    /**
     * Validates the status of the application.
     *
     * @param newStatus The status to validate.
     * @return True if the status is valid, false otherwise.
     */
    private static boolean isValidStatus(String newStatus) {
        return newStatus != null && (
                newStatus.equals("Pending") ||
                newStatus.equals("Successful") ||
                newStatus.equals("Unsuccessful") ||
                newStatus.equals("Booked") ||
                newStatus.equals("Withdrawn"));
    }

    /**
     * Gets the applicant associated with this application.
     *
     * @return The Applicant object.
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Gets the BTO project associated with this application.
     *
     * @return The BTOProject object.
     */
    public BTOProject getProjectObject() {
        return project;
    }

    /**
     * Gets the status of the application.
     *
     * @return The status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the flat type of the application.
     *
     * @return The flat type.
     */
    public String getFlatType() {
        return flatType;
    }

    /**
     * Gets the NRIC of the applicant.
     *
     * @return The applicant's NRIC, or null if applicant is null.
     */
    public String getApplicantNRIC() {
        return applicant != null ? applicant.getNric() : null;
    }

    /**
     * Gets the name of the project.
     *
     * @return The project name, or null if project is null.
     */
    public String getProjectName() {
        return project != null ? project.getProjectName() : null;
    }

    /**
     * Sets the status of the application (without validation for internal use).
     *
     * @param status The new status.
     */
    public void setStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    /**
     * Converts the application to a CSV row.
     *
     * @return An array of strings representing the CSV row.
     * @throws IllegalStateException If applicant or project is null.
     */
    public String[] toCSVRow() {
        if (getApplicantNRIC() == null || getProjectName() == null) {
            throw new IllegalStateException("Cannot generate CSV row: Applicant NRIC or project name is null.");
        }
        return new String[] { getApplicantNRIC(), getProjectName(), flatType, status };
    }

    /**
     * Applies for a flat using applicant and project objects.
     *
     * @param applicant The applicant submitting the application.
     * @param project The BTO project to apply for.
     * @param flatType The type of flat to apply for.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public static void applyForFlat(Applicant applicant, BTOProject project, String flatType) {
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant cannot be null.");
        }
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be '2-Room' or '3-Room'.");
        }
        BTOApplication application = new BTOApplication(applicant, project, flatType, "Pending");
        CSVUtils.appendToCSV(APPLICATIONS_CSV, application.toCSVRow());
        System.out.println("Flat application submitted successfully.");
    }

    /**
     * Applies for a flat using NRIC and project name.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @param project The name of the project.
     * @param flatType The type of flat to apply for.
     * @throws IllegalArgumentException If any input is invalid or applicant/project not found.
     */
    public static void applyForFlat(String applicantNRIC, String project, String flatType) {
        if (!Validator.isValidNRIC(applicantNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (project == null || project.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be '2-Room' or '3-Room'.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(applicantNRIC);
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant not found for NRIC: " + applicantNRIC);
        }
        BTOProject btoProject = BTOProject.getProjectByName(project);
        if (btoProject == null) {
            throw new IllegalArgumentException("Project not found: " + project);
        }
        BTOApplication application = new BTOApplication(applicant, btoProject, flatType, "Pending");
        CSVUtils.appendToCSV(APPLICATIONS_CSV, application.toCSVRow());
        System.out.println("Flat application submitted successfully.");
    }

    /**
     * Updates an existing BTO application in the CSV file.
     *
     * @param updatedApplication The updated application object.
     * @throws IllegalArgumentException If the application is null or invalid.
     */
    public static void updateBTOApplication(BTOApplication updatedApplication) {
        if (updatedApplication == null) {
            throw new IllegalArgumentException("Updated application cannot be null.");
        }
        if (updatedApplication.getApplicantNRIC() == null || updatedApplication.getProjectName() == null) {
            throw new IllegalArgumentException("Applicant NRIC or project name cannot be null.");
        }
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
                } else if (row.length < 4) {
                    System.out.println("Skipping malformed row: " + String.join(",", row));
                    continue;
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

    /**
     * Retrieves applications for a specific applicant by NRIC.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @return A list of BTO applications for the applicant.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public static List<BTOApplication> viewApplications(String applicantNRIC) {
        if (!Validator.isValidNRIC(applicantNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<BTOApplication> applications = getApplications();
        if (applications == null) {
            return new ArrayList<>();
        }
        List<BTOApplication> applicantApps = new ArrayList<>();
        for (BTOApplication app : applications) {
            if (app != null && app.getApplicantNRIC() != null && app.getApplicantNRIC().equals(applicantNRIC)) {
                applicantApps.add(app);
            }
        }
        return applicantApps;
    }

    /**
     * Retrieves all BTO applications from the CSV file.
     *
     * @return A list of all BTO applications, or an empty list if an error occurs.
     */
    public static List<BTOApplication> getApplications() {
        List<String[]> raw = CSVUtils.readCSV(APPLICATIONS_CSV);
        if (raw == null) {
            return new ArrayList<>();
        }
        List<BTOApplication> applications = new ArrayList<>();
        for (String[] row : raw) {
            try {
                if (row.length >= 4) {
                    applications.add(fromCSVRow(row));
                } else {
                    System.out.println("Skipping malformed row: " + String.join(",", row));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error parsing application row: " + e.getMessage());
            }
        }
        return applications;
    }

    /**
     * Retrieves a BTO application by applicant NRIC.
     *
     * @param nric The NRIC of the applicant.
     * @return The BTO application if found, null otherwise.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public static BTOApplication getApplicationByNRIC(String nric) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<BTOApplication> applications = getApplications();
        if (applications == null) {
            return null;
        }
        for (BTOApplication app : applications) {
            if (app != null && app.getApplicantNRIC() != null && app.getApplicantNRIC().equals(nric)) {
                return app;
            }
        }
        return null;
    }

    /**
     * Writes a list of applications to the CSV file.
     *
     * @param applications The list of applications to write.
     * @throws IllegalArgumentException If the applications list is null.
     */
    public static void writeApplications(List<BTOApplication> applications) {
        if (applications == null) {
            throw new IllegalArgumentException("Applications list cannot be null.");
        }
        List<String[]> rows = new ArrayList<>();
        for (BTOApplication app : applications) {
            if (app != null) {
                rows.add(app.toCSVRow());
            }
        }
        CSVUtils.writeCSV(APPLICATIONS_CSV, rows);
    }

    /**
     * Converts a CSV row to a BTOApplication object.
     *
     * @param row The CSV row data.
     * @return A BTOApplication object.
     * @throws IllegalArgumentException If the row is invalid or data is missing.
     */
    public static BTOApplication fromCSVRow(String[] row) {
        if (row == null || row.length < 4) {
            throw new IllegalArgumentException("Invalid CSV row: Must have at least 4 fields.");
        }
        if (!Validator.isValidNRIC(row[0])) {
            throw new IllegalArgumentException("Invalid NRIC in CSV row: " + row[0]);
        }
        if (row[1] == null || row[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Project name in CSV row cannot be empty.");
        }
        if (!Validator.isValidFlatType(row[2])) {
            throw new IllegalArgumentException("Invalid flat type in CSV row: Must be '2-Room' or '3-Room'.");
        }
        if (!isValidStatus(row[3])) {
            throw new IllegalArgumentException("Invalid status in CSV row: Must be 'Pending', 'Successful', 'Unsuccessful', 'Booked', or 'Withdrawn'.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(row[0]);
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant not found for NRIC: " + row[0]);
        }
        BTOProject project = BTOProject.getProjectByName(row[1]);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + row[1]);
        }
        return new BTOApplication(applicant, project, row[2], row[3]);
    }

    /**
     * Deletes an application by applicant NRIC.
     *
     * @param nric The NRIC of the applicant.
     * @return True if the application was deleted, false otherwise.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public static boolean deleteApplicationByNRIC(String nric) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<BTOApplication> applications = getApplications();
        if (applications == null) {
            return false;
        }
        boolean removed = applications.removeIf(app -> app != null && app.getApplicantNRIC() != null && app.getApplicantNRIC().equals(nric));
        if (removed) {
            writeApplications(applications);
            System.out.println("Application deleted successfully.");
        }
        return removed;
    }
}
