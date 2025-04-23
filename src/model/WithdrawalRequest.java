package model;

public class WithdrawalRequest {
    private String applicantNRIC;
    private String projectName;
    private String flatType;
    private String status; // "Pending", "Approved", "Rejected"

    public WithdrawalRequest(String applicantNRIC, String projectName, String flatType, String status) {
        this.applicantNRIC = applicantNRIC;
        this.projectName = projectName;
        this.flatType = flatType;
        this.status = status;
    }

    public String getApplicantNRIC() { return applicantNRIC; }
    public String getProjectName() { return projectName; }
    public String getFlatType() { return flatType; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return applicantNRIC + "," + projectName + "," + flatType + "," + status;
    }

    public static WithdrawalRequest fromCSV(String[] row) {
        return new WithdrawalRequest(row[0], row[1], row[2], row[3]);
    }
}
