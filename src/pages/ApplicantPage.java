package pages;

import controller.ApplicantController;
import model.BTOProject;
import model.BTOApplication;
import model.Enquiry;

import java.util.List;
import java.util.Scanner;

public class ApplicantPage {
    final private ApplicantController controller;
    final private Scanner scanner;

    public ApplicantPage(ApplicantController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        int choice;

        do {
            System.out.println("\n=== Applicant Menu ===");
            System.out.println("1. View Available BTO Projects");
            System.out.println("2. Apply for BTO Project");
            System.out.println("3. View My Application");
            System.out.println("4. Withdraw My Application");
            System.out.println("5. Submit Enquiry");
            System.out.println("6. View My Enquiries");
            System.out.println("7. Edit Enquiry");
            System.out.println("8. Delete Enquiry");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> displayAvailableProjects();
                case 2 -> applyForProject();
                case 3 -> viewMyApplication();
                case 4 -> withdrawApplication();
                case 5 -> submitEnquiry();
                case 6 -> viewMyEnquiries();
                case 7 -> editEnquiry();
                case 8 -> deleteEnquiry();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // Display available projects for the applicant
    public void displayAvailableProjects() {
        List<BTOProject> availableProjects = controller.viewAvailableProjects();
        if (availableProjects != null && !availableProjects.isEmpty()) {
            System.out.println("Available Projects:");
            for (BTOProject project : availableProjects) {
                System.out.println(project.getProjectName() + " - " + project.getVisibility());
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

        boolean success = controller.applyForProject(projectName, flatType);
        if (success) {
            System.out.println("You have successfully applied for the project.");
        } else {
            System.out.println(
                    "You cannot apply for the project. You may have already applied or the project is unavailable.");
        }
    }

    // View the applicant's current application status
    public void viewMyApplication() {
        BTOApplication application = controller.viewMyApplication();
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
        boolean success = controller.requestWithdrawal();
        if (success) {
            System.out.println("Your application has been successfully withdrawn.");
        } else {
            System.out.println("You do not have an active application to withdraw.");
        }
    }

    // View available actions related to enquiries
    public void manageEnquiries() {
        System.out.println("1. View my enquiries");
        System.out.println("2. Submit a new enquiry");
        System.out.println("3. Edit an enquiry");
        System.out.println("4. Delete an enquiry");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1:
                viewMyEnquiries();
                break;
            case 2:
                submitEnquiry();
                break;
            case 3:
                editEnquiry();
                break;
            case 4:
                deleteEnquiry();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // View the applicant's existing enquiries
    private void viewMyEnquiries() {
        List<Enquiry> enquiries = controller.viewMyEnquiries();
        if (enquiries != null && !enquiries.isEmpty()) {
            System.out.println("Your Enquiries:");
            for (Enquiry enquiry : enquiries) {
                System.out.println("Project: " + enquiry.getProjectName() + " | Title: " + enquiry.getTitle()
                        + " | Details: " + enquiry.getDetail() + " | Response: " + enquiry.getResponse());
            }
        } else {
            System.out.println("You have no enquiries.");
        }
    }

    // Allow the applicant to submit a new enquiry
    private void submitEnquiry() {
        System.out.print("Enter the project name: ");
        String project = scanner.nextLine();
        System.out.print("Enter the title of your enquiry: ");
        String title = scanner.nextLine();
        System.out.print("Enter the details of your enquiry: ");
        String detail = scanner.nextLine();

        boolean success = controller.submitEnquiry(project, title, detail);
        if (success) {
            System.out.println("Your enquiry has been successfully submitted.");
        } else {
            System.out.println("Failed to submit your enquiry.");
        }
    }

    // Allow the applicant to edit an existing enquiry
    private void editEnquiry() {
        List<Enquiry> enquiries = controller.viewMyEnquiries();
        if (enquiries != null && !enquiries.isEmpty()) {
            System.out.print("Enter the index of the enquiry you want to edit: ");
            int index = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter the new title: ");
            String newTitle = scanner.nextLine();
            System.out.print("Enter the new detail: ");
            String newDetail = scanner.nextLine();

            boolean success = controller.editEnquiry(index, newTitle, newDetail);
            if (success) {
                System.out.println("Your enquiry has been successfully updated.");
            } else {
                System.out.println("Failed to update your enquiry.");
            }
        } else {
            System.out.println("You have no enquiries to edit.");
        }
    }

    // Allow the applicant to delete an existing enquiry
    private void deleteEnquiry() {
        List<Enquiry> enquiries = controller.viewMyEnquiries();
        if (enquiries != null && !enquiries.isEmpty()) {
            System.out.print("Enter the index of the enquiry you want to delete: ");
            int index = Integer.parseInt(scanner.nextLine());

            boolean success = controller.deleteEnquiry(index);
            if (success) {
                System.out.println("Your enquiry has been successfully deleted.");
            } else {
                System.out.println("Failed to delete your enquiry.");
            }
        } else {
            System.out.println("You have no enquiries to delete.");
        }
    }
}
