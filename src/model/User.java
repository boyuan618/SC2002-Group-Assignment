package model;

import utils.CSVUtils;
import utils.Validator;
import java.util.List;

/**
 * Represents a user in the HDB BTO Management System, storing personal details and role.
 * Provides methods for user authentication and password management. Serves as a base class
 * for specific user types (e.g., Applicant, Officer, Manager).
 *
 * @author SC2002Team
 */
public class User {
    protected String name;
    protected String NRIC;
    protected String password;
    protected int age;
    protected String maritalStatus;
    protected String role;

    /** Path to the CSV file storing user data. */
    public static final String USERS_CSV = "data/Users.csv";

    /**
     * Constructs a User with the specified details.
     *
     * @param name The user's name.
     * @param NRIC The user's NRIC.
     * @param password The user's password.
     * @param age The user's age.
     * @param maritalStatus The user's marital status ("Single" or "Married").
     * @param role The user's role (e.g., "Applicant", "Officer", "Manager").
     * @throws IllegalArgumentException If any input is invalid.
     */
    public User(String name, String NRIC, String password, int age, String maritalStatus, String role) {
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name: Must contain only letters and spaces and cannot be empty.");
        }
        if (!Validator.isValidNRIC(NRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid password: Cannot be null or empty.");
        }
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Must be between 18 and 120.");
        }
        if (maritalStatus == null || (!maritalStatus.trim().equalsIgnoreCase("Single") && !maritalStatus.trim().equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }
        if (role == null || role.trim().isEmpty() || !isValidRole(role.trim())) {
            throw new IllegalArgumentException("Invalid role: Must be a valid role (e.g., 'Applicant', 'Officer', 'Manager').");
        }

        this.name = name.trim();
        this.NRIC = NRIC.trim();
        this.password = password.trim();
        this.age = age;
        this.maritalStatus = maritalStatus.trim();
        this.role = role.trim();
    }

    /**
     * Validates the user role.
     *
     * @param role The role to validate.
     * @return True if the role is valid, false otherwise.
     */
    private boolean isValidRole(String role) {
        return role.equalsIgnoreCase("Applicant") ||
               role.equalsIgnoreCase("HDBOfficer") ||
               role.equalsIgnoreCase("HDBManager");
    }

    /**
     * Gets the user's name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's NRIC.
     *
     * @return The NRIC.
     */
    public String getNRIC() {
        return NRIC;
    }

    /**
     * Gets the user's password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The new password.
     * @throws IllegalArgumentException If the password is invalid.
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid password: Cannot be null or empty.");
        }
        this.password = password.trim();
    }

    /**
     * Gets the user's age.
     *
     * @return The age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the user's marital status.
     *
     * @return The marital status.
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the user's role.
     *
     * @return The role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Authenticates a user by verifying NRIC and password against the user database.
     *
     * @param NRIC The user's NRIC.
     * @param password The user's password.
     * @return A User object if authentication is successful, null otherwise.
     * @throws IllegalArgumentException If NRIC or password is invalid.
     */
    public static User login(String NRIC, String password) {
        if (!Validator.isValidNRIC(NRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid password: Cannot be null or empty.");
        }

        List<String[]> usersData = CSVUtils.readCSV(USERS_CSV);
        if (usersData == null) {
            System.out.println("Error: Unable to read user database.");
            return null;
        }

        for (String[] userData : usersData) {
            if (userData.length >= 6) {
                try {
                    String userNRIC = userData[1] != null ? userData[1].trim() : "";
                    String userPassword = userData[4] != null ? userData[4].trim() : "";
                    if (userNRIC.equals(NRIC.trim()) && userPassword.equals(password.trim())) {
                        String name = userData[0] != null ? userData[0].trim() : "";
                        int age = Integer.parseInt(userData[2]);
                        String maritalStatus = userData[3] != null ? userData[3].trim() : "";
                        String role = userData[5] != null ? userData[5].trim() : "";
                        return new User(name, userNRIC, userPassword, age, maritalStatus, role);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid age in user data: " + String.join(",", userData));
                } catch (IllegalArgumentException e) {
                    System.out.println("Warning: Invalid user data: " + String.join(",", userData) + " - " + e.getMessage());
                }
            } else {
                System.out.println("Warning: Malformed user row in CSV: " + String.join(",", userData));
            }
        }

        System.out.println("Error: Invalid login credentials.");
        return null;
    }

    /**
     * Changes the user's password if the old password is correct.
     *
     * @param oldPassword The current password.
     * @param newPassword The new password.
     * @throws IllegalArgumentException If oldPassword or newPassword is invalid.
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid old password: Cannot be null or empty.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid new password: Cannot be null or empty.");
        }
        if (!this.password.equals(oldPassword.trim())) {
            System.out.println("Error: Old password is incorrect.");
            return;
        }

        try {
            this.password = newPassword.trim();
            updatePasswordInCSV();
            System.out.println("Password updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Error: Failed to update password: " + e.getMessage());
        }
    }

    /**
     * Updates the user's password in the CSV file.
     *
     * @throws RuntimeException If an error occurs while writing to the CSV file.
     */
    private void updatePasswordInCSV() {
        List<String[]> usersData = CSVUtils.readCSV(USERS_CSV);
        if (usersData == null) {
            throw new RuntimeException("Unable to read user database for password update.");
        }

        boolean updated = false;
        for (int i = 0; i < usersData.size(); i++) {
            String[] userData = usersData.get(i);
            if (userData.length >= 6 && userData[1] != null && userData[1].trim().equals(this.NRIC)) {
                userData[4] = this.password; // Update password (index 4 based on login method)
                usersData.set(i, userData);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new RuntimeException("User not found in database for NRIC: " + this.NRIC);
        }

        try {
            CSVUtils.writeCSV(USERS_CSV, usersData);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to write to user database: " + e.getMessage());
        }
    }
}
