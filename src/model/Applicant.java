package model;

import java.util.List;
import utils.CSVUtils;

public class Applicant {
    final private String name;
    private String nric;
    private int age;
    private String maritalStatus;

    // Constructor for Applicant
    public Applicant(String name, String nric, int age, String maritalStatus) {
        this.nric = nric;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    // Getter and setter methods for the fields
    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public static Applicant getApplicantByNRIC(String nric) {
        List<String[]> users = CSVUtils.readCSV("data/users.csv");
        for (String[] row : users) {
            if (row[0].equals(nric)) {
                int age = Integer.parseInt(row[1]);
                String password = row[2];
                String maritalStatus = row[3];
                return new Applicant(nric, password, age, maritalStatus);
            }
        }
        return null; // not found
    }

    // Override toString to display the applicant's details
    @Override
    public String toString() {
        return nric + "," + name + "," + age + "," + maritalStatus;
    }
}
