package pages;

import controller.ProjectManagerController;
import model.BTOApplication;
import model.BTOProject;
import model.Room;

import java.util.*;

public class HDBManagerPage {
    final private ProjectManagerController manager;
    final private Scanner scanner;

    public HDBManagerPage(ProjectManagerController manager) {
        this.manager = manager;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        int choice = -1;
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
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> createProject();
                case 2 -> editProject();
                case 3 -> deleteProject();
                case 4 -> viewAllProjects();
                case 5 -> viewOwnProjects();
                case 6 -> viewOfficerApplications();
                case 7 -> approveRejectOfficer();
                case 8 -> ManageEnquiries(scanner);
                case 9 -> viewBTOApplications();
                case 10 -> approveBTOApplications();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    private void createProject() {

        System.out.println("Enter project name:");
        String name = scanner.nextLine();

        System.out.println("Enter neighborhood:");
        String neighborhood = scanner.nextLine();

        ArrayList<Room> rooms = new ArrayList<>();
        System.out.println("Enter number of room types:");
        int types = Integer.parseInt(scanner.nextLine());

        for (int i = 1; i <= types; i++) {
            System.out.println("Enter type " + i + " (2-Room/3-Room):");
            String type = scanner.nextLine();
            System.out.println("Enter units for type " + i + ":");
            int units = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter price for type " + i + ":");
            int price = Integer.parseInt(scanner.nextLine());

            rooms.add(new Room(type, units, price));
        }

        System.out.println("Enter application opening date (m/dd/yyyy):");
        String openDate = scanner.nextLine();

        System.out.println("Enter application closing date (m/dd/yyyy):");
        String closeDate = scanner.nextLine();

        System.out.println("Enter number of officer slots:");
        int officerSlot = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter officer list (comma-separated Names or leave blank):");
        String officerList = scanner.nextLine();

        System.out.println("Enter project visibility (on/off):");
        String visibility = scanner.nextLine();

        BTOProject project = manager.createNewProject(name, neighborhood, rooms, openDate, closeDate,
                officerSlot, officerList, visibility);

        if (project != null) {
            System.out.println("Project created successfully:\n" + project.toCSV());
        } else {
            System.out.println("Failed to create project.");
        }
    }

    private void editProject() {
        System.out.println("\n-- Edit Project --");
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the project name to edit: ");
        String projectName = sc.nextLine();

        manager.editProject(sc, projectName);
    }

    private void deleteProject() {
        System.out.print("Enter Project Name to delete: ");
        String name = scanner.nextLine();
        manager.deleteProject(name);
        System.out.println("Project deleted.");
    }

    private void viewAllProjects() {
        List<BTOProject> all = manager.getAllProjects();
        for (BTOProject p : all) {
            System.out.println(p.toCSV());
        }
    }

    private void viewOwnProjects() {
        List<BTOProject> own = manager.getMyProjects();
        for (BTOProject p : own) {
            System.out.println(p.toCSV());
        }
    }

    private void viewOfficerApplications() {
        System.out.println("\n-- Officer Applications --");
        manager.viewOfficerApplications();

    }

    private void approveRejectOfficer() {
        System.out.print("Enter Officer NRIC: ");
        String nric = scanner.nextLine();
        System.out.print("Approve? (yes/no): ");
        String decision = scanner.nextLine();
        boolean approved = decision.equalsIgnoreCase("yes");
        manager.approveRejectOfficerRegistration(nric, approved);
    }

    private void ManageEnquiries(Scanner sc) {
        System.out.println("\n-- All Enquiries --");
        manager.manageEnquiries(sc);
    }

    private void approveBTOApplications() {
        if (manager.getProjectManaging() != null) {
            System.out.print("Enter the applicant's NRIC to view booking status: ");
            String applicantNric = scanner.nextLine();

            BTOApplication application = BTOApplication.getApplicationByNRIC(applicantNric);
            // Check if status is successful
            if (application.getStatus().equals("Successful")) {
                System.out.println("Application cannot be handled by you, invalid status");
                return;
            }
            if (application != null
                    && application.getProjectName().equals(manager.getProjectManaging().getProjectName())) {
                System.out.println(
                        "Applicant: " + model.Applicant.getApplicantByNRIC(application.getApplicantNRIC()).getName());
                System.out.println("Status: " + application.getStatus());
                System.out.println("Flat Type: " + application.getFlatType());
                if (application.getStatus().equalsIgnoreCase("Pending")) {
                    System.out.print("Do you want to update status to 'Successful'? (yes/no): ");
                    String response = scanner.nextLine();
                    if ("yes".equalsIgnoreCase(response)) {
                        manager.approveRejectBTOApplication(applicantNric, true);
                    } else if ("no".equalsIgnoreCase(response)) {
                        manager.approveRejectBTOApplication(applicantNric, false);
                    }
                }
            } else {
                System.out.println("No application found for this NRIC in your assigned project.");
            }
        } else {
            System.out.println("You are not assigned to any project.");
        }
    }

    public void viewBTOApplications() {
        ArrayList<BTOApplication> applications = manager.viewBTOApplications();

        for (BTOApplication application : applications) {
            System.out.println(
                    "Applicant: " + model.Applicant.getApplicantByNRIC(application.getApplicantNRIC()).getName());
            System.out.println("Status: " + application.getStatus());
            System.out.println("Flat Type: " + application.getFlatType());
            System.out.println();
        }
    }
}
