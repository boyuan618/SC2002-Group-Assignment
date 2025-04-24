package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import utils.CSVUtils;
import utils.Validator;

/**
 * Represents a BTO project in the HDB BTO Management System.
 * Manages project details, room types, application dates, and officer assignments.
 *
 * @author SC2002Team
 */
public class BTOProject {
    private String projectName;
    private String neighborhood;
    private ArrayList<Room> rooms;
    private String openDate;
    private String closeDate;
    private String manager;
    private int officerSlot;
    private ArrayList<String> officerList;
    private String visibility;

    private static final String PROJECTS_CSV = "data/ProjectList.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");

    /**
     * Default constructor for creating an empty BTOProject.
     */
    public BTOProject() {
    }

    /**
     * Constructs a BTOProject with the specified details.
     *
     * @param projectName The name of the project.
     * @param neighborhood The neighborhood where the project is located.
     * @param rooms The list of room types available in the project.
     * @param openDate The application opening date (format: M/dd/yyyy).
     * @param closeDate The application closing date (format: M/dd/yyyy).
     * @param manager The name of the project manager.
     * @param officerSlot The number of available officer slots.
     * @param originalList A comma-separated list of officer names.
     * @param visibility The visibility status ("on" or "off").
     * @throws IllegalArgumentException If any input is invalid.
     */
    public BTOProject(String projectName, String neighborhood, ArrayList<Room> rooms, String openDate, String closeDate,
            String manager, int officerSlot, String originalList, String visibility) {
        if (!Validator.isValidProjectName(projectName)) {
            throw new IllegalArgumentException("Invalid project name: Must be non-empty and contain only letters, numbers, and spaces.");
        }
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty.");
        }
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Rooms list cannot be null or empty.");
        }
        if (!Validator.isValidDate(openDate) || !Validator.isValidDate(closeDate)) {
            throw new IllegalArgumentException("Invalid date format: Must be M/dd/yyyy.");
        }
        LocalDate open = LocalDate.parse(openDate, formatter);
        LocalDate close = LocalDate.parse(closeDate, formatter);
        if (!open.isBefore(close)) {
            throw new IllegalArgumentException("Open date must be before close date.");
        }
        if (!Validator.isValidName(manager)) {
            throw new IllegalArgumentException("Invalid manager name: Must contain only letters and spaces and cannot be empty.");
        }
        if (officerSlot < 0) {
            throw new IllegalArgumentException("Officer slots cannot be negative.");
        }
        if (!Validator.isValidCommaSeparatedList(originalList)) {
            throw new IllegalArgumentException("Invalid officer list: Must be a comma-separated list of valid names or empty.");
        }
        if (!Validator.isValidVisibility(visibility)) {
            throw new IllegalArgumentException("Invalid visibility: Must be 'on' or 'off'.");
        }
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.rooms = new ArrayList<>(rooms); // Defensive copy
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.manager = manager;
        this.officerSlot = officerSlot;
        this.visibility = visibility;
        originalList = originalList.replaceAll("^\"|\"$", "");
        this.officerList = new ArrayList<>(Arrays.asList(originalList.split(",")));
        if (this.officerList.size() == 1 && this.officerList.get(0).isEmpty()) {
            this.officerList.clear();
        }
    }

    /**
     * Gets the project name.
     *
     * @return The project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the neighborhood.
     *
     * @return The neighborhood.
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the neighborhood.
     *
     * @param neighborhood The new neighborhood.
     * @throws IllegalArgumentException If the neighborhood is invalid.
     */
    public void setNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty.");
        }
        this.neighborhood = neighborhood;
    }

    /**
     * Gets the list of rooms.
     *
     * @return The list of rooms.
     */
    public ArrayList<Room> getRooms() {
        return new ArrayList<>(rooms); // Defensive copy
    }

    /**
     * Sets the list of rooms.
     *
     * @param rooms The new list of rooms.
     * @throws IllegalArgumentException If the rooms list is invalid.
     */
    public void setRooms(ArrayList<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Rooms list cannot be null or empty.");
        }
        this.rooms = new ArrayList<>(rooms); // Defensive copy
    }

    /**
     * Gets the application opening date.
     *
     * @return The opening date.
     */
    public String getOpenDate() {
        return openDate;
    }

    /**
     * Sets the application opening date.
     *
     * @param openDate The new opening date.
     * @throws IllegalArgumentException If the date is invalid.
     */
    public void setOpenDate(String openDate) {
        if (!Validator.isValidDate(openDate)) {
            throw new IllegalArgumentException("Invalid date format: Must be M/dd/yyyy.");
        }
        if (closeDate != null && Validator.isValidDate(closeDate)) {
            LocalDate open = LocalDate.parse(openDate, formatter);
            LocalDate close = LocalDate.parse(closeDate, formatter);
            if (!open.isBefore(close)) {
                throw new IllegalArgumentException("Open date must be before close date.");
            }
        }
        this.openDate = openDate;
    }

    /**
     * Gets the application closing date.
     *
     * @return The closing date.
     */
    public String getCloseDate() {
        return closeDate;
    }

    /**
     * Sets the application closing date.
     *
     * @param closeDate The new closing date.
     * @throws IllegalArgumentException If the date is invalid.
     */
    public void setCloseDate(String closeDate) {
        if (!Validator.isValidDate(closeDate)) {
            throw new IllegalArgumentException("Invalid date format: Must be M/dd/yyyy.");
        }
        if (openDate != null && Validator.isValidDate(openDate)) {
            LocalDate open = LocalDate.parse(openDate, formatter);
            LocalDate close = LocalDate.parse(closeDate, formatter);
            if (!open.isBefore(close)) {
                throw new IllegalArgumentException("Open date must be before close date.");
            }
        }
        this.closeDate = closeDate;
    }

    /**
     * Gets the project manager.
     *
     * @return The manager's name.
     */
    public String getManager() {
        return manager;
    }

    /**
     * Sets the project manager.
     *
     * @param manager The new manager's name.
     * @throws IllegalArgumentException If the manager name is invalid.
     */
    public void setManager(String manager) {
        if (!Validator.isValidName(manager)) {
            throw new IllegalArgumentException("Invalid manager name: Must contain only letters and spaces and cannot be empty.");
        }
        this.manager = manager;
    }

    /**
     * Gets the number of available officer slots.
     *
     * @return The number of officer slots.
     */
    public int getOfficerSlot() {
        return officerSlot;
    }

    /**
     * Sets the number of available officer slots.
     *
     * @param slots The new number of slots.
     * @throws IllegalArgumentException If the slots value is invalid.
     */
    public void setOfficerSlot(int slots) {
        if (slots < 0) {
            throw new IllegalArgumentException("Officer slots cannot be negative.");
        }
        this.officerSlot = slots;
    }

    /**
     * Gets the list of officers as a comma-separated string.
     *
     * @return The officer list as a string.
     */
    public String getOfficerList() {
        String[] filteredList = officerList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .toArray(String[]::new);
        return String.join(",", filteredList);
    }

    /**
     * Sets the list of officers from a comma-separated string.
     *
     * @param officerList The new officer list.
     * @throws IllegalArgumentException If the officer list is invalid.
     */
    public void setOfficerList(String officerList) {
        if (!Validator.isValidCommaSeparatedList(officerList)) {
            throw new IllegalArgumentException("Invalid officer list: Must be a comma-separated list of valid names or empty.");
        }
        String[] officers = officerList.split("\\s*,\\s*");
        this.officerList = new ArrayList<>(Arrays.asList(officers));
        if (this.officerList.size() == 1 && this.officerList.get(0).isEmpty()) {
            this.officerList.clear();
        }
    }

    /**
     * Checks if the current date is within the application's open and close dates.
     *
     * @param today The current date.
     * @return True if within the application period, false otherwise.
     * @throws IllegalArgumentException If the date is null or project dates are invalid.
     */
    public boolean isWithinApplicationPeriod(LocalDate today) {
        if (today == null) {
            throw new IllegalArgumentException("Current date cannot be null.");
        }
        try {
            LocalDate open = LocalDate.parse(openDate, formatter);
            LocalDate close = LocalDate.parse(closeDate, formatter);
            return (today.equals(open) || today.isAfter(open)) &&
                   (today.equals(close) || today.isBefore(close));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid project dates: Unable to parse open or close date.");
        }
    }

    /**
     * Adds an officer to the project and decrements the officer slot.
     *
     * @param name The name of the officer to add.
     * @throws IllegalArgumentException If the name is invalid or no slots are available.
     */
    public void addOfficer(String name) {
        if (!Validator.isValidName(name)) {
            throw new IllegalArgumentException("Invalid officer name: Must contain only letters and spaces and cannot be empty.");
        }
        if (officerSlot <= 0) {
            throw new IllegalArgumentException("No available officer slots.");
        }
        officerList.add(name);
        officerSlot--;
    }

    /**
     * Gets the visibility status of the project.
     *
     * @return The visibility ("on" or "off").
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility status of the project.
     *
     * @param visibility The new visibility status.
     * @throws IllegalArgumentException If the visibility is invalid.
     */
    public void setVisibility(String visibility) {
        if (!Validator.isValidVisibility(visibility)) {
            throw new IllegalArgumentException("Invalid visibility: Must be 'on' or 'off'.");
        }
        this.visibility = visibility;
    }

    /**
     * Retrieves all BTO projects from the CSV file.
     *
     * @return A list of BTO projects, or an empty list if an error occurs.
     */
    public static List<BTOProject> getProjects() {
        List<BTOProject> projectList = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(PROJECTS_CSV);
        if (rows == null) {
            return projectList;
        }

        for (String[] row : rows) {
            try {
                if (row.length < 8) {
                    System.out.println("Skipping malformed row: " + String.join(",", row));
                    continue;
                }
                String projectName = row[0];
                if (!Validator.isValidProjectName(projectName)) {
                    System.out.println("Invalid project name in row: " + projectName);
                    continue;
                }
                String neighborhood = row[1];
                if (neighborhood == null || neighborhood.trim().isEmpty()) {
                    System.out.println("Empty neighborhood in row: " + String.join(",", row));
                    continue;
                }

                // Determine how many flat types exist (assumes flat types are in groups of 3)
                int roomCount = (row.length - 8) / 3;
                if (roomCount < 1) {
                    System.out.println("No room types defined in row: " + String.join(",", row));
                    continue;
                }
                ArrayList<Room> rooms = new ArrayList<>();
                for (int i = 0; i < roomCount; i++) {
                    if (2 + i * 3 + 2 >= row.length) {
                        System.out.println("Incomplete room data in row: " + String.join(",", row));
                        break;
                    }
                    String type = row[2 + i * 3];
                    if (!Validator.isValidFlatType(type)) {
                        System.out.println("Invalid room type in row: " + type);
                        continue;
                    }
                    int units = Integer.parseInt(row[3 + i * 3]);
                    if (units < 0) {
                        System.out.println("Negative units in row: " + String.join(",", row));
                        continue;
                    }
                    int price = Integer.parseInt(row[4 + i * 3]);
                    if (price < 0) {
                        System.out.println("Negative price in row: " + String.join(",", row));
                        continue;
                    }
                    rooms.add(new Room(type, units, price));
                }
                if (rooms.isEmpty()) {
                    System.out.println("No valid rooms in row: " + String.join(",", row));
                    continue;
                }

                String openDate = row[2 + roomCount * 3];
                String closeDate = row[3 + roomCount * 3];
                if (!Validator.isValidDate(openDate) || !Validator.isValidDate(closeDate)) {
                    System.out.println("Invalid dates in row: " + String.join(",", row));
                    continue;
                }
                String manager = row[4 + roomCount * 3];
                if (!Validator.isValidName(manager)) {
                    System.out.println("Invalid manager name in row: " + manager);
                    continue;
                }
                int officerSlot = Integer.parseInt(row[5 + roomCount * 3]);
                if (officerSlot < 0) {
                    System.out.println("Negative officer slots in row: " + String.join(",", row));
                    continue;
                }
                String officerList = row[6 + roomCount * 3];
                if (!Validator.isValidCommaSeparatedList(officerList)) {
                    System.out.println("Invalid officer list in row: " + officerList);
                    continue;
                }
                String visibility = row[7 + roomCount * 3];
                if (!Validator.isValidVisibility(visibility)) {
                    System.out.println("Invalid visibility in row: " + visibility);
                    continue;
                }

                BTOProject project = new BTOProject(
                        projectName, neighborhood, rooms, openDate, closeDate,
                        manager, officerSlot, officerList, visibility);
                        
                projectList.add(project);

            } catch (NumberFormatException e) {
                System.out.println("Error parsing project row: " + String.join(",", row) + " - NumberFormatException: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error parsing project row: " + String.join(",", row) + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error parsing project row: " + String.join(",", row) + " - Unexpected error: " + e.getMessage());
            }
        }

        return projectList;
    }

    /**
     * Adds a new BTO project to the CSV file.
     *
     * @param project The project to add.
     * @throws IllegalArgumentException If the project is invalid or already exists.
     */
    public static void addProject(BTOProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (getProjectByName(project.getProjectName()) != null) {
            throw new IllegalArgumentException("Project already exists: " + project.getProjectName());
        }
        try (FileWriter fw = new FileWriter(PROJECTS_CSV, true);
                BufferedWriter bw = new BufferedWriter(fw)) {

            ArrayList<String> roomCSV = new ArrayList<>();
            for (Room room : project.getRooms()) {
                String csv = room.toCSV();
                if (csv == null || csv.trim().isEmpty()) {
                    throw new IllegalArgumentException("Invalid room data for project: " + project.getProjectName());
                }
                roomCSV.add(csv);
            }
            String[] filteredList = project.officerList.stream()
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .toArray(String[]::new);
                    
            bw.write(String.join(",",
                    project.getProjectName(),
                    project.getNeighborhood(),
                    String.join(",", roomCSV),
                    project.getOpenDate(),
                    project.getCloseDate(),
                    project.getManager(),
                    Integer.toString(project.getOfficerSlot()),
                    "\"" + String.join(",", filteredList) + "\"",
                    project.getVisibility()));

            bw.newLine();

            System.out.println("Project added successfully: " + project.getProjectName());
        } catch (IOException e) {
            System.out.println("Failed to write project: " + e.getMessage());
            throw new RuntimeException("Error writing project to CSV: " + e.getMessage());
        }
    }

    /**
     * Updates an existing BTO project in the CSV file.
     *
     * @param updatedProject The updated project object.
     * @throws IllegalArgumentException If the project is invalid or does not exist.
     */
    public static void editProject(BTOProject updatedProject) {
        if (updatedProject == null) {
            throw new IllegalArgumentException("Updated project cannot be null.");
        }
        if (getProjectByName(updatedProject.getProjectName()) == null) {
            throw new IllegalArgumentException("Project does not exist: " + updatedProject.getProjectName());
        }
        List<String> lines = new ArrayList<>();

        ArrayList<String> roomCSV = new ArrayList<>();
        for (Room room : updatedProject.getRooms()) {
            String csv = room.toCSV();
            if (csv == null || csv.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid room data for project: " + updatedProject.getProjectName());
            }
            roomCSV.add(csv);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(PROJECTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // Preserve empty fields
                if (parts.length >= 8 && parts[0].equals(updatedProject.getProjectName())) {
                    String[] filteredList = updatedProject.officerList.stream()
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .toArray(String[]::new);
                    lines.add(String.join(",",
                            updatedProject.projectName,
                            updatedProject.neighborhood,
                            String.join(",", roomCSV),
                            updatedProject.openDate,
                            updatedProject.closeDate,
                            updatedProject.manager,
                            Integer.toString(updatedProject.officerSlot),
                            "\"" + String.join(",", filteredList) + "\"",
                            updatedProject.visibility));
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading project CSV: " + e.getMessage());
            throw new RuntimeException("Error reading project CSV: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROJECTS_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Project updated successfully: " + updatedProject.getProjectName());
        } catch (IOException e) {
            System.out.println("Error writing project CSV: " + e.getMessage());
            throw new RuntimeException("Error writing project CSV: " + e.getMessage());
        }
    }

    /**
     * Deletes a BTO project from the CSV file.
     *
     * @param projectName The name of the project to delete.
     * @throws IllegalArgumentException If the project name is invalid or does not exist.
     */
    public static void deleteProject(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        if (getProjectByName(projectName) == null) {
            throw new IllegalArgumentException("Project does not exist: " + projectName);
        }
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PROJECTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 1 && !parts[0].equals(projectName)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading project CSV: " + e.getMessage());
            throw new RuntimeException("Error reading project CSV: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROJECTS_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Project deleted successfully: " + projectName);
        } catch (IOException e) {
            System.out.println("Error writing project CSV: " + e.getMessage());
            throw new RuntimeException("Error writing project CSV: " + e.getMessage());
        }
    }

    /**
     * Retrieves a BTO project by its name.
     *
     * @param projectName The name of the project.
     * @return The BTO project if found, null otherwise.
     * @throws IllegalArgumentException If the project name is invalid.
     */
    public static BTOProject getProjectByName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        List<BTOProject> projectList = getProjects();
        for (BTOProject project : projectList) {
            if (project != null && project.getProjectName().equalsIgnoreCase(projectName)) {
                return project;
            }
        }
        return null;
    }

    /**
     * Converts the project to a CSV-compatible string.
     *
     * @return A comma-separated string representing the project.
     * @throws IllegalStateException If required fields are null or invalid.
     */
    public String toCSV() {
        if (projectName == null || neighborhood == null || rooms == null || openDate == null ||
                closeDate == null || manager == null || visibility == null) {
            throw new IllegalStateException("Cannot generate CSV: One or more required fields are null.");
        }
        ArrayList<String> roomCSV = new ArrayList<>();
        for (Room room : rooms) {
            String csv = room.toCSV();
            if (csv == null || csv.trim().isEmpty()) {
                throw new IllegalStateException("Invalid room data in project: " + projectName);
            }
            roomCSV.add(csv);
        }
        String[] filteredList = officerList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .toArray(String[]::new);

        return String.join(",",
                projectName,
                neighborhood,
                String.join(",", roomCSV),
                openDate,
                closeDate,
                manager,
                Integer.toString(officerSlot),
                String.join(",", filteredList),
                visibility);
    }

}
