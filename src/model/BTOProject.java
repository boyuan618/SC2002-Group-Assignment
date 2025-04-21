package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    public BTOProject(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, String openDate, String closeDate,
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

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public int getUnits1() {
        return units1;
    }

    public int getPrice1() {
        return price1;
    }

    public void setPrice1(int price1) {
        this.price1 = price1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public int getUnits2() {
        return units2;
    }

    public int getPrice2() {
        return price2;
    }

    public void setPrice2(int price2) {
        this.price2 = price2;
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
                BTOProject project = new BTOProject(
                        row[0], // projectName
                        row[1], // neighborhood
                        row[2], // type1
                        Integer.parseInt(row[3]), // units1
                        Integer.parseInt(row[4]), // price1
                        row[5], // type2
                        Integer.parseInt(row[6]), // units2
                        Integer.parseInt(row[7]), // price2
                        row[8], // openDate
                        row[9], // closeDate
                        row[10], // manager
                        Integer.parseInt(row[11]), // officerSlot
                        row[12], // originalList
                        row[13] // visibility
                );
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
        try (FileWriter fw = new FileWriter(PROJECTS_CSV, true); BufferedWriter bw = new BufferedWriter(fw)) {
            String[] filteredlist = project.officerList.stream()
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .toArray(String[]::new);
            bw.write(String.join(",",
                    project.projectName,
                    project.neighborhood,
                    project.type1,
                    Integer.toString(project.units1),
                    Integer.toString(project.price1),
                    project.type2,
                    Integer.toString(project.units2),
                    Integer.toString(project.price2),
                    project.openDate,
                    project.closeDate,
                    project.manager,
                    Integer.toString(project.officerSlot),
                    "\"" + String.join(",", filteredlist) + "\"", // Special to add ""
                    project.visibility));
            bw.newLine();
        } catch (IOException e) {
        }
    }

    public static void editProject(BTOProject updatedProject) {
        List<String> lines = new ArrayList<>();

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
                            updatedProject.type1,
                            Integer.toString(updatedProject.units1),
                            Integer.toString(updatedProject.price1),
                            updatedProject.type2,
                            Integer.toString(updatedProject.units2),
                            Integer.toString(updatedProject.price2),
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
        String[] filteredlist = officerList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .toArray(String[]::new);
        return String.join(",",
                projectName,
                neighborhood,
                type1,
                Integer.toString(units1),
                Integer.toString(price1),
                type2,
                Integer.toString(units2),
                Integer.toString(price2),
                openDate,
                closeDate,
                manager,
                Integer.toString(officerSlot),
                String.join(",", filteredlist), // Officers separated by semicolon
                visibility);
    }

}
