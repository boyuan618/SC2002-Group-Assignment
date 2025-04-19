package model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class HDBOfficer extends Applicant implements EnquiryInt {
    private BTOProject projectAssigned;

    // Constructor
    public HDBOfficer(String name, String nric, int age, String maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.projectAssigned = null;

        List<BTOProject> projects = BTOProject.getProjects();
        for (BTOProject project : projects) {

            if (Arrays.asList(project.getOfficerList()).contains(name)) {
                this.projectAssigned = project;
            }
        }

    }

    // Getters and setters
    public BTOProject getProjectAssigned() {
        return projectAssigned;
    }

    public void setProjectAssigned(BTOProject projectAssigned) {
        this.projectAssigned = projectAssigned;
    }

    // Method to check if an officer can register for a project
    public boolean canRegisterForProject(BTOProject project) {
        LocalDate today = LocalDate.now(); // Now we have LocalDate properly imported

        // Check if the officer is already handling another project during the
        // application period
        if (this.projectAssigned != null && project.isWithinApplicationPeriod(today)) {
            System.out.println("You are already registered for another project during this period.");
            return false;
        }

        // Ensure the officer has not already applied for this project as an applicant
        List<BTOApplication> applications = BTOApplication.getApplications();

        for (BTOApplication app : applications) {
            if (app.getApplicantNRIC().equals(this.getNric())
                    && app.getProjectName().equals(project.getProjectName())) {
                System.out.println("You have already applied for this project as an applicant.");
                return false;
            }
        }

        return true;
    }

    // Method for the officer to register for a project
    public void registerForProject(BTOProject project) {
        if (canRegisterForProject(project)) {
            // Register the officer for the project
            this.projectAssigned = project;
            // Add the officer to the project officer list
            project.addOfficer(this.getNric());
            System.out.println("You have been successfully registered as a HDB Officer for the project: "
                    + project.getProjectName());
        }
    }

    // Method to view project details that the officer is handling
    @Override
    public void viewAllProjectDetails() {
        if (projectAssigned != null) {
            System.out.println("\n===== Officer Handling Project =====");
            System.out.println("Project Name       : " + projectAssigned.getProjectName());
            System.out.println("Neighborhood       : " + projectAssigned.getNeighborhood());
            System.out.println("Type 1             : " + projectAssigned.getType1() + " | Units: "
                    + projectAssigned.getUnits1() + " | Price: $" + projectAssigned.getPrice1());
            System.out.println("Type 2             : " + projectAssigned.getType2() + " | Units: "
                    + projectAssigned.getUnits2() + " | Price: $" + projectAssigned.getPrice2());
            System.out.println("Opening Date       : " + projectAssigned.getOpenDate());
            System.out.println("Closing Date       : " + projectAssigned.getCloseDate());
            System.out.println("Manager            : " + projectAssigned.getManager());
            System.out.println("Officer Slots      : " + projectAssigned.getOfficerSlot());
            System.out.println("Assigned Officers  : " + String.join(",", projectAssigned.getOfficerList()));
        } else {
            System.out.println("You are not handling any project currently.");
        }
    }

    // Method to handle flat selection and update applicant details for successful
    // applicants
    public void handleFlatSelection(BTOApplication application, String flatType) {
        if (application.getStatus().equals("Successful")) {
            // Update the applicant's flat type
            application.setFlatType(flatType);
            // Decrease the number of available units of the selected flat type
            if (flatType.equals("2-Room")) {
                projectAssigned.setUnits1(projectAssigned.getUnits1() - 1);
            } else {
                projectAssigned.setUnits2(projectAssigned.getUnits2() - 1);
            }
            // Update the applicant's status to "Booked"
            application.setStatus("Booked");
            BTOApplication.updateBTOApplication(application);

            // Create and generate a receipt for the applicant
            Receipt.fromBTOApplication(application).printReceipt();

        } else {
            System.out.println("This application is not successful yet.");
        }
    }

}
