package model;

import java.util.*;

public class ProjectManager extends HDBManager {

    public static final String ENQUIRIES_CSV = "data/FlatEnquiries.csv";
    public static final String APPLICATIONS_CSV = "data/BTOApplications.csv";

    private BTOProject project; // Project that this manager is handling

    public ProjectManager(String name, String nric, String password, int age, String maritalStatus) {
        super(name, nric, password, age, maritalStatus);
        this.project = null;

        List<BTOProject> projects = BTOProject.getProjects();
        for (BTOProject projectlist : projects) {
            if (Arrays.asList(projectlist.getManager()).contains(name)) {
                this.project = projectlist;
                break;
            }
        }

    }

    // View pending officer registrations for this project
    public void viewPendingOfficerRegistrations() {
        if (project == null) {
            System.out.println("You are not in charge of any project!");
            return;
        }
        List<OfficerApplication> officerApplications = OfficerApplication.readOfficerApplications();
        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp.getProject().equals(project.getProjectName()) && officerApp.getStatus().equals("Pending")) {
                System.out.println(officerApp.toString());
            }
        }
    }

    // Approve or reject officer registration
    public void approveRejectOfficer(String officerNRIC, boolean approve) {
        if (project == null) {
            System.out.println("You are not in charge of any project!");
            return;
        }
        List<OfficerApplication> officerApplications = OfficerApplication.readOfficerApplications();
        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp.getProject().equals(project.getProjectName())
                    && officerApp.getOfficerNRIC().equals(officerNRIC)) {
                if (approve) {
                    if (project.getOfficerSlot() > 0) {
                        officerApp.setStatus("Approved");
                        project.addOfficer(HDBOfficer.getApplicantByNRIC(officerNRIC).getName());
                        BTOProject.editProject(project);
                        OfficerApplication.updateOfficerApplication(officerApp); // Update the application status in CSV
                        System.out.println("Officer approved for the project.");

                    } else {
                        System.out.println("No officer slots available.");
                    }
                } else {
                    officerApp.setStatus("Rejected");
                    OfficerApplication.updateOfficerApplication(officerApp); // Update rejection in CSV
                    System.out.println("Officer registration rejected.");
                }
                return;
            }
        }
        System.out.println("Officer application not found.");
    }

    // Approve or reject an applicant's BTO application
    public void approveRejectApplication(String applicantNRIC, boolean approve) {
        if (project == null) {
            System.out.println("You are not in charge of any project!");
            return;
        }
        List<BTOApplication> applications = BTOApplication.getApplications(); // Assuming this reads
                                                                              // from
        // BTOApplications.csv
        for (BTOApplication app : applications) {
            if (app.getProjectName().equals(project.getProjectName()) && app.getApplicantNRIC().equals(applicantNRIC)
                    && app.getStatus().equals("Pending")) {
                if (approve) {
                    for (Room type : project.getRooms()) {
                        if (type.getRoomType().equals(app.getFlatType()) && type.getUnits() > 0) {
                            app.setStatus("Successful");
                            BTOApplication.updateBTOApplication(app);
                            System.out.println("Applicant approved.");
                        } else {
                            System.out.println("Not enough units available for requested flat type.");
                        }
                    }

                } else {
                    app.setStatus("Unsuccessful");
                    BTOApplication.updateBTOApplication(app); // Update rejection in CSV
                    System.out.println("Applicant rejected.");
                }
                return;
            }
        }
        System.out.println("Application not found.");
    }

    // Approve or reject withdrawal request from an applicant
    public void approveRejectWithdrawal(String applicantNRIC, boolean approve) {
        if (project == null) {
            System.out.println("You are not in charge of any project!");
            return;
        }
        List<BTOApplication> applications = BTOApplication.getApplications();
        for (BTOApplication app : applications) {
            if (app.getProjectName().equals(project.getProjectName()) && app.getApplicantNRIC().equals(applicantNRIC)) {
                if (approve) {
                    app.setStatus("Unsuccessful");
                    for (Room type : project.getRooms()) {
                        if (type.getRoomType().equals(app.getFlatType())) {
                            type.setUnits(type.getUnits() + 1);
                            BTOProject.editProject(project); // Write Updates
                            break;
                        }
                    }
                    BTOApplication.updateBTOApplication(app); // Update withdrawal status in CSV
                    System.out.println("Applicant withdrawal approved.");
                } else {
                    System.out.println("Applicant withdrawal rejected.");
                }
                return;
            }
        }
        System.out.println("Application not found.");
    }

    public BTOProject getProjectManaging() {
        return project;
    }
}