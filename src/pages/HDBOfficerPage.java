package pages;

import controller.HDBOfficerController;
import model.BTOApplication;
import model.BTOProject;
import model.Room;
import model.Applicant;
import utils.Validator;

import java.util.*;

/**
 * User interface for HDB Officers, providing a menu-driven dashboard to manage projects, applications,
 * enquiries, and perform applicant tasks such as applying for BTO projects.
 *
 * @author SC2002Team
 */
public class HDBOfficerPage {
    final private HDBOfficerController officer;
    final private Scanner scanner;

    /**
     * Constructs an HDBOfficerPage with the specified controller, initializing the scanner for user input.
     *
     * @param controller The HDBOfficerController to handle business logic.
     * @throws IllegalArgumentException If the controller is null.
     */
    public HDBOfficerPage(HDBOfficerController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null.");
        }
        this.officer = controller;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the HDB Officer dashboard menu, allowing the user to select actions such as registering
     * for a project, managing applications, or applying for BTO projects. Continues until the user chooses to exit.
     */
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
            if (!scanner.hasNextInt()) {
                scanner.nextLine(); // Clear invalid input
                choice = -1; // Trigger default case
            } else {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            }

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
                case 0 -> System.out.println("Exiting HDB Officer Dashboard...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

    }

    /**
     * Prompts the user to enter a project name and registers the officer for that project.
     *
     * @param scanner The Scanner for user input.
     * @throws IllegalArgumentException If the scanner is null.
     */
    private void registerAsOfficer(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        System.out.print("Enter the project name to register for: ");
        String projectName = scanner.nextLine();
        if (projectName == null || projectName.trim().isEmpty()) {
            System.out.println("Project name cannot be null or empty.");
            return;
        }
        if (!Validator.isValidProjectName(projectName)) {
            System.out.println("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
            return;
        }
        try {
            officer.registerForProject(projectName.trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to register for project: " + e.getMessage());
        }
    }

    /**
     * Displays details of the project assigned to the officer.
     */
    private void viewProjectDetails() {
        try {
            officer.viewAssignedProject();
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Allows the officer to view and respond to enquiries for their assigned project.
     *
     * @param sc The Scanner for user input.
     * @throws IllegalArgumentException If the scanner is null.
     */
    private void viewAndRespondToEnquiries(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        try {
            officer.respondToEnquiries(sc);
            System.out.println("Enquiry responses processed successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to process enquiries: " + e.getMessage());
        }
    }

    /**
     * Displays all successful applications for the officer's assigned project.
     */
    private void viewApplications() {
        try {
            ArrayList<BTOApplication> applications = officer.getApplications();
            if (applications == null) {
                System.out.println("No applications available: Invalid project state.");
                return;
            }
            if (applications.isEmpty()) {
                System.out.println("No successful applications found for your assigned project.");
                return;
            }
            System.out.println("Successful Applications:");
            for (BTOApplication application : applications) {
                if ("Successful".equals(application.getStatus())) {
                    System.out.println("---");
                    Applicant applicant = Applicant.getApplicantByNRIC(application.getApplicantNRIC());
                    if (applicant == null) {
                        System.out.println("Applicant not found for NRIC: " + application.getApplicantNRIC());
                        continue;
                    }
                    System.out.println("Applicant Nric: " + applicant.getNric());
                    System.out.println("Applicant: " + applicant.getName());
                    System.out.println("Status: " + application.getStatus());
                    System.out.println("Flat Type: " + application.getFlatType());
                    System.out.println("---");
                }
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Prompts the user to enter an applicant's NRIC and manage their booking status, updating to 'Booked' if applicable.
     *
     * @param scanner The Scanner for user input.
     * @throws IllegalArgumentException If the scanner is null.
     */
    private void manageApplicantBookingStatus(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        BTOProject assignedProject = officer.getProjectAssigned();
        if (assignedProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        System.out.print("Enter the applicant's NRIC to view booking status: ");
        String applicantNric = scanner.nextLine();
        if (applicantNric == null || applicantNric.trim().isEmpty()) {
            System.out.println("NRIC cannot be null or empty.");
            return;
        }
        if (!Validator.isValidNRIC(applicantNric)) {
            System.out.println("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
            return;
        }
        BTOApplication application = BTOApplication.getApplicationByNRIC(applicantNric.trim());
        if (application == null) {
            System.out.println("No application found for NRIC: " + applicantNric.trim());
            return;
        }
        if (!application.getProjectName().equals(assignedProject.getProjectName())) {
            System.out.println("Application is not for your assigned project: " + assignedProject.getProjectName());
            return;
        }
        Applicant applicant = Applicant.getApplicantByNRIC(applicantNric.trim());
        if (applicant == null) {
            System.out.println("Applicant not found for NRIC: " + applicantNric.trim());
            return;
        }
        System.out.println("Applicant: " + applicant.getName());
        System.out.println("Status: " + application.getStatus());
        System.out.println("Flat Type: " + application.getFlatType());
        if ("Successful".equalsIgnoreCase(application.getStatus())) {
            System.out.print("Do you want to update status to 'Booked'? (yes/no): ");
            String response = scanner.nextLine();
            if (response == null || response.trim().isEmpty()) {
                System.out.println("Response cannot be null or empty.");
                return;
            }
            response = response.trim().toLowerCase();
            if (!"yes".equals(response) && !"no".equals(response)) {
                System.out.println("Invalid response: Must be 'yes' or 'no'.");
                return;
            }
            if ("yes".equals(response)) {
                try {
                    officer.handleFlatSelection(application, application.getFlatType());
                    
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Failed to update booking status: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Application cannot be booked: Status is " + application.getStatus());
        }
    }

    /**
     * Displays all BTO projects available to the officer based on their eligibility.
     */
    public void displayAvailableProjects() {
        try {
            List<BTOProject> availableProjects = officer.viewAvailableProjects();
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
                System.out.println("Manager              : " + p.getManager());
                System.out.println("Officer Slot         : " + p.getOfficerSlot());

                System.out.println("Officer(s) Assigned  : " + p.getOfficerList());

                System.out.println("Visibility           : " + p.getVisibility());
                System.out.println("=========================");
                System.out.println();
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Prompts the user to apply for a BTO project as an applicant, specifying project name and flat type.
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
            boolean success = officer.applyForProject(projectName.trim(), flatType.trim());
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
     * Displays the officer's current BTO application status, if any.
     */
    public void viewMyApplication() {
        try {
            BTOApplication application = officer.viewMyApplication();
            if (application != null) {
                System.out.println("Your Current Application Status:");
                System.out.println("Project: " + application.getProjectName());
                System.out.println("Flat Type: " + application.getFlatType());
                System.out.println("Status: " + application.getStatus());
            } else {
                System.out.println("You have not applied for any projects yet.");
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }

    /**
     * Allows the officer to withdraw their current BTO application.
     */
    public void withdrawApplication() {
        try {
            boolean success = officer.requestWithdrawal();
            if (success) {
                System.out.println("Your application has been successfully withdrawn.");
            } else {
                System.out.println("You do not have an active application to withdraw.");
            }
        } catch (IllegalStateException e) {
            System.out.println("" + e.getMessage());
        }
    }
}
