package model;

import java.time.LocalDate;
import java.util.*;

import utils.CSVUtils;
import utils.Validator;

/**
 * Represents an HDB Officer in the HDB BTO Management System, responsible for handling
 * project-related tasks, flat selections, and enquiries. Extends Applicant and implements
 * EnquiryInt for enquiry-related operations.
 *
 * @author SC2002Team
 */
public class HDBOfficer extends Applicant implements EnquiryInt {
    private BTOProject projectAssigned;

    /**
     * Constructs an HDBOfficer with the specified details and assigns a project if the officer's
     * name is found in a project's officer list.
     *
     * @param name The officer's name.
     * @param nric The officer's NRIC.
     * @param age The officer's age.
     * @param maritalStatus The officer's marital status.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public HDBOfficer(String name, String nric, int age, String maritalStatus) {
        super(name, nric, age, maritalStatus);
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name: Must contain only letters and spaces and cannot be empty.");
        }
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Must be between 18 and 120.");
        }
        if (maritalStatus == null || (!maritalStatus.equalsIgnoreCase("Single") && !maritalStatus.equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }

        this.projectAssigned = null;

        List<BTOProject> projects = BTOProject.getProjects();
        if (projects == null) {
            System.out.println("Warning: Unable to retrieve projects for officer assignment.");
            return;
        }
        for (BTOProject project : projects) {
            if (project == null || project.getOfficerList() == null) {
                continue;
            }
            try {
                String officerList = project.getOfficerList().trim();
                if (!officerList.isEmpty() && Arrays.asList(officerList.split("\\s*,\\s*")).contains(name)) {
                    this.projectAssigned = project;
                    break;
                }
            } catch (Exception e) {
                System.out.println("Warning: Error parsing officer list for project: " + (project.getProjectName() != null ? project.getProjectName() : "<Unknown>"));
            }
        }
    }

    /**
     * Gets the project assigned to this officer.
     *
     * @return The assigned BTOProject, or null if none.
     */
    public BTOProject getProjectAssigned() {
        return projectAssigned;
    }

    /**
     * Sets the project assigned to this officer.
     *
     * @param projectAssigned The BTOProject to assign.
     * @throws IllegalArgumentException If the project is invalid or does not exist.
     */
    public void setProjectAssigned(BTOProject projectAssigned) {
        if (projectAssigned == null) {
            throw new IllegalArgumentException("Assigned project cannot be null.");
        }
        List<BTOProject> projects = BTOProject.getProjects();
        if (projects == null || !projects.contains(projectAssigned)) {
            throw new IllegalArgumentException("Project does not exist in the system: " + (projectAssigned.getProjectName() != null ? projectAssigned.getProjectName() : "<Unknown>"));
        }
        this.projectAssigned = projectAssigned;
    }

