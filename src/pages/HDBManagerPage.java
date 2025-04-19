package pages;

import controller.ProjectManagerController;
import model.BTOProject;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

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
            System.out.println("8. View Enquiries");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    createProject();
                    break;
                case 2:
                    editProject();
                    break;
                case 3:
                    deleteProject();
                    break;
                case 4:
                    viewAllProjects();
                    break;
                case 5:
                    viewOwnProjects();
                    break;
                case 6:
                    viewOfficerApplications();
                    break;
                case 7:
                    approveRejectOfficer();
                    break;
                case 8:
                    ManageEnquiries();
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    private void createProject() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter project name:");
        String name = scanner.nextLine();

        System.out.println("Enter neighborhood:");
        String neighborhood = scanner.nextLine();

        System.out.println("Enter type 1 (e.g., 2-Room):");
        String type1 = scanner.nextLine();
        System.out.println("Enter units for type 1:");
        int units1 = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter price for type 1:");
        int price1 = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter type 2 (e.g., 3-Room):");
        String type2 = scanner.nextLine();
        System.out.println("Enter units for type 2:");
        int units2 = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter price for type 2:");
        int price2 = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter application opening date (yyyy-mm-dd):");
        LocalDate openDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Enter application closing date (yyyy-mm-dd):");
        LocalDate closeDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Enter number of officer slots:");
        int officerSlot = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter officer list (comma-separated NRICs or leave blank):");
        String officerList = scanner.nextLine();

        System.out.println("Enter project visibility (on/off):");
        String visibility = scanner.nextLine();

        BTOProject project = manager.createNewProject(name, neighborhood, type1, units1, price1,
                type2, units2, price2, openDate, closeDate,
                officerSlot, officerList, visibility);

        if (project != null) {
            System.out.println("Project created successfully:\n" + project);
        } else {
            System.out.println("Failed to create project.");
        }
    }

    private void editProject() {
        System.out.println("\n-- Edit Project --");
        // manager.editProject(...) // implement input logic
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

    private void ManageEnquiries() {
        System.out.println("\n-- All Enquiries --");
        manager.manageEnquiries();
    }
}
