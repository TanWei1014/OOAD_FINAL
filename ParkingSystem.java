//import java.awt.*;
import javax.swing.*;
//import javax.swing.table.DefaultTableModel;

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

