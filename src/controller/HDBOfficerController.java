package controller;

import model.BTOApplication;
import model.Receipt;
import utils.CSVUtils;

import java.util.*;

public class HDBOfficerController {

    private static final String PROJECTS_CSV = "data/ProjectList.csv";
    private static final String APPLICATIONS_CSV = "data/OfficerApplications.csv";
    private static final String RECEIPTS_CSV = "data/Receipts.csv";

    // 1. View all projects
    public static void viewAllProjects() {
        System.out.println("\n📋 All BTO Projects:");
        List<String[]> projects = CSVUtils.readCSV(PROJECTS_CSV);
        for (String[] row : projects) {
            System.out.println("- " + String.join(" | ", row));
        }
    }

    // 2. Register to handle a project
    public static void registerForProject(String nric) {
        Scanner sc = new Scanner(System.in);
        List<String[]> projects = CSVUtils.readCSV(PROJECTS_CSV);
        System.out.println("\n📁 Available Projects:");
        for (int i = 0; i < projects.size(); i++) {
            String[] p = projects.get(i);
            System.out.println(i + 1 + ". " + p[0] + " in " + p[1]);
        }
        System.out.print("Enter project number to register: ");
        int choice = Integer.parseInt(sc.nextLine()) - 1;
        if (choice >= 0 && choice < projects.size()) {
            String[] selected = projects.get(choice);
            // Assume project has 11th column as officer list (semicolon-separated NRICs)
            if (selected.length < 11)
                selected = Arrays.copyOf(selected, 11);
            if (selected[10] == null)
                selected[10] = "";
            if (!selected[10].contains(nric)) {
                selected[10] += (selected[10].isEmpty() ? "" : ";") + nric;
                projects.set(choice, selected);
                CSVUtils.writeCSV(PROJECTS_CSV, projects);
                System.out.println("✅ Registration submitted for approval.");
            } else {
                System.out.println("⚠️ Already registered.");
            }
        }
    }

    // 3. View assigned project
    public static void viewMyProject(String nric) {
        List<String[]> projects = CSVUtils.readCSV(PROJECTS_CSV);
        for (String[] p : projects) {
            if (p.length >= 11 && p[10] != null && Arrays.asList(p[10].split(";")).contains(nric)) {
                System.out.println("\n🔍 Assigned Project:");
                System.out.println(String.join(" | ", p));
                return;
            }
        }
        System.out.println("❌ No assigned project found.");
    }

    // 4. View and manage applications
    public static void manageApplications(String nric) {
        Scanner sc = new Scanner(System.in);
        List<BTOApplication> updatedApplications = new ArrayList<>();

        System.out.println("\n👤 Managing Applications:");

        for (BTOApplication app : CSVUtils.getApplications(APPLICATIONS_CSV)) {
            if (app.getStatus().equalsIgnoreCase("Successful") && app.getApplicant().getNric().equalsIgnoreCase(nric)) {
                // Officer's NRIC
                System.out.println("Applicant: " + app.getApplicant().toString() + " | Status: " + app.getStatus());

                // Handle flat selection for successful applicants
                System.out.print("Enter flat type to book (2-Room / 3-Room): ");
                String flatType = sc.nextLine();
                app.updateStatus("Booked"); // Update status to "Booked"
                app.setFlatType(flatType); // Update flat type
                updatedApplications.add(app);

                // Generate receipt for this applicant
                generateReceipt(app);
            } else {
                updatedApplications.add(app);
            }
        }

        CSVUtils.writeApplications(APPLICATIONS_CSV, updatedApplications);
        System.out.println("✅ Applications updated.");
    }

    // 5. Generate receipt logic extracted for reuse
    private static void generateReceipt(BTOApplication btoApplication) {
        Receipt receipt = Receipt.fromBTOApplication(btoApplication);
        CSVUtils.appendToCSV(RECEIPTS_CSV, receipt.toCSV());
        receipt.printReceipt();
        System.out.println("🧾 Receipt generated.");
    }

    // 6. Respond to enquiry (handling enquiries based on project details)
    public static void handleEnquiries(String projectName, String enquiryResponse) {
        // This function will handle and reply to enquiries related to the project being
        // handled by the officer
        System.out.println("Enquiry regarding project: " + projectName);
        System.out.println("Response: " + enquiryResponse);
        // Add logic to save or process enquiry responses as needed
    }

    // 7. View project details (for officer's handling)
    public static void viewProjectDetails(String nric) {
        List<String[]> projects = CSVUtils.readCSV(PROJECTS_CSV);
        for (String[] p : projects) {
            if (p.length >= 11 && p[10] != null && Arrays.asList(p[10].split(";")).contains(nric)) {
                System.out.println("\n🔍 Project Details for: " + p[0]);
                System.out.println("Project: " + String.join(" | ", p));
                return;
            }
        }
        System.out.println("❌ No project assigned.");
    }

    // 8. Update flat availability for booking (managing flat availability for
    // booking)
    public static void updateFlatAvailability(String projectName, String flatType, int unitsBooked) {
        List<String[]> projects = CSVUtils.readCSV(PROJECTS_CSV);
        for (String[] project : projects) {
            if (project[0].equalsIgnoreCase(projectName)) {
                if (flatType.equalsIgnoreCase("2-Room")) {
                    int unitsRemaining = Integer.parseInt(project[3]) - unitsBooked;
                    project[3] = String.valueOf(unitsRemaining);
                } else if (flatType.equalsIgnoreCase("3-Room")) {
                    int unitsRemaining = Integer.parseInt(project[6]) - unitsBooked;
                    project[6] = String.valueOf(unitsRemaining);
                }
                CSVUtils.writeCSV(PROJECTS_CSV, projects);
                System.out.println("✅ Flat availability updated.");
                return;
            }
        }
        System.out.println("❌ Project not found.");
    }
}
