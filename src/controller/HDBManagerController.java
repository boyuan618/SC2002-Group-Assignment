package controller;

import model.*;
import utils.Validator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for handling HDB Manager interactions, enabling project creation, editing, deletion,
 * and visibility management within the HDB BTO Management System.
 *
 * @author SC2002Team
 */
public class HDBManagerController {
    final public User user;
    protected HDBManager hdbManager;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");

    /**
     * Constructs an HDBManagerController with the specified user.
     *
     * @param user The authenticated user (must have HDBManager role).
     * @throws IllegalArgumentException If the user is null or not an HDBManager.
     */
    public HDBManagerController(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (!user.getRole().equals("HDBManager")) {
            throw new IllegalArgumentException("User must have HDBManager role.");
        }
        this.user = user;
        this.hdbManager = new HDBManager(user.getName(), user.getNRIC(), user.getPassword(), user.getAge(),
                user.getMaritalStatus());
    }

    /**
     * Creates a new BTO project with the specified details.
     *
     * @param projectName The name of the project.
     * @param neighborhood The neighborhood of the project.
     * @param rooms The list of room types.
     * @param openDate The application opening date (format: M/dd/yyyy).
     * @param closeDate The application closing date (format: M/dd/yyyy).
     * @param officerSlot The number of officer slots.
     * @param officerList A comma-separated list of officer names.
     * @param visibility The project visibility ("on" or "off").
     * @return The created BTOProject, or null if creation fails.
     * @throws IllegalArgumentException If any input is invalid.
     */
    public BTOProject createNewProject(String projectName, String neighborhood, ArrayList<Room> rooms, String openDate,
            String closeDate, int officerSlot, String officerList, String visibility) {
        if (Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be null or empty.");
        }
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Rooms list cannot be null or empty.");
        }
        for (Room room : rooms) {
            if (room == null || !Validator.isValidFlatType(room.getRoomType()) || room.getUnits() < 0 || room.getPrice() < 0) {
                throw new IllegalArgumentException("Invalid room: Must have valid type, non-negative units, and non-negative price.");
            }
        }
        if (!Validator.isValidDate(openDate) || !Validator.isValidDate(closeDate)) {
            throw new IllegalArgumentException("Invalid date format: Must be M/dd/yyyy.");
        }
        LocalDate open = LocalDate.parse(openDate, formatter);
        LocalDate close = LocalDate.parse(closeDate, formatter);
        if (!open.isBefore(close)) {
            throw new IllegalArgumentException("Open date must be before close date.");
        }
        if (officerSlot < 0) {
            throw new IllegalArgumentException("Officer slots cannot be negative.");
        }
        if (!Validator.isValidCommaSeparatedList(officerList)) {
            throw new IllegalArgumentException("Invalid officer list: Must be a comma-separated list of valid names or empty.");
        }
        if (!Validator.isValidVisibility(visibility)) {
            throw new IllegalArgumentException("Invalid visibility: Must be 'on' or 'off'.");
        }

