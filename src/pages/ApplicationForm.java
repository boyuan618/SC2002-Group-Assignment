package pages;

import utils.CSVUtils;
import utils.IDGenerator;
import utils.Validator;

import java.util.List;
import java.util.Scanner;

public class ApplicationForm {

    private static final String APPLICATIONS_CSV = "data/applications.csv";

    public static void applyForProject(String applicantNric, int age, String maritalStatus) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter project name to apply for: ");
        String projectName = sc.nextLine().trim();

        if (!Validator.isSingleEligible(age, maritalStatus) && !Validator.isMarriedEligible(age, maritalStatus)) {
            System.out.println("❌ You are not eligible to apply based on age and marital status.");
            return;
        }

        if (hasExistingApplication(applicantNric)) {
            System.out.println("⚠️ You already have an existing application.");
            return;
        }

        String flatType;
        if (maritalStatus.equalsIgnoreCase("Single")) {
            flatType = "2-Room";
        } else {
            System.out.print("Enter flat type to apply for (2-Room or 3-Room): ");
            flatType = sc.nextLine().trim();

            if (!Validator.isValidFlatType(flatType)) {
                System.out.println("❌ Invalid flat type.");
                return;
            }
        }

        String applicationId = IDGenerator.generateID("APP");
        String[] applicationRow = { applicantNric, "Pending", flatType, applicationId, projectName };
        CSVUtils.appendToCSV(APPLICATIONS_CSV, applicationRow);

        System.out.println("\n✅ Application submitted successfully! Application ID: " + applicationId);
    }

    public static void viewApplication(String applicantNric) {
        List<String[]> applications = CSVUtils.readCSV(APPLICATIONS_CSV);
        boolean found = false;

        System.out.println("\n===== Your BTO Application =====");
        for (String[] row : applications) {
            if (row.length >= 5 && row[0].equalsIgnoreCase(applicantNric)) {
                found = true;
                System.out.println("Application ID  : " + row[3]);
                System.out.println("Project Name    : " + row[4]);
                System.out.println("Flat Type       : " + row[2]);
                System.out.println("Application Status: " + row[1]);
            }
        }

        if (!found) {
            System.out.println("No application found.");
        }
    }

    public static void requestWithdrawal(String applicantNric) {
        List<String[]> applications = CSVUtils.readCSV(APPLICATIONS_CSV);
        boolean found = false;

        for (String[] app : applications) {
            if (app[0].equalsIgnoreCase(applicantNric) && !app[1].equalsIgnoreCase("Withdrawn")) {
                app[1] = "WithdrawalRequested";
                found = true;
                break;
            }
        }

        if (found) {
            CSVUtils.writeCSV(APPLICATIONS_CSV, applications);
            System.out.println("✅ Withdrawal request submitted.");
        } else {
            System.out.println("❌ No active application found to withdraw.");
        }
    }

    private static boolean hasExistingApplication(String nric) {
        List<String[]> applications = CSVUtils.readCSV(APPLICATIONS_CSV);
        for (String[] app : applications) {
            if (app[0].equalsIgnoreCase(nric) && !app[1].equalsIgnoreCase("Withdrawn")
                    && !app[1].equalsIgnoreCase("Unsuccessful")) {
                return true;
            }
        }
        return false;
    }
}