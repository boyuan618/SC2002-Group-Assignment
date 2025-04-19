package controller;

import model.HDBManager;
import model.User;
import model.BTOProject;
import java.time.LocalDate;
import java.util.List;

public class HDBManagerController {
    final public User user;
    final private HDBManager hdbManager;

    // Constructor with required parameters to initialize HDBManager
    public HDBManagerController(User user) {
        this.user = user;
        hdbManager = new HDBManager(user.getName(), user.getNRIC(), user.getPassword(), user.getAge(),
                user.getMaritalStatus()); // Pass parameters to constructor
    }

    // Method to create a new project
    public BTOProject createNewProject(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
            int officerSlot, String officerList, String visibility) {
        return hdbManager.createListing(projectName, neighborhood, type1, units1, price1, type2, units2, price2,
                openDate, closeDate, officerSlot, officerList, visibility);
    }

    // Method to edit an existing project
    public void editProject(BTOProject updatedProject) {
        hdbManager.editListing(updatedProject);
    }

    // Method to delete a project
    public void deleteProject(String projectName) {
        hdbManager.deleteListing(projectName);
    }

    // Method to toggle the visibility of a project
    public void toggleProjectVisibility(String projectName, String visibility) {
        hdbManager.toggleVisibility(projectName, visibility);
    }

    // Method to get the list of projects managed by this manager
    public List<BTOProject> getMyProjects() {
        return hdbManager.viewMyProjects();
    }

}
