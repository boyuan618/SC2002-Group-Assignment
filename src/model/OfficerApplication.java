package model;


import java.util.ArrayList;
import java.util.List;
import utils.CSVUtils;
import utils.Validator;

/**
 * Represents an officer's application to be assigned to a BTO project in the HDB BTO Management System.
 * Manages creation, reading, and updating of officer applications stored in a CSV file.
 *
 * @author SC2002Team
 */
public class OfficerApplication {

    private String officerNRIC;
    private String project;
    private String status;
    private static final String OFFICERAPPLICATION_CSV = "data/OfficerApplication.csv";

    /**
     * Constructs an OfficerApplication with the specified details.
     *
     * @param officerNRIC The NRIC of the officer.
     * @param project The name of the project.
     * @param status The status of the application ("Pending", "Approved", or "Rejected").
     * @throws IllegalArgumentException If any input is invalid.
     */
    public OfficerApplication(String officerNRIC, String project, String status) {
        if (!Validator.isValidNRIC(officerNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (!Validator.isValidProjectName(project)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (BTOProject.getProjectByName(project) == null) {
            throw new IllegalArgumentException("Project does not exist: " + project);
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: Must be 'Pending', 'Approved', or 'Rejected'.");
        }
        this.officerNRIC = officerNRIC.trim();
        this.project = project.trim();
        this.status = status.trim();
    }

    /**
     * Gets the officer's NRIC.
     *
     * @return The officer's NRIC.
     */
    public String getOfficerNRIC() {
        return officerNRIC;
    }

    /**
     * Sets the officer's NRIC.
     *
     * @param officerNRIC The new NRIC.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public void setOfficerNRIC(String officerNRIC) {
        if (!Validator.isValidNRIC(officerNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        this.officerNRIC = officerNRIC.trim();
    }

    /**
     * Gets the project name.
     *
     * @return The project name.
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the project name.
     *
     * @param project The new project name.
     * @throws IllegalArgumentException If the project name is invalid or does not exist.
     */
    public void setProject(String project) {
        if (!Validator.isValidProjectName(project)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (BTOProject.getProjectByName(project) == null) {
            throw new IllegalArgumentException("Project does not exist: " + project);
        }
        this.project = project.trim();
    }

    /**
     * Gets the application status.
     *
     * @return The application status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the application status.
     *
     * @param status The new status ("Pending", "Approved", or "Rejected").
     * @throws IllegalArgumentException If the status is invalid.
     */
    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: Must be 'Pending', 'Approved', or 'Rejected'.");
        }
        this.status = status.trim();
    }

    /**
     * Validates the application status.
     *
     * @param status The status to validate.
     * @return True if the status is valid, false otherwise.
     */
    private static boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        String trimmedStatus = status.trim();
        return trimmedStatus.equalsIgnoreCase("Pending") ||
               trimmedStatus.equalsIgnoreCase("Approved") ||
               trimmedStatus.equalsIgnoreCase("Rejected");
    }

    /**
     * Returns a string representation of the officer application.
     *
     * @return A string containing the officer NRIC, project, and status.
     */
    @Override
    public String toString() {
        return "OfficerApplication{" +
               "officerNRIC='" + (officerNRIC != null ? officerNRIC : "<Unknown>") + '\'' +
               ", project='" + (project != null ? project : "<Unknown>") + '\'' +
               ", status='" + (status != null ? status : "<Unknown>") + '\'' +
               '}';
    }

    /**
     * Reads all officer applications from the CSV file.
     *
     * @return A list of OfficerApplication objects.
     * @throws RuntimeException If an error occurs while reading the CSV file.
     */
    public static List<OfficerApplication> readOfficerApplications() {
        List<OfficerApplication> officerApplications = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(OFFICERAPPLICATION_CSV);
        if (rows == null) {
            System.out.println("Warning: Unable to read officer applications CSV.");
            return officerApplications;
        }

        for (String[] parts : rows) {
            if (parts.length >= 3) {
                try {
                    String officerNRIC = parts[0].trim();
                    String project = parts[1].trim();
                    String status = parts[2].trim();
                    OfficerApplication officerApplication = new OfficerApplication(officerNRIC, project, status);
                    officerApplications.add(officerApplication);
                } catch (IllegalArgumentException e) {
                    System.out.println("Warning: Invalid officer application in CSV: " + String.join(",", parts) + " - " + e.getMessage());
                }
            } else {
                System.out.println("Warning: Malformed officer application row in CSV: " + String.join(",", parts));
            }

        }

        return officerApplications;
    }

    /**
     * Updates an existing officer application in the CSV file.
     *
     * @param updatedApplication The updated OfficerApplication object.
     * @throws IllegalArgumentException If the updated application is invalid.
     * @throws RuntimeException If an error occurs while writing to the CSV file.
     */
    public static void updateOfficerApplication(OfficerApplication updatedApplication) {
        if (updatedApplication == null) {
            throw new IllegalArgumentException("Updated application cannot be null.");
        }
        if (!Validator.isValidNRIC(updatedApplication.getOfficerNRIC())) {
            throw new IllegalArgumentException("Invalid NRIC in updated application: " + updatedApplication.getOfficerNRIC());
        }
        if (!Validator.isValidProjectName(updatedApplication.getProject())) {
            throw new IllegalArgumentException("Invalid project name in updated application: " + updatedApplication.getProject());
        }
        if (!isValidStatus(updatedApplication.getStatus())) {
            throw new IllegalArgumentException("Invalid status in updated application: " + updatedApplication.getStatus());
        }

        List<OfficerApplication> officerApplications = readOfficerApplications();
        boolean applicationFound = false;

        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp != null &&
                officerApp.getOfficerNRIC().equals(updatedApplication.getOfficerNRIC()) &&
                officerApp.getProject().equals(updatedApplication.getProject())) {
                officerApp.setStatus(updatedApplication.getStatus());
                applicationFound = true;
                break;
            }
        }

