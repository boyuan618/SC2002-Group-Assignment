import pages.MainMenu;

public class Main {
    public static void main(String[] args) {
        // Simulated login
        String username = "john_doe";
        String role = "Applicant"; // Can be "ProjectManager" or "HDBOfficer"

        MainMenu menu = new MainMenu(username, role);
        menu.displayMenu();
    }
}
