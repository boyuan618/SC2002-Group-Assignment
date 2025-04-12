package pages;

import model.BTOProject;
import utils.CSVUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Projects {

    private static final String PROJECTS_CSV = "data/projects.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public static void viewOpenProjects(String maritalStatus) {
        List<String[]> projectData = CSVUtils.readCSV(PROJECTS_CSV);
        LocalDate today = LocalDate.now();
        List<String[]> openProjects = new ArrayList<>();

        System.out.println("\n===== Open BTO Projects for " + maritalStatus + " Applicants =====");

        for (String[] row : projectData) {
            if (row.length < 13)
                continue;
            boolean visible = true; // Assume visibility check if needed
            LocalDate openDate = LocalDate.parse(row[8], FORMATTER);
            LocalDate closeDate = LocalDate.parse(row[9], FORMATTER);

            if (visible && !today.isBefore(openDate) && !today.isAfter(closeDate)) {
                openProjects.add(row);
            }
        }

        for (String[] p : openProjects) {
            String projectName = p[0];
            String neighborhood = p[1];
            String type1 = p[2];
            int units1 = Integer.parseInt(p[3]);
            String price1 = p[4];
            String type2 = p[5];
            int units2 = Integer.parseInt(p[6]);
            String price2 = p[7];

            if (maritalStatus.equalsIgnoreCase("Single") && type1.equals("2-Room")) {
                System.out.printf("- %s (%s) | %s: %d units @ $%s\n",
                        projectName, neighborhood, type1, units1, price1);
            } else if (maritalStatus.equalsIgnoreCase("Married")) {
                System.out.printf("- %s (%s) | %s: %d @ $%s, %s: %d @ $%s\n",
                        projectName, neighborhood,
                        type1, units1, price1,
                        type2, units2, price2);
            }
        }

        if (openProjects.isEmpty()) {
            System.out.println("No available projects at the moment.");
        }
    }
}