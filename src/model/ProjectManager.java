package model;

import java.util.*;

public class ProjectManager extends HDBManager {

    public static final String ENQUIRIES_CSV = "data/FlatEnquiries.csv";
    public static final String APPLICATIONS_CSV = "data/BTOApplications.csv";

    private BTOProject project; // Project that this manager is handling

    public ProjectManager(String name, String nric, String password, int age, String maritalStatus) {
        super(name, nric, password, age, maritalStatus);

    }

    // View pending officer registrations for this project
    public void viewPendingOfficerRegistrations() {
        List<OfficerApplication> officerApplications = OfficerApplication.readOfficerApplications();
        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp.getProject().equals(project.getProjectName()) && officerApp.getStatus().equals("Pending")) {
                System.out.println(officerApp);
            }
        }
    }

    // Approve or reject officer registration
    public void approveRejectOfficer(String officerNRIC, boolean approve) {
        List<OfficerApplication> officerApplications = OfficerApplication.readOfficerApplications();
        for (OfficerApplication officerApp : officerApplications) {
            if (officerApp.getProject().equals(project.getProjectName())
                    && officerApp.getOfficerNRIC().equals(officerNRIC)) {
                if (approve) {
                    if (project.getOfficerSlot() > 0) {
                        officerApp.setStatus("Approved");
                        project.addOfficer(this.name);
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
    public void approveRejectApplication(String applicantNRIC, String flatType, boolean approve) {
        List<BTOApplication> applications = BTOApplication.getApplications(); // Assuming this reads
                                                                              // from
        // BTOApplications.csv
        for (BTOApplication app : applications) {
            if (app.getProjectName().equals(project.getProjectName()) && app.getApplicantNRIC().equals(applicantNRIC)) {
                if (approve) {
                    if (flatType.equals(project.getType1()) && project.getUnits1() > 0) {
                        app.setStatus("Approved");
                        project.setUnits1(project.getUnits1() - 1); // Decrease units of flat type 1
                        BTOApplication.updateBTOApplication(app);
                        System.out.println("Applicant approved.");
                    } else if (flatType.equals(project.getType2()) && project.getUnits2() > 0) {
                        app.setStatus("Approved");
                        project.setUnits2(project.getUnits2() - 1); // Decrease units of flat type 2
                        BTOApplication.updateBTOApplication(app);
                        System.out.println("Applicant approved.");
                    } else {
                        System.out.println("Not enough units available for requested flat type.");
                    }
                } else {
                    app.setStatus("Rejected");
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
        List<BTOApplication> applications = BTOApplication.getApplications();
        for (BTOApplication app : applications) {
            if (app.getProjectName().equals(project.getProjectName()) && app.getApplicantNRIC().equals(applicantNRIC)) {
                if (approve) {
                    app.setStatus("Withdrawn");
                    if (app.getFlatType().equals(project.getType1())) {
                        project.setUnits1(project.getUnits1() + 1); // Increase units of flat type 1
                    } else if (app.getFlatType().equals(project.getType2())) {
                        project.setUnits2(project.getUnits2() + 1); // Increase units of flat type 2
                    }
                    BTOApplication.updateBTOApplication(app); // Update withdrawal status in CSV
                    System.out.println("Applicant withdrawal approved.");
                } else {
                    app.setStatus("Pending"); // Or any other appropriate status
                    BTOApplication.updateBTOApplication(app); // Update status in CSV
                    System.out.println("Applicant withdrawal rejected.");
                }
                return;
            }
        }
        System.out.println("Application not found.");
    }

}