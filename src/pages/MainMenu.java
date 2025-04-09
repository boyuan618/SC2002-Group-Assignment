package pages;

import java.util.Scanner;

public class MainMenu {
    private final String nric, role, maritalStatus;
    private final int age;
    private final Scanner sc = new Scanner(System.in);

    public MainMenu(String nric, String role, int age, String maritalStatus) {
        this.nric = nric;
        this.role = role;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n===== BTO SYSTEM MENU (" + role.toUpperCase() + ") =====");

            switch (role.toLowerCase()) {
                case "applicant":
                    showApplicantMenu();
                    break;
                case "hdbofficer":
                    showHDBOfficerMenu();
                    break;
                case "hdbmanager":
                    showHDBManagerMenu();
                    break;
                default:
                    System.out.println("❌ Unknown role.");
                    return;
            }
        }
    }

    private void showApplicantMenu() {
        System.out.println("1. View Available Projects");
        System.out.println("2. Apply for Project");
        System.out.println("3. View Application Status");
        System.out.println("4. Book Flat (if eligible)");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. Submit Enquiry");
        System.out.println("7. View/Edit/Delete Enquiry");
        System.out.println("0. Logout");

        int choice = Integer.parseInt(sc.nextLine());
        switch (choice) {
            case 1 -> ApplicantController.viewAvailableProjects(maritalStatus);
            case 2 -> ApplicantController.applyForProject(nric, age, maritalStatus);
            case 3 -> ApplicantController.viewApplication(nric);
            case 4 -> ApplicantController.bookFlat(nric);
            case 5 -> ApplicantController.requestWithdrawal(nric);
            case 6 -> ApplicantController.submitEnquiry(nric);
            case 7 -> ApplicantController.manageEnquiries(nric);
            case 0 -> System.exit(0);
            default -> System.out.println("❌ Invalid choice.");
        }
    }

    private void showHDBOfficerMenu() {
        System.out.println("1. View All Visible Projects");
        System.out.println("2. Register to Handle Project");
        System.out.println("3. View My Project Details");
        System.out.println("4. View & Reply to Enquiries");
        System.out.println("5. Manage Flat Booking");
        System.out.println("6. Generate Receipt");
        System.out.println("0. Logout");

        int choice = Integer.parseInt(sc.nextLine());
        switch (choice) {
            case 1 -> HDBOfficerController.viewAllProjects();
            case 2 -> HDBOfficerController.registerForProject(nric);
            case 3 -> HDBOfficerController.viewMyProject(nric);
            case 4 -> HDBOfficerController.handleEnquiries(nric);
            case 5 -> HDBOfficerController.manageBooking(nric);
            case 6 -> HDBOfficerController.generateReceipt(nric);
            case 0 -> System.exit(0);
            default -> System.out.println("❌ Invalid choice.");
        }
    }

    private void showHDBManagerMenu() {
        System.out.println("1. Create/Edit/Delete Project");
        System.out.println("2. Toggle Project Visibility");
        System.out.println("3. View All Projects / My Projects");
        System.out.println("4. Handle Officer Registrations");
        System.out.println("5. Approve/Reject Applications");
        System.out.println("6. Approve/Reject Withdrawals");
        System.out.println("7. Generate Reports");
        System.out.println("8. View & Reply Enquiries");
        System.out.println("0. Logout");

        int choice = Integer.parseInt(sc.nextLine());
        switch (choice) {
            case 1 -> HDBManagerController.manageProjects(nric);
            case 2 -> HDBManagerController.toggleVisibility(nric);
            case 3 -> HDBManagerController.viewProjects(nric);
            case 4 -> HDBManagerController.handleOfficerRegistration(nric);
            case 5 -> HDBManagerController.processApplications(nric);
            case 6 -> HDBManagerController.processWithdrawals(nric);
            case 7 -> HDBManagerController.generateReport(nric);
            case 8 -> HDBManagerController.handleEnquiries(nric);
            case 0 -> System.exit(0);
            default -> System.out.println("❌ Invalid choice.");
        }
    }
}
