package pages;

import utils.CSVUtils;
import utils.IDGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EnquiryForm {

    private static final String ENQUIRIES_CSV = "data/enquiries.csv";

    public static void submitEnquiry(String applicantNric) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the project name: ");
        String projectName = sc.nextLine().trim();

        System.out.print("Enter your enquiry: ");
        String enquiryText = sc.nextLine().trim();

        String enquiryId = IDGenerator.generateID("ENQ");
        String[] enquiryRow = { enquiryId, applicantNric, projectName, enquiryText, "" }; // last column for reply
        CSVUtils.appendToCSV(ENQUIRIES_CSV, enquiryRow);

        System.out.println("\n✅ Enquiry submitted successfully! Enquiry ID: " + enquiryId);
    }

    public static void viewMyEnquiries(String applicantNric) {
        List<String[]> enquiries = CSVUtils.readCSV(ENQUIRIES_CSV);
        List<Integer> myIndices = new ArrayList<>();
        System.out.println("\n===== Your Submitted Enquiries =====");

        for (int i = 0; i < enquiries.size(); i++) {
            String[] row = enquiries.get(i);
            if (row.length >= 4 && row[1].equalsIgnoreCase(applicantNric)) {
                myIndices.add(i);
                System.out.println((myIndices.size()) + ". " + row[2] + " - " + row[3]);
                System.out.println("   Reply: " + (row.length > 4 ? row[4] : "<Pending>") + "\n");
            }
        }

        if (myIndices.isEmpty()) {
            System.out.println("No enquiries found.");
        }
    }

    public static void editOrDeleteEnquiry(String applicantNric) {
        List<String[]> enquiries = CSVUtils.readCSV(ENQUIRIES_CSV);
        List<Integer> myIndices = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i)[1].equalsIgnoreCase(applicantNric)) {
                myIndices.add(i);
            }
        }

        if (myIndices.isEmpty()) {
            System.out.println("No enquiries to modify.");
            return;
        }

        System.out.print("Enter the number of the enquiry you want to modify/delete: ");
        int index = Integer.parseInt(sc.nextLine()) - 1;

        if (index < 0 || index >= myIndices.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        int actualIndex = myIndices.get(index);
        String[] selected = enquiries.get(actualIndex);

        System.out.println("1. Edit Enquiry\n2. Delete Enquiry\nChoose option: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1" -> {
                System.out.print("Enter new enquiry text: ");
                selected[3] = sc.nextLine();
                enquiries.set(actualIndex, selected);
                CSVUtils.writeCSV(ENQUIRIES_CSV, enquiries);
                System.out.println("✅ Enquiry updated.");
            }
            case "2" -> {
                enquiries.remove(actualIndex);
                CSVUtils.writeCSV(ENQUIRIES_CSV, enquiries);
                System.out.println("🗑️ Enquiry deleted.");
            }
            default -> System.out.println("Invalid option.");
        }
    }
}
