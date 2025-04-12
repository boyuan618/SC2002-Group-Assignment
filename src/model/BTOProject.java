package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private String officerList;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public BTOProject(String projectName, String neighborhood, String type1, int units1, int price1,
            String type2, int units2, int price2, LocalDate openDate, LocalDate closeDate,
            String manager, int officerSlot, String officerList) {
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
        this.officerList = officerList;
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

    public String getOfficerList() {
        return officerList;
    }

    public boolean isWithinApplicationPeriod(LocalDate today) {
        return (today.equals(openDate) || today.isAfter(openDate))
                && (today.equals(closeDate) || today.isBefore(closeDate));
    }

    public static BTOProject fromCSV(String[] row) {
        return new BTOProject(
                row[0], // Project Name
                row[1], // Neighborhood
                row[2], // Type 1
                Integer.parseInt(row[3]),
                Integer.parseInt(row[4]),
                row[5], // Type 2
                Integer.parseInt(row[6]),
                Integer.parseInt(row[7]),
                LocalDate.parse(row[8], FORMATTER),
                LocalDate.parse(row[9], FORMATTER),
                row[10], // Manager
                Integer.parseInt(row[11]),
                row[12] // Officer list
        );
    }

    public String[] toCSV() {
        return new String[] {
                projectName, neighborhood, type1, String.valueOf(units1), String.valueOf(price1),
                type2, String.valueOf(units2), String.valueOf(price2),
                openDate.format(FORMATTER), closeDate.format(FORMATTER),
                manager, String.valueOf(officerSlot), officerList
        };
    }
}