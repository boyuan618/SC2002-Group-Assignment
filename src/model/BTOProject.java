package model;

import java.time.LocalDate;

public class BTOProject {
    private String projectName;
    private String neighborhood;
    private int twoRoomUnits;
    private int threeRoomUnits;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String managerNric;
    private boolean visible;
    private int officerSlots;

    public BTOProject(String projectName, String neighborhood, int twoRoomUnits, int threeRoomUnits,
            LocalDate openDate, LocalDate closeDate, String managerNric, boolean visible, int officerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.twoRoomUnits = twoRoomUnits;
        this.threeRoomUnits = threeRoomUnits;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.managerNric = managerNric;
        this.visible = visible;
        this.officerSlots = officerSlots;
    }

    // Getters
    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public int getTwoRoomUnits() {
        return twoRoomUnits;
    }

    public int getThreeRoomUnits() {
        return threeRoomUnits;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public String getManagerNric() {
        return managerNric;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getOfficerSlots() {
        return officerSlots;
    }

    // Setters
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setTwoRoomUnits(int units) {
        this.twoRoomUnits = units;
    }

    public void setThreeRoomUnits(int units) {
        this.threeRoomUnits = units;
    }

    public void setOfficerSlots(int officerSlots) {
        this.officerSlots = officerSlots;
    }

    // Helper
    public boolean isWithinApplicationPeriod(LocalDate today) {
        return (today.equals(openDate) || today.isAfter(openDate))
                && (today.equals(closeDate) || today.isBefore(closeDate));
    }

    @Override
    public String toString() {
        return String.join(",",
                projectName,
                neighborhood,
                String.valueOf(twoRoomUnits),
                String.valueOf(threeRoomUnits),
                openDate.toString(),
                closeDate.toString(),
                managerNric,
                String.valueOf(visible),
                String.valueOf(officerSlots));
    }

    public static BTOProject fromCSV(String[] data) {
        return new BTOProject(
                data[0],
                data[1],
                Integer.parseInt(data[2]),
                Integer.parseInt(data[3]),
                LocalDate.parse(data[4]),
                LocalDate.parse(data[5]),
                data[6],
                Boolean.parseBoolean(data[7]),
                Integer.parseInt(data[8]));
    }
}
