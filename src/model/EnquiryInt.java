package model;

import java.util.List;
import java.util.Scanner;

public interface EnquiryInt {
    default void viewAllProjectDetails() {
        List<BTOProject> projects = BTOProject.getProjects();

        for (BTOProject p : projects) {
            System.out.println("\n===== Officer Project Details =====");
            System.out.println("Project Name       : " + p.getProjectName());
            System.out.println("Neighborhood       : " + p.getNeighborhood());
            for (Room type : p.getRooms()) {
                System.out.println(
                        "Type            : " + type.getRoomType() + " | Units: " + type.getUnits() + " | Price: $"
                                + type.getPrice());
            }
            System.out.println("Opening Date       : " + p.getOpenDate());
            System.out.println("Closing Date       : " + p.getCloseDate());
            System.out.println("Manager            : " + p.getManager());
            System.out.println("Officer Slots      : " + p.getOfficerSlot());

            // Join non-null officer names from the array
            String officerList = p.getOfficerList();
            String officers = officerList;
            System.out.println("Assigned Officers  : " + officers);
        }
    }

    default void viewAndReplyEnquiries(Scanner sc, String assignedProject) {
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        boolean found = false;

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry e = enquiries.get(i);
            if (e.getProjectName().equalsIgnoreCase(assignedProject)) {
                found = true;
                System.out.println("\nEnquiry from NRIC : " + e.getEnquirerNRIC());
                System.out.println("Title             : " + e.getTitle());
                System.out.println("Detail            : " + e.getDetail());
                System.out.println("Reply             : "
                        + (e.getResponse() == null || e.getResponse().isEmpty() ? "<None>" : e.getResponse()));
                System.out.print("Reply to this enquiry? (y/n): ");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("y")) {
                    System.out.print("Enter reply: ");
                    String reply = sc.nextLine();
                    e.setResponse(reply);
                }
            }
        }

        if (found) {
            Enquiry.writeEnquiries(enquiries);
            System.out.println("✅ Replies saved.");
        } else {
            System.out.println("No enquiries found for project: " + assignedProject);
        }

    }

    default void viewallEnquiries(String assignedProject) {
        List<Enquiry> enquiries = Enquiry.getEnquiries();

        for (Enquiry e : enquiries) {
            if (e.getProjectName().equalsIgnoreCase(assignedProject)) {
                System.out.println("\nEnquiry from NRIC : " + e.getEnquirerNRIC());
                System.out.println("Title             : " + e.getTitle());
                System.out.println("Detail            : " + e.getDetail());
                System.out.println("Reply             : "
                        + (e.getResponse() == null || e.getResponse().isEmpty() ? "<None>" : e.getResponse()));
            }
        }
    }
}
