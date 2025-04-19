package pages;

import model.User;
import controller.*;

import java.util.Scanner;

public class Login {

    public static void display() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Welcome to the HDB BTO Management System ===");

        while (true) {
            System.out.print("Enter NRIC (or type 'exit' to quit): ");
            String nric = scanner.nextLine().trim();

            if (nric.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            User user = User.login(nric, password);
            launchRoleInterface(user);

        }

        scanner.close();
    }

    private static void launchRoleInterface(User user) {
        switch (user.getRole().toLowerCase()) {
            case "Applicant":
                ApplicantController applicant = new ApplicantController(user);
                ApplicantPage applicantPage = new ApplicantPage(applicant);
                applicantPage.displayMenu();
                break;
            case "HDBOfficer":
                HDBOfficerController officer = new HDBOfficerController(user);
                HDBOfficerPage officerPage = new HDBOfficerPage(officer);
                officerPage.displayMenu();
                break;
            case "HDBManager":
                ProjectManagerController manager = new ProjectManagerController(user);
                HDBManagerPage managerPage = new HDBManagerPage(manager);
                managerPage.displayMenu();
                break;
            default:
                System.out.println("Unknown role: " + user.getRole());
        }
    }
}
