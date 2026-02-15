import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends JPanel implements AdminObserver {
    // Fields for dynamic updates
    private JLabel revenueLabel, occupancyLabel, finesLabel, parkedLabel;
    private DefaultTableModel tableModel;
    private final int TOTAL_SPOTS = 250;
    private final String FILE_PATH = "entry_exit.txt";

    public AdminPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize labels as fields
        revenueLabel = new JLabel("RM 0.00", JLabel.CENTER);
        occupancyLabel = new JLabel("0%", JLabel.CENTER);
        finesLabel = new JLabel("RM 0.00", JLabel.CENTER);
        parkedLabel = new JLabel("0", JLabel.CENTER);

        // 1. Stats Cards (Top)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.add(createStatCard("Total Revenue", revenueLabel, Color.GREEN));
        statsPanel.add(createStatCard("Occupancy", occupancyLabel, Color.BLUE));
        statsPanel.add(createStatCard("Total Fines", finesLabel, Color.RED));
        statsPanel.add(createStatCard("Parked Vehicles", parkedLabel, Color.ORANGE));
        add(statsPanel, BorderLayout.NORTH);

        // 2. Data Table (Center)
        String[] columns = {"Spot ID", "Type", "Status", "Vehicle Plate", "Fine Owed"};
        tableModel = new DefaultTableModel(columns, 0); 
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Configuration Panel (Bottom)
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        configPanel.setBorder(BorderFactory.createTitledBorder("System Configuration"));
        configPanel.add(new JLabel("Set Future Fine Scheme: "));
        
        JComboBox<String> schemeBox = new JComboBox<>(new String[]{
            "Option A: Fixed (RM 50)", 
            "Option B: Progressive", 
            "Option C: Hourly (RM 20/hr)"
        });
        configPanel.add(schemeBox);
        configPanel.add(new JButton("Apply Changes"));
        add(configPanel, BorderLayout.SOUTH);

        // Load initial data
        onParkingStatusChanged();
    }

    /**
     * Implementing the Observer method.
     * This uses the default logic you placed in AdminObserver.
     */
   @Override
public void onParkingStatusChanged() {
    // 1. Get fresh financial data from the file
    double[] stats = calculateFinancials("entry_exit.txt");
    revenueLabel.setText(String.format("RM %.2f", stats[0]));
    finesLabel.setText(String.format("RM %.2f", stats[1]));

    // 2. Get currently active vehicles (Entry minus Exit)
    Map<String, String[]> activeParkers = calculateCurrentOccupancy("entry_exit.txt");
    parkedLabel.setText(String.valueOf(activeParkers.size()));
    
    // Calculate occupancy % based on 250 total spots
    double occupancyPercent = (activeParkers.size() / 250.0) * 100;
    occupancyLabel.setText(String.format("%.1f%%", occupancyPercent));

    // 3. Refresh the Table View
    tableModel.setRowCount(0);
    for (String[] data : activeParkers.values()) {
        // data[3]=SpotID, data[2]=Type, data[1]=Plate
        // Check if this specific record has a fine
        boolean isViolation = Arrays.toString(data).contains("ReservedViolation: true");
        
        tableModel.addRow(new Object[]{ 
            data[3].trim(), 
            data[2].trim(), 
            "Occupied", 
            data[1].trim(), 
            isViolation ? "RM 50.00" : "RM 0.00" 
        });
    }
}

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color), title));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        card.add(valueLabel);
        return card;
    }
}