package pages;

import java.util.Scanner;

public class MainMenu {
    private String userRole;
    private String username;

    public MainMenu(String username, String userRole) {
        this.username = username;
        this.userRole = userRole;
    }

    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== BTO Application System ===");
            System.out.println("Logged in as: " + username + " (" + userRole + ")");
            System.out.println("--------------------------------");

            switch (userRole) {
                case "Applicant":
                    System.out.println("1. View BTO Projects");
                    System.out.println("2. Apply for BTO");
                    System.out.println("3. View My Application");
                    System.out.println("4. Withdraw Application");
                    System.out.println("5. Submit Enquiry");
                    System.out.println("6. View Receipts");
                    System.out.println("0. Logout");
                    break;

                case "ProjectManager":
                    System.out.println("1. Create BTO Project");
                    System.out.println("2. Edit Project");
                    System.out.println("3. Toggle Project Visibility");
                    System.out.println("4. View All Applications");
                    System.out.println("0. Logout");
                    break;

                case "HDBOfficer":
                    System.out.println("1. View Applications");
                    System.out.println("2. Approve/Reject Applications");
                    System.out.println("3. View Enquiries");
                    System.out.println("0. Logout");
                    break;

                default:
                    System.out.println("Invalid role.");
                    running = false;
                    break;
            }

            System.out.print("Select an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    handleOption1();
                    break;
                case "2":
                    handleOption2();
                    break;
                case "0":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    private void handleOption1() {
        switch (userRole) {
            case "Applicant":
                System.out.println("→ Viewing available BTO projects...");
                // ProjectController.viewProjects();
                break;
            case "ProjectManager":
                System.out.println("→ Creating new BTO project...");
                // ProjectController.createProject();
                break;
            case "HDBOfficer":
                System.out.println("→ Viewing all applications...");
                // ApplicationController.viewAllApplications();
                break;
        }
    }

    private void handleOption2() {
        switch (userRole) {
            case "Applicant":
                System.out.println("→ Applying for BTO...");
                // ApplicationController.applyForBTO(username);
                break;
            case "ProjectManager":
                System.out.println("→ Editing existing project...");
                // ProjectController.editProject();
                break;
            case "HDBOfficer":
                System.out.println("→ Approving/Rejecting applications...");
                // ApplicationController.reviewApplications();
                break;
        }
    }

    // Add more handlers for options 3, 4, etc. based on your roles

}
