package controller;

import model.Applicant;
import model.BTOProject;
import model.BTOApplication;
import model.Enquiry;
import model.User;

import java.util.List;

public class ApplicantController {
    final public User user;

    public ApplicantController(User user) {
        this.user = user;
    }

    // View available projects based on eligibility
    public List<BTOProject> viewAvailableProjects() {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.viewAvailableProjects();
        }
        System.out.println("Applicant not found.");
        return null;
    }

    // Apply for a specific BTO project and flat type
    public boolean applyForProject(String projectName, String flatType) {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.applyForProject(projectName, flatType);
        }
        System.out.println("Applicant not found.");
        return false;
    }

    // View applicant's current application status
    public BTOApplication viewMyApplication() {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.viewMyApplication();
        }
        System.out.println("Applicant not found.");
        return null;
    }

    // Request to withdraw an application
    public boolean requestWithdrawal() {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.requestWithdrawal();
        }
        System.out.println("Applicant not found.");
        return false;
    }

    // Submit a new enquiry
    public boolean submitEnquiry(String project, String title, String detail) {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            applicant.submitEnquiry(project, title, detail);
            return true;
        }
        System.out.println("Applicant not found.");
        return false;
    }

    // View all enquiries made by the applicant
    public List<Enquiry> viewMyEnquiries() {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.viewMyEnquiries();
        }
        System.out.println("Applicant not found.");
        return null;
    }

    // Edit an enquiry made by the applicant
    public boolean editEnquiry(int enquiryIndex, String newTitle, String newDetail) {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.editEnquiry(enquiryIndex, newTitle, newDetail);
        }
        System.out.println("Applicant not found.");
        return false;
    }

    // Delete an enquiry made by the applicant
    public boolean deleteEnquiry(int enquiryIndex) {
        Applicant applicant = Applicant.getApplicantByNRIC(user.getNRIC());
        if (applicant != null) {
            return applicant.deleteEnquiry(enquiryIndex);
        }
        System.out.println("Applicant not found.");
        return false;
    }
}
