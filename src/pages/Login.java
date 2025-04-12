package pages;

import utils.CSVUtils;

import java.util.List;
import java.util.Scanner;

public class Login {
    private static final String USER_CSV = "data/Users.csv";

    /**
     * Authenticates a user and returns user info: [Name, NRIC, Role, Age,
     * MaritalStatus]
     */
    public static String[] loginUser() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Welcome to Singpass Login ===");

        while (true) {
            System.out.print("Enter NRIC (e.g. S1234567A): ");
            String nric = sc.nextLine().toUpperCase().trim();

            if (!nric.matches("^[ST]\\d{7}[A-Z]$")) {
                System.out.println("❌ Invalid NRIC format. Try again.\n");
                continue;
            }

            System.out.print("Enter password: ");
            String password = sc.nextLine().trim();

            List<String[]> users = CSVUtils.readCSV(USER_CSV);
            for (String[] user : users) {
                if (user.length < 5)
                    continue;

                String name = user[0];
                String fileNRIC = user[1];
                String age = user[2];
                String maritalStatus = user[3];
                String filePassword = user[4];
                String role = user[5];

                if (nric.equals(fileNRIC) && password.equals(filePassword)) {
                    System.out.println("\n✅ Login successful as " + role + "!");

                    // Ask if they want to change their password
                    promptPasswordChange(sc, nric, user);

                    return new String[] { name, nric, role, age, maritalStatus };
                }
            }

            System.out.println("❌ Invalid NRIC or password. Try again.\n");
        }
    }

    /**
     * Option to change password after login.
     */
    private static void promptPasswordChange(Scanner sc, String nric, String[] userRow) {
        System.out.print("\nWould you like to change your password? (y/n): ");
        String response = sc.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
            System.out.print("Enter new password: ");
            String newPassword = sc.nextLine().trim();

            userRow[1] = newPassword;
            CSVUtils.updateCSV(USER_CSV, nric, 0, userRow);

            System.out.println("🔒 Password updated successfully!\n");
        }
    }
}
