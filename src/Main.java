import pages.Login;
import pages.MainMenu;

public class Main {
    public static void main(String[] args) {
        // Login and get user info
        String[] userInfo = Login.loginUser(); // [NRIC, Role, Age, MaritalStatus]
        if (userInfo == null)
            return;

        String nric = userInfo[0];
        String role = userInfo[1];
        int age = Integer.parseInt(userInfo[2]);
        String maritalStatus = userInfo[3];

        // Start main menu depending on role
        MainMenu menu = new MainMenu(nric, role, age, maritalStatus);
        menu.displayMenu();
    }
}
