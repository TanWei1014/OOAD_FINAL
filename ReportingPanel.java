import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReportingPanel extends JPanel implements AdminObserver {
    private DefaultTableModel occupancyModel;
    private DefaultTableModel revenueModel;
    private DefaultTableModel finesModel;
    private JTable occupancyTable;

    public ReportingPanel() {
        setLayout(new BorderLayout());

        // 1. Setup Tabs
        JTabbedPane tabs = new JTabbedPane();

        // --- Tab 1: Occupancy Report (The one you asked for) ---
        // Columns: Spot ID, Type, Status, Vehicle Plate
        String[] occupancyCols = {"Spot ID", "Type", "Status", "Vehicle Plate"};
        occupancyModel = new DefaultTableModel(occupancyCols, 0);
        occupancyTable = new JTable(occupancyModel);
        tabs.addTab("Occupancy Report", new JScrollPane(occupancyTable));

        // --- Tab 2: Revenue Report (Placeholder for now) ---
        String[] revenueCols = {"Date", "Transaction ID", "Amount"};
        revenueModel = new DefaultTableModel(revenueCols, 0);
        tabs.addTab("Revenue Report", new JScrollPane(new JTable(revenueModel)));

        // --- Tab 3: Outstanding Fines (Placeholder for now) ---
        String[] fineCols = {"Plate", "Violation Type", "Amount Owed"};
        finesModel = new DefaultTableModel(fineCols, 0);
        tabs.addTab("Outstanding Fines", new JScrollPane(new JTable(finesModel)));

        // 2. Admin Control Panel (Bottom)
        JPanel adminControl = new JPanel(new FlowLayout());
        adminControl.add(new JLabel("Choose Active Fine Scheme:"));
        adminControl.add(new JComboBox<>(new String[]{"Option A: Fixed", "Option B: Progressive", "Option C: Hourly"}));
        adminControl.add(new JButton("Apply to Future Entries"));

        add(tabs, BorderLayout.CENTER);
        add(adminControl, BorderLayout.SOUTH);

        // Load data immediately
        refreshReports();
    }

    @Override
    public void onParkingStatusChanged() {
        refreshReports();
    }

    private void refreshReports() {
        // Clear existing rows to prevent duplicates
        occupancyModel.setRowCount(0);
        
        File file = new File("entry_exit.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                String type = parts[0].trim();   // ENTRY or EXIT
                String plate = parts[1].trim();  // License Plate
                String vehicleType = parts[2].trim(); // Car/Motorcycle
                String spotId = parts[3].trim(); // F1-R1-S1

                if (type.equals("ENTRY")) {
                    // Add row for ENTRY -> Status: Occupied
                    occupancyModel.addRow(new Object[]{
                        spotId, 
                        vehicleType, 
                        "Occupied", 
                        plate
                    });
                } else if (type.equals("EXIT")) {
                    // Add NEW row for EXIT -> Status: Unoccupied
                    occupancyModel.addRow(new Object[]{
                        spotId, 
                        vehicleType, 
                        "Unoccupied", 
                        plate
                    });
                }
            }
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
}