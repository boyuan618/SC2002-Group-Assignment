package model;

import utils.CSVUtils;
import java.util.List;

public class User {
    protected String name;
    protected String NRIC;
    protected String password;
    protected int age;
    protected String maritalStatus;
    protected String role;

    public static final String USERS_CSV = "data/Users.csv";

    // Constructor
    public User(String name, String NRIC, String password, int age, String maritalStatus, String role) {
        this.name = name;
        this.NRIC = NRIC;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.role = role;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getNRIC() {
        return NRIC;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getRole() {
        return role;
    }

    // Static method to login a user by verifying NRIC and password
    public static User login(String NRIC, String password) {
        List<String[]> usersData = CSVUtils.readCSV(USERS_CSV);

        for (String[] userData : usersData) {
            if (userData.length >= 6) {
                String userNRIC = userData[1];
                String userPassword = userData[5];

                if (userNRIC.equals(NRIC) && userPassword.equals(password)) {
                    // Return the user object (you can create the appropriate User subclass here)
                    return new User(userData[0], userNRIC, userPassword, Integer.parseInt(userData[3]), userData[4],
                            userData[5]);
                }
            }
        }

        System.out.println("Invalid login credentials.");
        return null;
    }

    // Method to change the password of the user
    public void changePassword(String oldPassword, String newPassword) {
        if (this.password.equals(oldPassword)) {
            this.password = newPassword;
            updatePasswordInCSV();
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Old password is incorrect.");
        }
    }

    // Helper method to update the user's password in the CSV file
    private void updatePasswordInCSV() {
        List<String[]> usersData = CSVUtils.readCSV(USERS_CSV);
        for (int i = 0; i < usersData.size(); i++) {
            String[] userData = usersData.get(i);
            if (userData[1].equals(this.NRIC)) {
                userData[5] = this.password; // Update the password
                usersData.set(i, userData);
                break;
            }
        }

        // Write updated data back to CSV
        CSVUtils.writeCSV(USERS_CSV, usersData);
    }
}
