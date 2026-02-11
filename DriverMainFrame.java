import java.awt.*;
import javax.swing.*;

public class DriverMainFrame extends JFrame {
    private JPanel mainContent;
    private CardLayout cardLayout;

    public DriverMainFrame() {
        setTitle("Driver Self-Service Terminal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        // Modular Views - Requirement 6.2
        mainContent.add(createDriverDashboard(), "DASHBOARD");
        mainContent.add(new EntryPanel(), "ENTRY"); 
        mainContent.add(new ExitPanel(), "EXIT");   

        add(mainContent);
    }

    private JPanel createDriverDashboard() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // 1. Welcome Header (Top)
        JLabel welcome = new JLabel("Welcome to Grand Design Parking", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(welcome, BorderLayout.NORTH);

        // 2. Center Buttons (Requirement 3: Entry/Exit)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        buttonPanel.setOpaque(false);

        JButton btnCheckIn = createLargeTile("CHECK-IN", "Enter the Lot", new Color(46, 204, 113));
        JButton btnCheckOut = createLargeTile("CHECK-OUT", "Pay & Exit", new Color(52, 152, 219));

        buttonPanel.add(btnCheckIn);
        buttonPanel.add(btnCheckOut);

        // Navigation Actions
        btnCheckIn.addActionListener(e -> cardLayout.show(mainContent, "ENTRY"));
        btnCheckOut.addActionListener(e -> cardLayout.show(mainContent, "EXIT"));

        panel.add(buttonPanel, BorderLayout.CENTER);

        // 3. Footer Panel (Bottom) - Combined Help Label + Staff Button
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        // Only declare 'help' ONCE here
        JLabel help = new JLabel("Press button to begin. Contact security for assistance.", JLabel.CENTER);
        
        JButton btnStaff = new JButton("Staff Login");
        btnStaff.setFont(new Font("Arial", Font.PLAIN, 10));
        btnStaff.setFocusPainted(false);
        btnStaff.addActionListener(e -> {
            new MainFrame().setVisible(true); // Requirement 6.1: Launch Admin View
        });

        footerPanel.add(help, BorderLayout.CENTER);
        footerPanel.add(btnStaff, BorderLayout.EAST);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createLargeTile(String title, String subtitle, Color bg) {
        JButton btn = new JButton("<html><center><font size='7'><b>" + title + 
                                  "</b></font><br><font size='5'>" + subtitle + 
                                  "</font></center></html>");
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DriverMainFrame().setVisible(true));
    }
}