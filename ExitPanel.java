import java.awt.*;
import javax.swing.*;

public class ExitPanel extends JPanel {
    public ExitPanel() {
        setLayout(new BorderLayout(20, 20));
        
        // Top: Search
        JPanel top = new JPanel();
        top.add(new JLabel("Enter License Plate:"));
        top.add(new JTextField(12));
        top.add(new JButton("Find Vehicle"));
        add(top, BorderLayout.NORTH);

        // Center: Receipt & Fee Breakdown (Requirement 5)
        JPanel receipt = new JPanel(new GridLayout(8, 1));
        receipt.setBorder(BorderFactory.createTitledBorder("Payment & Receipt"));
        receipt.add(new JLabel(" Entry Time: 10:00 AM | Exit Time: 12:00 PM"));
        receipt.add(new JLabel(" Duration: 2 Hours (Ceiling Rounding)"));
        receipt.add(new JLabel(" Parking Fee: 2 hrs x RM 5.00 = RM 10.00"));
        receipt.add(new JLabel(" Fines Due: RM 0.00 (Previous unpaid fines included)"));
        receipt.add(new JLabel(" TOTAL DUE: RM 10.00"));
        
        JPanel payMethods = new JPanel();
        payMethods.add(new JLabel("Payment Method:"));
        payMethods.add(new JRadioButton("Cash"));
        payMethods.add(new JRadioButton("Card"));
        receipt.add(payMethods);

        add(receipt, BorderLayout.CENTER);
        add(new JButton("Process Payment & Mark Spot Available"), BorderLayout.SOUTH);
    }
}