import java.awt.*;
import javax.swing.*;

public class EntryPanel extends JPanel {
    public EntryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createTitledBorder("Vehicle Entry Interface"));

        // Left: Vehicle Detail Form (Requirement 2)
        JPanel form = new JPanel(new GridLayout(10, 1, 5, 5));
        form.add(new JLabel("License Plate:"));
        form.add(new JTextField(10));
        form.add(new JLabel("Vehicle Type:"));
        form.add(new JComboBox<>(new String[]{"Motorcycle", "Car", "SUV/Truck", "Handicapped"}));
        form.add(new JCheckBox("Handicapped Card Holder? (Free in H-Spot)"));
        form.add(new JLabel("Select Floor:"));
        form.add(new JComboBox<>(new String[]{"Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5"}));
        
        JButton btnTicket = new JButton("Generate Ticket (T-PLATE-TIME)");
        form.add(btnTicket);

        // Center: Spot Selection Grid (Requirement 1 & 2.3)
        JPanel grid = new JPanel(new GridLayout(5, 10, 5, 5));
        grid.setBorder(BorderFactory.createTitledBorder("Spot Selection (Compact/Regular/H/Reserved)"));
        for(int i=1; i<=50; i++) {
            JButton spot = new JButton("S" + i);
            spot.setBackground(Color.GREEN); // Available
            grid.add(spot);
        }

        add(form, BorderLayout.WEST);
        add(new JScrollPane(grid), BorderLayout.CENTER);
    }
}