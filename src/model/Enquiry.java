package model;

import java.util.*;
import utils.CSVUtils;
import utils.Validator;

/**
 * Represents an enquiry submitted by an applicant for a BTO project in the HDB BTO Management System.
 * Manages enquiry details, responses, and CSV storage.
 *
 * @author SC2002Team
 */
public class Enquiry {
    final private int enquiryid;
    final private String enquirerNRIC;
    final private String projectName;
    private String title;
    private String detail;
    private String response;

    public static final String ENQUIRIES_CSV = "data/FlatEnquiries.csv";

    /**
     * Constructs an Enquiry with the specified details.
     *
     * @param enquiryid The unique ID of the enquiry.
     * @param enquirerNRIC The NRIC of the enquirer.
     * @param projectName The name of the project the enquiry is about.
     * @param title The title of the enquiry.
     * @param detail The detailed description of the enquiry.
     * @param response The response to the enquiry, if any.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public Enquiry(int enquiryid, String enquirerNRIC, String projectName, String title, String detail,
            String response) {
        if (enquiryid < 0) {
            throw new IllegalArgumentException("Enquiry ID cannot be negative.");
        }
        if (!Validator.isValidNRIC(enquirerNRIC)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (Applicant.getApplicantByNRIC(enquirerNRIC) == null) {
            throw new IllegalArgumentException("Applicant not found for NRIC: " + enquirerNRIC);
        }
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        if (BTOProject.getProjectByName(projectName) == null) {
            throw new IllegalArgumentException("Project not found: " + projectName);
        }
        if (!Validator.isValidEnquiryTitle(title)) {
            throw new IllegalArgumentException("Invalid enquiry title: Must be non-empty and less than 100 characters.");
        }
        if (!Validator.isValidEnquiryDetail(detail)) {
            throw new IllegalArgumentException("Invalid enquiry detail: Must be non-empty and less than 500 characters.");
        }
        this.enquiryid = enquiryid;
        this.enquirerNRIC = enquirerNRIC.trim();
        this.projectName = projectName.trim();
        this.title = title.trim();
        this.detail = detail.trim();
        this.response = response != null ? response.trim() : null;
    }

    /**
     * Gets the enquiry ID.
     *
     * @return The enquiry ID.
     */
    public int getId() {
        return enquiryid;
    }

    /**
     * Gets the NRIC of the enquirer.
     *
     * @return The enquirer's NRIC.
     */
    public String getEnquirerNRIC() {
        return enquirerNRIC;
    }

    /**
     * Gets the project name associated with the enquiry.
     *
     * @return The project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the title of the enquiry.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the enquiry.
     *
     * @param title The new title.
     * @throws IllegalArgumentException If the title is invalid.
     */
    public void setTitle(String title) {
        if (!Validator.isValidEnquiryTitle(title)) {
            throw new IllegalArgumentException("Invalid enquiry title: Must be non-empty and less than 100 characters.");
        }
        this.title = title.trim();
    }

    /**
     * Gets the detailed description of the enquiry.
     *
     * @return The detail.
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the detailed description of the enquiry.
     *
     * @param detail The new detail.
     * @throws IllegalArgumentException If the detail is invalid.
     */
    public void setDetail(String detail) {
        if (!Validator.isValidEnquiryDetail(detail)) {
            throw new IllegalArgumentException("Invalid enquiry detail: Must be non-empty and less than 500 characters.");
        }
        this.detail = detail.trim();
    }

    /**
     * Gets the response to the enquiry.
     *
     * @return The response, or null if not set.
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the response to the enquiry.
     *
     * @param response The new response, or null if no response.
     * @throws IllegalArgumentException If the response is invalid.
     */
    public void setResponse(String response) {
        if (response != null && response.trim().isEmpty()) {
            throw new IllegalArgumentException("Response cannot be empty if provided.");
        }
        this.response = response != null ? response.trim() : null;
    }

    /**
     * Converts the enquiry to a CSV row.
     *
     * @return An array of strings representing the CSV row.
     * @throws IllegalStateException If required fields are null.
     */
    public String[] toCSVRow() {
        if (enquirerNRIC == null || projectName == null || title == null || detail == null) {
            throw new IllegalStateException("Cannot generate CSV row: One or more required fields are null.");
        }
        return new String[] { String.valueOf(enquiryid), enquirerNRIC, projectName, title, detail, response != null ? response : "" };
    }

