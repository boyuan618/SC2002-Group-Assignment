package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import utils.CSVUtils;
import utils.Validator;

/**
 * Represents an applicant in the HDB BTO Management System.
 * An applicant can apply for BTO projects, submit enquiries, and manage their applications.
 * 
 * @author SC2002Team
 */
public class Applicant {
    final private String name;
    private String nric;
    private int age;
    private String maritalStatus;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");

    /**
     * Constructs an Applicant with the specified details.
     * 
     * @param name The name of the applicant.
     * @param nric The NRIC of the applicant.
     * @param age The age of the applicant.
     * @param maritalStatus The marital status of the applicant ("Single" or "Married").
     * @throws IllegalArgumentException If any input is invalid.
     */
    public Applicant(String name, String nric, int age, String maritalStatus) {
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid name: Name must contain only letters and spaces and cannot be empty.");
        }
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Age must be between 1 and 120.");
        }
        if (maritalStatus == null || 
            !(maritalStatus.equalsIgnoreCase("Single") || maritalStatus.equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }
        this.nric = nric;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the NRIC of the applicant.
     * 
     * @return The NRIC.
     */
    public String getNric() {
        return nric;
    }

    /**
     * Sets the NRIC of the applicant.
     * 
     * @param nric The new NRIC.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public void setNric(String nric) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        this.nric = nric;
    }

    /**
     * Gets the name of the applicant.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the age of the applicant.
     * 
     * @return The age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of the applicant.
     * 
     * @param age The new age.
     * @throws IllegalArgumentException If the age is invalid.
     */
    public void setAge(int age) {
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("Invalid age: Age must be between 1 and 120.");
        }
        this.age = age;
    }

    /**
     * Gets the marital status of the applicant.
     * 
     * @return The marital status.
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the marital status of the applicant.
     * 
     * @param maritalStatus The new marital status.
     * @throws IllegalArgumentException If the marital status is invalid.
     */
    public void setMaritalStatus(String maritalStatus) {
        if (maritalStatus == null || 
            !(maritalStatus.equalsIgnoreCase("Single") || maritalStatus.equalsIgnoreCase("Married"))) {
            throw new IllegalArgumentException("Invalid marital status: Must be 'Single' or 'Married'.");
        }
        this.maritalStatus = maritalStatus;
    }

    /**
     * Retrieves an applicant by their NRIC from the users CSV file.
     * 
     * @param nric The NRIC to search for.
     * @return The Applicant object if found, null otherwise.
     * @throws IllegalArgumentException If the NRIC is invalid.
     */
    public static Applicant getApplicantByNRIC(String nric) {
        if (!Validator.isValidNRIC(nric)) {
            throw new IllegalArgumentException("Invalid NRIC: Must start with S or T, followed by 7 digits and a capital letter.");
        }
        List<String[]> users = CSVUtils.readCSV("data/users.csv");
        for (String[] row : users) {
            if (row.length < 4) {
                continue; // Skip malformed rows
            }
            if (row[1].equals(nric)) {
                try {
                    String name = row[0];
                    int age = Integer.parseInt(row[2]);
                    String maritalStatus = row[3];
                    return new Applicant(name, nric, age, maritalStatus);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing user data for NRIC: " + nric);
                }
            }
        }
        return null; // not found
    }

    /**
     * Retrieves a list of available BTO projects the applicant can apply for or view.
     * Filters projects based on visibility, application dates, eligibility, and room availability.
     * 
     * @return A list of available BTO projects.
     */
    public List<BTOProject> viewAvailableProjects() {
        List<BTOProject> allProjects = BTOProject.getProjects();
        if (allProjects == null) {
            return new ArrayList<>(); // Return empty list if null
        }
        List<BTOProject> availableProjects = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (BTOProject project : allProjects) {
            if (project == null || project.getRooms() == null) {
                continue; // Skip invalid projects
            }
            if (project.getVisibility().equalsIgnoreCase("on")) {
                try {
                    LocalDate openDate = LocalDate.parse(project.getOpenDate(), formatter);
                    LocalDate closeDate = LocalDate.parse(project.getCloseDate(), formatter);
                    // Within Application Date
                    if (!today.isBefore(openDate) && !today.isAfter(closeDate) && isEligible(project)) {
                        // Create a copy to avoid modifying original project
                        BTOProject projectCopy = new BTOProject(
                            project.getProjectName(),
                            project.getNeighborhood(),
                            new ArrayList<>(project.getRooms()),
                            project.getOpenDate(),
                            project.getCloseDate(),
                            project.getManager(), // Include manager
                            project.getOfficerSlot(),
                            project.getOfficerList(),
                            project.getVisibility()
                        );
                        
                        ArrayList<Room> rooms = projectCopy.getRooms();
                        // If Single (Only two room)
                        if (getMaritalStatus().equalsIgnoreCase("Single")) {
                            
                            rooms.removeIf(room -> room.getRoomType().equals("3-Room"));
                            
                        }

                        // Remove Rooms with no availability
                        rooms.removeIf(room -> room.getUnits() <= 0);
                        projectCopy.setRooms(rooms);

                        if (!projectCopy.getRooms().isEmpty()) {
                            availableProjects.add(projectCopy);
                        }
                    } else if (BTOApplication.getApplicationByNRIC(getNric()) != null && BTOApplication.getApplicationByNRIC(getNric()).getProjectName().equals(project.getProjectName())) { // If applicant has applied
                        if (getMaritalStatus().equalsIgnoreCase("Single")) {
                            // Create a copy for viewing applied projects
                            BTOProject projectCopy = new BTOProject(
                                project.getProjectName(),
                                project.getNeighborhood(),
                                new ArrayList<>(project.getRooms()),
                                project.getOpenDate(),
                                project.getCloseDate(),
                                project.getManager(), // Include manager
                                project.getOfficerSlot(),
                                project.getOfficerList(),
                                project.getVisibility()
                            );
                            ArrayList<Room> rooms = projectCopy.getRooms();
                            rooms.removeIf(room -> room.getRoomType().equals("3-Room"));
                            projectCopy.setRooms(rooms);
                            if (!projectCopy.getRooms().isEmpty()) {
                                availableProjects.add(projectCopy);
                            }
                        } else {
                            availableProjects.add(project);
                        }

                    }
                } catch (Exception e) {
                    System.out.println("Error processing project: " + project.getProjectName());
                }
            }

            
        }
        return availableProjects;
    }

    /**
     * Checks if the applicant is eligible to apply for a project based on marital status and age.
     * 
     * @param project The BTO project to check eligibility for.
     * @return True if eligible, false otherwise.
     */
    private boolean isEligible(BTOProject project) {
        if (project == null || project.getRooms() == null) {
            return false;
        }
        if (getMaritalStatus().equalsIgnoreCase("Single") && getAge() >= 35) {
            return project.getRooms().stream().anyMatch(room -> room.getRoomType().equals("2-Room"));
        } else if (getMaritalStatus().equalsIgnoreCase("Married") && getAge() >= 21) {
            return true;
        }
        return false;
    }

    /**
     * Applies for a BTO project with the specified project name and flat type.
     * 
     * @param projectName The name of the project to apply for.
     * @param flatType The flat type to apply for (e.g., "2-Room", "3-Room").
     * @return True if the application is successful, false otherwise.
     * @throws IllegalArgumentException If inputs are invalid.
     */
    public boolean applyForProject(String projectName, String flatType) {
        if (projectName == null || projectName.trim().isEmpty()) {
            System.out.println("Project name cannot be empty.");
            return false;
        }
        if (!Validator.isValidFlatType(flatType)) {
            System.out.println("Invalid flat type: Must be '2-Room' or '3-Room'.");
            return false;
        }
        if (!Validator.isValidProjectName(projectName)) {
            System.out.println("Invalid or non-existent project name.");
            return false;
        }
        if (hasExistingApplication()) {
            System.out.println("You have already applied for a project.");
            return false;
        }

        // Check eligibility for flat type
        if (flatType.equals("3-Room")) {
            if (getMaritalStatus().equalsIgnoreCase("Single") || getAge() < 21) {
                System.out.println("You are not eligible to apply for a 3-Room flat.");
                return false;
            }
        }

        boolean exists = false;
        boolean typeAvail = false;
        for (BTOProject project : viewAvailableProjects()) {
            if (project.getProjectName().equals(projectName)) {
                exists = true;

                for (Room room : project.getRooms()) {
                    if (room.getRoomType().equals(flatType) && room.getUnits() > 0) {
                        typeAvail = true;
                        break;
                    }
                }
                break;
            }
        }
        //Check if Applicant can see
        if (!exists) {
            System.out.println("You are not allowed to apply for this project!");
            return false;
        }

        if (!typeAvail) {
            System.out.println("Chosen flat type is no longer available.");
            return false;
        }

        BTOApplication.applyForFlat(this, BTOProject.getProjectByName(projectName), flatType);
        System.out.println("Application submitted successfully.");
        return true;
    }

    /**
     * Retrieves the applicant's current BTO application, if any.
     * 
     * @return The BTOApplication object if found, null otherwise.
     */
    public BTOApplication viewMyApplication() {
        List<BTOApplication> allApplications = BTOApplication.getApplications();
        if (allApplications == null) {
            return null;
        }
        for (BTOApplication app : allApplications) {
            if (app != null && app.getApplicantNRIC().equals(getNric()) && !app.getStatus().equals("Unsuccessful")) {
                return app;
            }
        }
        return null;
    }

    /**
     * Checks if the applicant has an existing BTO application.
     * 
     * @return True if an application exists, false otherwise.
     */
    public boolean hasExistingApplication() {
        return viewMyApplication() != null;
    }

    /**
     * Submits a withdrawal request for the applicant's current BTO application.
     * 
     * @return True if the withdrawal request is submitted, false if no application exists.
     */
    public boolean requestWithdrawal() {
        BTOApplication myApp = viewMyApplication();
        if (myApp == null) {
            System.out.println("No existing application found.");
            return false;
        }
        
        String[] row = {getNric(), myApp.getProjectObject().getProjectName(), myApp.getFlatType(), "Pending"};
        CSVUtils.appendToCSV("data/withdrawals.csv", row);

        System.out.println("Withdrawal request submitted.");
        return true;
    }

    /**
     * Submits an enquiry for a specific project.
     * 
     * @param project The name of the project.
     * @param title The title of the enquiry.
     * @param detail The details of the enquiry.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public void submitEnquiry(String project, String title, String detail) {
        if (!Validator.isValidProjectName(project)) {
            throw new IllegalArgumentException("Invalid or non-existent project name.");
        }
        if (!Validator.isValidEnquiryTitle(title)) {
            throw new IllegalArgumentException("Invalid enquiry title: Must be non-empty and less than 100 characters.");
        }
        if (!Validator.isValidEnquiryDetail(detail)) {
            throw new IllegalArgumentException("Invalid enquiry detail: Must be non-empty and less than 500 characters.");
        }
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        int id = enquiries.isEmpty() ? 1 : enquiries.get(enquiries.size() - 1).getId() + 1;
        Enquiry enquiry = new Enquiry(id, getNric(), project, title, detail, "");
        Enquiry.appendEnquiry(enquiry);
        System.out.println("Enquiry submitted successfully.");
    }

    /**
     * Retrieves all enquiries submitted by the applicant.
     * 
     * @return A list of the applicant's enquiries.
     */
    public List<Enquiry> viewMyEnquiries() {
        List<Enquiry> allEnquiries = Enquiry.getEnquiries();
        if (allEnquiries == null) {
            return new ArrayList<>();
        }
        List<Enquiry> myEnquiries = new ArrayList<>();
        for (Enquiry enquiry : allEnquiries) {
            if (enquiry != null && enquiry.getEnquirerNRIC().equals(getNric())) {
                myEnquiries.add(enquiry);
            }
        }
        return myEnquiries;
    }

    /**
     * Edits an existing enquiry submitted by the applicant.
     * 
     * @param index The index of the enquiry to edit (0-based).
     * @param newTitle The new title for the enquiry.
     * @param newDetail The new details for the enquiry.
     * @return True if the enquiry is updated successfully, false otherwise.
     * @throws IllegalArgumentException If inputs are invalid.
     */
    public boolean editEnquiry(int index, String newTitle, String newDetail) {
        if (index < 0) {
            throw new IllegalArgumentException("Invalid index: Index must be non-negative.");
        }
        if (!Validator.isValidEnquiryTitle(newTitle)) {
            throw new IllegalArgumentException("Invalid enquiry title: Must be non-empty and less than 100 characters.");
        }
        if (!Validator.isValidEnquiryDetail(newDetail)) {
            throw new IllegalArgumentException("Invalid enquiry detail: Must be non-empty and less than 500 characters.");
        }
        List<Enquiry> myEnquiries = viewMyEnquiries();
        if (index >= myEnquiries.size()) {
            System.out.println("Invalid enquiry index.");
            return false;
        }
        boolean status = Enquiry.updateEnquiry(getNric(), index, newTitle, newDetail);
        if (status) {
            System.out.println("Enquiry updated successfully.");
        } else {
            System.out.println("Failed to update enquiry.");
        }
        return status;
    }

    /**
     * Deletes an existing enquiry submitted by the applicant.
     * 
     * @param index The index of the enquiry to delete (0-based).
     * @return True if the enquiry is deleted successfully, false otherwise.
     * @throws IllegalArgumentException If the index is invalid.
     */
    public boolean deleteEnquiry(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Invalid index: Index must be non-negative.");
        }
        boolean status = Enquiry.removeEnquiry(getNric(), index);
        if (status) {
            System.out.println("Enquiry deleted successfully.");
        } else {
            System.out.println("Failed to delete enquiry.");
        }
        return status;
    }

    /**
     * Returns a string representation of the applicant's details for CSV storage.
     * 
     * @return A comma-separated string of the applicant's details.
     */
    @Override
    public String toString() {
        return nric + "," + name + "," + age + "," + maritalStatus;
    }

}
