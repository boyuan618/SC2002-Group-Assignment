package model;

public class ProjectManager extends HDBManager implements EnquiryInt{ 
    private BTOProject myProject;
    private boolean validity; /*idk how they want us to address this*/
    private boolean visibility;

    public ProjectManager(String a, String b, String c, int d, boolean e, BTOProject f, boolean g, boolean h){
        super(a,b,c,d,e); myProject=f; validity=g; visibility=h;}

    public void addressOfficerRegistration(String name){
        if(myProject.getOfficerSlot()==0){
            System.out.println("No more officer slots left. Registration denied.");
            return;}
        myProject.addOfficer(name);
        return;}
    
    public void toggle(){
        if(visibility==true){
            visibility=false;
            return;}
        else{
            visibility=true;}}
    }
