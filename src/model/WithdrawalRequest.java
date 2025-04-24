package model;

import utils.*;
import java.util.*;

/**
 * Represents a withdrawal request for a Build-To-Order (BTO) application, storing the applicant's
 * NRIC, project name, flat type, and status. Provides methods to access and modify the request
 * details and convert to CSV format.
 *
 * @author SC2002Team
 */
public class WithdrawalRequest {
    private String applicantNRIC;
    private String projectName;
    private String flatType;
    private String status; // "Pending", "Approved", "Rejected"

    /**
     * Constructs a WithdrawalRequest with the specified details.
     *
     * @param applicantNRIC The NRIC of the applicant.
     * @param projectName The name of the BTO project.
     * @param flatType The type of flat (e.g., "2-room", "3-room").
     * @param status The status of the request ("Pending", "Approved", or "Rejected").
     * @throws IllegalArgumentException If any input is invalid.
     */
    public WithdrawalRequest(String applicantNRIC, String projectName, String flatType, String status) {
        if (!Validator.isValidNRIC(applicantNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (BTOProject.getProjectByName(projectName) == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName);
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be a valid flat type (e.g., '2-room', '3-room').");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: Must be 'Pending', 'Approved', or 'Rejected'.");
        }

        this.applicantNRIC = applicantNRIC.trim();
        this.projectName = projectName.trim();
        this.flatType = flatType.trim();
        this.status = status.trim();
    }

    /**
     * Validates the withdrawal request status.
     *
     * @param status The status to validate.
     * @return True if the status is valid, false otherwise.
     */
    private boolean isValidStatus(String status) {
        return status != null && (
               status.trim().equalsIgnoreCase("Pending") ||
               status.trim().equalsIgnoreCase("Approved") ||
               status.trim().equalsIgnoreCase("Rejected"));
    }

    /**
     * Gets the applicant's NRIC.
     *
     * @return The NRIC.
     */
    public String getApplicantNRIC() {
        return applicantNRIC;
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
     * Gets the flat type.
     *
     * @return The flat type.
     */
    public String getFlatType() {
        return flatType;
    }

    /**
     * Gets the status of the withdrawal request.
     *
     * @return The status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the withdrawal request.
     *
     * @param status The new status ("Pending", "Approved", or "Rejected").
     * @throws IllegalArgumentException If the status is invalid.
     */
    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: Must be 'Pending', 'Approved', or 'Rejected'.");
        }
        this.status = status.trim();
    }

    /**
     * Converts the withdrawal request to a CSV-compatible string.
     *
     * @return A comma-separated string of NRIC, project name, flat type, and status.
     * @throws IllegalStateException If any field is null.
     */
    @Override
    public String toString() {
        if (applicantNRIC == null || projectName == null || flatType == null || status == null) {
            throw new IllegalStateException("Withdrawal request fields cannot be null for CSV output.");
        }
        return String.join(",", applicantNRIC, projectName, flatType, status);
    }

    /**
     * Creates a WithdrawalRequest from a CSV row.
     *
     * @param row An array of strings representing the NRIC, project name, flat type, and status.
     * @return A new WithdrawalRequest object.
     * @throws IllegalArgumentException If the row is invalid or contains invalid data.
     */
    public static WithdrawalRequest fromCSV(String[] row) {
        if (row == null || row.length < 4) {
            throw new IllegalArgumentException("Invalid CSV row: Must contain at least 4 fields (NRIC, project name, flat type, status).");
        }
        for (int i = 0; i < 4; i++) {
            if (row[i] == null) {
                throw new IllegalArgumentException("Invalid CSV row: Field at index " + i + " cannot be null.");
            }
        }
        return new WithdrawalRequest(row[0], row[1], row[2], row[3]);
    }

    /**
     * Retrieves a Withdrawal Request by nric
     *
     * @param nric the nric of applicant
     * @return The Withdrawal Request if found, null otherwise.
     */

    public static WithdrawalRequest getWithdrawalRequestByNric(String nric) {
        ArrayList<WithdrawalRequest> requests = getWithdrawalRequests();
        for (WithdrawalRequest request : requests) {
            if (request.getApplicantNRIC().equals(nric)) {
                return request;
            }
        }
        System.out.println("No such Request found");
        return null;
    }

    /**
     * Retrieves all Withdrawal Requests
     *
     * 
     * @return ArrayList of Withdrawal Requests.
     */

    public static ArrayList<WithdrawalRequest> getWithdrawalRequests() {
        List<String[]> rows = CSVUtils.readCSV("data/withdrawals.csv");
        ArrayList<WithdrawalRequest> requests = new ArrayList<>();
        for (String[] row: rows) {
            requests.add(new WithdrawalRequest(row[0], row[1], row[2], row[3]));
        }

        return requests;
    }
}
