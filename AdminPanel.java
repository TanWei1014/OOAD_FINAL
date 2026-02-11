import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Stats Cards (Lec 07: User Interface Principle (Visual feedback for users))
        // Top Area: Statistics Cards (Visual only)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.add(createStatCard("Total Revenue", "RM 0.00", Color.GREEN));
        statsPanel.add(createStatCard("Occupancy", "0%", Color.BLUE));
        statsPanel.add(createStatCard("Total Fines", "RM 0.00", Color.RED));
        statsPanel.add(createStatCard("Parked Vehicles", "0", Color.ORANGE));
        add(statsPanel, BorderLayout.NORTH);

        // 2. Data Table (Reporting module requirement)
        // Middle Area: Table for vehicle records (JTable)
        // Requirement 6.1: View all floors and spots (Data Table)
        String[] cols = {"Spot ID", "Type", "Status", "Vehicle Plate", "Fine Owed"};        DefaultTableModel model = new DefaultTableModel(cols, 15); // 15 empty rows
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Configuration Panel
        // Bottom Area: Fine Configuration UI
        // Requirement 4 & 6.1: Choose Fine Scheme
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBorder(BorderFactory.createTitledBorder("System Configuration"));
        bottom.add(new JLabel("Set Future Fine Scheme: "));
        JComboBox<String> schemeBox = new JComboBox<>(new String[]{
            "Option A: Fixed (RM 50)", 
            "Option B: Progressive", 
            "Option C: Hourly (RM 20/hr)"
        });
        bottom.add(schemeBox);
        bottom.add(new JButton("Apply Changes"));
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color), title));
        JLabel val = new JLabel(value, JLabel.CENTER);
        val.setFont(new Font("Arial", Font.BOLD, 18));
        card.add(val);
        return card;
    }
}