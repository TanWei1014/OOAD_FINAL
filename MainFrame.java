import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private JPanel mainContent; // The container that swaps pages
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("Parking System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar Navigation (Design Principle: Lec 07)
        JPanel sidebar = new JPanel(new GridLayout(10, 1, 5, 5));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, 0));

        // Navigation Buttons
        JButton btnAdmin = createNavBtn("Admin Dashboard");
        JButton btnEntry = createNavBtn("Vehicle Entry");
        JButton btnExit = createNavBtn("Vehicle Exit & Pay");
        JButton btnReports = createNavBtn("Reporting Panel");

        sidebar.add(btnAdmin);
        sidebar.add(btnEntry);
        sidebar.add(btnExit);
        sidebar.add(btnReports);

        // Main Card Container
        // Center Content Area
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        // Registering Modular Panels (Requirement 6)
        // Modularization: Adding the separate UI classes
        mainContent.add(new AdminPanel(), "ADMIN");
        mainContent.add(new EntryPanel(), "ENTRY");
        mainContent.add(new ExitPanel(), "EXIT");
        mainContent.add(new ReportingPanel(), "REPORTS");

        // Navigation Actions
        // Action Listeners to switch "Cards"
        btnAdmin.addActionListener(e -> cardLayout.show(mainContent, "ADMIN"));
        btnEntry.addActionListener(e -> cardLayout.show(mainContent, "ENTRY"));
        btnExit.addActionListener(e -> cardLayout.show(mainContent, "EXIT"));
        btnReports.addActionListener(e -> cardLayout.show(mainContent, "REPORTS"));

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    private JButton createNavBtn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}