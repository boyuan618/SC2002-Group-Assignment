package model;

import java.util.List;
import java.util.Scanner;
import utils.Validator;

/**
 * Interface defining methods for managing and responding to enquiries in the HDB BTO Management System.
 * Provides default implementations for viewing project details and handling enquiries.
 *
 * @author SC2002Team
 */
public interface EnquiryInt {

    /**
     * Displays details of all BTO projects available in the system.
     * Includes project name, neighborhood, room types, dates, manager, officer slots, and assigned officers.
     *
     * @throws IllegalStateException If project data is invalid or inaccessible.
     */
    default void viewAllProjectDetails() {
        List<BTOProject> projects = BTOProject.getProjects();
        if (projects == null) {
            System.out.println("Error: Unable to retrieve projects.");
            return;
        }
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        for (BTOProject p : projects) {
            if (p == null) {
                System.out.println("Warning: Skipping null project.");
                continue;
            }
            System.out.println("\n===== Officer Project Details =====");
            System.out.println("Project Name       : " + (p.getProjectName() != null ? p.getProjectName() : "<Unknown>"));
            System.out.println("Neighborhood       : " + (p.getNeighborhood() != null ? p.getNeighborhood() : "<Unknown>"));
            List<Room> rooms = p.getRooms();
            if (rooms == null || rooms.isEmpty()) {
                System.out.println("Room Types         : <None>");
            } else {
                for (Room type : rooms) {
                    if (type != null) {
                        System.out.println(
                                "Type            : " + (type.getRoomType() != null ? type.getRoomType() : "<Unknown>") +
                                " | Units: " + type.getUnits() +
                                " | Price: $" + type.getPrice());
                    }
                }
            }
            System.out.println("Opening Date       : " + (p.getOpenDate() != null ? p.getOpenDate() : "<Unknown>"));
            System.out.println("Closing Date       : " + (p.getCloseDate() != null ? p.getCloseDate() : "<Unknown>"));
            System.out.println("Manager            : " + (p.getManager() != null ? p.getManager() : "<Unknown>"));
            System.out.println("Officer Slots      : " + p.getOfficerSlot());
            
            String officerList = p.getOfficerList();
            System.out.println("Assigned Officers  : " + (officerList != null && !officerList.isEmpty() ? officerList : "<None>"));
        }
    }

    /**
     * Displays enquiries for the specified project and allows the user to reply to them.
     * Saves replies to the enquiry list if changes are made.
     *
     * @param sc The Scanner object for reading user input.
     * @param assignedProject The name of the project to view enquiries for.
     * @throws IllegalArgumentException If the scanner is null or the project is invalid.
     */
    default void viewAndReplyEnquiries(Scanner sc, String assignedProject) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        if (assignedProject == null || assignedProject.trim().isEmpty()) {
            System.out.println("Error: Project name cannot be empty.");
            return;
        }
        assignedProject = assignedProject.trim();
        if (BTOProject.getProjectByName(assignedProject) == null) {
            System.out.println("Error: Project not found: " + assignedProject);
            return;
        }
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        if (enquiries == null) {
            System.out.println("Error: Unable to retrieve enquiries.");
            return;
        }
        boolean found = false;
        boolean modified = false;

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry e = enquiries.get(i);
            if (e == null) {
                System.out.println("Warning: Skipping null enquiry.");
                continue;
            }
            if (e.getProjectName() != null && e.getProjectName().equalsIgnoreCase(assignedProject)) {
                found = true;
                System.out.println("\nEnquiry from NRIC : " + (e.getEnquirerNRIC() != null ? e.getEnquirerNRIC() : "<Unknown>"));
                System.out.println("Title             : " + (e.getTitle() != null ? e.getTitle() : "<Unknown>"));
                System.out.println("Detail            : " + (e.getDetail() != null ? e.getDetail() : "<Unknown>"));
                System.out.println("Reply             : " +
                        (e.getResponse() == null || e.getResponse().isEmpty() ? "<None>" : e.getResponse()));
                System.out.print("Reply to this enquiry? (y/n): ");
                String choice = sc.nextLine().trim();
                while (!choice.equalsIgnoreCase("y") && !choice.equalsIgnoreCase("n")) {
                    System.out.print("Invalid input. Please enter 'y' or 'n': ");
                    choice = sc.nextLine().trim();
                }
                if (choice.equalsIgnoreCase("y")) {
                    System.out.print("Enter reply: ");
                    String reply = sc.nextLine().trim();
                    while (!Validator.isValidEnquiryResponse(reply)) {
                        System.out.print("Invalid reply: Must be non-empty and up to 500 characters. Enter reply: ");
                        reply = sc.nextLine().trim();
                    }
                    e.setResponse(reply);
                    modified = true;
                }
            }
        }

        if (found && modified) {
            Enquiry.writeEnquiries(enquiries);
            System.out.println("✅ Replies saved.");
        } else if (found && !modified) {
            System.out.println("No replies were added.");
        } else {
            System.out.println("No enquiries found for project: " + assignedProject);
        }

    }

    /**
     * Displays all enquiries for the specified project.
     *
     * @param assignedProject The name of the project to view enquiries for.
     * @throws IllegalArgumentException If the project name is invalid.
     */
    default void viewallEnquiries(String assignedProject) {
        if (assignedProject == null || assignedProject.trim().isEmpty()) {
            System.out.println("Error: Project name cannot be empty.");
            return;
        }
        assignedProject = assignedProject.trim();
        if (BTOProject.getProjectByName(assignedProject) == null) {
            System.out.println("Error: Project not found: " + assignedProject);
            return;
        }
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        if (enquiries == null) {
            System.out.println("Error: Unable to retrieve enquiries.");
            return;
        }
        boolean found = false;

        for (Enquiry e : enquiries) {
            if (e == null) {
                System.out.println("Warning: Skipping null enquiry.");
                continue;
            }
            if (e.getProjectName() != null && e.getProjectName().equalsIgnoreCase(assignedProject)) {
                found = true;
                System.out.println("\nEnquiry from NRIC : " + (e.getEnquirerNRIC() != null ? e.getEnquirerNRIC() : "<Unknown>"));
                System.out.println("Title             : " + (e.getTitle() != null ? e.getTitle() : "<Unknown>"));
                System.out.println("Detail            : " + (e.getDetail() != null ? e.getDetail() : "<Unknown>"));
                System.out.println("Reply             : " +
                        (e.getResponse() == null || e.getResponse().isEmpty() ? "<None>" : e.getResponse()));
            }
        }
        if (!found) {
            System.out.println("No enquiries found for project: " + assignedProject);
        }
    }
}
