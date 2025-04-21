package controller;

import model.HDBManager;
import model.User;
import model.BTOProject;
import java.util.*;

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
            String type2, int units2, int price2, String openDate, String closeDate,
            int officerSlot, String officerList, String visibility) {
        return hdbManager.createListing(projectName, neighborhood, type1, units1, price1, type2, units2, price2,
                openDate, closeDate, officerSlot, officerList, visibility);
    }

    // Method to edit an existing project
    public void editProject(Scanner sc, String projectName) {
        List<BTOProject> allProjects = BTOProject.getProjects();
        BTOProject selected = null;

        for (BTOProject p : allProjects) {
            if (p.getProjectName().equalsIgnoreCase(projectName) && p.getManager().equals(hdbManager.getName())) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("❌ You do not have permission to edit this project or it does not exist.");
            return;
        }

        System.out.println("Editing project: " + selected.getProjectName());

        System.out.print("New Neighborhood [" + selected.getNeighborhood() + "]: ");
        String neighborhood = sc.nextLine();
        if (!neighborhood.isEmpty())
            selected.setNeighborhood(neighborhood);

        System.out.print("New Type 1 [" + selected.getType1() + "]: ");
        String type1 = sc.nextLine();
        if (!type1.isEmpty())
            selected.setType1(type1);

        System.out.print("New Units for Type 1 [" + selected.getUnits1() + "]: ");
        String units1 = sc.nextLine();
        if (!units1.isEmpty())
            selected.setUnits1(Integer.parseInt(units1));

        System.out.print("New Price for Type 1 [" + selected.getPrice1() + "]: ");
        String price1 = sc.nextLine();
        if (!price1.isEmpty())
            selected.setPrice1(Integer.parseInt(price1));

        System.out.print("New Type 2 [" + selected.getType2() + "]: ");
        String type2 = sc.nextLine();
        if (!type2.isEmpty())
            selected.setType2(type2);

        System.out.print("New Units for Type 2 [" + selected.getUnits2() + "]: ");
        String units2 = sc.nextLine();
        if (!units2.isEmpty())
            selected.setUnits2(Integer.parseInt(units2));

        System.out.print("New Price for Type 2 [" + selected.getPrice2() + "]: ");
        String price2 = sc.nextLine();
        if (!price2.isEmpty())
            selected.setPrice2(Integer.parseInt(price2));

        System.out.print("New Application Opening Date [" + selected.getOpenDate() + "]: ");
        String openDateStr = sc.nextLine();
        if (!openDateStr.isEmpty())
            selected.setOpenDate(openDateStr);

        System.out.print("New Application Closing Date [" + selected.getCloseDate() + "]: ");
        String closeDateStr = sc.nextLine();
        if (!closeDateStr.isEmpty())
            selected.setClosedate(closeDateStr);

        System.out.print("New Officer Slot [" + selected.getOfficerSlot() + "]: ");
        String officerSlot = sc.nextLine();
        if (!officerSlot.isEmpty())
            selected.setOfficerSlot(Integer.parseInt(officerSlot));

        // ✅ New Officer List input
        System.out.print(
                "New Officer List (comma-separated Names) [" + String.join(",", selected.getOfficerList()) + "]: ");
        String officerListStr = sc.nextLine();
        if (!officerListStr.trim().isEmpty()) {
            selected.setOfficerList(officerListStr);
        }

        System.out.print("Set visibility (on/off) [" + selected.getVisibility() + "]: ");
        String visibility = sc.nextLine();
        if (!visibility.isEmpty())
            selected.setVisibility(visibility);

        hdbManager.editProject(selected);
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
