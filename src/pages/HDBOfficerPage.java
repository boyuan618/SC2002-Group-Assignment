package pages;

import controller.HDBOfficerController;
import model.BTOApplication;

import java.util.Scanner;

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
            System.out.println("4. View Applicant's Booking Status");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerAsOfficer(scanner);
                    break;
                case 2:
                    viewProjectDetails();
                    break;
                case 3:
                    viewAndRespondToEnquiries();
                    break;
                case 4:
                    viewApplicantBookingStatus(scanner);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        scanner.close();
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
        ;
    }

    // View and respond to enquiries related to the project
    private void viewAndRespondToEnquiries() {
        officer.respondToEnquiries();
    }

    // View the applicant's booking status and update if needed
    private void viewApplicantBookingStatus(Scanner scanner) {
        if (officer.getProjectAssigned() != null) {
            System.out.print("Enter the applicant's NRIC to view booking status: ");
            String applicantNric = scanner.nextLine();

            BTOApplication application = BTOApplication.getApplicationByNRIC(applicantNric);
            if (application != null
                    && application.getProjectName().equals(officer.getProjectAssigned().getProjectName())) {
                System.out.println(
                        "Applicant: " + model.Applicant.getApplicantByNRIC(application.getApplicantNRIC()).getName());
                System.out.println("Status: " + application.getStatus());
                System.out.println("Flat Type: " + application.getFlatType());
                if (application.getStatus().equalsIgnoreCase("successful")) {
                    System.out.print("Do you want to update status to 'booked'? (yes/no): ");
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
}
