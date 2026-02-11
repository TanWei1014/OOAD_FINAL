import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ParkingSystem extends JFrame {

    public ParkingSystem() {
        setTitle("Modular Parking Admin System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Modular Design: Admin Panel is a separate component
        add(new AdminPanel()); 
        
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // Ensures the GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new ParkingSystem().setVisible(true);
        });
    }
}

class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        
        // Top: Statistics (High Cohesion - Lecture 8)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        statsPanel.add(new JLabel("Occupancy: 85%", JLabel.CENTER));
        statsPanel.add(new JLabel("Total Revenue: RM 1,250", JLabel.CENTER));
        statsPanel.add(new JLabel("Active Fines: 5", JLabel.CENTER));
        add(statsPanel, BorderLayout.NORTH);

        // Center: Data Reporting (Professional JTable)
        String[] cols = {"Vehicle ID", "Floor", "Status", "Fine Owed"};
        Object[][] data = {{"ABC1234", "1", "Parked", "RM 0"}, {"WXY9988", "3", "Overdue", "RM 50"}};
        JTable table = new JTable(new DefaultTableModel(data, cols));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: Fine Management (Strategy Selection)
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Select Fine Scheme:"));
        controlPanel.add(new JComboBox<>(new String[]{"Fixed Rate", "Progressive", "Hourly"}));
        controlPanel.add(new JButton("Update System"));
        add(controlPanel, BorderLayout.SOUTH);
    }
}