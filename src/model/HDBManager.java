package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import utils.CSVUtils;
import utils.Validator;

/**
 * Represents an HDB Manager in the HDB BTO Management System, responsible for managing projects,
 * handling withdrawal requests, and responding to enquiries.
 * Extends User and implements EnquiryInt for enquiry-related operations.
 *
 * @author SC2002Team
 */
public class HDBManager extends User implements EnquiryInt {

    public static final String PROJECT_CSV = "data/ProjectList.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");

    /**
     * Constructs an HDBManager with the specified details.
     *
     * @param name The manager's name.
     * @param nric The manager's NRIC.
     * @param password The manager's password.
     * @param age The manager's age.
     * @param maritalStatus The manager's marital status.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public HDBManager(String name, String nric, String password, int age, String maritalStatus) {
        super(name, nric, password, age, maritalStatus, "HDBManager");
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name: Must contain only letters and spaces and cannot be empty.");
        }
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (password == null || password.trim().isEmpty() || password.length() > 50) {
            throw new IllegalArgumentException("Invalid password: Must be non-empty and up to 50 characters.");
        }
        if (age < 18 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Must be between 18 and 120.");
        }
        if (maritalStatus == null || (!maritalStatus.equalsIgnoreCase("Single") && !maritalStatus.equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }
    }

    /**
     * Creates a new BTO project listing and saves it to the CSV file.
     *
     * @param projectName The name of the project.
     * @param neighborhood The neighborhood of the project.
     * @param rooms The list of room types.
     * @param openDate The application opening date (M/dd/yyyy).
     * @param closeDate The application closing date (M/dd/yyyy).
     * @param officerSlot The number of officer slots.
     * @param officerList A comma-separated list of officer names.
     * @param visibility The visibility status ("on" or "off").
     * @return The created BTOProject, or null if creation fails.
     * @throws IllegalArgumentException If any input is invalid or the manager is already managing a project in the period.
     */
    public BTOProject createListing(String projectName, String neighborhood, ArrayList<Room> rooms, String openDate,
            String closeDate, int officerSlot, String officerList, String visibility) {
        if (projectName == null || projectName.trim().isEmpty()) {
            System.out.println("Error: Project name cannot be empty.");
            return null;
        }
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            System.out.println("Error: Neighborhood cannot be empty.");
            return null;
        }
        if (rooms == null || rooms.isEmpty()) {
            System.out.println("Error: Rooms list cannot be null or empty.");
            return null;
        }
        for (Room room : rooms) {
            if (room == null || !Validator.isValidFlatType(room.getRoomType()) || room.getUnits() < 0 || room.getPrice() < 0) {
                System.out.println("Error: Invalid room data.");
                return null;
            }
        }
        if (!Validator.isValidDate(openDate) || !Validator.isValidDate(closeDate)) {
            System.out.println("Error: Invalid date format. Must be M/dd/yyyy.");
            return null;
        }
        LocalDate open = LocalDate.parse(openDate, formatter);
        LocalDate close = LocalDate.parse(closeDate, formatter);
        if (!open.isBefore(close)) {
            System.out.println("Error: Open date must be before close date.");
            return null;
        }
        if (officerSlot < 0) {
            System.out.println("Error: Officer slots cannot be negative.");
            return null;
        }
        if (!Validator.isValidCommaSeparatedList(officerList)) {
            System.out.println("Error: Invalid officer list. Must be a comma-separated list of valid names or empty.");
            return null;
        }
        if (!Validator.isValidVisibility(visibility)) {
            System.out.println("Error: Invalid visibility. Must be 'on' or 'off'.");
            return null;
        }
        if (isManagingAnotherProjectInPeriod(open, close)) {
            System.out.println("Error: You are already managing a project in this application period.");
            return null;
        }
        for (BTOProject project : BTOProject.getProjects()) {
            if (project != null && project.getProjectName().equals(projectName)) {
                System.out.println("Error: Duplicate project name: " + projectName + ". Please ensure name is unique.");
                return null;
            }
        }

        try {
            BTOProject newProject = new BTOProject(projectName, neighborhood, rooms, openDate, closeDate, this.getName(),
                    officerSlot, officerList, visibility);
            BTOProject.addProject(newProject);
            System.out.println("✅ Project created successfully: " + projectName);
            return newProject;
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if the manager is already managing another project within the given application period.
     *
     * @param openDate The opening date of the new project.
     * @param closeDate The closing date of the new project.
     * @return True if there is an overlapping project, false otherwise.
     * @throws IllegalArgumentException If dates are null or invalid.
     */
    private boolean isManagingAnotherProjectInPeriod(LocalDate openDate, LocalDate closeDate) {
        if (openDate == null || closeDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        List<BTOProject> projects = BTOProject.getProjects();
        if (projects == null) {
            return false;
        }
        for (BTOProject project : projects) {
            if (project == null || project.getManager() == null) {
                continue;
            }
            if (project.getManager().equals(this.getName())) {
                try {
                    LocalDate projectOpen = LocalDate.parse(project.getOpenDate(), formatter);
                    LocalDate projectClose = LocalDate.parse(project.getCloseDate(), formatter);
                    if (!openDate.isAfter(projectClose) && !closeDate.isBefore(projectOpen)) {
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Invalid dates for project " + project.getProjectName());
                }
            }
        }
        return false;
    }

    /**
     * Edits an existing BTO project and updates the CSV file.
     *
     * @param project The updated project object.
     * @throws IllegalArgumentException If the project is invalid or not managed by this manager.
     */
    public void editProject(BTOProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (getProjectFromCSV(project.getProjectName()) == null) {
            throw new IllegalArgumentException("Project does not exist: " + project.getProjectName());
        }
        if (!project.getManager().equals(this.getName())) {
            throw new IllegalArgumentException("You are not the manager of project: " + project.getProjectName());
        }
        try {
            BTOProject.editProject(project);
            System.out.println("✅ Project updated successfully: " + project.getProjectName());
        } catch (RuntimeException e) {
            System.out.println("Error updating project: " + e.getMessage());
        }
    }

    /**
     * Deletes a project from the CSV file.
     *
     * @param projectName The name of the project to delete.
     * @throws IllegalArgumentException If the project name is invalid or not managed by this manager.
     */
    public void deleteListing(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        BTOProject project = getProjectFromCSV(projectName);
        if (project == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName);
        }
        if (!project.getManager().equals(this.getName())) {
            throw new IllegalArgumentException("You are not the manager of project: " + projectName);
        }
        try {
            BTOProject.deleteProject(projectName);
            System.out.println("✅ Project deleted successfully: " + projectName);
        } catch (RuntimeException e) {
            System.out.println("Error deleting project: " + e.getMessage());
        }
    }

    /**
     * Toggles the visibility of a project ("on" or "off").
     *
     * @param projectName The name of the project.
     * @param visibility The new visibility status ("on" or "off").
     * @throws IllegalArgumentException If the project name or visibility is invalid or not managed by this manager.
     */
    public void toggleVisibility(String projectName, String visibility) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        if (!Validator.isValidVisibility(visibility)) {
            throw new IllegalArgumentException("Invalid visibility: Must be 'on' or 'off'.");
        }
        BTOProject project = getProjectFromCSV(projectName);
        if (project == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName);
        }
        if (!project.getManager().equals(this.getName())) {
            throw new IllegalArgumentException("You are not the manager of project: " + projectName);
        }
        try {
            project.setVisibility(visibility);
            editProject(project);
            System.out.println("✅ Visibility updated to " + visibility + " for project: " + projectName);
        } catch (IllegalArgumentException e) {
            System.out.println("Error updating visibility: " + e.getMessage());
        }
    }

    /**
     * Retrieves all pending withdrawal requests from the CSV file.
     *
     * @return A list of pending WithdrawalRequest objects.
     */
    public static List<WithdrawalRequest> viewPendingWithdrawals() {
        List<String[]> rows = CSVUtils.readCSV("data/withdrawals.csv");
        List<WithdrawalRequest> pending = new ArrayList<>();
        if (rows == null) {
            System.out.println("Warning: Unable to read withdrawals CSV.");
            return pending;
        }
        for (String[] row : rows) {
            try {
                WithdrawalRequest req = WithdrawalRequest.fromCSV(row);
                if (req != null && req.getStatus() != null && req.getStatus().equalsIgnoreCase("Pending")) {
                    pending.add(req);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: Invalid withdrawal request in CSV: " + String.join(",", row));
            }
        }
        
        return pending;

    }

    /**
     * Approves a withdrawal request and deletes the associated application.
     *
     * @param req The withdrawal request to approve.
     * @throws IllegalArgumentException If the request is invalid or not pending.
     */
    public static void approveWithdrawal(WithdrawalRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Withdrawal request cannot be null.");
        }
        if (!Validator.isValidNRIC(req.getApplicantNRIC())) {
            throw new IllegalArgumentException("Invalid NRIC in withdrawal request: " + req.getApplicantNRIC());
        }
        if (!req.getStatus().equalsIgnoreCase("Pending")) {
            throw new IllegalArgumentException("Withdrawal request is not pending: " + req.getStatus());
        }
        try {
            BTOApplication.deleteApplicationByNRIC(req.getApplicantNRIC());
            updateWithdrawalStatus(req.getApplicantNRIC(), "Approved");
            System.out.println("✅ Withdrawal approved for NRIC: " + req.getApplicantNRIC());
        } catch (RuntimeException e) {
            System.out.println("Error approving withdrawal: " + e.getMessage());
        }
    }

    /**
     * Rejects a withdrawal request.
     *
     * @param req The withdrawal request to reject.
     * @throws IllegalArgumentException If the request is invalid or not pending.
     */
    public static void rejectWithdrawal(WithdrawalRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Withdrawal request cannot be null.");
        }
        if (!Validator.isValidNRIC(req.getApplicantNRIC())) {
            throw new IllegalArgumentException("Invalid NRIC in withdrawal request: " + req.getApplicantNRIC());
        }
        if (!req.getStatus().equalsIgnoreCase("Pending")) {
            throw new IllegalArgumentException("Withdrawal request is not pending: " + req.getStatus());
        }
        try {
            updateWithdrawalStatus(req.getApplicantNRIC(), "Rejected");
            System.out.println("✅ Withdrawal rejected for NRIC: " + req.getApplicantNRIC());
        } catch (RuntimeException e) {
            System.out.println("Error rejecting withdrawal: " + e.getMessage());
        }
    }

    /**
     * Updates the status of a withdrawal request in the CSV file.
     *
     * @param nric The NRIC of the applicant.
     * @param newStatus The new status ("Approved" or "Rejected").
     * @throws IllegalArgumentException If the NRIC or status is invalid.
     */
    private static void updateWithdrawalStatus(String nric, String newStatus) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: " + nric);
        }
        if (newStatus == null || (!newStatus.equalsIgnoreCase("Approved") && !newStatus.equalsIgnoreCase("Rejected"))) {
            throw new IllegalArgumentException("Invalid status: Must be 'Approved' or 'Rejected'.");
        }
        List<String[]> rows = CSVUtils.readCSV("data/withdrawals.csv");
        if (rows == null) {
            throw new RuntimeException("Unable to read withdrawals CSV.");
        }
        boolean updated = false;
        for (String[] row : rows) {
            if (row.length >= 4 && row[0].equals(nric) && row[3].equalsIgnoreCase("Pending")) {
                row[3] = newStatus;
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new IllegalArgumentException("No pending withdrawal found for NRIC: " + nric);
        }
        try {
            CSVUtils.writeCSV("data/withdrawals.csv", rows);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error writing to withdrawals CSV: " + e.getMessage());
        }
    }

    /**
     * Retrieves all projects managed by this manager.
     *
     * @return A list of BTOProject objects managed by this manager.
     */
    public List<BTOProject> viewMyProjects() {
        List<BTOProject> projects = BTOProject.getProjects();
        List<BTOProject> myProjects = new ArrayList<>();
        if (projects == null) {
            System.out.println("Warning: Unable to retrieve projects.");
            return myProjects;
        }
        String managerName = this.getName();
        if (managerName == null) {
            System.out.println("Error: Manager name is null.");
            return myProjects;
        }
        for (BTOProject project : projects) {
            if (project != null && project.getManager() != null && project.getManager().equals(managerName)) {
                myProjects.add(project);
            }
        }
        return myProjects;
    }

    /**
     * Retrieves a project by name from the CSV file.
     *
     * @param projectName The name of the project.
     * @return The BTOProject object, or null if not found.
     * @throws IllegalArgumentException If the project name is invalid.
     */
    private BTOProject getProjectFromCSV(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        List<BTOProject> projects = BTOProject.getProjects();
        if (projects == null) {
            System.out.println("Warning: Unable to retrieve projects.");
            return null;
        }
        for (BTOProject project : projects) {
            if (project != null && project.getProjectName() != null && project.getProjectName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    /**
     * Displays enquiries for the specified project and allows the manager to reply to them.
     * Only enquiries for projects managed by this manager are shown.
     * Saves replies to the enquiry list if changes are made.
     *
     * @param sc The Scanner object for reading user input.
     * @param assignedProject The name of the project to view enquiries for.
     * @throws IllegalArgumentException If the scanner is null or the project is invalid or not managed by this manager.
     */
    @Override
    public void viewAndReplyEnquiries(Scanner sc, String assignedProject) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        if (assignedProject == null || assignedProject.trim().isEmpty()) {
            System.out.println("Error: Project name cannot be empty.");
            return;
        }
        assignedProject = assignedProject.trim();
        BTOProject project = getProjectFromCSV(assignedProject);
        if (project == null) {
            System.out.println("Error: Project not found: " + assignedProject);
            return;
        }
        if (!project.getManager().equals(this.getName())) {
            System.out.println("Error: You are not the manager of project: " + assignedProject);
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
                    try {
                        e.setResponse(reply);
                        modified = true;
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Error setting response: " + ex.getMessage());
                    }
                }
            }
        }

        if (found && modified) {
            try {
                Enquiry.writeEnquiries(enquiries);
                System.out.println("✅ Replies saved.");
            } catch (RuntimeException e) {
                System.out.println("Error saving replies: " + e.getMessage());
            }
        } else if (found && !modified) {
            System.out.println("No replies were added.");
        } else {
            System.out.println("No enquiries found for project: " + assignedProject);
        }
    }
}