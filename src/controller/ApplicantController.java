package controller;

import model.Applicant;
import model.BTOProject;
import model.BTOApplication;
import model.Enquiry;
import model.User;
import utils.Validator;

import java.util.List;

/**
 * Controller for handling Applicant interactions in the HDB BTO Management System, enabling
 * applicants to view available projects, apply for projects, manage applications, and handle
 * enquiries.
 *
 * @author SC2002Team
 */
public class ApplicantController {
    final private User user;

    /**
     * Constructs an ApplicantController with the specified user.
     *
     * @param user The authenticated user (must have Applicant role).
     * @throws IllegalArgumentException If the user is null or not an Applicant.
     */
    public ApplicantController(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
    
        this.user = user;
    }

    /**
     * Gets the user associated with this controller, accessible to subclasses.
     *
     * @return The authenticated user.
     */
    protected User getUser() {
        return user;
    }

    /**
     * Retrieves a list of BTO projects available to the applicant based on their eligibility.
     *
     * @return A list of available BTO projects.
     * @throws IllegalStateException If the applicant is not found.
     */
    public List<BTOProject> viewAvailableProjects() {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        return applicant.viewAvailableProjects();
    }

    /**
     * Applies for a specific BTO project with the specified flat type.
     *
     * @param projectName The name of the BTO project.
     * @param flatType The type of flat (e.g., "2-room", "3-room").
     * @return True if the application is successful, false otherwise.
     * @throws IllegalArgumentException If the project name or flat type is invalid, or the project does not exist.
     * @throws IllegalStateException If the applicant is not found.
     */
    public boolean applyForProject(String projectName, String flatType) {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be a valid type (e.g., '2-room', '3-room').");
        }
        BTOProject project = BTOProject.getProjectByName(projectName.trim());
        if (project == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName.trim());
        }
        boolean success = applicant.applyForProject(projectName.trim(), flatType.trim());
        if (success) {
            System.out.println("Application submitted successfully for project: " + projectName.trim());
        }
        return success;
    }

    /**
     * Retrieves the applicant's current BTO application status.
     *
     * @return The current BTO application, or null if none exists.
     * @throws IllegalStateException If the applicant is not found.
     */
    public BTOApplication viewMyApplication() {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        return applicant.viewMyApplication();
    }

    /**
     * Requests withdrawal of the applicant's current BTO application.
     *
     * @return True if the withdrawal request is successful, false otherwise.
     * @throws IllegalStateException If the applicant is not found.
     */
    public boolean requestWithdrawal() {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        boolean success = applicant.requestWithdrawal();
        if (success) {
            System.out.println("Withdrawal request submitted successfully.");
        }
        return success;
    }

    /**
     * Submits a new enquiry for a specific project.
     *
     * @param project The name of the project.
     * @param title The title of the enquiry.
     * @param detail The detailed description of the enquiry.
     * @return True if the enquiry is submitted successfully, false otherwise.
     * @throws IllegalArgumentException If the project, title, or detail is invalid, or the project does not exist.
     * @throws IllegalStateException If the applicant is not found.
     */
    public boolean submitEnquiry(String project, String title, String detail) {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        if (!Validator.isValidProjectName(project)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Enquiry title cannot be null or empty.");
        }
        if (detail == null || detail.trim().isEmpty()) {
            throw new IllegalArgumentException("Enquiry detail cannot be null or empty.");
        }
        BTOProject btoProject = BTOProject.getProjectByName(project.trim());
        if (btoProject == null) {
            throw new IllegalArgumentException("Project does not exist: " + project.trim());
        }
        applicant.submitEnquiry(project.trim(), title.trim(), detail.trim());
        System.out.println("Enquiry submitted successfully for project: " + project.trim());
        return true;
    }

    /**
     * Retrieves all enquiries submitted by the applicant.
     *
     * @return A list of the applicant's enquiries.
     * @throws IllegalStateException If the applicant is not found.
     */
    public List<Enquiry> viewMyEnquiries() {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        return applicant.viewMyEnquiries();
    }

    /**
     * Edits an existing enquiry submitted by the applicant.
     *
     * @param enquiryIndex The index of the enquiry to edit.
     * @param newTitle The new title for the enquiry.
     * @param newDetail The new detailed description for the enquiry.
     * @return True if the enquiry is edited successfully, false otherwise.
     * @throws IllegalArgumentException If the enquiry index is invalid or the new title/detail is invalid.
     * @throws IllegalStateException If the applicant is not found.
     */
    public boolean editEnquiry(int enquiryIndex, String newTitle, String newDetail) {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        if (enquiryIndex < 0) {
            throw new IllegalArgumentException("Enquiry index cannot be negative.");
        }
        List<Enquiry> enquiries = applicant.viewMyEnquiries();
        if (enquiryIndex >= enquiries.size()) {
            throw new IllegalArgumentException("Invalid enquiry index: " + enquiryIndex + ". Must be less than " + enquiries.size() + ".");
        }
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("New enquiry title cannot be null or empty.");
        }
        if (newDetail == null || newDetail.trim().isEmpty()) {
            throw new IllegalArgumentException("New enquiry detail cannot be null or empty.");
        }
        boolean success = applicant.editEnquiry(enquiryIndex, newTitle.trim(), newDetail.trim());
        if (success) {
            System.out.println("Enquiry edited successfully at index: " + enquiryIndex);
        }
        return success;
    }

    /**
     * Deletes an existing enquiry submitted by the applicant.
     *
     * @param enquiryIndex The index of the enquiry to delete.
     * @return True if the enquiry is deleted successfully, false otherwise.
     * @throws IllegalArgumentException If the enquiry index is invalid.
     * @throws IllegalStateException If the applicant is not found.
     */
    public boolean deleteEnquiry(int enquiryIndex) {
        if (user.getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + user.getNRIC());
        }
        if (enquiryIndex < 0) {
            throw new IllegalArgumentException("Enquiry index cannot be negative.");
        }
        List<Enquiry> enquiries = applicant.viewMyEnquiries();
        if (enquiryIndex >= enquiries.size()) {
            throw new IllegalArgumentException("Invalid enquiry index: " + enquiryIndex + ". Must be less than " + enquiries.size() + ".");
        }
        boolean success = applicant.deleteEnquiry(enquiryIndex);
        if (success) {
            System.out.println("Enquiry deleted successfully at index: " + enquiryIndex);
        }
        return success;
    }
}
