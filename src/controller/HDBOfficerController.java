package controller;

import model.BTOApplication;
import model.BTOProject;
import model.HDBOfficer;
import model.Receipt;
import model.User;

import java.util.List;

public class HDBOfficerController extends ApplicantController {

    public User user;

    public HDBOfficerController(User user) {
        super(user);

    }

    final private HDBOfficer hdbOfficer = new HDBOfficer(user.getName(), user.getName(), user.getAge(),
            user.getMaritalStatus());

    // 1. View all projects
    public List<BTOProject> viewAllProjects() {
        return BTOProject.getProjects();
    }

    // 2. Register to handle a project
    public void registerForProject(String projectname) {
        hdbOfficer.registerForProject(BTOProject.getProjectByName(projectname));
    }

    // 3. View assigned project
    public void viewAssignedProject() {
        hdbOfficer.viewAllProjectDetails();
    }

    // 4. View and manage applications
    public void manageApplications(BTOApplication application, String flatType) {
        hdbOfficer.handleFlatSelection(application, flatType);
    }

    // 5. Generate receipt logic
    public void generateReceipt(BTOApplication application) {
        Receipt receipt = Receipt.fromBTOApplication(application);
        receipt.printReceipt();
    }

    // 6. Respond to enquiry
    public void respondToEnquiries() {
        hdbOfficer.viewAndReplyEnquiries(hdbOfficer.getProjectAssigned().getProjectName());
    }

    // 7. Get assigned project
    public BTOProject getProjectAssigned() {
        return hdbOfficer.getProjectAssigned();
    }

    // 8. Handel Flat update
    public void handleFlatSelection(BTOApplication application, String flatType) {
        hdbOfficer.handleFlatSelection(application, flatType);
    }

}