        if (!applicationFound) {
            System.out.println("Error: Officer application not found for NRIC: " + updatedApplication.getOfficerNRIC() + " and project: " + updatedApplication.getProject());
            return;
        }

        List<String[]> rows = new ArrayList<>();
        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp != null) {
                rows.add(officerApp.toCSV());
            }
        }

        try {
            CSVUtils.writeCSV(OFFICERAPPLICATION_CSV, rows);
            System.out.println("✅ Officer application updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Error updating officer application: " + e.getMessage());
            throw new RuntimeException("Failed to write to officer applications CSV: " + e.getMessage());
        }
    }

    /**
     * Creates a new officer application and appends it to the CSV file.
     *
     * @param newApp The new OfficerApplication to create.
     * @throws IllegalArgumentException If the new application is invalid.
     * @throws RuntimeException If an error occurs while writing to the CSV file.
     */
    public static void createApplication(OfficerApplication newApp) {
        if (newApp == null) {
            throw new IllegalArgumentException("New application cannot be null.");
        }
        if (!Validator.isValidNRIC(newApp.getOfficerNRIC())) {
            throw new IllegalArgumentException("Invalid NRIC in new application: " + newApp.getOfficerNRIC());
        }
        if (!Validator.isValidProjectName(newApp.getProject())) {
            throw new IllegalArgumentException("Invalid project name in new application: " + newApp.getProject());
        }
        if (BTOProject.getProjectByName(newApp.getProject()) == null) {
            throw new IllegalArgumentException("Project does not exist: " + newApp.getProject());
        }
        if (!isValidStatus(newApp.getStatus())) {
            throw new IllegalArgumentException("Invalid status in new application: " + newApp.getStatus());
        }

        List<OfficerApplication> existingApps = readOfficerApplications();
        for (OfficerApplication app : existingApps) {
            if (app != null &&
                app.getOfficerNRIC().equals(newApp.getOfficerNRIC()) &&
                app.getProject().equals(newApp.getProject())) {
                System.out.println("Error: Officer application already exists for NRIC: " + newApp.getOfficerNRIC() + " and project: " + newApp.getProject());
                return;
            }
        }

        try {
            CSVUtils.appendToCSV(OFFICERAPPLICATION_CSV, newApp.toCSV());
            System.out.println("✅ Officer application created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Error creating officer application: " + e.getMessage());
            throw new RuntimeException("Failed to append to officer applications CSV: " + e.getMessage());
        }
    }

    /**
     * Converts the officer application to a CSV-compatible array.
     *
     * @return An array of strings representing the officer NRIC, project, and status.
     * @throws IllegalStateException If any field is null.
     */
    public String[] toCSV() {
        if (officerNRIC == null || project == null || status == null) {
            throw new IllegalStateException("Officer application fields cannot be null for CSV output.");
        }
        return new String[] { officerNRIC, project, status };
    }
    
}
