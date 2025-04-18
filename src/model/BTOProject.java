package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BTOProject {
    private String projectName;
    private String neighborhood;
    private String type1;
    private int units1;
    private int price1;
    private String type2;
    private int units2;
    private int price2;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String manager;
    private int officerSlot;
    private String[] officerList;
    public Map<Applicant, String> Applied = new HashMap<>();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public BTOProject() {
    }; /* defaulter object */

    public BTOProject(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
            String manager, int officerSlot, String originalList) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.type1 = type1;
        this.units1 = units1;
        this.price1 = price1;
        this.type2 = type2;
        this.units2 = units2;
        this.price2 = price2;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.manager = manager;
        this.officerSlot = officerSlot; /* i interpreted this as the number of officer slots available */
        this.officerList = new String[10]; /* 10 is the max number of officers as given */
        String[] splitParts = originalList.split(",");
        for (int i = 0; i < splitParts.length && i < 10; i++) {
            officerList[i] = splitParts[i];
        } /*
           * to change our csv string input of the officers into a String Array for easier
           * use
           */
    }

    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getType1() {
        return type1;
    }

    public int getUnits1() {
        return units1;
    }

    public int getPrice1() {
        return price1;
    }

    public String getType2() {
        return type2;
    }

    public int getUnits2() {
        return units2;
    }

    public int getPrice2() {
        return price2;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public String getManager() {
        return manager;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public String[] getOfficerList() {
        return officerList;
    }

    public boolean isWithinApplicationPeriod(LocalDate today) {
        return (today.equals(openDate) || today.isAfter(openDate))
                && (today.equals(closeDate) || today.isBefore(closeDate));
    }

    public void addOfficer(String name) { /* for the manager's use */
        officerList[10 - officerSlot] = name;
        officerSlot -= 1;
    }

    public void setUnits1(int x) { /* for manager's use */
        units1 = x;
    }

    public void setUnits2(int y) { /* for manager's use */
        units2 = y;
    }

    public void editApplied(Applicant a, String type, int change){
        if(!(change==-1)|(change==1)){
            System.out.println("Invalid edit! (increment error)"); return;}
        if(!(type==type1)|(type==type2)){
            System.out.println("Invalid edit! (flat type error)"); return;}
        if(change==1){
            if(type==type1){
                Applied.put(a,type1);
                units1-=1;
                System.out.println("Applicant successfully added to list!");
                return;}
            else{
                Applied.put(a,type2);
                units2-=1;
                System.out.println("Applicant successfully added to list!");
                return;}}

        else{
            if(!Applied.containsKey(a)){
                System.out.println("Applicant is already not in project!");
                return;}
            if(Applied.get(a)==type1){
                units1+=1;}
            else{
                units2+=1;}
            Applied.remove(a);
            System.out.println("Applicant successfully removed from project!");
            return;}}
            

}
