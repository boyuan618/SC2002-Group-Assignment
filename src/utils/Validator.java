package utils;

import model.BTOProject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for validating inputs in the BTO Management System.
 * @author SC2002Team
 */
public class Validator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/dd/yyyy");

    /**
     * Validates NRIC format: starts with S or T, followed by 7 digits and a capital letter.
     * Example: S1234567A
     * @param nric The NRIC to validate.
     * @return True if the NRIC is valid, false otherwise.
     */
    public static boolean isValidNRIC(String nric) {
        return nric != null && nric.matches("^[ST]\\d{7}[A-Z]$");
    }

    /**
     * Checks if a single applicant is eligible to apply (age >= 35).
     * @param age The applicant's age.
     * @param maritalStatus The applicant's marital status.
     * @return True if eligible, false otherwise.
     */
    public static boolean isSingleEligible(int age, String maritalStatus) {
        return maritalStatus != null && maritalStatus.equalsIgnoreCase("Single") && age >= 35;
    }

    /**
     * Checks if a married applicant is eligible to apply (age >= 21).
     * @param age The applicant's age.
     * @param maritalStatus The applicant's marital status.
     * @return True if eligible, false otherwise.
     */
    public static boolean isMarriedEligible(int age, String maritalStatus) {
        return maritalStatus != null && maritalStatus.equalsIgnoreCase("Married") && age >= 21;
    }

    /**
     * Validates flat type selection.
     * @param flatType The flat type to validate.
     * @return True if valid (2-Room or 3-Room), false otherwise.
     */
    public static boolean isValidFlatType(String flatType) {
        return flatType != null && (flatType.equalsIgnoreCase("2-Room") || flatType.equalsIgnoreCase("3-Room"));
    }

    /**
     * Checks if input string is a valid positive integer.
     * @param input The string to validate.
     * @return True if the input is a positive integer, false otherwise.
     */
    public static boolean isPositiveInteger(String input) {
        try {
            return input != null && Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates project name: non-empty and exists in ProjectList.csv.
     * @param projectName The project name to validate.
     * @return True if valid and exists, false otherwise.
     */
    public static boolean isValidProjectName(String projectName) {
        return projectName != null && !projectName.trim().isEmpty() && BTOProject.getProjectByName(projectName) != null;
    }

    /**
     * Validates date format (M/dd/yyyy) and ensures it's a valid date.
     * @param date The date string to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates visibility is "on" or "off".
     * @param visibility The visibility string to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidVisibility(String visibility) {
        return visibility != null && (visibility.equalsIgnoreCase("on") || visibility.equalsIgnoreCase("off"));
    }

    /**
     * Validates name: non-empty and contains only letters and spaces.
     * @param name The name to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }

    /**
     * Validates comma-separated list (e.g., officer names): non-empty and valid names.
     * @param list The comma-separated list to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidCommaSeparatedList(String list) {
        if (list == null || list.trim().isEmpty()) {
            return true; // Allow empty list
        }
        String[] items = list.split("\\s*,\\s*");
        for (String item : items) {
            if (!item.isEmpty() && !isValidName(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates enquiry title: non-empty and reasonable length (e.g., <= 100 chars).
     * @param title The title to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidEnquiryTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.length() <= 100;
    }

    /**
     * Validates enquiry detail: non-empty and reasonable length (e.g., <= 500 chars).
     * @param detail The detail to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidEnquiryDetail(String detail) {
        return detail != null && !detail.trim().isEmpty() && detail.length() <= 500;
    }

    /**
     * Validates enquiry response: non-empty and reasonable length (e.g., <= 500 chars).
     * @param response The response to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidEnquiryResponse(String response) {
        return response != null && !response.trim().isEmpty() && response.length() <= 500;
    }
}