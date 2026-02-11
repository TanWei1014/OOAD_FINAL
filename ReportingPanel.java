import java.awt.*;
import javax.swing.*;

public class ReportingPanel extends JPanel {
    public ReportingPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // Requirement 6.3: Reporting
        tabs.addTab("Occupancy Report", new JScrollPane(new JTable(20, 4)));
        tabs.addTab("Revenue Report", new JScrollPane(new JTable(20, 3)));
        tabs.addTab("Outstanding Fines", new JScrollPane(new JTable(20, 4)));

        // Requirement 4 & 6.1: Admin Control
        JPanel adminControl = new JPanel(new FlowLayout());
        adminControl.add(new JLabel("Choose Active Fine Scheme:"));
        adminControl.add(new JComboBox<>(new String[]{"Option A: Fixed", "Option B: Progressive", "Option C: Hourly"}));
        adminControl.add(new JButton("Apply to Future Entries"));

        add(tabs, BorderLayout.CENTER);
        add(adminControl, BorderLayout.SOUTH);
    }
}