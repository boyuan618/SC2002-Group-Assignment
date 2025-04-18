package model;

public class BTOApplication {
    final private Applicant applicant;
    final private BTOProject project;
    private String status;
    private String flatType;

    public BTOApplication(Applicant applicant, BTOProject project, String status) {
        this.applicant = applicant;
        this.project = project;
        this.status = status;
        this.flatType = null;
    }

    public void updateStatus(String newStatus) {
        if (isValidStatus(newStatus)) {
            this.status = newStatus;
        }
    }

    public void setFlatType(String flatType) {
        if (flatType.equals("2-Room") || flatType.equals("3-Room")) {
            this.flatType = flatType;
        }
    }

    private boolean isValidStatus(String newStatus) {
        return newStatus.equals("Pending") ||
                newStatus.equals("Successful") ||
                newStatus.equals("Unsuccessful") ||
                newStatus.equals("Booked") ||
                newStatus.equals("Withdrawn");
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public String getStatus() {
        return status;
    }

    public String getFlatType() {
        return flatType;
    }
}
