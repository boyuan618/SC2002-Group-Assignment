package pages;

import controller.ProjectManagerController;
import model.BTOApplication;
import model.BTOProject;
import model.Room;
import model.Applicant;
import model.WithdrawalRequest;
import utils.Validator;

import java.util.*;

/**
 * Command-line interface for HDB Managers to manage BTO projects, officer applications, BTO applications,
 * and enquiries through a menu-driven dashboard.
 *
 * @author SC2002Team
 */
public class HDBManagerPage {
    final private ProjectManagerController manager;
    final private Scanner scanner;

    /**
     * Constructs an HDBManagerPage with the specified controller, initializing the scanner for user input.
     *
     * @param manager The ProjectManagerController to handle business logic.
     * @throws IllegalArgumentException If the manager is null.
     */
    public HDBManagerPage(ProjectManagerController manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Controller cannot be null.");
        }
        this.manager = manager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the HDB Manager menu, allowing the user to select actions such as creating projects,
     * managing applications, or generating reports. Continues until the user chooses to logout.
     */
    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- HDB Manager Menu ---");
            System.out.println("1. Create Project");
            System.out.println("2. Edit Project");
            System.out.println("3. Delete Project");
            System.out.println("4. View All Projects");
            System.out.println("5. View My Projects");
            System.out.println("6. View Officer Applications");
            System.out.println("7. Approve/Reject Officer Application");
            System.out.println("8. Manage Enquiries");
            System.out.println("9. View BTO Applications");
            System.out.println("10. Approve BTO Applications");
            System.out.println("11. Approve/Reject BTO Withdrawal Request");
            System.out.println("12. Generate Report");
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
                case 1 -> createProject();
                case 2 -> editProject();
                case 3 -> deleteProject();
                case 4 -> viewAllProjects();
                case 5 -> viewOwnProjects();
                case 6 -> viewOfficerApplications();
                case 7 -> approveRejectOfficer();
                case 8 -> manageEnquiries(scanner);
                case 9 -> viewBTOApplications();
                case 10 -> approveBTOApplications();
                case 11 -> approveRejectBTOWithdrawal(scanner);
                case 12 -> generateReport();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    /**
     * Prompts the user to create a new BTO project with details such as name, neighborhood, and room types.
     */
    private void createProject() {

        System.out.println("Enter project name:");
        String name = scanner.nextLine();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Project name cannot be null or empty.");
            return;
        }
        if (Validator.isValidProjectName(name)) {
            System.out.println("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
            return;
        }

        System.out.println("Enter neighborhood:");
        String neighborhood = scanner.nextLine();
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            System.out.println("Neighborhood cannot be null or empty.");
            return;
        }

