import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;

public class EntryPanel extends JPanel implements ActionListener {
    
    
     private JTextField licenseField;
     private JComboBox<String> vehicleTypeCombo;
     private JCheckBox handicappedCheck;
     private JCheckBox VIP_reservationcheck;
     private JComboBox<String> floorCombo;

     private JButton selectedSpotBtn = null;
     private ParkingSpot selectedSpot = null;

     private java.util.List<JButton> spotButtons = new java.util.ArrayList<>();
     private java.util.Map<JButton, ParkingSpot> spotMap = new java.util.HashMap<>();
     private java.util.Map<String, Floor> floors = new java.util.HashMap<>();
     private Floor currentFloor; // selected floor
     private java.util.Map<String, java.util.List<JButton>> floorButtons = new java.util.HashMap<>();
     private JPanel gridContainer;
     private CardLayout cardLayout;
     private java.util.Map<String, JPanel> floorGrids = new java.util.HashMap<>();


private void initFloors() {
    for (int f = 1; f <= 5; f++) {
        String floorName = "Floor " + f;
        Floor floor = new Floor(floorName);

        int spotNumber = 1;
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 10; col++) {
                ParkingSpot ps;
                String spotId = "F" + f + "-R" + row + "-S" + spotNumber;

                if (row == 1) ps = new Compact(spotId);
                else if (row == 2) ps = new Handicapped(spotId);
                else if (row == 3 && col <= 4) ps = new Reserved(spotId);
                else ps = new Regular(spotId);

                floor.addSpot(ps);
                spotNumber++;
            }
        }

        floors.put(floorName, floor);
        if (f == 1) currentFloor = floor;
    }
    
    // IMPORTANT: Add all floors to the ParkingLot singleton
    for (Floor floor : floors.values()) {
        ParkingLot.getInstance().addFloor(floor);
    }
}

//     private void initFloors() {
//     for (int f = 1; f <= 5; f++) {
//         String floorName = "Floor " + f;
//         Floor floor = new Floor(floorName);

//         int spotNumber = 1;
//         for (int row = 1; row <= 5; row++) {
//             for (int col = 1; col <= 10; col++) {
//                 ParkingSpot ps;
//                 String spotId = "F" + f + "-R" + row + "-S" + spotNumber;

//                 if (row == 1) ps = new Compact(spotId);
//                 else if (row == 2) ps = new Handicapped(spotId);
//                 else if (row == 3 && col <= 4) ps = new Reserved(spotId);
//                 else ps = new Regular(spotId);

//                 floor.addSpot(ps);
//                 spotNumber++;
//             }
//         }

//         floors.put(floorName, floor);
//         if (f == 1) currentFloor = floor;
//     }
// }


    public EntryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createTitledBorder("Vehicle Entry Interface"));

        initFloors();
        ParkingLot.getInstance().setFloors(new ArrayList<>(floors.values()));
        loadOccupiedSpotsFromFile();

        // Left: Vehicle Detail Form (Requirement 2)
        JPanel form = new JPanel(new GridLayout(10, 1, 5, 5));
        form.add(new JLabel("License Plate:"));

        licenseField = new JTextField(10);
        form.add(licenseField);
        
        form.add(new JLabel("Vehicle Type:"));

        
        vehicleTypeCombo = new JComboBox<>(new String[]{"Motorcycle", "Car", "SUV/Truck", "Handicapped"});
        form.add(vehicleTypeCombo);
        vehicleTypeCombo.addActionListener(e -> {
         String type = (String) vehicleTypeCombo.getSelectedItem();

    // Enable handicapped card checkbox only for Handicapped vehicles
        if ("Handicapped".equals(type)) {
        handicappedCheck.setEnabled(true);
        VIP_reservationcheck.setEnabled(true);
      } 
       else {
        handicappedCheck.setEnabled(false);
        handicappedCheck.setSelected(false); // uncheck automatically
        VIP_reservationcheck.setEnabled(false);
        VIP_reservationcheck.setSelected(false);

       }

    updateSpotAvailability();
    });



        handicappedCheck = new JCheckBox("Handicapped Card Holder? (Free in H-Spot)");
        form.add(handicappedCheck);

        VIP_reservationcheck = new JCheckBox("Do you have VIP reservation?");
        form.add(VIP_reservationcheck);


        handicappedCheck.setEnabled(false);
        VIP_reservationcheck.setEnabled(false);
        handicappedCheck.setSelected(false);
        VIP_reservationcheck.setSelected(false);
        
        
        form.add(new JLabel("Select Floor:"));
        

        floorCombo = new JComboBox<>(new String[]{"Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5"});
        form.add(floorCombo);
        floorCombo.addActionListener(e -> {
    String selected = (String) floorCombo.getSelectedItem();
    currentFloor = floors.get(selected);

    selectedSpot = null;
    if (selectedSpotBtn != null) {
        selectedSpotBtn.setBorder(UIManager.getBorder("Button.border"));
        selectedSpotBtn = null;
    }

    spotButtons = floorButtons.get(selected);

    cardLayout.show(gridContainer, selected);

    updateSpotAvailability();
});



        
        JButton btnTicket = new JButton("Generate Ticket (T-PLATE-TIME)");
        form.add(btnTicket);
        btnTicket.addActionListener(this);

        // Center: Spot Selection Grid (Requirement 1 & 2.3)
        // Center: Spot Selection Grid (all floors prebuilt)
        
        cardLayout = new CardLayout();
