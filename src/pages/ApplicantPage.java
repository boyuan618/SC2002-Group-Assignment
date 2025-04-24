package pages;

import controller.ApplicantController;
import model.BTOProject;
import model.BTOApplication;
import model.Enquiry;
import model.Room;
import utils.Validator;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for Applicants, providing a menu-driven dashboard to view BTO projects,
 * apply for projects, manage applications, and handle enquiries.
 *
 * @author SC2002Team
 */
public class ApplicantPage {
    final private ApplicantController controller;
    final private Scanner scanner;

    /**
     * Constructs an ApplicantPage with the specified controller, initializing the scanner for user input.
     *
     * @param controller The ApplicantController to handle business logic.
     * @throws IllegalArgumentException If the controller is null.
     */
    public ApplicantPage(ApplicantController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null.");
        }
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the Applicant menu, allowing the user to select actions such as viewing projects,
     * applying for projects, or managing enquiries. Continues until the user chooses to logout.
     */
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
            if (!scanner.hasNextInt()) {
                scanner.nextLine(); // Clear invalid input
                choice = -1; // Trigger default case
            } else {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            }

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
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    /**
     * Displays all BTO projects available to the applicant based on their eligibility.
     */
    public void displayAvailableProjects() {
        try {
            List<BTOProject> availableProjects = controller.viewAvailableProjects();
            if (availableProjects == null) {
                System.out.println("No available projects: Invalid eligibility state.");
                return;
            }
            if (availableProjects.isEmpty()) {
                System.out.println("No available projects found or you are not eligible.");
                return;
            }
            System.out.println("Available Projects:");
            for (BTOProject p : availableProjects) {
                System.out.println();
                System.out.println("=== Project Details ===");
                System.out.println("Project Name         : " + p.getProjectName());
                System.out.println("Neighborhood         : " + p.getNeighborhood());

                System.out.println("Room Types:");
                for (Room room : p.getRooms()) {
                    System.out.println("  - Type             : " + room.getRoomType());
                    System.out.println("    Units Available  : " + room.getUnits());
                    System.out.println("    Selling Price    : $" + room.getPrice());
                }

                System.out.println("Application Open Date: " + p.getOpenDate());
                System.out.println("Application Close Date: " + p.getCloseDate());
                System.out.println("=========================");
                System.out.println();
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Prompts the user to apply for a BTO project, specifying project name and flat type.
     */
    public void applyForProject() {
        System.out.print("Enter the project name you want to apply for: ");
        String projectName = scanner.nextLine();
        if (projectName == null || projectName.trim().isEmpty()) {
            System.out.println("Project name cannot be null or empty.");
            return;
        }
        if (!Validator.isValidProjectName(projectName)) {
            System.out.println("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
            return;
        }
        System.out.print("Enter the flat type (e.g., 2-Room, 3-Room): ");
        String flatType = scanner.nextLine();
        if (flatType == null || flatType.trim().isEmpty()) {
            System.out.println("Flat type cannot be null or empty.");
            return;
        }
        if (!Validator.isValidFlatType(flatType)) {
            System.out.println("Invalid flat type: Must be a valid type (e.g., '2-Room', '3-Room').");
            return;
        }
        try {
            boolean success = controller.applyForProject(projectName.trim(), flatType.trim());
            if (success) {
                System.out.println("Successfully applied for project: " + projectName.trim());
            } else {
                System.out.println("Failed to apply: You may have already applied or the project is unavailable.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to apply for project: " + e.getMessage());
        }
    }

    /**
     * Displays the applicant's current BTO application status, if any.
     */
    public void viewMyApplication() {
        try {
            BTOApplication application = controller.viewMyApplication();
            if (application != null) {
                System.out.println("Your Current Application Status:");
                System.out.println("Project: " + application.getProjectName());
                System.out.println("Flat Type: " + application.getFlatType());
                System.out.println("Status: " + application.getStatus());
            } else {
                System.out.println("You have no active applications for any projects yet.");
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Allows the applicant to withdraw their current BTO application.
     */
    public void withdrawApplication() {
        try {
            boolean success = controller.requestWithdrawal();
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Prompts the applicant to submit a new enquiry for a project.
     */
    private void submitEnquiry() {
        System.out.print("Enter the project name: ");
        String project = scanner.nextLine();
        if (project == null || project.trim().isEmpty()) {
            System.out.println("Project name cannot be null or empty.");
            return;
        }
        if (!Validator.isValidProjectName(project)) {
            System.out.println("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
            return;
        }
        System.out.print("Enter the title of your enquiry: ");
        String title = scanner.nextLine();
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Enquiry title cannot be null or empty.");
            return;
        }
        System.out.print("Enter the details of your enquiry: ");
        String detail = scanner.nextLine();
        if (detail == null || detail.trim().isEmpty()) {
            System.out.println("Enquiry detail cannot be null or empty.");
            return;
        }
        try {
            boolean success = controller.submitEnquiry(project.trim(), title.trim(), detail.trim());
            if (success) {
                System.out.println("Your enquiry has been successfully submitted.");
            } else {
                System.out.println("Failed to submit your enquiry.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to submit enquiry: " + e.getMessage());
        }
    }

    /**
     * Displays all enquiries submitted by the applicant.
     */
    private void viewMyEnquiries() {
        try {
            List<Enquiry> enquiries = controller.viewMyEnquiries();
            if (enquiries == null) {
                System.out.println("No enquiries available: Invalid applicant state.");
                return;
            }
            if (enquiries.isEmpty()) {
                System.out.println("You have no enquiries.");
                return;
            }
            System.out.println("Your Enquiries:");
            for (Enquiry enquiry : enquiries) {
                System.out.println("ID: " + enquiry.getId() + " | Project: " + enquiry.getProjectName() +
                        " | Title: " + enquiry.getTitle() + " | Details: " + enquiry.getDetail() +
                        " | Response: " + enquiry.getResponse());
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Prompts the applicant to edit an existing enquiry by specifying its index and new details.
     */
    private void editEnquiry() {
        try {
            List<Enquiry> enquiries = controller.viewMyEnquiries();
            if (enquiries == null) {
                System.out.println("No enquiries available: Invalid applicant state.");
                return;
            }
            if (enquiries.isEmpty()) {
                System.out.println("You have no enquiries to edit.");
                return;
            }
            System.out.print("Enter the index of the enquiry you want to edit: ");
            int index;
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input: Index must be a number.");
                scanner.nextLine(); // Clear invalid input
                return;
            }
            index = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (index < 0 || index >= enquiries.size()) {
                System.out.println("Invalid index: Must be between 0 and " + (enquiries.size() - 1) + ".");
                return;
            }
            System.out.print("Enter the new title: ");
            String newTitle = scanner.nextLine();
            if (newTitle == null || newTitle.trim().isEmpty()) {
                System.out.println("New title cannot be null or empty.");
                return;
            }
            System.out.print("Enter the new detail: ");
            String newDetail = scanner.nextLine();
            if (newDetail == null || newDetail.trim().isEmpty()) {
                System.out.println("New detail cannot be null or empty.");
                return;
            }
            boolean success = controller.editEnquiry(index, newTitle.trim(), newDetail.trim());
            if (success) {
                System.out.println("Your enquiry has been successfully updated.");
            } else {
                System.out.println("Failed to update your enquiry.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to edit enquiry: " + e.getMessage());
        }
    }

    /**
     * Prompts the applicant to delete an existing enquiry by specifying its index.
     */
    private void deleteEnquiry() {
        try {
            List<Enquiry> enquiries = controller.viewMyEnquiries();
            if (enquiries == null) {
                System.out.println("No enquiries available: Invalid applicant state.");
                return;
            }
            if (enquiries.isEmpty()) {
                System.out.println("You have no enquiries to delete.");
                return;
            }
            System.out.print("Enter the index of the enquiry you want to delete: ");
            int index;
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input: Index must be a number.");
                scanner.nextLine(); // Clear invalid input
                return;
            }
            index = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (index < 0 || index >= enquiries.size()) {
                System.out.println("Invalid index: Must be between 0 and " + (enquiries.size() - 1) + ".");
                return;
            }
            boolean success = controller.deleteEnquiry(index);
            if (success) {
                System.out.println("Your enquiry has been successfully deleted.");
            } else {
                System.out.println("Failed to delete your enquiry.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to delete enquiry: " + e.getMessage());
        }
    }
}
