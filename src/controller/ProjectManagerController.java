package controller;

import model.*;
import utils.Validator;
import java.util.*;

/**
 * Controller for handling Project Manager interactions, extending HDBManagerController to include
 * project creation, editing, deletion, and adding report generation, officer and BTO application
 * management, and enquiry handling in the HDB BTO Management System.
 *
 * @author SC2002Team
 */
public class ProjectManagerController extends HDBManagerController {

    /**
     * Constructs a ProjectManagerController with the specified user, initializing the HDBManager as a ProjectManager.
     *
     * @param user The authenticated user (must have HDBManager role).
     * @throws IllegalArgumentException If the user is null or not an HDBManager.
     */
    public ProjectManagerController(User user) {
        super(user);
        // Override hdbManager with ProjectManager instance
        this.hdbManager = new ProjectManager(user.getName(), user.getNRIC(), user.getPassword(), user.getAge(),
                user.getMaritalStatus());
    }

    /**
     * Generates a report for the specified project.
     *
     * @param projectName The name of the project.
     * @return The formatted report string.
     * @throws IllegalArgumentException If the project name is invalid or not managed by this manager.
     */
    public String generateReport(String projectName) {
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        BTOProject project = BTOProject.getProjectByName(projectName.trim());
        if (project == null || !project.getManager().equals(hdbManager.getName())) {
            throw new IllegalArgumentException("Project does not exist or is not managed by this manager: " + projectName.trim());
        }
        try {
            return ((ProjectManager) hdbManager).generateReport(projectName.trim());
        } catch (Exception e) {
            throw new RuntimeException("Error generating report: " + e.getMessage());
        }
    }

    /**
     * Retrieves all BTO projects.
     *
     * @return A list of all BTO projects.
     */
    public List<BTOProject> getAllProjects() {
        return BTOProject.getProjects();
    }

    /**
     * Displays pending officer applications for the manager's project.
     *
     * @throws IllegalStateException If no project is managed by this manager.
     */
    public void viewOfficerApplications() {
        if (((ProjectManager) hdbManager).getProjectManaging() == null) {
            throw new IllegalStateException("No project is currently managed by this manager.");
        }
        ((ProjectManager) hdbManager).viewPendingOfficerRegistrations();
    }

    /**
     * Approves or rejects an officer registration.
     *
     * @param officerNRIC The NRIC of the officer.
     * @param approve True to approve, false to reject.
     * @throws IllegalArgumentException If the officer NRIC is invalid or not found.
     */
    public void approveRejectOfficerRegistration(String officerNRIC, boolean approve) {
        if (!Validator.isValidNRIC(officerNRIC)) {
            throw new IllegalArgumentException("Invalid officer NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<OfficerApplication> applications = OfficerApplication.readOfficerApplications();
        boolean found = false;
        for (OfficerApplication app : applications) {
            if (app.getStatus().equalsIgnoreCase("Pending") && 
                app.getOfficerNRIC().equals(officerNRIC.trim()) && 
                app.getProject().equals(((ProjectManager) hdbManager).getProjectManaging().getProjectName())) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("No pending officer application found for NRIC: " + officerNRIC.trim() + " in managed project.");
        }
        ((ProjectManager) hdbManager).approveRejectOfficer(officerNRIC.trim(), approve);
        System.out.println("✅ Officer registration " + (approve ? "approved" : "rejected") + " for NRIC: " + officerNRIC.trim());
    }

    /**
     * Manages enquiries for the manager's project.
     *
     * @param sc The Scanner for user input.
     * @throws IllegalArgumentException If the scanner is null.
     * @throws IllegalStateException If no project is managed by this manager.
     */
    public void manageEnquiries(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        if (((ProjectManager) hdbManager).getProjectManaging() == null) {
            throw new IllegalStateException("No project is currently managed by this manager.");
        }
        // Placeholder logic; replace with actual implementation
        System.out.println("Enquiry management not fully implemented.");
        // Example: ((ProjectManager) hdbManager).viewAndReplyEnquiries(sc);
    }

    /**
     * Retrieves BTO applications for the manager's project.
     *
     * @return A list of BTO applications.
     * @throws IllegalStateException If no project is managed by this manager.
     */
    public ArrayList<BTOApplication> viewBTOApplications() {
        if (((ProjectManager) hdbManager).getProjectManaging() == null) {
            throw new IllegalStateException("No project is currently managed by this manager.");
        }
        List<BTOApplication> allApplications = BTOApplication.getApplications();
        ArrayList<BTOApplication> managerApplications = new ArrayList<>();
        for (BTOApplication app : allApplications) {
            if (app.getProjectName().equals(((ProjectManager) hdbManager).getProjectManaging().getProjectName())) {
                managerApplications.add(app);
            }
        }
        return managerApplications;
    }

    /**
     * Approves or rejects a BTO application.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @param approve True to approve, false to reject.
     * @throws IllegalArgumentException If the applicant NRIC is invalid or not found.
     */
    public void approveRejectBTOApplication(String applicantNRIC, boolean approve) {
        if (!Validator.isValidNRIC(applicantNRIC)) {
            throw new IllegalArgumentException("Invalid applicant NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<BTOApplication> applications = BTOApplication.getApplications();
        boolean found = false;
        for (BTOApplication app : applications) {
            if (app.getApplicantNRIC().equals(applicantNRIC.trim()) && 
                app.getProjectName().equals(((ProjectManager) hdbManager).getProjectManaging().getProjectName())) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("No BTO application found for NRIC: " + applicantNRIC.trim() + " in managed project.");
        }
        ((ProjectManager) hdbManager).approveRejectApplication(applicantNRIC.trim(), approve);
        System.out.println("✅ BTO application " + (approve ? "approved" : "rejected") + " for NRIC: " + applicantNRIC.trim());
    }

    /**
     * Retrieves the project managed by this manager.
     *
     * @return The BTOProject managed by this manager, or null if none.
     */
    public BTOProject getProjectManaging() {
        return ((ProjectManager) hdbManager).getProjectManaging();
    }

}