gridContainer = new JPanel(cardLayout);

spotMap.clear();
floorButtons.clear();
floorGrids.clear();

for (Floor floor : floors.values()) {
    JPanel grid = new JPanel(new GridLayout(5, 10, 5, 5));
    grid.setBorder(BorderFactory.createTitledBorder(
        floor.getFloorId() + " (Compact/Regular/H/Reserved)"
    ));

    java.util.List<JButton> buttons = new ArrayList<>();

    for (ParkingSpot ps : floor.getSpots()) {
        JButton spotBtn = new JButton(ps.getSpotId());

        spotMap.put(spotBtn, ps);
        buttons.add(spotBtn);

        switch (ps.getType()) {
            case "Compact": spotBtn.setBackground(Color.GREEN); break;
            case "Handicapped": spotBtn.setBackground(Color.BLUE); break;
            case "Reserved": spotBtn.setBackground(Color.YELLOW); break;
            case "Regular": spotBtn.setBackground(Color.GREEN); break;
        }

        if (!ps.isAvailable()) {
            spotBtn.setEnabled(false);
            spotBtn.setBackground(Color.RED);
        }

        spotBtn.addActionListener(e -> {
            if (!ps.isAvailable()) return;

            if (selectedSpotBtn != null)
                selectedSpotBtn.setBorder(UIManager.getBorder("Button.border"));

            selectedSpotBtn = spotBtn;
            selectedSpot = ps;
            spotBtn.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        });

        grid.add(spotBtn);
    }

    floorButtons.put(floor.getFloorId(), buttons);
    floorGrids.put(floor.getFloorId(), grid);
    gridContainer.add(grid, floor.getFloorId());
}


  add(form, BorderLayout.WEST);
  add(new JScrollPane(gridContainer), BorderLayout.CENTER);

  String firstFloor = floorCombo.getItemAt(0);
  currentFloor = floors.get(firstFloor);

  cardLayout.show(gridContainer, firstFloor);
 spotButtons = floorButtons.get(firstFloor);

 updateSpotAvailability();



    }


    private void saveEntry(Vehicle v, ParkingSpot s) {
    try (java.io.FileWriter fw = new java.io.FileWriter("entry_exit.txt", true)) {
        fw.write("ENTRY | " + v.getLicenseplate() +
                 " | " + v.getClass().getSimpleName() +
                 " | " + s.getSpotId() +
                 " | " + java.time.LocalDateTime.now() + 
                " | HandicappedCard: " + v.getHandiCappedCard() +
                 " | ReservedViolation: " + v.reservedViolation() + "\n"
                
                );
    } catch (Exception ex) {
        System.out.println("Error saving into file");
    }
}


