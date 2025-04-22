package model;

import java.util.*;
import utils.CSVUtils;

public class Enquiry {
    final private int enquiryid;
    final private String enquirerNRIC;
    final private String projectName;
    private String title;
    private String detail;
    private String response;

    public static final String ENQUIRIES_CSV = "data/FlatEnquiries.csv";

    public Enquiry(int enquiryid, String enquirerNRIC, String projectName, String title, String detail,
            String response) {
        this.enquiryid = enquiryid;
        this.enquirerNRIC = enquirerNRIC;
        this.projectName = projectName;
        this.title = title;
        this.detail = detail;
        this.response = response;
    }

    public int getId() {
        return enquiryid;
    }

    public String getEnquirerNRIC() {
        return enquirerNRIC;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String[] toCSVRow() {
        return new String[] { String.valueOf(enquiryid), enquirerNRIC, projectName, title, detail, response };
    }

    // Add to CSVUtils
    public static List<Enquiry> getEnquiries() {
        List<String[]> raw = CSVUtils.readCSV(ENQUIRIES_CSV);
        List<Enquiry> list = new ArrayList<>();
        for (String[] row : raw) {
            if (row.length >= 5)
                list.add(Enquiry.fromCSVRow(row));
        }
        return list;
    }

    public static void writeEnquiries(List<Enquiry> enquirers) {
        enquirers.sort(Comparator.comparingInt(Enquiry::getId));
        List<String[]> rows = new ArrayList<>();
        for (Enquiry e : enquirers) {
            rows.add(e.toCSVRow());
        }
        CSVUtils.writeCSV(ENQUIRIES_CSV, rows);
    }

    public static void appendEnquiry(Enquiry e) {
        CSVUtils.appendToCSV(ENQUIRIES_CSV, e.toCSVRow());
    }

    public static Enquiry fromCSVRow(String[] row) {
        return new Enquiry(Integer.parseInt(row[0]), row[1], row[2], row[3], row[4], row[5]);
    }

    // Update Enquiry Method
    public static boolean updateEnquiry(String nric, int index, String newTitle, String newDetail) {
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        int count = -1;

        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i).getEnquirerNRIC().equals(nric)) {
                count++;
                if (count == index) {
                    Enquiry enquiry = enquiries.get(i);
                    enquiry.setTitle(newTitle);
                    enquiry.setDetail(newDetail);
                    Enquiry.writeEnquiries(enquiries);
                    return true;
                }
            }
        }
        return false;
    }

    // Remove Enquiry Method
    public static boolean removeEnquiry(String nric, int index) {
        List<Enquiry> enquiries = Enquiry.getEnquiries();
        int count = -1;

        for (int i = 0; i < enquiries.size(); i++) {
            if (enquiries.get(i).getEnquirerNRIC().equals(nric)) {
                count++;
                if (count == index) {
                    // Check if there is reply alr
                    if (enquiries.get(i).getResponse() != null) {
                        System.out.println("You cannot delete as there is already a response!");
                        return false;
                    }
                    enquiries.remove(i);
                    Enquiry.writeEnquiries(enquiries);
                    return true;
                }
            }
        }
        return false;
    }
}
