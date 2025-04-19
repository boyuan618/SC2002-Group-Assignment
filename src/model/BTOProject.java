package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import utils.CSVUtils;

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
    private String visibility;

    private static final String PROJECTS_CSV = "data/ProjectList.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy");

    public BTOProject() {
    }; /* defaulter object */

    public BTOProject(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
            String manager, int officerSlot, String originalList, String visibility) {
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
        this.visibility = visibility;
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
                if (row.length >= 14) {
                    BTOProject project = new BTOProject(
                            row[0], // projectName
                            row[1], // neighborhood
                            row[2], // type1
                            Integer.parseInt(row[3]), // units1
                            Integer.parseInt(row[4]), // price1
                            row[5], // type2
                            Integer.parseInt(row[6]), // units2
                            Integer.parseInt(row[7]), // price2
                            LocalDate.parse(row[8], formatter), // openDate
                            LocalDate.parse(row[9], formatter), // closeDate
                            row[10], // manager
                            Integer.parseInt(row[11]), // officerSlot
                            row[12], // originalList
                            row[13] // visibility
                    );
                    projectList.add(project);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error parsing project row: " + String.join(",", row));
            }
        }

        return projectList;
    }

    public static void addProject(String filepath, BTOProject project) {
        try (FileWriter fw = new FileWriter(filepath, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(project.toCSV());
            bw.newLine();
        } catch (IOException e) {
        }
    }

    public static void editProject(String filepath, BTOProject updatedProject) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(updatedProject.getProjectName())) {
                    lines.add(updatedProject.toCSV());
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

    public static void deleteProject(String filepath, String projectName) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
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
        return String.join(",",
                projectName,
                neighborhood,
                type1,
                Integer.toString(units1),
                Integer.toString(price1),
                type2,
                Integer.toString(units2),
                Integer.toString(price2),
                openDate.toString(),
                closeDate.toString(),
                manager,
                Integer.toString(officerSlot),
                String.join(";", officerList), // Officers separated by semicolon
                visibility);
    }

}
