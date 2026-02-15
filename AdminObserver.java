import java.io.*;
import java.util.*;

public interface AdminObserver {
    /**
     * Triggered by Entry/Exit panels to refresh the UI.
     */
    void onParkingStatusChanged();

    /**
     * Logic to find which vehicles are currently inside the lot.
     */
    default Map<String, String[]> calculateCurrentOccupancy(String filePath) {
        Map<String, String[]> activeParkers = new LinkedHashMap<>();
        File file = new File(filePath);
        if (!file.exists()) return activeParkers;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                String logType = parts[0].trim(); 
                String plate = parts[1].trim(); 

                if ("ENTRY".equals(logType)) {
                    activeParkers.put(plate, parts);
                } else if ("EXIT".equals(logType)) {
                    activeParkers.remove(plate);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading occupancy: " + e.getMessage());
        }
        return activeParkers;
    }

    /**
     * Logic to calculate historical data (Revenue and Fines).
     * Returns a double array: [0] = Total Revenue, [1] = Total Fines.
     */
    default double[] calculateFinancials(String filePath) {
        double revenue = 0;
        double fines = 0;
        File file = new File(filePath);
        if (!file.exists()) return new double[]{0, 0};

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 1. Calculate Fines (Recorded on ENTRY lines)
                if (line.contains("ENTRY") && line.contains("ReservedViolation: true")) {
                    fines += 50.0;
                }
                
                // 2. Calculate Revenue (Recorded on EXIT lines)
                if (line.contains("EXIT") && line.contains("Paid:")) {
                    try {
                        String amount = line.split("Paid:")[1].replaceAll("[^0-9.]", "").trim();
                        revenue += Double.parseDouble(amount);
                    } catch (Exception e) { /* Ignore parsing errors */ }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading financials: " + e.getMessage());
        }
        return new double[]{revenue, fines};
    }
}
