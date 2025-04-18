package controller;

import utils.CSVUtils;

import java.util.*;

public class HDBOfficerController extends ProjectManagerController implements EnquiryInt {

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

    // 4. Manage flat booking
    public static void manageBooking(String nric) {
        List<String[]> apps = CSVUtils.readCSV(APPLICATIONS_CSV);
        List<String[]> updated = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        for (String[] app : apps) {
            if (app[1].equalsIgnoreCase("Successful")) {
                System.out.println("👤 Applicant: " + app[0] + " | Status: " + app[1]);
                System.out.print("Book flat type (2-Room / 3-Room): ");
                String flatType = sc.nextLine();
                app[1] = "Booked";
                if (app.length < 4)
                    app = Arrays.copyOf(app, 4);
                app[3] = flatType;
                updated.add(app);

                // Generate receipt here as part of booking
                generateReceipt(app);
            } else {
                updated.add(app);
            }
        }
        CSVUtils.writeCSV(APPLICATIONS_CSV, updated);
        System.out.println("✅ Booking updated.");
    }

    // Generate receipt logic extracted for reuse
    private static void generateReceipt(String[] app) {
        String[] receipt = new String[] {
                app[0], // NRIC
                app[2], // Name
                app[4], // Age
                app[5], // Marital Status
                app[3], // Flat Type
                app[6] // Project Name
        };
        CSVUtils.appendToCSV(RECEIPTS_CSV, receipt);
        System.out.println("🧾 Receipt generated: " + String.join(", ", receipt));
    }

    // Enquiry interface implementation
    @Override
    public void handleEnquiries(String nric) {
        EnquiryInt.handleEnquiries(nric);
    }
}
