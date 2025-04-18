package model;

public class HDBOfficer extends User {
    private String assignedProject;

    public HDBOfficer(String nric, String password, int age, String maritalStatus, String assignedProject) {
        super(nric, password, "HDBOfficer", age, maritalStatus);
        this.assignedProject = assignedProject;
    }

    public String getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(String assignedProject) {
        this.assignedProject = assignedProject;
    }

    @Override
    public String toString() {
        return super.toString() + "," + assignedProject;
    }
}
