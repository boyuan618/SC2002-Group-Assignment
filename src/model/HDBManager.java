package model;

import java.time.LocalDate;

public class HDBManager extends User {
    private boolean inProject;
    private BTOProject currentProject;
    private boolean visibility; /* placed inside BTO manager cuz i think BTOProject got fixed formatting alr */
    private Applicant[] applicantList; /* need the Applicant Class */

    public HDBManager(String a, String b, int c, boolean d) {
        super(a, b, c, d);
        this.inProject = false;
        currentProject = new BTOProject();
        visibility = false;
        applicantList = null;
    } /* watch out for compilation error */

    public void createProject(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
            String manager, int officerSlot, String officerList) {

        currentProject = new BTOProject(projectName, neighborhood, type1, units1, price1, type2, units2, price2,
                openDate,
                closeDate, manager, officerSlot, officerList);

        inProject = true;

        System.out.println("Project successfully created!");
    }

    public void editProjectListing() {
    } /* insert code */

    public void deleteProjectListing() {
    } /* insert code */

    public void toggle() {
        if (visibility == true) {
            visibility = false;
            return;
        }
        visibility = true;
    }

    public void viewProjects() {
    } /* insertCode here */

    public void respondToOfficerRegistration(String name) {
        if (inProject == false) {
            System.out.println("Manager currently does not have a project.");
            return;
        }

        if (currentProject.getOfficerSlot() != 0) {
            currentProject.addOfficer(name);
            System.out.println("Officer successfully registered!");
            return;
        }

        System.out.println("No more officer slots for the project. Registration denied.");
    }

    public boolean respondToApplicantBTOApplication(String requestedType) {
        if (inProject == false) {
            System.out.println("Manager currently does not have a project.");
            return false;
        }

        if (requestedType.equals(currentProject.getType1())) {
            if (currentProject.getUnits1() == 0) {
                System.out.println("No more units of your requested type is available. Sorry!");
                return false;
            }
            /*
             * code to add applicant into applicant list, need Applicant class. Need to
             * include flat type too
             */
            System.out.println("Application is successful!");
            return true;
        } else if (requestedType.equals(currentProject.getType2())) {
            if (currentProject.getUnits2() == 0) {
                System.out.println("No more units of your requested type is available. Sorry!");
                return false;
            }
            /*
             * code to add applicant into applicant list, need Applicant class. Need to
             * include flat type too
             */
            System.out.println("Application is successful!");
            return true;
        } else {
            System.out.println("Please request for a valid flat type.");
            return false;
        }
    }

    public void generateListOfApplicants() {
    } /* insert code here */

    public void viewEnquiry(BTOProject project) {
    } /* insert code here */

    public void respondToEnquiry() {
    } /* insert code here, use currentProject to code */

}
