package model;

import utils.CSVUtils;

import java.util.List;
import java.util.Scanner;

public interface EnquiryInt {
    default void viewHandledProjectDetails() {
        List<String[]> projects = CSVUtils.readCSV("data/projects.csv");
        for (String[] p : projects) {
            System.out.println("\n===== Officer Project Details =====");
            System.out.println("Project Name       : " + p[0]);
            System.out.println("Neighborhood       : " + p[1]);
            System.out.println("Type 1             : " + p[2] + " | Units: " + p[3] + " | Price: $" + p[4]);
            System.out.println("Type 2             : " + p[5] + " | Units: " + p[6] + " | Price: $" + p[7]);
            System.out.println("Opening Date       : " + p[8]);
            System.out.println("Closing Date       : " + p[9]);
            System.out.println("Manager            : " + p[10]);
            System.out.println("Officer Slots      : " + p[11]);
            System.out.println("Assigned Officers  : " + p[12]);
        }
    }

    default void viewAndReplyEnquiries(String assignedProject) {
        List<String[]> enquiries = CSVUtils.readCSV("data/enquiries.csv");
        Scanner sc = new Scanner(System.in); // Make sure this scanner is reused or handled appropriately
        boolean found = false;

        for (int i = 0; i < enquiries.size(); i++) {
            String[] e = enquiries.get(i);
            if (e.length >= 4 && e[2].equalsIgnoreCase(assignedProject)) {
                found = true;
                System.out.println("\nEnquiry ID: " + e[0]);
                System.out.println("From NRIC : " + e[1]);
                System.out.println("Message   : " + e[3]);
                System.out.println("Reply     : " + (e.length > 4 ? e[4] : "<None>"));
                System.out.print("Reply to this enquiry? (y/n): ");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("y")) {
                    System.out.print("Enter reply: ");
                    String reply = sc.nextLine();
                    if (e.length < 5)
                        e = java.util.Arrays.copyOf(e, 5);
                    e[4] = reply;
                    enquiries.set(i, e);
                }
            }
        }

        if (found) {
            CSVUtils.writeCSV("data/enquiries.csv", enquiries);
            System.out.println("✅ Replies saved.");
        } else {
            System.out.println("No enquiries found for project: " + assignedProject);
        }
    }

}