    /**
     * Checks if the officer can register for the specified project.
     * Ensures the officer is not already assigned to another project during the application
     * period and has not applied as an applicant for the same project.
     *
     * @param project The BTOProject to register for.
     * @return True if the officer can register, false otherwise.
     * @throws IllegalArgumentException If the project is invalid.
     */
    public boolean canRegisterForProject(BTOProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (BTOProject.getProjectByName(project.getProjectName()) == null) {
            System.out.println("Error: Project does not exist: " + project.getProjectName());
            return false;
        }
        LocalDate today = LocalDate.now();
        if (this.projectAssigned != null && project.isWithinApplicationPeriod(today)) {
            System.out.println("Error: You are already registered for another project during this period.");
            return false;
        }


        List<BTOApplication> applications = BTOApplication.getApplications();

        if (applications == null) {
            System.out.println("Warning: Unable to retrieve applications.");
            return false;
        }
        for (BTOApplication app : applications) {
            if (app == null || app.getApplicantNRIC() == null || app.getProjectName() == null) {
                continue;
            }
            if (app.getApplicantNRIC().equals(this.getNric()) && app.getProjectName().equals(project.getProjectName())) {
                System.out.println("Error: You have already applied for this project as an applicant.");
                return false;
            }
        }
        

        try {
            OfficerApplication.createApplication(new OfficerApplication(this.getNric(), project.getProjectName(), "Pending"));
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating officer application: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registers the officer for the specified project if eligible.
     * Updates the project’s officer list and assigns the project to the officer.
     *
     * @param project The BTOProject to register for.
     * @throws IllegalArgumentException If the project is invalid.
     */
    public void registerForProject(BTOProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (canRegisterForProject(project)) {
            try {
                String officerList = project.getOfficerList() != null ? project.getOfficerList().trim() : "";
                if (!officerList.isEmpty() && Arrays.asList(officerList.split("\\s*,\\s*")).contains(this.getName())) {
                    System.out.println("Error: You are already registered as an officer for this project.");
                    return;
                }
                this.projectAssigned = project;
                project.addOfficer(this.getName());
                System.out.println("✅ Successfully registered as an HDB Officer for project: " + project.getProjectName());
            } catch (IllegalArgumentException e) {
                System.out.println("Error registering for project: " + e.getMessage());
            }
        }
    }

    /**
     * Displays details of the project assigned to this officer.
     * Overrides the EnquiryInt implementation to show only the assigned project.
     */
    @Override
    public void viewAllProjectDetails() {
        if (projectAssigned == null) {
            System.out.println("You are not handling any project currently.");
            return;
        }
        System.out.println("\n===== Officer Handling Project =====");
        System.out.println("Project Name       : " + (projectAssigned.getProjectName() != null ? projectAssigned.getProjectName() : "<Unknown>"));
        System.out.println("Neighborhood       : " + (projectAssigned.getNeighborhood() != null ? projectAssigned.getNeighborhood() : "<Unknown>"));
        List<Room> rooms = projectAssigned.getRooms();
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
        System.out.println("Opening Date       : " + (projectAssigned.getOpenDate() != null ? projectAssigned.getOpenDate() : "<Unknown>"));
        System.out.println("Closing Date       : " + (projectAssigned.getCloseDate() != null ? projectAssigned.getCloseDate() : "<Unknown>"));
        System.out.println("Manager            : " + (projectAssigned.getManager() != null ? projectAssigned.getManager() : "<Unknown>"));
        System.out.println("Officer Slots      : " + projectAssigned.getOfficerSlot());
        String officerList = projectAssigned.getOfficerList();
        System.out.println("Assigned Officers  : " + (officerList != null && !officerList.isEmpty() ? officerList : "<None>"));
    }

    /**
     * Handles flat selection for a successful application by updating the application
     * and project details.
     *
     * @param application The BTOApplication to process.
     * @param flatType The selected flat type (e.g., "2-Room", "3-Room").
     * @throws IllegalArgumentException If the application, flat type, or project is invalid.
     */
    public void handleFlatSelection(BTOApplication application, String flatType) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null.");
        }
        if (!Validator.isValidFlatType(flatType)) {
            throw new IllegalArgumentException("Invalid flat type: Must be '2-Room' or '3-Room'.");
        }
        if (projectAssigned == null) {
            throw new IllegalArgumentException("No project assigned to this officer.");
        }
        if (!application.getProjectName().equals(projectAssigned.getProjectName())) {
            throw new IllegalArgumentException("Application does not belong to the assigned project: " + application.getProjectName());
        }
        if (!application.getStatus().equalsIgnoreCase("Successful")) {
            System.out.println("Error: This application is not successful yet.");
            return;
        }

        boolean flatTypeExists = false;
        boolean unitsAvailable = false;
        for (Room type : projectAssigned.getRooms()) {
            if (type != null && type.getRoomType() != null && type.getRoomType().equalsIgnoreCase(flatType)) {
                flatTypeExists = true;
                if (type.getUnits() > 0) {
                    unitsAvailable = true;
                    try {
                        type.setUnits(type.getUnits() - 1);
                        application.setFlatType(flatType);
                        application.setStatus("Booked");
                        BTOProject.editProject(projectAssigned);
                        BTOApplication.updateBTOApplication(application);
                        Receipt.fromBTOApplication(application).printReceipt();
                        System.out.println("✅ Flat selection completed for application: " + application.getApplicantNRIC());
                    } catch (RuntimeException e) {
                        System.out.println("Error processing flat selection: " + e.getMessage());
                    }
                    return;
                }
            }
        }
        if (!flatTypeExists) {
            System.out.println("Error: Flat type not available in project: " + flatType);
        } else if (!unitsAvailable) {
            System.out.println("Error: No units available for flat type: " + flatType);
        }
    }

    /**
     * Retrieves an applicant by their NRIC from the users CSV file.
     *
     * @param nric The NRIC of the applicant.
     * @return The Applicant object, or null if not found.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public static Applicant getApplicantByNRIC(String nric) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<String[]> users = CSVUtils.readCSV("data/users.csv");
        if (users == null) {
            System.out.println("Warning: Unable to read users CSV.");
            return null;
        }
        for (String[] row : users) {
            if (row.length < 4) {
                System.out.println("Warning: Malformed user row in CSV.");
                continue;
            }
            if (row[1].equals(nric)) {
                try {
                    String name = row[0];
                    int age = Integer.parseInt(row[2]);
                    String maritalStatus = row[3];
                    return new HDBOfficer(name, nric, age, maritalStatus);
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid age in user row for NRIC: " + nric);
                } catch (IllegalArgumentException e) {
                    System.out.println("Warning: Invalid user data for NRIC: " + nric + " - " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all BTO applications.
     *
     * @return A list of BTOApplication objects.
     */
    public ArrayList<BTOApplication> getApplications() {
        List<BTOApplication> applications = BTOApplication.getApplications();
        if (applications == null) {
            System.out.println("Warning: Unable to retrieve applications.");
            return new ArrayList<>();
        }
        return new ArrayList<>(applications);
    }
}