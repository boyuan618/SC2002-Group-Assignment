package controller;

import model.BTOProject;
import model.ProjectManager;
import model.User;

import java.util.List;
import java.util.Scanner;

public class ProjectManagerController extends HDBManagerController {

    public ProjectManagerController(User user) {
        super(user);
    }

    final private ProjectManager projectManager = new ProjectManager(user.getName(), user.getNRIC(), user.getPassword(),
            user.getAge(),
            user.getMaritalStatus());

    // Get all projects from the CSV file
    public List<BTOProject> getAllProjects() {
        return BTOProject.getProjects();
    }

    // Find a project by its name
    public BTOProject findProjectByName(String name) {
        return BTOProject.getProjectByName(name);
    }

    public void viewOfficerApplications() {
        projectManager.viewPendingOfficerRegistrations();
    }

    // Approve or reject officer registration
    public void approveRejectOfficerRegistration(String officerNRIC, boolean approve) {
        projectManager.approveRejectOfficer(officerNRIC, approve);
    }

    // Approve or reject a BTO application
    public void approveRejectBTOApplication(String applicantNRIC, String flatType,
            boolean approve) {
        projectManager.approveRejectApplication(applicantNRIC, flatType, approve);
    }

    // Approve or reject withdrawal request for a BTO application
    public void approveRejectBTOApplicationWithdrawal(String applicantNRIC, boolean approve) {
        projectManager.approveRejectWithdrawal(applicantNRIC, approve);
    }

    public void manageEnquiries(Scanner sc) {
        projectManager.viewAndReplyEnquiries(sc);
    }

}
