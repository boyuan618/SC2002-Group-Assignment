package utils;

public class Validator {

    /**
     * Validates NRIC format: starts with S or T, followed by 7 digits and a capital
     * letter
     * Example: S1234567A
     */
    public static boolean isValidNRIC(String nric) {
        return nric != null && nric.matches("^[ST]\\d{7}[A-Z]$");
    }

    /**
     * Checks if a single applicant is eligible to apply (age >= 35)
     */
    public static boolean isSingleEligible(int age, String maritalStatus) {
        return maritalStatus.equalsIgnoreCase("Single") && age >= 35;
    }

    /**
     * Checks if a married applicant is eligible to apply (age >= 21)
     */
    public static boolean isMarriedEligible(int age, String maritalStatus) {
        return maritalStatus.equalsIgnoreCase("Married") && age >= 21;
    }

    /**
     * Validates flat type selection
     */
    public static boolean isValidFlatType(String flatType) {
        return flatType.equalsIgnoreCase("2-Room") || flatType.equalsIgnoreCase("3-Room");
    }

    /**
     * Checks if input string is a valid positive integer
     */
    public static boolean isPositiveInteger(String input) {
        try {
            return Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
