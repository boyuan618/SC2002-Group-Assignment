package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Receipt {
    final private String nric;
    final private String applicantName;
    final private int age;
    final private String maritalStatus;
    final private String flatType;
    final private String projectName;
    final private String neighborhood;
    final private LocalDate bookingDate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    // Constructor
    public Receipt(String nric, String applicantName, int age, String maritalStatus, String flatType,
            String projectName, String neighborhood) {
        this.nric = nric;
        this.applicantName = applicantName;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.flatType = flatType;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.bookingDate = LocalDate.now();
    }

    // Getters for receipt details
    public String getNric() {
        return nric;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public int getAge() {
        return age;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getFlatType() {
        return flatType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    // Method to display receipt information
    public void printReceipt() {
        System.out.println("🧾 Receipt for BTO Application");
        System.out.println("=====================================");
        System.out.println("Applicant Name   : " + applicantName);
        System.out.println("NRIC             : " + nric);
        System.out.println("Age              : " + age);
        System.out.println("Marital Status   : " + maritalStatus);
        System.out.println("Flat Type        : " + flatType);
        System.out.println("Project Name     : " + projectName);
        System.out.println("Neighborhood     : " + neighborhood);
        System.out.println("Booking Date     : " + bookingDate.format(FORMATTER));
        System.out.println("=====================================");
    }

    // Method to convert the receipt data to CSV format (if required for
    // persistence)
    public String[] toCSV() {
        return new String[] {
                nric,
                applicantName,
                String.valueOf(age),
                maritalStatus,
                flatType,
                projectName,
                neighborhood,
                bookingDate.format(FORMATTER)
        };
    }

    // Static method to generate receipt from BTOApplication object
    public static Receipt fromBTOApplication(BTOApplication application) {
        return new Receipt(
                application.getApplicant().getNric(),
                application.getApplicant().getName(),
                application.getApplicant().getAge(),
                application.getApplicant().getMaritalStatus(),
                application.getFlatType(),
                application.getProject().getProjectName(),
                application.getProject().getNeighborhood());
    }
}
