package pages;

import controller.HDBOfficerController;
import model.BTOApplication;
import model.BTOProject;

import java.util.*;

public class HDBOfficerPage {
    final private HDBOfficerController officer;
    final private Scanner scanner;

    public HDBOfficerPage(HDBOfficerController controller) {
        this.officer = controller;
        this.scanner = new Scanner(System.in);
    }

    // Display available actions for HDB Officer
    public void displayMenu() {
        int choice;

        do {
            System.out.println("\nHDB Officer Dashboard");
            System.out.println("1. Register as Officer for a Project");
            System.out.println("2. View Project Details");
            System.out.println("3. View and Respond to Enquiries");
            System.out.println("4. View Applications");
            System.out.println("5. Manage Applicant's Booking Status");
            System.out.println("6. View Available BTO Projects");
            System.out.println("7. Apply for BTO Project");
            System.out.println("8. View My Application");
            System.out.println("9. Withdraw My Application");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> registerAsOfficer(scanner);
                case 2 -> viewProjectDetails();
                case 3 -> viewAndRespondToEnquiries(scanner);
                case 4 -> viewApplications();
                case 5 -> manageApplicantBookingStatus(scanner);
                case 6 -> displayAvailableProjects();
                case 7 -> applyForProject();
                case 8 -> viewMyApplication();
                case 9 -> withdrawApplication();
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

    }

    // Register the officer for a project
    private void registerAsOfficer(Scanner scanner) {

        System.out.print("Enter the project name to register for: ");
        String projectName = scanner.nextLine();

        officer.registerForProject(projectName);
    }

    // View the details of the project the officer is handling
    private void viewProjectDetails() {
        officer.viewAssignedProject();

    }

    // View and respond to enquiries related to the project
    private void viewAndRespondToEnquiries(Scanner sc) {
        officer.respondToEnquiries(sc);
    }

    // View Applications
    private void viewApplications() {
        ArrayList<BTOApplication> applications = officer.getApplications();

        for (BTOApplication application : applications) {
            if (application.getStatus().equals("Successful")) {
                System.out.println(
                        "Applicant: " + model.Applicant.getApplicantByNRIC(application.getApplicantNRIC()).getName());
                System.out.println("Status: " + application.getStatus());
                System.out.println("Flat Type: " + application.getFlatType());

            }
        }
    }

    // View the applicant's booking status and update if needed
    private void manageApplicantBookingStatus(Scanner scanner) {
        if (officer.getProjectAssigned() != null) {
            System.out.print("Enter the applicant's NRIC to view booking status: ");
            String applicantNric = scanner.nextLine();

            BTOApplication application = BTOApplication.getApplicationByNRIC(applicantNric);
            // Check if status is successful
            if (application.getStatus().equals("Successful")) {
                System.out.println("Application cannot be handled by you, invalid status");
                return;
            }
            if (application != null
                    && application.getProjectName().equals(officer.getProjectAssigned().getProjectName())) {
                System.out.println(
                        "Applicant: " + model.Applicant.getApplicantByNRIC(application.getApplicantNRIC()).getName());
                System.out.println("Status: " + application.getStatus());
                System.out.println("Flat Type: " + application.getFlatType());
                if (application.getStatus().equalsIgnoreCase("Successful")) {
                    System.out.print("Do you want to update status to 'Booked'? (yes/no): ");
                    String response = scanner.nextLine();
                    if ("yes".equalsIgnoreCase(response)) {
                        officer.handleFlatSelection(application, application.getFlatType());
                    }
                }
            } else {
                System.out.println("No application found for this NRIC in your assigned project.");
            }
        } else {
            System.out.println("You are not assigned to any project.");
        }
    }

    public void displayAvailableProjects() {
        List<BTOProject> availableProjects = officer.viewAvailableProjects();
        if (availableProjects != null && !availableProjects.isEmpty()) {
            System.out.println("Available Projects:");
            for (BTOProject project : availableProjects) {
                System.out.println(project.getProjectName() + " - " + project.toCSV());
            }
        } else {
            System.out.println("No available projects found or you are not eligible.");
        }
    }

    // Ask the user to apply for a project
    public void applyForProject() {
        System.out.print("Enter the project name you want to apply for: ");
        String projectName = scanner.nextLine();
        System.out.print("Enter the flat type (e.g., 2-Room, 3-Room): ");
        String flatType = scanner.nextLine();

        boolean success = officer.applyForProject(projectName, flatType);
        if (success) {
            System.out.println("You have successfully applied for the project.");
        } else {
            System.out.println(
                    "You cannot apply for the project. You may have already applied or the project is unavailable.");
        }
    }

    // View the applicant's current application status
    public void viewMyApplication() {
        BTOApplication application = officer.viewMyApplication();
        if (application != null) {
            System.out.println("Your Current Application Status: ");
            System.out.println("Project: " + application.getProjectName());
            System.out.println("Flat Type: " + application.getFlatType());
            System.out.println("Status: " + application.getStatus());
        } else {
            System.out.println("You have not applied for any projects yet.");
        }
    }

    // Allow the user to withdraw their application
    public void withdrawApplication() {
        boolean success = officer.requestWithdrawal();
        if (success) {
            System.out.println("Your application has been successfully withdrawn.");
        } else {
            System.out.println("You do not have an active application to withdraw.");
        }
    }
}
