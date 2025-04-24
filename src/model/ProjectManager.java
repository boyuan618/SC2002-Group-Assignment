package model;

import java.util.*;
import utils.Validator;

/**
 * Represents an HDB Project Manager responsible for managing BTO projects, officer registrations,
 * BTO applications, and withdrawal requests in the HDB BTO Management System.
 *
 * @author SC2002Team
 */
public class ProjectManager extends HDBManager {

    public static final String ENQUIRIES_CSV = "data/FlatEnquiries.csv";
    public static final String APPLICATIONS_CSV = "data/BTOApplications.csv";

    private BTOProject project; // Project that this manager is handling

    /**
     * Constructs a ProjectManager with the specified details and assigns a project if the manager's
     * name is found in a project's manager field.
     *
     * @param name The manager's name.
     * @param nric The manager's NRIC.
     * @param password The manager's password.
     * @param age The manager's age.
     * @param maritalStatus The manager's marital status.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public ProjectManager(String name, String nric, String password, int age, String maritalStatus) {
        super(name, nric, password, age, maritalStatus);
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name: Must contain only letters and spaces and cannot be empty.");
        }
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid password: Cannot be null or empty.");
        }
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Must be between 18 and 120.");
        }
        if (maritalStatus == null || (!maritalStatus.equalsIgnoreCase("Single") && !maritalStatus.equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }

        this.project = null;
        
        List<BTOProject> projects = BTOProject.getProjects();
        if (projects == null) {
            System.out.println("Warning: Unable to retrieve projects for manager assignment.");
            return;
        }
        for (BTOProject projectlist : projects) {
            if (projectlist == null || projectlist.getManager() == null) {
                continue;
            }
            try {
                if (projectlist.getManager().trim().equals(name)) {
                    this.project = projectlist;
                    break;
                }
            } catch (Exception e) {
                System.out.println("Warning: Error parsing manager for project: " + (projectlist.getProjectName() != null ? projectlist.getProjectName() : "<Unknown>"));
            }
        }
    }

    /**
     * Generates a report summarizing application statistics for a project.
     *
     * @param projectName The name of the project to generate the report for.
     * @return A formatted string containing the report.
     * @throws IllegalArgumentException If the project name is invalid or non-existent.
     */
    public String generateReport(String projectName) {
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (BTOProject.getProjectByName(projectName) == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName);
        }

        List<BTOApplication> applications = BTOApplication.getApplications();
        if (applications == null) {
            System.out.println("Warning: Unable to retrieve applications.");
            return "Error: Unable to generate report due to missing application data.";
        }

        Map<String, Integer> statusCount = new HashMap<>();
        Map<String, Integer> flatTypeCount = new HashMap<>();

        for (BTOApplication app : applications) {
            if (app != null && projectName.equals(app.getProjectName())) {
                String status = app.getStatus() != null ? app.getStatus() : "<Unknown>";
                String flatType = app.getFlatType() != null ? app.getFlatType() : "<Unknown>";
                statusCount.merge(status, 1, Integer::sum);
                flatTypeCount.merge(flatType, 1, Integer::sum);
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("Report for Project: ").append(projectName).append("\n");
        report.append("Applications by Status:\n");
        if (statusCount.isEmpty()) {
            report.append("  No applications found.\n");
        } else {
            statusCount.forEach((status, count) -> report.append("  ").append(status).append(": ").append(count).append("\n"));
        }
        report.append("Applications by Flat Type:\n");
        if (flatTypeCount.isEmpty()) {
            report.append("  No applications found.\n");
        } else {
            flatTypeCount.forEach((type, count) -> report.append("  ").append(type).append(": ").append(count).append("\n"));
        }
        return report.toString();
    }

    /**
     * Displays pending officer registration applications for the managed project.
     */
    public void viewPendingOfficerRegistrations() {
        if (project == null) {
            System.out.println("Error: You are not in charge of any project!");
            return;
        }
        List<OfficerApplication> officerApplications = OfficerApplication.readOfficerApplications();
        if (officerApplications == null) {
            System.out.println("Warning: Unable to retrieve officer applications.");
            return;
        }

        boolean found = false;
        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp != null &&
                project.getProjectName().equals(officerApp.getProject()) &&
                "Pending".equalsIgnoreCase(officerApp.getStatus())) {
                System.out.println(officerApp.toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No pending officer registrations found for project: " + project.getProjectName());
        }
    }

    /**
     * Approves or rejects an officer's registration for the managed project.
     *
     * @param officerNRIC The NRIC of the officer.
     * @param approve True to approve, false to reject.
     * @throws IllegalArgumentException If the NRIC is invalid or the project is not assigned.
     */
    public void approveRejectOfficer(String officerNRIC, boolean approve) {
        if (project == null) {
            throw new IllegalArgumentException("No project assigned to this manager.");
        }
        if (!Validator.isValidNRIC(officerNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }

        List<OfficerApplication> officerApplications = OfficerApplication.readOfficerApplications();
        if (officerApplications == null) {
            System.out.println("Warning: Unable to retrieve officer applications.");
            return;
        }

        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp != null &&
                project.getProjectName().equals(officerApp.getProject()) &&
                officerNRIC.equals(officerApp.getOfficerNRIC()) &&
                "Pending".equalsIgnoreCase(officerApp.getStatus())) {
                try {
                    if (approve) {
                        if (project.getOfficerSlot() <= 0) {
                            System.out.println("Error: No officer slots available for project: " + project.getProjectName());
                            return;
                        }
                        Applicant officer = HDBOfficer.getApplicantByNRIC(officerNRIC);
                        if (officer == null) {
                            System.out.println("Error: Officer not found for NRIC: " + officerNRIC);
                            return;
                        }
                        officerApp.setStatus("Approved");
                        project.addOfficer(officer.getName());
                        BTOProject.editProject(project);
                        OfficerApplication.updateOfficerApplication(officerApp);
                        System.out.println("✅ Officer approved for project: " + project.getProjectName());
                    } else {
                        officerApp.setStatus("Rejected");
                        OfficerApplication.updateOfficerApplication(officerApp);
                        System.out.println("✅ Officer registration rejected for project: " + project.getProjectName());
                    }
                    return;
                } catch (RuntimeException e) {
                    System.out.println("Error updating officer application: " + e.getMessage());
                    return;
                }
            }
        }
        System.out.println("Error: Pending officer application not found for NRIC: " + officerNRIC);
    }

