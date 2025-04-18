package controller;

import model.BTOProject;
import utils.CSVUtils;

import java.time.LocalDate;
import java.util.*;

public class ProjectController {

    private static final String PROJECT_FILE = "data/projects.csv";

    public static List<BTOProject> getAllProjects() {
        List<String[]> rawData = CSVUtils.readCSV(PROJECT_FILE);
        List<BTOProject> projects = new ArrayList<>();

        for (String[] row : rawData) {
            if (row.length < 13)
                continue; // Basic validation

            try {
                String projectName = row[0];
                String neighborhood = row[1];
                String type1 = row[2];
                int units1 = Integer.parseInt(row[3]);
                int price1 = Integer.parseInt(row[4]);
                String type2 = row[5];
                int units2 = Integer.parseInt(row[6]);
                int price2 = Integer.parseInt(row[7]);
                LocalDate openDate = LocalDate.parse(row[8]);
                LocalDate closeDate = LocalDate.parse(row[9]);
                String manager = row[10];
                int officerSlot = Integer.parseInt(row[11]);
                String officerList = row[12];

                BTOProject project = new BTOProject(projectName, neighborhood, type1, units1, price1,
                        type2, units2, price2, openDate, closeDate, manager, officerSlot, officerList);
                projects.add(project);
            } catch (Exception e) {
                System.out.println("Skipping invalid project entry.");
            }
        }

        return projects;
    }

    public static BTOProject findProjectByName(String name) {
        for (BTOProject p : getAllProjects()) {
            if (p.getProjectName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public static void saveAllProjects(List<BTOProject> projects) {
        List<String[]> rows = new ArrayList<>();
        for (BTOProject p : projects) {
            String[] row = new String[] {
                    p.getProjectName(),
                    p.getNeighborhood(),
                    p.getType1(),
                    String.valueOf(p.getUnits1()),
                    String.valueOf(p.getPrice1()),
                    p.getType2(),
                    String.valueOf(p.getUnits2()),
                    String.valueOf(p.getPrice2()),
                    p.getOpenDate().toString(),
                    p.getCloseDate().toString(),
                    p.getManager(),
                    String.valueOf(p.getOfficerSlot()),
                    String.join(",", p.getOfficerList())
            };
            rows.add(row);
        }

        CSVUtils.writeCSV(PROJECT_FILE, rows);
    }

    public static void updateProject(BTOProject updatedProject) {
        List<BTOProject> allProjects = getAllProjects();
        for (int i = 0; i < allProjects.size(); i++) {
            if (allProjects.get(i).getProjectName().equals(updatedProject.getProjectName())) {
                allProjects.set(i, updatedProject);
                saveAllProjects(allProjects);
                return;
            }
        }
    }
}
