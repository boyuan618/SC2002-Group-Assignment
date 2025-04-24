package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import utils.Validator;

/**
 * Represents a receipt for a Build-To-Order (BTO) application, storing applicant and project details
 * along with the booking date. Provides methods to print the receipt and convert it to CSV format.
 *
 * @author SC2002Team
 */
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

    /**
     * Constructs a Receipt with the specified details, setting the booking date to the current date.
     *
     * @param nric The applicant's NRIC.
     * @param applicantName The applicant's name.
     * @param age The applicant's age.
     * @param maritalStatus The applicant's marital status ("Single" or "Married").
     * @param flatType The type of flat (e.g., "2-room", "3-room").
     * @param projectName The name of the BTO project.
     * @param neighborhood The neighborhood of the project.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public Receipt(String nric, String applicantName, int age, String maritalStatus, String flatType,
            String projectName, String neighborhood) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (!Validator.isValidName(applicantName)) {
            throw new IllegalArgumentException("Invalid name: Must contain only letters and spaces and cannot be empty.");
        }
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Must be between 18 and 120.");
        }
        if (maritalStatus == null || (!maritalStatus.trim().equalsIgnoreCase("Single") && !maritalStatus.trim().equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be a valid flat type (e.g., '2-room', '3-room').");
        }
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (BTOProject.getProjectByName(projectName) == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName);
        }
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid neighborhood: Cannot be null or empty.");
        }

        this.nric = nric.trim();
        this.applicantName = applicantName.trim();
        this.age = age;
        this.maritalStatus = maritalStatus.trim();
        this.flatType = flatType.trim();
        this.projectName = projectName.trim();
        this.neighborhood = neighborhood.trim();
        this.bookingDate = LocalDate.now();
    }

    /**
     * Gets the applicant's NRIC.
     *
     * @return The NRIC.
     */
    public String getNric() {
        return nric;
    }

    /**
     * Gets the applicant's name.
     *
     * @return The name.
     */
    public String getApplicantName() {
        return applicantName;
    }

    /**
     * Gets the applicant's age.
     *
     * @return The age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the applicant's marital status.
     *
     * @return The marital status.
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the flat type.
     *
     * @return The flat type.
     */
    public String getFlatType() {
        return flatType;
    }

    /**
     * Gets the project name.
     *
     * @return The project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the neighborhood.
     *
     * @return The neighborhood.
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Gets the booking date.
     *
     * @return The booking date.
     */
    public LocalDate getBookingDate() {
        return bookingDate;
    }

    /**
     * Prints the receipt details in a formatted manner to the console.
     */
    public void printReceipt() {
        System.out.println("🧾 Receipt for BTO Application");
        System.out.println("=====================================");
        System.out.println("Applicant Name   : " + (applicantName != null ? applicantName : "<Unknown>"));
        System.out.println("NRIC             : " + (nric != null ? nric : "<Unknown>"));
        System.out.println("Age              : " + age);
        System.out.println("Marital Status   : " + (maritalStatus != null ? maritalStatus : "<Unknown>"));
        System.out.println("Flat Type        : " + (flatType != null ? flatType : "<Unknown>"));
        System.out.println("Project Name     : " + (projectName != null ? projectName : "<Unknown>"));
        System.out.println("Neighborhood     : " + (neighborhood != null ? neighborhood : "<Unknown>"));
        System.out.println("Booking Date     : " + (bookingDate != null ? bookingDate.format(FORMATTER) : "<Unknown>"));
        System.out.println("=====================================");
    }

    /**
     * Converts the receipt data to a CSV-compatible array.
     *
     * @return An array of strings representing the receipt fields.
     * @throws IllegalStateException If any field is null.
     */
    public String[] toCSV() {
        if (nric == null || applicantName == null || maritalStatus == null ||
            flatType == null || projectName == null || neighborhood == null || bookingDate == null) {
            throw new IllegalStateException("Receipt fields cannot be null for CSV output.");
        }
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

    /**
     * Creates a Receipt from a BTOApplication object.
     *
     * @param application The BTOApplication to generate the receipt from.
     * @return A new Receipt object.
     * @throws IllegalArgumentException If the application or its fields are invalid.
     */
    public static Receipt fromBTOApplication(BTOApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null.");
        }
        Applicant applicant = application.getApplicant();
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant cannot be null.");
        }
        BTOProject project = application.getProjectObject();
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        return new Receipt(
                applicant.getNric(),
                applicant.getName(),
                applicant.getAge(),
                applicant.getMaritalStatus(),
                application.getFlatType(),
                project.getProjectName(),
                project.getNeighborhood());
    }
}