        ArrayList<Room> rooms = new ArrayList<>();
        System.out.println("Enter number of room types:");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input: Number of room types must be a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }
        int types = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (types <= 0) {
            System.out.println("Number of room types must be positive.");
            return;
        }

        for (int i = 1; i <= types; i++) {
            System.out.println("Enter type " + i + " (2-Room/3-Room):");
            String type = scanner.nextLine();
            if (type == null || type.trim().isEmpty()) {
                System.out.println("Room type cannot be null or empty.");
                return;
            }
            if (!Validator.isValidFlatType(type)) {
                System.out.println("Invalid room type: Must be a valid type (e.g., '2-Room', '3-Room').");
                return;
            }
            System.out.println("Enter units for type " + i + ":");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input: Units must be a number.");
                scanner.nextLine(); // Clear invalid input
                return;
            }
            int units = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (units <= 0) {
                System.out.println("Units must be a positive integer.");
                return;
            }
            System.out.println("Enter price for type " + i + ":");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input: Price must be a number.");
                scanner.nextLine(); // Clear invalid input
                return;
            }
            int price = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (price <= 0) {
                System.out.println("Price must be a positive integer.");
                return;
            }
            rooms.add(new Room(type.trim(), units, price));
        }

        System.out.println("Enter application opening date (m/dd/yyyy):");
        String openDate = scanner.nextLine();
        if (openDate == null || openDate.trim().isEmpty()) {
            System.out.println("Open date cannot be null or empty.");
            return;
        }
        if (!Validator.isValidDate(openDate)) {
            System.out.println("Invalid date format: Must be m/dd/yyyy.");
            return;
        }

        System.out.println("Enter application closing date (m/dd/yyyy):");
        String closeDate = scanner.nextLine();
        if (closeDate == null || closeDate.trim().isEmpty()) {
            System.out.println("Close date cannot be null or empty.");
            return;
        }
        if (!Validator.isValidDate(closeDate)) {
            System.out.println("Invalid date format: Must be m/dd/yyyy.");
            return;
        }

        System.out.println("Enter number of officer slots:");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input: Officer slots must be a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }
        int officerSlot = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (officerSlot <= 0) {
            System.out.println("Officer slots must be a positive integer.");
            return;
        }

        System.out.println("Enter officer list (comma-separated names or leave blank):");
        String officerList = scanner.nextLine();
        if (officerList == null) {
            System.out.println("Officer list cannot be null.");
            return;
        }
        officerList = officerList.trim();
        if (!officerList.isEmpty() && !officerList.matches("^[a-zA-Z\\s,]*$")) {
            System.out.println("Invalid officer list: Must be comma-separated names or empty.");
            return;
        }

        System.out.println("Enter project visibility (on/off):");
        String visibility = scanner.nextLine();
        if (visibility == null || visibility.trim().isEmpty()) {
            System.out.println("Visibility cannot be null or empty.");
            return;
        }
        visibility = visibility.trim().toLowerCase();
        if (!"on".equals(visibility) && !"off".equals(visibility)) {
            System.out.println("Visibility must be 'on' or 'off'.");
            return;
        }

        try {
            BTOProject project = manager.createNewProject(name.trim(), neighborhood.trim(), rooms, openDate.trim(),
                    closeDate.trim(), officerSlot, officerList, visibility);
            if (project != null) {
                System.out.println("Project created successfully:\n" + project.toCSV());
            } else {
                System.out.println("Failed to create project.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to create project: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to edit an existing BTO project by specifying its name.
     */
    private void editProject() {
        System.out.println("\n-- Edit Project --");

        System.out.print("Enter the project name to edit: ");
        

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
            manager.editProject(scanner, projectName.trim());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to edit project: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to delete a BTO project by specifying its name.
     */
    private void deleteProject() {
        System.out.print("Enter project name to delete: ");
        String name = scanner.nextLine();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Project name cannot be null or empty.");
            return;
        }
        if (!Validator.isValidProjectName(name)) {
            System.out.println("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
            return;
        }
        try {
            manager.deleteProject(name.trim());
            System.out.println("Project deleted successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to delete project: " + e.getMessage());
        }
    }

    /**
     * Displays all BTO projects in the system.
     */
    private void viewAllProjects() {
        try {
            List<BTOProject> all = manager.getAllProjects();
            if (all == null) {
                System.out.println("No projects available: Invalid system state.");
                return;
            }
            if (all.isEmpty()) {
                System.out.println("No projects found.");
                return;
            }
            for (BTOProject p : all) {
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
            System.out.println("Failed to view projects: " + e.getMessage());
        }
    }

    /**
     * Displays all BTO projects managed by the current manager.
     */
    private void viewOwnProjects() {
        try {
            List<BTOProject> own = manager.getMyProjects();
            if (own == null) {
                System.out.println("No projects available: Invalid manager state.");
                return;
            }
            if (own.isEmpty()) {
                System.out.println("No projects found for this manager.");
                return;
            }
            for (BTOProject p : own) {
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
            System.out.println("Failed to view projects: " + e.getMessage());
        }
    }

    /**
     * Displays all pending officer applications for the manager's projects.
     */
    private void viewOfficerApplications() {
        System.out.println("\n-- Officer Applications --");
        try {
            manager.viewOfficerApplications();
        } catch (IllegalStateException e) {
            System.out.println("Failed to view officer applications: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to approve or reject an officer's application by specifying their NRIC.
     */
    private void approveRejectOfficer() {
        System.out.print("Enter officer NRIC: ");
        String nric = scanner.nextLine();
        if (nric == null || nric.trim().isEmpty()) {
            System.out.println("NRIC cannot be null or empty.");
            return;
        }
        if (!Validator.isValidNRIC(nric)) {
            System.out.println("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
            return;
        }
        System.out.print("Approve? (yes/no): ");
        String decision = scanner.nextLine();
        if (decision == null || decision.trim().isEmpty()) {
            System.out.println("Decision cannot be null or empty.");
            return;
        }
        decision = decision.trim().toLowerCase();
        if (!"yes".equals(decision) && !"no".equals(decision)) {
            System.out.println("Invalid decision: Must be 'yes' or 'no'.");
            return;
        }
        try {
            boolean approved = "yes".equals(decision);
            manager.approveRejectOfficerRegistration(nric.trim(), approved);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to process officer application: " + e.getMessage());
        }
    }

    /**
     * Allows the manager to view and respond to enquiries for their assigned project.
     *
     * @param sc The Scanner for user input.
     * @throws IllegalArgumentException If the scanner is null.
     */
    private void manageEnquiries(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        System.out.println("\n-- All Enquiries --");
        try {
            manager.manageEnquiries(sc);
            System.out.println("Enquiries managed successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to manage enquiries: " + e.getMessage());
        }
    }

    /**
     * Displays all BTO applications for the manager's assigned project.
     */
    public void viewBTOApplications() {
        System.out.println("---");
        try {
            ArrayList<BTOApplication> applications = manager.viewBTOApplications();
            if (applications == null) {
                System.out.println("No applications available: Invalid project state.");
                return;
            }
            if (applications.isEmpty()) {
                System.out.println("No applications found for your project.");
                return;
            }
            System.out.println("BTO Applications:");
            for (BTOApplication application : applications) {
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
        } catch (IllegalStateException e) {
            System.out.println("Failed to view applications: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to approve or reject a BTO application by specifying the applicant's NRIC.
     */
    private void approveBTOApplications() {
        BTOProject assignedProject = manager.getProjectManaging();
        if (assignedProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        System.out.print("Enter the applicant's NRIC to view application: ");
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
        if ("Pending".equalsIgnoreCase(application.getStatus())) {
            System.out.print("Do you want to update status to 'Successful'? (yes/no): ");
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
            try {
                boolean approved = "yes".equals(response);
                manager.approveRejectBTOApplication(applicantNric.trim(), approved);
                System.out.println("Application " + (approved ? "approved" : "rejected") + " successfully.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Failed to process application: " + e.getMessage());
            }
        } else {
            System.out.println("Application cannot be processed: Status is " + application.getStatus());
        }
    }

    private void approveRejectBTOWithdrawal(Scanner sc) {
        ArrayList<WithdrawalRequest> requests = WithdrawalRequest.getWithdrawalRequests();
        boolean anyPending = false;

        for (WithdrawalRequest req : requests) {
            if (!req.getStatus().equalsIgnoreCase("Pending")) {
                continue; // Skip non-pending requests
            }

            anyPending = true;
            System.out.println("\n=============================");
            System.out.println("Withdrawal Request:");
            System.out.println("NRIC       : " + req.getApplicantNRIC());
            System.out.println("Project    : " + req.getProjectName());
            System.out.println("Flat Type  : " + req.getFlatType());
            System.out.println("Status     : " + req.getStatus());
            System.out.println("=============================");

            System.out.print("Approve or Reject this request? (A/R): ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("A")) {
                manager.approveRejectBTOWithdrawal(req.getApplicantNRIC(), true);
                System.out.println("Request approved.");
            } else if (input.equals("R")) {
                manager.approveRejectBTOWithdrawal(req.getApplicantNRIC(), false);
                System.out.println("Request rejected.");
            } else {
                System.out.println("Invalid input. Skipping request.");
            }
        }

        if (!anyPending) {
            System.out.println("No pending withdrawal requests.");
        }
    }

    /**
     * Prompts the user to generate a report for a specified BTO project.
     */
    private void generateReport() {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();
        if (projectName == null || projectName.trim().isEmpty()) {
            System.out.println("Project name cannot be null or empty.");
            return;
        }
        if (!Validator.isValidProjectName(projectName)) {
            System.out.println("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
            return;
        }
    
        // Ask for filter
        System.out.println("Choose a filter:");
        System.out.println("1. No Filter");
        System.out.println("2. Marital Status");
        System.out.println("3. Flat Type");
        System.out.println("4. Age");
        System.out.print("Enter choice (1-4): ");
        String choice = scanner.nextLine().trim();
    
        String filterType = null;
        String filterValue = null;
    
        switch (choice) {
            case "1":
                // No filter
                break;
            case "2":
                filterType = "maritalStatus";
                System.out.print("Enter marital status to filter by (e.g., Married, Single): ");
                filterValue = scanner.nextLine().trim();
                break;
            case "3":
                filterType = "flatType";
                System.out.print("Enter flat type to filter by (e.g., 2-Room, 3-Room): ");
                filterValue = scanner.nextLine().trim();
                break;
            case "4":
                filterType = "age";
                System.out.println("Choose age filter:");
                System.out.println("1. Age >= 35");
                System.out.println("2. Age < 35");
                System.out.print("Enter choice (1-2): ");
                String ageChoice = scanner.nextLine().trim();
                if ("1".equals(ageChoice)) {
                    filterValue = "35"; // Age greater than or equal to 35
                } else if ("2".equals(ageChoice)) {
                    filterValue = "lessThan35"; // Age less than 35
                } else {
                    System.out.println("Invalid choice.");
                    return;
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
    
        try {
            String report = manager.generateReport(projectName.trim(), filterType, filterValue);
            if (report == null || report.trim().isEmpty()) {
                System.out.println("No report generated for project: " + projectName.trim());
                return;
            }
            System.out.println("Report generated successfully:\n" + report);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to generate report: " + e.getMessage());
        }
    }
    
    
}
