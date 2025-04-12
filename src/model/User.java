package model;

public class User {
  protected String NRIC;
  protected String password;
  protected int age;
  protected boolean maritalStatus;

  public User(){
      NRIC="pending"; password="pending"; age=0; maritalStatus=false;}
        
  public User(String a, String b, int c, boolean d){
      NRIC=a; password=b; age=c; maritalStatus=d;}

  public boolean login(String entry){  /*Idk what our control class is like, so I added a string parameter and boolean return type for easing code manipulating later on*/
      if(entry.equals(password)){
          return true;}
      return false;}
    
  public void changePassword(String newPassword){
      if(newPassword==null){
          System.out.println("Password change not successful. Please try again!");return;}
      password=newPassword;
      System.out.println("Password change is successful!");
      return;}
}
