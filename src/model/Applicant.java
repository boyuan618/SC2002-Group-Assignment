package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import utils.CSVUtils;

public class Applicant {
    final private String name;
    private String nric;
    private int age;
    private String maritalStatus;

    // Constructor for Applicant
    public Applicant(String name, String nric, int age, String maritalStatus) {
        this.nric = nric;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    // Getter and setter methods for the fields
    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public static Applicant getApplicantByNRIC(String nric) {
        List<String[]> users = CSVUtils.readCSV("data/users.csv");
        for (String[] row : users) {
            if (row[1].equals(nric)) {
                String name = row[0];
                int age = Integer.parseInt(row[2]);
                String maritalStatus = row[3];
                return new Applicant(name, nric, age, maritalStatus);
            }
        }
        return null; // not found
    }

    public List<BTOProject> viewAvailableProjects() {
        List<BTOProject> allProjects = BTOProject.getProjects();
        List<BTOProject> availableProjects = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (BTOProject project : allProjects) {
            if (project.getVisibility().equalsIgnoreCase("on") &&
                    !today.isBefore(project.getOpenDate()) &&
                    !today.isAfter(project.getCloseDate()) &&
                    isEligible(project)) {
                availableProjects.add(project);
            }
        }
        return availableProjects;
    }

    private boolean isEligible(BTOProject project) {
        if (getMaritalStatus().equalsIgnoreCase("Single") && getAge() >= 35) {
            return project.getType1().equalsIgnoreCase("2-Room") ||
                    project.getType2().equalsIgnoreCase("2-Room");
        } else if (getMaritalStatus().equalsIgnoreCase("Married") && getAge() >= 21) {
            return true;
        }
        return false;
    }

    public boolean applyForProject(String projectName, String flatType) {
        if (hasExistingApplication()) {
            System.out.println("You have already applied for a project.");
            return false;
        }

        BTOApplication.applyForFlat(this, BTOProject.getProjectByName(projectName), flatType);
        return true;
    }

    public BTOApplication viewMyApplication() {
        List<BTOApplication> allApplications = BTOApplication.getApplications();
        for (BTOApplication app : allApplications) {
            if (app.getApplicantNRIC().equals(getNric())) {
                return app;
            }
        }
        return null;
    }

    public boolean hasExistingApplication() {
        return viewMyApplication() != null;
    }

    public boolean requestWithdrawal() {
        boolean removed = BTOApplication.deleteApplicationByNRIC(getNric());
        return removed;
    }

    public void submitEnquiry(String project, String title, String detail) {
        int id = Enquiry.getEnquiries().get(Enquiry.getEnquiries().size() - 1).getId() + 1;
        Enquiry enquiry = new Enquiry(id, getNric(), project, title, detail, "");
        Enquiry.appendEnquiry(enquiry);
    }

    public List<Enquiry> viewMyEnquiries() {
        List<Enquiry> allEnquiries = Enquiry.getEnquiries();
        List<Enquiry> myEnquiries = new ArrayList<>();
        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getEnquirerNRIC().equals(getNric())) {
                myEnquiries.add(enquiry);
            }
        }
        return myEnquiries;
    }

    public boolean editEnquiry(int index, String newTitle, String newDetail) {
        boolean status = Enquiry.updateEnquiry(getNric(), index, newTitle, newDetail);

        return status;
    }

    public boolean deleteEnquiry(int index) {
        boolean status = Enquiry.removeEnquiry(getNric(), index);

        return status;
    }

    // Override toString to display the applicant's details
    @Override
    public String toString() {
        return nric + "," + name + "," + age + "," + maritalStatus;
    }
}
