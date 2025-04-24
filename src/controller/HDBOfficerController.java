package controller;

import model.Applicant;
import model.BTOApplication;
import model.BTOProject;
import model.HDBOfficer;
import model.Receipt;
import model.User;
import utils.Validator;

import java.util.*;

/**
 * Controller for handling HDB Officer interactions in the HDB BTO Management System, extending
 * ApplicantController to include officer-specific tasks such as project registration, application
 * management, receipt generation, and enquiry responses, while allowing officers to apply for
 * projects as applicants under specific conditions.
 *
 * @author SC2002Team
 */
public class HDBOfficerController extends ApplicantController {
    final private HDBOfficer hdbOfficer;

    /**
     * Constructs an HDBOfficerController with the specified user, initializing the HDBOfficer.
     *
     * @param user The authenticated user (must have HDBOfficer role).
     * @throws IllegalArgumentException If the user is null, has an invalid NRIC, or is not an HDBOfficer.
     */
    public HDBOfficerController(User user) {
        super(user);
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (!user.getRole().equals("HDBOfficer")) {
            throw new IllegalArgumentException("User must have HDBOfficer role.");
        }
        if (!Validator.isValidNRIC(user.getNRIC())) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        this.hdbOfficer = new HDBOfficer(user.getName(), user.getNRIC(), user.getAge(), user.getMaritalStatus());
    }

    /**
     * Retrieves all BTO projects in the system.
     *
     * @return A list of all BTO projects.
     */
    public List<BTOProject> viewAllProjects() {
        return BTOProject.getProjects();
    }

    /**
     * Registers the officer to handle a specified BTO project.
     *
     * @param projectName The name of the project to register for.
     * @throws IllegalArgumentException If the project name is invalid or the project does not exist.
     */
    public void registerForProject(String projectName) {
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        BTOProject project = BTOProject.getProjectByName(projectName.trim());
        if (project == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName.trim());
        }
        hdbOfficer.registerForProject(project);
    }

    /**
     * Displays details of the officer's assigned project, if any.
     *
     * @throws IllegalStateException If the officer is not assigned to any project.
     */
    public void viewAssignedProject() {
        BTOProject assignedProject = hdbOfficer.getProjectAssigned();
        if (assignedProject == null) {
            throw new IllegalStateException("Officer is not handling any project currently.");
        }
        hdbOfficer.viewAllProjectDetails();
    }

    /**
     * Handles flat selection for a BTO application, updating the application's flat type.
     *
     * @param application The BTO application to process.
     * @param flatType The type of flat to select (e.g., "2-room", "3-room").
     * @throws IllegalArgumentException If the application is null, has an invalid NRIC, or the flat type is invalid.
     * @throws IllegalStateException If the officer is not assigned to any project.
     */
    public void handleFlatSelection(BTOApplication application, String flatType) {
        BTOProject assignedProject = hdbOfficer.getProjectAssigned();
        if (assignedProject == null) {
            throw new IllegalStateException("Officer is not handling any project currently.");
        }
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null.");
        }
        if (!Validator.isValidNRIC(application.getApplicantNRIC())) {
            throw new IllegalArgumentException("Invalid applicant NRIC in application: " + application.getApplicantNRIC());
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be a valid type (e.g., '2-room', '3-room').");
        }
        hdbOfficer.handleFlatSelection(application, flatType.trim());
        
    }

    /**
     * Generates and prints a receipt for a BTO application.
     *
     * @param application The BTO application for which to generate the receipt.
     * @throws IllegalArgumentException If the application is null or has an invalid NRIC.
     * @throws IllegalStateException If the officer is not assigned to any project.
     */
    public void generateReceipt(BTOApplication application) {
        BTOProject assignedProject = hdbOfficer.getProjectAssigned();
        if (assignedProject == null) {
            throw new IllegalStateException("Officer is not handling any project currently.");
        }
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null.");
        }
        if (!Validator.isValidNRIC(application.getApplicantNRIC())) {
            throw new IllegalArgumentException("Invalid applicant NRIC in application: " + application.getApplicantNRIC());
        }
        Receipt receipt = Receipt.fromBTOApplication(application);
        receipt.printReceipt();
        System.out.println("Receipt generated successfully for application NRIC: " + application.getApplicantNRIC());
    }

    /**
     * Allows the officer to view and respond to enquiries for their assigned project.
     *
     * @param sc The Scanner for user input.
     * @throws IllegalArgumentException If the scanner is null.
     * @throws IllegalStateException If the officer is not assigned to any project.
     */
    public void respondToEnquiries(Scanner sc) {
        BTOProject assignedProject = hdbOfficer.getProjectAssigned();
        if (assignedProject == null) {
            throw new IllegalStateException("Officer is not handling any project currently.");
        }
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        hdbOfficer.viewAndReplyEnquiries(sc, assignedProject.getProjectName());
        System.out.println("Enquiry responses processed successfully for project: " + assignedProject.getProjectName());
    }

    /**
     * Retrieves the project assigned to the officer.
     *
     * @return The assigned BTO project, or null if none is assigned.
     */
    public BTOProject getProjectAssigned() {

        return hdbOfficer.getProjectAssigned();
    }

    /**
     * Retrieves all BTO applications for the officer's assigned project.
     *
     * @return A list of BTO applications.
     * @throws IllegalStateException If the officer is not assigned to any project.
     */
    public ArrayList<BTOApplication> getApplications() {
        BTOProject assignedProject = hdbOfficer.getProjectAssigned();
        if (assignedProject == null) {
            throw new IllegalStateException("Officer is not handling any project currently.");
        }
        return hdbOfficer.getApplications();
    }

    /**
     * Applies for a BTO project as an applicant, overridden to prevent officers from applying to
     * their own assigned project.
     *
     * @param projectName The name of the BTO project.
     * @param flatType The type of flat (e.g., "2-room", "3-room").
     * @return True if the application is successful, false otherwise.
     * @throws IllegalArgumentException If the project name or flat type is invalid, or the project does not exist.
     * @throws IllegalStateException If the applicant is not found or the officer is assigned to the project.
     */
    @Override
    public boolean applyForProject(String projectName, String flatType) {
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
        if (getUser().getNRIC() == null) {
            throw new IllegalStateException("User NRIC cannot be null.");
        }
        Applicant applicant = Applicant.getApplicantByNRIC(getUser().getNRIC());
        if (applicant == null) {
            throw new IllegalStateException("Applicant not found for NRIC: " + getUser().getNRIC());
        }
        BTOProject assignedProject = hdbOfficer.getProjectAssigned();
        if (assignedProject != null && assignedProject.getProjectName().equals(projectName.trim())) {
            throw new IllegalStateException("Officer cannot apply for their own assigned project: " + projectName.trim());
        }
        boolean success = applicant.applyForProject(projectName.trim(), flatType.trim());
        if (success) {
            System.out.println("Application submitted successfully for project: " + projectName.trim());
        }
        return success;
    }
    
}