@Override
public void actionPerformed(ActionEvent e) {
    if (selectedSpot == null) {
        JOptionPane.showMessageDialog(this, "Please select a parking spot first.");
        return;
    }

    String plate = licenseField.getText().trim();
    if (plate.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter license plate.");
        return;
    }

    double unpaid = Payment.getOutstandingBalance(plate);
    if (unpaid > 0) {
        int choice = JOptionPane.showConfirmDialog(this,
            "⚠️ This vehicle has unpaid balance of RM " + String.format("%.2f", unpaid) +
            " from previous visit.\n\n" +
            "This amount will be added to your total when you exit.\n\n" +
            "Continue with entry?",
            "Unpaid Balance Warning",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
    }

    Vehicle vehicle;
    String type = (String) vehicleTypeCombo.getSelectedItem();

    if ("Motorcycle".equals(type)) {
        vehicle = new Motorcycle(plate);
    } else if ("Car".equals(type)) {
        vehicle = new Car(plate);
    } else if ("SUV/Truck".equals(type)) {
        vehicle = new SUV(plate);
    } else { // Handicapped
        boolean hasCard = handicappedCheck.isSelected();
        boolean hasVIP = VIP_reservationcheck.isSelected();
        vehicle = new Handicapped_Vehicle(plate, hasCard);

        if (selectedSpot.getType().equals("Reserved") && !hasVIP) {
            vehicle.setReservedViolation(true);
        }
        
        // ✅ MOVED THIS CODE HERE - Handicapped spot warning
        if (vehicle instanceof Handicapped_Vehicle) {
            Handicapped_Vehicle hv = (Handicapped_Vehicle) vehicle;
            
            if (!hv.getHandiCappedCard() && selectedSpot.getType().equals("Handicapped")) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "⚠️ You do not have a handicapped card but are parking in a Handicapped spot.\n" +
                    "You will be charged RM " + selectedSpot.getHourlyRate() + "/hour.\n\n" +
                    "Would you like to choose a Regular spot instead (RM 5/hour)?\n" +
                    "Click No to continue with this spot, or Yes to select another spot.",
                    "Handicapped Spot Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    // Clear selection and let them choose another spot
                    selectedSpotBtn.setBorder(UIManager.getBorder("Button.border"));
                    selectedSpotBtn = null;
                    selectedSpot = null;
                    return; // Exit the method to let them choose another spot
                }
            }
        }
    }

    selectedSpot.parkVehicle(vehicle);
    
    // IMPORTANT: Also add this floor to ParkingLot if not already there
    ParkingLot lot = ParkingLot.getInstance();
    if (!lot.getFloors().contains(currentFloor)) {
        lot.addFloor(currentFloor);
    }

    // Persist entry
    saveEntry(vehicle, selectedSpot);

    // Generate ticket
    Ticket ticket = new Ticket(vehicle, selectedSpot);
    new TicketUI((JFrame) SwingUtilities.getWindowAncestor(this), ticket).setVisible(true);

    selectedSpotBtn.setEnabled(false);
    selectedSpotBtn.setBackground(Color.RED);
}

//     @Override
//     public void actionPerformed(ActionEvent e) {

//     if (selectedSpot == null) {
//         JOptionPane.showMessageDialog(this, "Please select a parking spot first.");
//         return;
//     }

//     String plate = licenseField.getText().trim();
//     if (plate.isEmpty()) {
//         JOptionPane.showMessageDialog(this, "Enter license plate.");
//         return;
//     }

//     Vehicle vehicle;

//     String type = (String) vehicleTypeCombo.getSelectedItem();

//      if ("Motorcycle".equals(type)) {
//         vehicle = new Motorcycle(plate);

//      } 
//      else if ("Car".equals(type)) {
//         vehicle = new Car(plate);

//     }  
//     else if ("SUV/Truck".equals(type)) {
//        vehicle = new SUV(plate);

//    } 
//     else { // Handicapped
//     boolean hasCard = handicappedCheck.isSelected();
//     boolean hasVIP = VIP_reservationcheck.isSelected();
//     vehicle = new Handicapped_Vehicle(plate, hasCard);


//     if (selectedSpot.getType().equals("Reserved") && !hasVIP) {
//     vehicle.setReservedViolation(true);
// }
//    }

    

//     selectedSpot.parkVehicle(vehicle);

//     // Persist entry
//     saveEntry(vehicle, selectedSpot);