        return hdbManager.createListing(projectName.trim(), neighborhood.trim(), rooms,
                openDate.trim(), closeDate.trim(), officerSlot, officerList.trim(), visibility.trim());
    }

    /**
     * Edits an existing BTO project based on user input.
     *
     * @param sc The Scanner for user input.
     * @param projectName The name of the project to edit.
     * @throws IllegalArgumentException If inputs are invalid or the project is not editable.
     */
    public void editProject(Scanner sc, String projectName) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }

        List<BTOProject> allProjects = BTOProject.getProjects();
        BTOProject selected = null;

        for (BTOProject p : allProjects) {
            if (p.getProjectName().equalsIgnoreCase(projectName.trim()) && p.getManager().equals(hdbManager.getName())) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("You do not have permission to edit this project or it does not exist.");
            return;
        }

        System.out.println("Editing project: " + selected.getProjectName());

        System.out.print("New Neighborhood [" + selected.getNeighborhood() + "]: ");
        String neighborhood = sc.nextLine().trim();
        if (!neighborhood.isEmpty()) {
            if (neighborhood.isEmpty()) {
                throw new IllegalArgumentException("New neighborhood cannot be empty.");
            }
            selected.setNeighborhood(neighborhood);
        }

        int count = 1;
        Iterator<Room> iterator = selected.getRooms().iterator();
        while (iterator.hasNext()) {
            Room type = iterator.next();
        
            System.out.print("Keep Room Type " + count + " [" + type.getRoomType() + "]? (yes/no): ");
            String keep = sc.nextLine().trim().toLowerCase();
            if (keep.equals("no")) {
                iterator.remove();
                System.out.println("Room Type " + count + " removed.");
                continue;
            }
        
            System.out.print("New Type " + count + " [" + type.getRoomType() + "]: ");
            String type1 = sc.nextLine().trim();
            if (!type1.isEmpty()) {
                if (!Validator.isValidFlatType(type1)) {
                    throw new IllegalArgumentException("Invalid room type: Must be a valid flat type (e.g., '2-room').");
                }
                type.setRoomType(type1);
            }
        
            System.out.print("New Units for Type " + count + " [" + type.getUnits() + "]: ");
            String units1 = sc.nextLine().trim();
            if (!units1.isEmpty()) {
                try {
                    int units = Integer.parseInt(units1);
                    if (units < 0) {
                        throw new IllegalArgumentException("Units cannot be negative.");
                    }
                    type.setUnits(units);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid units: Must be a valid integer.");
                }
            }
        
            System.out.print("New Price for Type " + count + " [" + type.getPrice() + "]: ");
            String price1 = sc.nextLine().trim();
            if (!price1.isEmpty()) {
                try {
                    int price = Integer.parseInt(price1);
                    if (price < 0) {
                        throw new IllegalArgumentException("Price cannot be negative.");
                    }
                    type.setPrice(price);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid price: Must be a valid integer.");
                }
            }
            count++;
        }
        
        // Ask to add new types
        while (true) {
            System.out.print("Do you want to add a new room type? (yes/no): ");
            String addMore = sc.nextLine().trim().toLowerCase();
            if (!addMore.equals("yes")) break;
        
            System.out.print("Enter new room type: ");
            String newType = sc.nextLine().trim();
            if (!Validator.isValidFlatType(newType)) {
                System.out.println("Invalid room type.");
                continue;
            }
        
            System.out.print("Enter units for " + newType + ": ");
            String newUnitsStr = sc.nextLine().trim();
            int newUnits;
            try {
                newUnits = Integer.parseInt(newUnitsStr);
                if (newUnits < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid units.");
                continue;
            }
        
            System.out.print("Enter price for " + newType + ": ");
            String newPriceStr = sc.nextLine().trim();
            int newPrice;
            try {
                newPrice = Integer.parseInt(newPriceStr);
                if (newPrice < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
                continue;
            }
            
            ArrayList<Room> rooms = selected.getRooms();
            rooms.add(new Room(newType, newUnits, newPrice));
            selected.setRooms(rooms);
            System.out.println("New room type added.");
        }
        

        System.out.print("New Application Opening Date [" + selected.getOpenDate() + "]: ");
        String openDateStr = sc.nextLine().trim();
        if (!openDateStr.isEmpty()) {
            if (!Validator.isValidDate(openDateStr)) {
                throw new IllegalArgumentException("Invalid open date format: Must be M/dd/yyyy.");
            }
            selected.setOpenDate(openDateStr);
        }

        System.out.print("New Application Closing Date [" + selected.getCloseDate() + "]: ");
        String closeDateStr = sc.nextLine().trim();
        if (!closeDateStr.isEmpty()) {
            if (!Validator.isValidDate(closeDateStr)) {
                throw new IllegalArgumentException("Invalid close date format: Must be M/dd/yyyy.");
            }
            selected.setCloseDate(closeDateStr);
        }

        System.out.print("New Officer Slot [" + selected.getOfficerSlot() + "]: ");
        String officerSlotStr = sc.nextLine().trim();
        if (!officerSlotStr.isEmpty()) {
            try {
                int officerSlot = Integer.parseInt(officerSlotStr);
                if (officerSlot < 0) {
                    throw new IllegalArgumentException("Officer slots cannot be negative.");
                }
                selected.setOfficerSlot(officerSlot);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid officer slot: Must be a valid integer.");
            }
        }

        System.out.print("New Officer List (comma-separated Names) [" + String.join(",", selected.getOfficerList()) + "]: ");
        String officerListStr = sc.nextLine().trim();
        if (!officerListStr.isEmpty()) {
            if (!Validator.isValidCommaSeparatedList(officerListStr)) {
                throw new IllegalArgumentException("Invalid officer list: Must be a comma-separated list of valid names.");
            }
            selected.setOfficerList(officerListStr);
        }

        System.out.print("Set visibility (on/off) [" + selected.getVisibility() + "]: ");
        String visibility = sc.nextLine().trim();
        if (!visibility.isEmpty()) {
            if (!Validator.isValidVisibility(visibility)) {
                throw new IllegalArgumentException("Invalid visibility: Must be 'on' or 'off'.");
            }
            selected.setVisibility(visibility);
        }

        hdbManager.editProject(selected);
    }

    /**
     * Deletes a BTO project.
     *
     * @param projectName The name of the project to delete.
     * @throws IllegalArgumentException If the project name is invalid or not editable.
     */
    public void deleteProject(String projectName) {
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        BTOProject project = BTOProject.getProjectByName(projectName.trim());
        if (project == null || !project.getManager().equals(hdbManager.getName())) {
            throw new IllegalArgumentException("You do not have permission to delete this project or it does not exist.");
        }
        hdbManager.deleteListing(projectName.trim());
    }

    /**
     * Toggles the visibility of a BTO project.
     *
     * @param projectName The name of the project.
     * @param visibility The new visibility status ("on" or "off").
     * @throws IllegalArgumentException If inputs are invalid or the project is not editable.
     */
    public void toggleProjectVisibility(String projectName, String visibility) {
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (!Validator.isValidVisibility(visibility)) {
            throw new IllegalArgumentException("Invalid visibility: Must be 'on' or 'off'.");
        }
        BTOProject project = BTOProject.getProjectByName(projectName.trim());
        if (project == null || !project.getManager().equals(hdbManager.getName())) {
            throw new IllegalArgumentException("You do not have permission to toggle visibility of this project or it does not exist.");
        }
        hdbManager.toggleVisibility(projectName.trim(), visibility.trim());
        System.out.println("Visibility updated successfully for project: " + projectName.trim());
    }

    /**
     * Retrieves the list of projects managed by this manager.
     *
     * @return A list of BTO projects managed by this manager.
     */
    public List<BTOProject> getMyProjects() {
        return hdbManager.viewMyProjects();
    }

}
