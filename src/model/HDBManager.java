package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HDBManager extends User {
    public static Map<String, BTOProject> LiveDatabase = new HashMap<>(); /*for all managers to access the data*/
    public static Map<String, List<String>> DisplayAllProjects = new HashMap<>(); /*View all projects, need to be changed*/
    public static Map<String, List<String>> OfficerPendingList = new HashMap<>(); /*View all pendeng officer registrations*/
    public static Map<String, List<String>> OfficerApprovedList = new HashMap<>(); /*view all approved officer registrations*/
    

    public HDBManager(){super();};

    public HDBManager(String a, String b, String c, int d, boolean e){
        super(a,b,c,d,e); DisplayAllProjects.put(a, new ArrayList<>());}

    public ProjectManager createListing(String projectName, String neighborhood, String type1, int units1, int price1,
        String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
        String manager, int officerSlot, String officerList){

        BTOProject newProject=new BTOProject(projectName, neighborhood, type1, units1, price1, type2, units2, price2, openDate,
        closeDate, manager, officerSlot, officerList);

        LiveDatabase.put(newProject.getProjectName(), newProject);
        DisplayAllProjects.get(this.name).add(newProject.getProjectName());

        ProjectManager boyuan= new ProjectManager(this.name, this.NRIC, this.password, this.age, this.maritalStatus, newProject, true, true);
        return boyuan;}

    public void editListing(){};

    public void deleteListing(String projectName){
        if(LiveDatabase.containsKey(projectName)){
            LiveDatabase.remove(projectName);}

        if(DisplayAllProjects.containsKey(projectName)){
            DisplayAllProjects.remove(projectName);
            System.out.println("Project Listing has been successfully deleted!");
            return;}

        System.out.println("Project Listing does not exist in the first place!");
        return;}
       
    
    public void viewAllProjects(){
        for (Map.Entry<String, List<String>> entry : DisplayAllProjects.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Name: " + key + ", Project(s): " + values);}
            return;}

    
    public void viewMyProjects(){
        List<String> values = DisplayAllProjects.get(this.name);
        System.out.println("Name: " + this.name + ", Project(s): " + values);
        return;}

    
    public void viewPendingOfficers(){
        for (Map.Entry<String, List<String>> entry : OfficerPendingList.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Project name: " + key + ", Officer name: " + values);
            return;}}

    public void viewApprovedOfficers(){
        for (Map.Entry<String, List<String>> entry : OfficerApprovedList.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                System.out.println("Project name: " + key + ", Officer name: " + values);
                return;}}

    public boolean addressApplicantApplication(Applicant a, String projectName, String requestedType){
        if(!LiveDatabase.containsKey(projectName)){
            System.out.println("Requested project does not exist!");
            return false;}
        if(requestedType==(LiveDatabase.get(projectName)).getType1()){
            if((LiveDatabase.get(projectName)).getUnits1()==0){
                System.out.println("No more available units of the requested type.");
                return false;}
            else{
                LiveDatabase.get(projectName).editApplied(a,requestedType,1);
                return true;}}

        else if(requestedType==(LiveDatabase.get(projectName)).getType2()){
            if((LiveDatabase.get(projectName)).getUnits2()==0){
                System.out.println("No more available units of the requested type.");
                return false;}
            else{
                LiveDatabase.get(projectName).editApplied(a,requestedType,1);
                return true;}}
        
        else{
            System.out.println("Requested flat type is not valid!");
            return false;}}
    

    public void addressApplicantWithdrawal(Applicant a, String projectName, boolean allow){
        if(!LiveDatabase.containsKey(projectName)){
            System.out.println("Project does not exist!");
            return;}
        
        LiveDatabase.get(projectName).editApplied(a,null,-1); /*null parameter will not be used*/
        return;} 

    public void generateReport(){}

    }