//     // Generate ticket
//     Ticket ticket = new Ticket(vehicle, selectedSpot);
//     new TicketUI((JFrame) SwingUtilities.getWindowAncestor(this), ticket).setVisible(true);

//     selectedSpotBtn.setEnabled(false);
//     selectedSpotBtn.setBackground(Color.RED);
// }

private Vehicle createVehicleFromType(String type, String plate, String line) {
    Vehicle vehicle = null;
    
    if (type.contains("Car") || type.equals("Car")) {
        vehicle = new Car(plate);
    } else if (type.contains("Motorcycle") || type.equals("Motorcycle")) {
        vehicle = new Motorcycle(plate);
    } else if (type.contains("SUV") || type.equals("SUV")) {
        vehicle = new SUV(plate);
    } else if (type.contains("Handicapped") || type.equals("Handicapped_Vehicle")) {
        boolean hasCard = line.contains("HandicappedCard: true");
        vehicle = new Handicapped_Vehicle(plate, hasCard);
    }
    
    if (vehicle != null && line.contains("ReservedViolation: true")) {
        vehicle.setReservedViolation(true);
    }
    
    return vehicle;
}

private void loadOccupiedSpotsFromFile() {
    try (java.io.BufferedReader br = new java.io.BufferedReader(
            new java.io.FileReader("entry_exit.txt"))) {

        String line;
        // First, clear all spots (set them to available)
        for (Floor floor : floors.values()) {
            for (ParkingSpot spot : floor.getSpots()) {
                spot.removeVehicle();
            }
        }
        
        // Then, mark spots as occupied based on ENTRY records without matching EXIT
        java.util.Map<String, Boolean> activeVehicles = new java.util.HashMap<>();
        
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length < 2) continue;
            
            String type = parts[0].trim();
            String plate = parts[1].trim();
            
            if (type.equals("ENTRY")) {
                activeVehicles.put(plate, true);
            } else if (type.equals("EXIT")) {
                activeVehicles.remove(plate);
            }
        }
        br.close();
        
        // Now read the file again to find ENTRY records for active vehicles
        java.io.BufferedReader br2 = new java.io.BufferedReader(
            new java.io.FileReader("entry_exit.txt"));
            
        while ((line = br2.readLine()) != null) {
            if (!line.startsWith("ENTRY")) continue;
            
            String[] parts = line.split("\\|");
            if (parts.length < 5) continue;
            
            String plate = parts[1].trim();
            
            // Only process if this vehicle hasn't exited
            if (activeVehicles.containsKey(plate)) {
                String spotId = parts[3].trim();
                String type = parts[2].trim();
                
                // Find the spot and mark it as occupied
                for (Floor floor : floors.values()) {
                    for (ParkingSpot ps : floor.getSpots()) {
                        if (ps.getSpotId().equals(spotId)) {
                            // USE THE NEW METHOD HERE
                            Vehicle vehicle = createVehicleFromType(type, plate, line);
                            if (vehicle != null) {
                                // Parse and set entry time from the file
                                try {
                                    String timeStr = parts[4].trim();
                                    java.time.LocalDateTime entryTime = 
                                        java.time.LocalDateTime.parse(timeStr);
                                    vehicle.setEntryTime(entryTime);
                                } catch (Exception e) {
                                    // If time parsing fails, use current time
                                    System.out.println("Could not parse time, using current");
                                }
                                
                                ps.parkVehicle(vehicle);
                                ParkingLot.getInstance().addFloor(floor);
                                ParkingLot.getInstance().incrementOccupiedSpots();
                                System.out.println("Loaded vehicle " + plate + " into spot " + spotId);
                            }
                            break;
                        }
                    }
                }
            }
        }
        br2.close();
        
    } catch (Exception e) {
        System.out.println("No previous occupancy file found or error reading: " + e.getMessage());
        e.printStackTrace();
    }
}

//     private void loadOccupiedSpotsFromFile() {
//     try (java.io.BufferedReader br = new java.io.BufferedReader(
//             new java.io.FileReader("entry_exit.txt"))) {

//         String line;
//         while ((line = br.readLine()) != null) {

//             // We only care about ENTRY records
//             if (!line.startsWith("ENTRY")) continue;

//             // ENTRY | PLATE | VehicleType | SpotID | time
//             String[] parts = line.split("\\|");
//             if (parts.length < 4) continue;