    /**
     * Approves or rejects an applicant's BTO application for the managed project.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @param approve True to approve, false to reject.
     * @throws IllegalArgumentException If the NRIC is invalid or the project is not assigned.
     */
    public void approveRejectApplication(String applicantNRIC, boolean approve) {
        if (project == null) {
            throw new IllegalArgumentException("No project assigned to this manager.");
        }
        if (!Validator.isValidNRIC(applicantNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }

        List<BTOApplication> applications = BTOApplication.getApplications();
        if (applications == null) {
            System.out.println("Warning: Unable to retrieve applications.");
            return;
        }

        for (BTOApplication app : applications) {
            if (app != null &&
                project.getProjectName().equals(app.getProjectName()) &&
                applicantNRIC.equals(app.getApplicantNRIC()) &&
                "Pending".equalsIgnoreCase(app.getStatus())) {
                try {
                    if (approve) {
                        List<Room> rooms = project.getRooms();
                        if (rooms == null || rooms.isEmpty()) {
                            System.out.println("Error: No room types available for project: " + project.getProjectName());
                            return;
                        }
                        String flatType = app.getFlatType();
                        if (!Validator.isValidFlatType(flatType)) {
                            System.out.println("Error: Invalid flat type in application: " + flatType);
                            return;
                        }
                        boolean unitAvailable = false;
                        for (Room type : rooms) {
                            if (type != null && flatType.equalsIgnoreCase(type.getRoomType()) && type.getUnits() > 0) {
                                unitAvailable = true;
                                break;
                            }
                        }
                        if (!unitAvailable) {
                            System.out.println("Error: No units available for flat type: " + flatType);
                            return;
                        }
                        app.setStatus("Successful");
                        BTOApplication.updateBTOApplication(app);
                        System.out.println("✅ Applicant approved for project: " + project.getProjectName());
                    } else {
                        app.setStatus("Unsuccessful");
                        BTOApplication.updateBTOApplication(app);
                        System.out.println("✅ Applicant rejected for project: " + project.getProjectName());
                    }
                    return;
                } catch (RuntimeException e) {
                    System.out.println("Error updating application: " + e.getMessage());
                    return;
                }
            }
        }
        System.out.println("Error: Pending application not found for NRIC: " + applicantNRIC);
    }

    /**
     * Approves or rejects an applicant's withdrawal request for the managed project.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @param approve True to approve, false to reject.
     * @throws IllegalArgumentException If the NRIC is invalid or the project is not assigned.
     */
    public void approveRejectWithdrawal(String applicantNRIC, boolean approve) {
        if (project == null) {
            throw new IllegalArgumentException("No project assigned to this manager.");
        }
        if (!Validator.isValidNRIC(applicantNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }

        List<BTOApplication> applications = BTOApplication.getApplications();
        if (applications == null) {
            System.out.println("Warning: Unable to retrieve applications.");
            return;
        }

        for (BTOApplication app : applications) {
            if (app != null &&
                project.getProjectName().equals(app.getProjectName()) &&
                applicantNRIC.equals(app.getApplicantNRIC()) &&
                ("Successful".equalsIgnoreCase(app.getStatus()) || "Booked".equalsIgnoreCase(app.getStatus()))) {
                try {
                    if (approve) {
                        List<Room> rooms = project.getRooms();
                        if (rooms == null || rooms.isEmpty()) {
                            System.out.println("Error: No room types available for project: " + project.getProjectName());
                            return;
                        }
                        String flatType = app.getFlatType();
                        if (!Validator.isValidFlatType(flatType)) {
                            System.out.println("Error: Invalid flat type in application: " + flatType);
                            return;
                        }
                        boolean flatTypeFound = false;
                        for (Room type : rooms) {
                            if (type != null && flatType.equalsIgnoreCase(type.getRoomType())) {
                                type.setUnits(type.getUnits() + 1);
                                flatTypeFound = true;
                                break;
                            }
                        }
                        if (!flatTypeFound) {
                            System.out.println("Error: Flat type not found in project: " + flatType);
                            return;
                        }
                        app.setStatus("Unsuccessful");
                        BTOProject.editProject(project);
                        BTOApplication.updateBTOApplication(app);
                        System.out.println("✅ Applicant withdrawal approved for project: " + project.getProjectName());
                    } else {
                        System.out.println("Applicant withdrawal rejected for project: " + project.getProjectName());
                    }
                    return;
                } catch (RuntimeException e) {
                    System.out.println("Error processing withdrawal: " + e.getMessage());
                    return;
                }
            }
        }
        System.out.println("Error: Withdrawable application not found for NRIC: " + applicantNRIC);
    }

    /**
     * Gets the project managed by this manager.
     *
     * @return The BTOProject assigned to this manager, or null if none.
     */
    public BTOProject getProjectManaging() {
        return project;
    }
}