package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import utils.CSVUtils;

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

    public BTOProject() {
    }; /* defaulter object */

    public BTOProject(String projectName, String neighborhood, ArrayList<Room> rooms, String openDate, String closeDate,
            String manager, int officerSlot, String originalList, String visibility) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.rooms = rooms;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.manager = manager;
        this.officerSlot = officerSlot;
        this.visibility = visibility;
        originalList = originalList.replaceAll("^\"|\"$", "");
        this.officerList = new ArrayList<>(Arrays.asList(originalList.split(",")));
    }

    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighoburhood) {
        this.neighborhood = neighoburhood;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> Rooms) {
        this.rooms = Rooms;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String opendate) {
        this.openDate = opendate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setClosedate(String closedate) {
        this.closeDate = closedate;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public void setOfficerSlot(int slots) {
        this.officerSlot = slots;
    }

    public String getOfficerList() {
        String[] filteredlist = officerList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .toArray(String[]::new);
        return String.join(",", filteredlist);
    }

    public void setOfficerList(String officerlist) {
        String[] officers = officerlist.split("\\s*,\\s*"); // trim spaces around commas
        this.officerList = new ArrayList<>(Arrays.asList(officers));
    }

    public boolean isWithinApplicationPeriod(LocalDate today) {
        return (today.equals(LocalDate.parse(openDate, formatter))
                || today.isAfter(LocalDate.parse(openDate, formatter)))
                && (today.equals(LocalDate.parse(closeDate, formatter))
                        || today.isBefore(LocalDate.parse(closeDate, formatter)));
    }

    public void addOfficer(String name) { /* for the manager's use */
        officerList.add(name);
        officerSlot -= 1;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public static List<BTOProject> getProjects() {
        List<BTOProject> projectList = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(PROJECTS_CSV);

        for (String[] row : rows) {
            try {
                String projectName = row[0];
                String neighborhood = row[1];

                // Determine how many flat types exist (assumes flat types are in groups of 3)
                int RoomCount = (row.length - 10) / 3;
                ArrayList<Room> Rooms = new ArrayList<>();

                for (int i = 0; i < RoomCount; i++) {
                    String type = row[2 + i * 3];
                    int units = Integer.parseInt(row[3 + i * 3]);
                    int price = Integer.parseInt(row[4 + i * 3]);
                    Rooms.add(new Room(type, units, price));
                }

                String openDate = row[2 + RoomCount * 3];
                String closeDate = row[3 + RoomCount * 3];
                String manager = row[4 + RoomCount * 3];
                int officerSlot = Integer.parseInt(row[5 + RoomCount * 3]);
                String officerList = row[6 + RoomCount * 3];
                String visibility = row[7 + RoomCount * 3];

                BTOProject project = new BTOProject(
                        projectName, neighborhood, Rooms, openDate, closeDate,
                        manager, officerSlot, officerList, visibility);

                projectList.add(project);

            } catch (NumberFormatException e) {
                System.out.println("⚠️ Error parsing project row: " + String.join(",", row) + " NumberFormatError");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("⚠️ Error parsing project row: " + String.join(",", row) + " Array out of bounds");
            }
        }

        return projectList;
    }

    public static void addProject(BTOProject project) {
        try (FileWriter fw = new FileWriter(PROJECTS_CSV, true);
                BufferedWriter bw = new BufferedWriter(fw)) {

            // Convert room list to CSV-compatible strings
            ArrayList<String> roomCSV = new ArrayList<>();
            for (Room room : project.getRooms()) {
                roomCSV.add(room.toCSV()); // Assume this returns something like "2-Room,100,200000"
            }

            String[] filteredlist = project.officerList.stream()
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .toArray(String[]::new);

            // Write full project row
            bw.write(String.join(",",
                    project.getProjectName(),
                    project.getNeighborhood(),
                    String.join(",", roomCSV), // All room details
                    project.getOpenDate(),
                    project.getCloseDate(),
                    project.getManager(),
                    Integer.toString(project.getOfficerSlot()),
                    "\"" + String.join(",", filteredlist) + "\"", // Officer list in quotes
                    project.getVisibility()));

            bw.newLine();
        } catch (IOException e) {
            System.out.println("❌ Failed to write project: " + e.getMessage());
        }
    }

    public static void editProject(BTOProject updatedProject) {
        List<String> lines = new ArrayList<>();

        // Convert room list to CSV-compatible strings
        ArrayList<String> roomCSV = new ArrayList<>();
        for (Room room : updatedProject.getRooms()) {
            roomCSV.add(room.toCSV()); // Assume this returns something like "2-Room,100,200000"
        }

        try (BufferedReader br = new BufferedReader(new FileReader(PROJECTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(updatedProject.getProjectName())) {
                    String[] filteredlist = updatedProject.officerList.stream()
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .toArray(String[]::new);
                    lines.add(String.join(",",
                            updatedProject.projectName,
                            updatedProject.neighborhood,
                            String.join(",", roomCSV), // All room details
                            updatedProject.openDate,
                            updatedProject.closeDate,
                            updatedProject.manager,
                            Integer.toString(updatedProject.officerSlot),
                            "\"" + String.join(",", filteredlist) + "\"", // Special to add ""
                            updatedProject.visibility));
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROJECTS_CSV))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

    public static void deleteProject(String projectName) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PROJECTS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(projectName)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROJECTS_CSV))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

    public static BTOProject getProjectByName(String projectName) {
        List<BTOProject> projectList = getProjects();
        for (BTOProject project : projectList) {
            if (project.getProjectName().equalsIgnoreCase(projectName)) {
                return project;
            }
        }
        return null;
    }

    public String toCSV() {
        // Convert room list to CSV-compatible strings
        ArrayList<String> roomCSV = new ArrayList<>();
        for (Room room : getRooms()) {
            roomCSV.add(room.toCSV()); // Assume this returns something like "2-Room,100,200000"
        }

        String[] filteredlist = officerList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .toArray(String[]::new);

        return String.join(",",
                projectName,
                neighborhood,
                String.join(",", roomCSV), // All room details
                openDate,
                closeDate,
                manager,
                Integer.toString(officerSlot),
                String.join(",", filteredlist), // Officers separated by semicolon
                visibility);
    }

}
