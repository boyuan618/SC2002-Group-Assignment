package model;

import java.time.LocalDate;
import java.util.*;

public class HDBManager extends User implements EnquiryInt {

    public static final String PROJECT_CSV = "data/ProjectList.csv"; // Path to your project CSV file

    public HDBManager(String name, String nric, String password, int age, String maritalStatus) {
        super(name, nric, password, age, maritalStatus, "HDBManager");

    }

    // Method to create a new project listing and save it to CSV
    public BTOProject createListing(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
            int officerSlot, String officerList, String visibility) {

        // Check if the manager is already managing a project within the application
        // period
        if (isManagingAnotherProjectInPeriod(openDate, closeDate)) {
            System.out.println("You are already managing a project in this application period.");
            return null;
        }

        BTOProject newProject = new BTOProject(projectName, neighborhood, type1, units1, price1,
                type2, units2, price2, openDate, closeDate, this.getName(), officerSlot, officerList, visibility);

        // Save the new project to the CSV file
        BTOProject.addProject(PROJECT_CSV, newProject);
        return newProject;
    }

    // Method to check if the manager is already handling a project within the given
    // application period
    private boolean isManagingAnotherProjectInPeriod(LocalDate openDate, LocalDate closeDate) {
        List<BTOProject> projects = BTOProject.getProjects();
        for (BTOProject project : projects) {
            if (project.getManager().equals(this.getName())) {
                // Check if the current project overlaps with the period of another project
                if (!openDate.isAfter(project.getCloseDate()) && !closeDate.isBefore(project.getOpenDate())) {
                    return true; // There's an overlap in the application period
                }
            }
        }
        return false; // No overlap, so the manager can create a new project
    }

    // Method to edit an existing project listing and update the CSV file
    public void editListing(BTOProject updatedProject) {
        // Update project details in the CSV file
        BTOProject.editProject(PROJECT_CSV, updatedProject);
    }

    // Method to delete a project from the CSV file
    public void deleteListing(String projectName) {
        // Delete the project from the CSV file
        BTOProject.deleteProject(PROJECT_CSV, projectName);
    }

    // Method to toggle the visibility of a project
    public void toggleVisibility(String projectName, String visibility) {
        BTOProject project = getProjectFromCSV(projectName);
        if (project != null) {
            project.setVisibility(visibility);
            editListing(project); // Update project in the CSV
        }
    }

    // Method to view only the manager's projects
    public List<BTOProject> viewMyProjects() {
        List<BTOProject> projects = BTOProject.getProjects();
        List<BTOProject> myProjects = new ArrayList<>();
        for (BTOProject project : projects) {
            if (project.getManager().equals(this.getName())) {
                myProjects.add(project);
            }
        }
        return myProjects;
    }

    // Helper method to retrieve a project from CSV by name
    private BTOProject getProjectFromCSV(String projectName) {
        List<BTOProject> projects = BTOProject.getProjects();
        for (BTOProject project : projects) {
            if (project.getProjectName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    public void viewAndReplyEnquiries() {
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry e = enquiries.get(i);
            System.out.println("\nEnquiry from NRIC : " + e.getEnquirerNRIC());
            System.out.println("Title             : " + e.getTitle());
            System.out.println("Detail            : " + e.getDetail());
            System.out.println("Reply             : "
                    + (e.getResponse() == null || e.getResponse().isEmpty() ? "<None>" : e.getResponse()));
            System.out.print("Reply to this enquiry? (y/n): ");
            String choice = sc.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                System.out.print("Enter reply: ");
                String reply = sc.nextLine();
                e.setResponse(reply);
            }

        }

        Enquiry.writeEnquiries(enquiries);
        System.out.println("✅ Replies saved.");

        sc.close();
    }
}
