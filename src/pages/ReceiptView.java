package pages;

import utils.CSVUtils;

import java.util.List;

public class ReceiptView {

    private static final String RECEIPTS_CSV = "data/receipts.csv";

    public static void displayReceiptsByNRIC(String nric) {
        List<String[]> receipts = CSVUtils.readCSV(RECEIPTS_CSV);
        boolean found = false;

        System.out.println("\n===== Your BTO Booking Receipt(s) =====");
        for (String[] row : receipts) {
            if (row.length >= 6 && row[0].equalsIgnoreCase(nric)) {
                found = true;
                System.out.println("--------------------------------------");
                System.out.println("NRIC           : " + row[0]);
                System.out.println("Name           : " + row[1]);
                System.out.println("Age            : " + row[2]);
                System.out.println("Marital Status : " + row[3]);
                System.out.println("Flat Type      : " + row[4]);
                System.out.println("Project Name   : " + row[5]);
            }
        }

        if (!found) {
            System.out.println("No receipts found for NRIC: " + nric);
        }
    }
}