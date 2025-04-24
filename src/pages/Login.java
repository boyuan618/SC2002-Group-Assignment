package pages;

import model.User;
import controller.*;
import utils.Validator;

import java.util.Scanner;

/**
 * Entry point for the HDB BTO Management System, handling user authentication and launching
 * role-specific interfaces for Applicants, HDB Officers, and HDB Managers.
 *
 * @author SC2002Team
 */
public class Login {

    /**
     * Displays the login interface, prompting for NRIC and password, authenticating users,
     * and launching the appropriate role-based interface. Continues until the user types 'exit'.
     */
    public static void display() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Welcome to the HDB BTO Management System ===");

            while (true) {
                System.out.print("Enter NRIC (or type 'exit' to quit): ");
                String nric = scanner.nextLine();
                if (nric == null) {
                    throw new IllegalArgumentException("NRIC input cannot be null.");
                }
                nric = nric.trim();
                if (nric.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                if (!Validator.isValidNRIC(nric)) {
                    System.out.println("❌ Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
                    continue;
                }

                System.out.print("Enter Password: ");
                String password = scanner.nextLine();
                if (password == null) {
                    throw new IllegalArgumentException("Password input cannot be null.");
                }
                password = password.trim();
                if (password.isEmpty()) {
                    System.out.println("❌ Password cannot be empty.");
                    continue;
                }

                try {
                    User user = User.login(nric, password);
                    if (user == null) {
                        throw new IllegalStateException("Login failed: Invalid NRIC or password.");
                    }
                    System.out.println("✅ Login successful for " + user.getName() + " (" + user.getRole() + ")");
                    launchRoleInterface(user);
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Login failed: " + e.getMessage());
                }
                
            }
        }
    }

    /**
     * Launches the role-specific interface based on the user's role, instantiating the appropriate
     * controller and page.
     *
     * @param user The authenticated user.
     * @throws IllegalArgumentException If the user is null or has an invalid role.
     */
    private static void launchRoleInterface(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        String role = user.getRole().trim(); // Trim to handle whitespace
        switch (role) {
            case "Applicant":
                ApplicantController applicant = new ApplicantController(user);
                ApplicantPage applicantPage = new ApplicantPage(applicant);
                applicantPage.displayMenu();
                break;
            case "Officer":
                HDBOfficerController officer = new HDBOfficerController(user);
                HDBOfficerPage officerPage = new HDBOfficerPage(officer);
                officerPage.displayMenu();
                break;
            case "Manager":
                ProjectManagerController manager = new ProjectManagerController(user);
                HDBManagerPage managerPage = new HDBManagerPage(manager);
                managerPage.displayMenu();
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + user.getRole());
        }
    }
}