//             String spotId = parts[3].trim();

//             // Find the ParkingSpot by spotId
//             for (Floor floor : floors.values()) {
//                 for (ParkingSpot ps : floor.getSpots()) {
//                     if (ps.getSpotId().equals(spotId)) {
//                         ps.parkVehicle(null); // mark occupied
//                         break;
//                     }
//                 }
//             }
//         }

//     } catch (Exception e) {
//         System.out.println("No previous occupancy file found.");
//     }
// }

public void refreshSpotAvailability() {
    // This method will be called when returning to EntryPanel
    // It updates the spot buttons based on current availability
    
    String currentFloorName = (String) floorCombo.getSelectedItem();
    currentFloor = floors.get(currentFloorName);
    spotButtons = floorButtons.get(currentFloorName);
    
    // Update all spot buttons
    for (JButton btn : spotButtons) {
        ParkingSpot ps = spotMap.get(btn);
        
        if (!ps.isAvailable()) {
            btn.setEnabled(false);
            btn.setBackground(Color.RED);
        } else {
            // Reset to original color based on spot type
            switch (ps.getType()) {
                case "Compact": btn.setBackground(Color.GREEN); break;
                case "Handicapped": btn.setBackground(Color.BLUE); break;
                case "Reserved": btn.setBackground(Color.YELLOW); break;
                case "Regular": btn.setBackground(Color.GREEN); break;
            }
            btn.setEnabled(true);
        }
    }
    
    // Reapply vehicle type restrictions
    updateSpotAvailability();
}


private void updateSpotAvailability() {
    String type = (String) vehicleTypeCombo.getSelectedItem();

    for (JButton btn : spotButtons) {
        ParkingSpot ps = spotMap.get(btn);

        if (!ps.isAvailable()) {
            btn.setEnabled(false);
            btn.setBackground(Color.RED);
            btn.setToolTipText("Occupied");
            continue;
        }

        boolean allowed = false;
        String reason = "";

        switch (type) {
            case "Motorcycle":
                allowed = ps.getType().equals("Compact");
                reason = allowed ? "Available" : "Motorcycles can only park in Compact spots";
                break;
            case "Car":
                allowed = ps.getType().equals("Compact") || ps.getType().equals("Regular");
                reason = allowed ? "Available" : "Cars can only park in Compact or Regular spots";
                break;
            case "SUV/Truck":
                allowed = ps.getType().equals("Regular");
                reason = allowed ? "Available" : "SUVs can only park in Regular spots";
                break;
            case "Handicapped":
                allowed = true; // Can park anywhere
                reason = "Available for handicapped vehicles";
                break;
        }

        btn.setEnabled(allowed);
        btn.setToolTipText(reason);

        if (!allowed) {
            btn.setBackground(Color.LIGHT_GRAY);
        } else {
            // Set color based on spot type
            switch (ps.getType()) {
                case "Compact": btn.setBackground(Color.GREEN); break;
                case "Handicapped": btn.setBackground(Color.BLUE); break;
                case "Reserved": btn.setBackground(Color.YELLOW); break;
                case "Regular": btn.setBackground(Color.GREEN); break;
            }
        }
    }
}


class TicketUI extends JDialog{


    public TicketUI(JFrame parent, Ticket ticket) {
        super(parent, "Parking Ticket", true); // modal dialog
        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        infoPanel.add(new JLabel("Ticket ID: " + ticket.getTicketId()));
        infoPanel.add(new JLabel("License Plate: " + ticket.getVehicle().getLicenseplate()));
        infoPanel.add(new JLabel("Vehicle Type: " + ticket.getVehicle().getClass().getSimpleName()));
        infoPanel.add(new JLabel("Spot ID: " + ticket.getSpot().getSpotId()));
        infoPanel.add(new JLabel("Spot Type: " + ticket.getSpot().getClass().getSimpleName()));
        infoPanel.add(new JLabel("Entry Time: " + ticket.getEntryTime().format(fmt)));

        JButton btnOk = new JButton("OK");

        btnOk.addActionListener(e -> dispose());

        add(infoPanel, BorderLayout.CENTER);
        add(btnOk, BorderLayout.SOUTH);
    }
}
}