    /**
     * Retrieves all enquiries from the CSV file.
     *
     * @return A list of enquiries, or an empty list if an error occurs.
     */
    public static List<Enquiry> getEnquiries() {
        List<String[]> raw = CSVUtils.readCSV(ENQUIRIES_CSV);
        if (raw == null) {
            return new ArrayList<>();
        }
        List<Enquiry> list = new ArrayList<>();
        for (String[] row : raw) {
            try {
                if (row.length >= 6) {
                    list.add(fromCSVRow(row));
                } else {
                    System.out.println("Skipping malformed row: " + String.join(",", row));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error parsing enquiry row: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Writes a list of enquiries to the CSV file, sorted by enquiry ID.
     *
     * @param enquirers The list of enquiries to write.
     * @throws IllegalArgumentException If the enquiries list is null.
     */
    public static void writeEnquiries(List<Enquiry> enquirers) {
        if (enquirers == null) {
            throw new IllegalArgumentException("Enquiries list cannot be null.");
        }
        enquirers.sort(Comparator.comparingInt(Enquiry::getId));
        List<String[]> rows = new ArrayList<>();
        for (Enquiry e : enquirers) {
            if (e != null) {
                rows.add(e.toCSVRow());
            } else {
                System.out.println("Skipping null enquiry.");
            }
        }
        CSVUtils.writeCSV(ENQUIRIES_CSV, rows);
    }

    /**
     * Appends a new enquiry to the CSV file.
     *
     * @param e The enquiry to append.
     * @throws IllegalArgumentException If the enquiry is null.
     */
    public static void appendEnquiry(Enquiry e) {
        if (e == null) {
            throw new IllegalArgumentException("Enquiry cannot be null.");
        }
        CSVUtils.appendToCSV(ENQUIRIES_CSV, e.toCSVRow());
    }

    /**
     * Converts a CSV row to an Enquiry object.
     *
     * @param row The CSV row data.
     * @return An Enquiry object.
     * @throws IllegalArgumentException If the row is invalid or data is missing.
     */
    public static Enquiry fromCSVRow(String[] row) {
        if (row == null || row.length < 6) {
            throw new IllegalArgumentException("Invalid CSV row: Must have at least 6 fields.");
        }
        int enquiryid;
        try {
            enquiryid = Integer.parseInt(row[0]);
            if (enquiryid < 0) {
                throw new IllegalArgumentException("Enquiry ID cannot be negative.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid enquiry ID: Must be a non-negative integer.");
        }
        if (!Validator.isValidNRIC(row[1])) {
            throw new IllegalArgumentException("Invalid NRIC in CSV row: " + row[1]);
        }
        if (Applicant.getApplicantByNRIC(row[1]) == null) {
            throw new IllegalArgumentException("Applicant not found for NRIC: " + row[1]);
        }
        if (row[2] == null || row[2].trim().isEmpty()) {
            throw new IllegalArgumentException("Project name in CSV row cannot be empty.");
        }
        if (BTOProject.getProjectByName(row[2]) == null) {
            throw new IllegalArgumentException("Project not found: " + row[2]);
        }
        if (!Validator.isValidEnquiryTitle(row[3])) {
            throw new IllegalArgumentException("Invalid enquiry title in CSV row: Must be non-empty and less than 100 characters.");
        }
        if (!Validator.isValidEnquiryDetail(row[4])) {
            throw new IllegalArgumentException("Invalid enquiry detail in CSV row: Must be non-empty and less than 500 characters.");
        }
        return new Enquiry(enquiryid, row[1], row[2], row[3], row[4], row[5]);
    }

    /**
     * Updates an existing enquiry for a given NRIC and index.
     *
     * @param nric The NRIC of the enquirer.
     * @param index The index of the enquiry to update (0-based among enquiries for this NRIC).
     * @param newTitle The new title for the enquiry.
     * @param newDetail The new detail for the enquiry.
     * @return True if the enquiry was updated, false if not found or invalid.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public static boolean updateEnquiry(String nric, int index, String newTitle, String newDetail) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative.");
        }
        if (!Validator.isValidEnquiryTitle(newTitle)) {
            throw new IllegalArgumentException("Invalid enquiry title: Must be non-empty and less than 100 characters.");
        }
        if (!Validator.isValidEnquiryDetail(newDetail)) {
            throw new IllegalArgumentException("Invalid enquiry detail: Must be non-empty and less than 500 characters.");
        }
        List<Enquiry> enquiries = getEnquiries();
        if (enquiries == null) {
            return false;
        }
        int count = -1;
        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i) != null && enquiries.get(i).getEnquirerNRIC().equals(nric)) {
                count++;
                if (count == index) {
                    Enquiry enquiry = enquiries.get(i);
                    enquiry.setTitle(newTitle);
                    enquiry.setDetail(newDetail);
                    writeEnquiries(enquiries);
                    System.out.println("Enquiry updated successfully.");
                    return true;
                }
            }
        }
        System.out.println("Enquiry not found for NRIC and index.");
        return false;
    }

    /**
     * Removes an existing enquiry for a given NRIC and index, if it has no response.
     *
     * @param nric The NRIC of the enquirer.
     * @param index The index of the enquiry to remove (0-based among enquiries for this NRIC).
     * @return True if the enquiry was removed, false if not found, invalid, or has a response.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public static boolean removeEnquiry(String nric, int index) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative.");
        }
        List<Enquiry> enquiries = getEnquiries();
        if (enquiries == null) {
            return false;
        }
        int count = -1;
        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i) != null && enquiries.get(i).getEnquirerNRIC().equals(nric)) {
                count++;
                if (count == index) {
                    if (enquiries.get(i).getResponse() != null && !enquiries.get(i).getResponse().isEmpty()) {
                        System.out.println("Cannot delete enquiry as it has a response.");
                        return false;
                    }
                    enquiries.remove(i);
                    writeEnquiries(enquiries);
                    System.out.println("Enquiry deleted successfully.");
                    return true;
                }
            }
        }
        System.out.println("Enquiry not found for NRIC and index.");
        return false;
    }
}