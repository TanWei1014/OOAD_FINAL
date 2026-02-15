import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class ExitPanel extends JPanel {
    private JTextField plateSearchField;
    private JLabel lblEntry, lblExit, lblDuration, lblFee, lblFines, lblTotal;
    private JLabel lblParkingBreakdown, lblFineBreakdown;
    private JRadioButton rbCash, rbCard;
    private double currentTotal = 0.0;
    private double unpaidFines = 0.0;
    private double parkingFee = 0.0;
    private double currentFine = 0.0;
    private Vehicle currentVehicle = null;
    private ParkingSpot currentSpot = null;

    public ExitPanel() {
        setLayout(new BorderLayout(20, 20));
        
        // 1. Search Bar
        JPanel top = new JPanel();
        plateSearchField = new JTextField(12);
        JButton btnFind = new JButton("Find Vehicle");
        top.add(new JLabel("Enter License Plate:"));
        top.add(plateSearchField);
        top.add(btnFind);
        add(top, BorderLayout.NORTH);

        // 2. Dynamic Receipt (Requirement 5 & 6)
        JPanel receipt = new JPanel();
        receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
        receipt.setBorder(BorderFactory.createTitledBorder("Payment Receipt"));
        
        // Time information
        JPanel timePanel = new JPanel(new GridLayout(3, 1));
        timePanel.setBorder(BorderFactory.createTitledBorder("Time Information"));
        lblEntry = new JLabel(" Entry Time: --:--");
        lblExit = new JLabel(" Exit Time: --:--");
        lblDuration = new JLabel(" Duration: 0 Hours");
        timePanel.add(lblEntry);
        timePanel.add(lblExit);
        timePanel.add(lblDuration);
        
        // Fee breakdown
        JPanel feePanel = new JPanel(new GridLayout(4, 1));
        feePanel.setBorder(BorderFactory.createTitledBorder("Fee Breakdown"));
        lblParkingBreakdown = new JLabel(" Parking Fee: --");
        lblFee = new JLabel(" Subtotal Parking: RM 0.00");
        lblFineBreakdown = new JLabel(" Fines: --");
        lblFines = new JLabel(" Total Fines: RM 0.00");
        feePanel.add(lblParkingBreakdown);
        feePanel.add(lblFee);
        feePanel.add(lblFineBreakdown);
        feePanel.add(lblFines);
        
        // Total
        lblTotal = new JLabel(" TOTAL DUE: RM 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(Color.RED);
        
        // Payment Method
        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        payPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        rbCash = new JRadioButton("Cash", true);
        rbCard = new JRadioButton("Card");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbCash); 
        bg.add(rbCard);
        payPanel.add(rbCash);
        payPanel.add(rbCard);
        
        // Add all components to receipt
        receipt.add(timePanel);
        receipt.add(Box.createRigidArea(new Dimension(0, 10)));
        receipt.add(feePanel);
        receipt.add(Box.createRigidArea(new Dimension(0, 10)));
        receipt.add(payPanel);
        receipt.add(Box.createRigidArea(new Dimension(0, 10)));
        receipt.add(lblTotal);

        add(new JScrollPane(receipt), BorderLayout.CENTER);

        // 3. Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton btnPay = new JButton("Process Payment & Generate Receipt");
        btnPay.setBackground(new Color(46, 204, 113));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(btnPay);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        btnFind.addActionListener(e -> calculateFees());
        btnPay.addActionListener(e -> processPayment());
    }

private void calculateFees() {
    String plate = plateSearchField.getText().trim();
    System.out.println("\n=== SEARCHING FOR VEHICLE: " + plate + " ===");
    
    if (plate.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a license plate number.");
        return;
    }
    
    // Check what's in ParkingLot
    ParkingLot lot = ParkingLot.getInstance();
    System.out.println("ParkingLot has " + lot.getFloors().size() + " floors");
    
    for (Floor floor : lot.getFloors()) {
        System.out.println("  Floor " + floor.getFloorId() + " has " + 
                          floor.getSpots().size() + " spots");
        for (ParkingSpot spot : floor.getSpots()) {
            Vehicle v = spot.getCurrentVehicle();
            if (v != null) {
                System.out.println("    Spot " + spot.getSpotId() + 
                                 " has vehicle: " + v.getLicenseplate());
            }
        }
    }
    
    // Try to find the spot
    currentSpot = findSpotByVehiclePlate(plate);
    
    if(currentSpot != null && currentSpot.getCurrentVehicle() != null) {
        System.out.println("FOUND VEHICLE in memory!");
        currentVehicle = currentSpot.getCurrentVehicle();
        
        // === REST OF CALCULATION CODE STARTS HERE ===
        
        // Get current time as exit time
        long exitTime = System.currentTimeMillis();
        currentVehicle.setExitTime(exitTime);
        
        // Calculate hours parked
        long hoursParked = currentVehicle.getParkingDurationHours();
        
        // REUSE: Use Payment class for calculations
        // 1. Calculate parking fee using Payment class
        parkingFee = Payment.calculateParkingFee(currentVehicle, currentSpot);
        
        // 2. Calculate overstaying fine using Payment class
        currentFine = Payment.getOnlyFine(hoursParked);
        
        // 3. Add reserved violation fine if applicable
        currentFine += Payment.getReservedViolationFine(currentVehicle);
        
        // 4. Get unpaid fines from previous sessions
        unpaidFines = getUnpaidFinesFromFile(currentVehicle.getLicenseplate());
        
        // 5. Calculate total using Payment class (includes parking fee + current fine)
        double calculatedTotal = Payment.calculateTotal(currentVehicle, currentSpot);
        // Add previous unpaid fines to the total
        currentTotal = calculatedTotal + unpaidFines;
        
        // Format times for display
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime entryTime = java.time.Instant.ofEpochMilli(currentVehicle.getEntryTime())
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime();
        LocalDateTime exitTimeLDT = java.time.Instant.ofEpochMilli(exitTime)
                                 .atZone(java.time.ZoneId.systemDefault())
                                 .toLocalDateTime();
        
        // Get rate for display
        double rate = parkingFee / hoursParked;        
        // Update UI
        lblEntry.setText(" Entry Time: " + entryTime.format(fmt));
        lblExit.setText(" Exit Time: " + exitTimeLDT.format(fmt));
        lblDuration.setText(" Duration: " + hoursParked + " Hours");
        
        // Fee breakdown - using Payment class values
        lblParkingBreakdown.setText(String.format(" Parking: %d hrs × RM %.2f/hr = RM %.2f", 
                                    hoursParked, rate, parkingFee));
        lblFee.setText(String.format(" Subtotal Parking: RM %.2f", parkingFee));
        
        // Fine breakdown
        String fineDetails = "";
        if (currentFine > 0 || unpaidFines > 0) {
            if (currentFine > 0) {
                String fineType = currentVehicle.reservedViolation() ? 
                    "(Overstay + Reserved Violation)" : "(Overstay)";
                fineDetails += "Current fine " + fineType + ": RM " + 
                              String.format("%.2f", currentFine);
            }
            if (unpaidFines > 0) {
                if (!fineDetails.isEmpty()) fineDetails += " + ";
                fineDetails += "Previous unpaid: RM " + String.format("%.2f", unpaidFines);
            }
        } else {
            fineDetails = "No fines";
        }
        lblFineBreakdown.setText(" Fines: " + fineDetails);
        lblFines.setText(String.format(" Total Fines: RM %.2f", (currentFine + unpaidFines)));
        
        lblTotal.setText(String.format(" TOTAL DUE: RM %.2f", currentTotal));
        
        // === REST OF CALCULATION CODE ENDS HERE ===
        
    } else {
        System.out.println("Vehicle not found in memory, checking file...");
        // Try to load from file
        boolean loaded = loadVehicleFromFile(plate);
        if (!loaded) {
            JOptionPane.showMessageDialog(this, "Vehicle not found in system!");
        }
    }
}

private boolean loadVehicleFromFile(String plate) {
    try (BufferedReader br = new BufferedReader(new FileReader("entry_exit.txt"))) {
        String line;
        String lastEntry = null;
        boolean hasExit = false;
        
        // Read all lines to find the last ENTRY without an EXIT
        while ((line = br.readLine()) != null) {
            if (line.startsWith("ENTRY") && line.contains(plate)) {
                lastEntry = line;
                hasExit = false;
                System.out.println("Found ENTRY: " + line);
            } else if (line.startsWith("EXIT") && line.contains(plate)) {
                hasExit = true;
                System.out.println("Found EXIT: " + line);
            }
        }
        
        if (lastEntry != null && !hasExit) {
            System.out.println("Vehicle found in file, recreating in memory...");
            return recreateVehicleFromFile(lastEntry);
        } else if (hasExit) {
            JOptionPane.showMessageDialog(this, "This vehicle has already exited!");
        } else {
            JOptionPane.showMessageDialog(this, "No record found for plate: " + plate);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
    }
    return false;
}

private boolean recreateVehicleFromFile(String entryLine) {
    try {
        // ENTRY | BJH 7777 | Car | S2 | 2026-02-13T20:59:21.662274400
        String[] parts = entryLine.split("\\|");
        if (parts.length < 5) {
            System.out.println("Invalid entry format: " + entryLine);
            return false;
        }
        
        String plate = parts[1].trim();
        String type = parts[2].trim();
        String spotId = parts[3].trim();
        String timeStr = parts[4].trim();
        
        System.out.println("Recreating: Plate=" + plate + ", Type=" + type + 
                         ", Spot=" + spotId + ", Time=" + timeStr);
        
        // Create vehicle
        Vehicle vehicle = null;
        if (type.contains("Car")) {
            vehicle = new Car(plate);
        } else if (type.contains("Motorcycle")) {
            vehicle = new Motorcycle(plate);
        } else if (type.contains("SUV")) {
            vehicle = new SUV(plate);
        } else if (type.contains("Handicapped")) {
            boolean hasCard = entryLine.contains("HandicappedCard: true");
            vehicle = new Handicapped_Vehicle(plate, hasCard);
        }
        
        if (vehicle == null) {
            System.out.println("Could not create vehicle of type: " + type);
            return false;
        }
        
        // Parse and set entry time
        LocalDateTime entryTime = LocalDateTime.parse(timeStr);
        vehicle.setEntryTime(entryTime);
        
        // Check for reserved violation
        if (entryLine.contains("ReservedViolation: true")) {
            vehicle.setReservedViolation(true);
        }
        
        // Find the spot and park the vehicle
        ParkingLot lot = ParkingLot.getInstance();
        for (Floor floor : lot.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.getSpotId().equals(spotId)) {
                    spot.parkVehicle(vehicle);
                    System.out.println("Successfully parked vehicle in spot: " + spotId);
                    
                    // Set this as current vehicle and spot
                    currentVehicle = vehicle;
                    currentSpot = spot;
                    
                    // Now calculate fees with this vehicle
                    calculateFeesWithVehicle();
                    return true;
                }
            }
        }
        
        System.out.println("Could not find spot: " + spotId);
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

private void calculateFeesWithVehicle() {
    if (currentVehicle == null || currentSpot == null) return;
    
    // Get current time as exit time
    long exitTime = System.currentTimeMillis();
    currentVehicle.setExitTime(exitTime);
    
    // Calculate hours parked
    long hoursParked = currentVehicle.getParkingDurationHours();
    
    // CALCULATE parking fee using your special logic
    parkingFee = Payment.calculateParkingFee(currentVehicle, currentSpot);
    
    // CALCULATE the effective rate (what they actually paid per hour)
    double effectiveRate = parkingFee / hoursParked;
    
    // Rest of calculations...
    currentFine = Payment.getOnlyFine(hoursParked);
    currentFine += Payment.getReservedViolationFine(currentVehicle);
    unpaidFines = Payment.getOutstandingBalance(currentVehicle.getLicenseplate());
    
    double calculatedTotal = Payment.calculateTotal(currentVehicle, currentSpot);
    currentTotal = calculatedTotal + unpaidFines;
    
    // Format times
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    LocalDateTime entryTime = java.time.Instant.ofEpochMilli(currentVehicle.getEntryTime())
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
    LocalDateTime exitTimeLDT = java.time.Instant.ofEpochMilli(exitTime)
                             .atZone(java.time.ZoneId.systemDefault())
                             .toLocalDateTime();
    
    // UPDATE UI with CORRECT effective rate
    lblEntry.setText(" Entry Time: " + entryTime.format(fmt));
    lblExit.setText(" Exit Time: " + exitTimeLDT.format(fmt));
    lblDuration.setText(" Duration: " + hoursParked + " Hours");
    
    // FIXED: Use effectiveRate instead of the wrong rate
    lblParkingBreakdown.setText(String.format(" Parking: %d hrs × RM %.2f/hr = RM %.2f", 
                                hoursParked, effectiveRate, parkingFee));
    
    lblFee.setText(String.format(" Subtotal Parking: RM %.2f", parkingFee));
    
    // Fine breakdown
    String fineDetails = (currentFine > 0 || unpaidFines > 0) ? 
        "Fines: RM " + String.format("%.2f", currentFine + unpaidFines) : "No fines";
    
    lblFineBreakdown.setText(" Fines: " + fineDetails);
    lblFines.setText(String.format(" Total Fines: RM %.2f", (currentFine + unpaidFines)));
    lblTotal.setText(String.format(" TOTAL DUE: RM %.2f", currentTotal));
}

private void processPayment() {
    if (currentVehicle == null || currentSpot == null) {
        JOptionPane.showMessageDialog(this, "Please find a vehicle first!");
        return;
    }
    
    String plate = plateSearchField.getText().trim();
    String method = rbCash.isSelected() ? "Cash" : "Card";
    
    // Calculate current stay fees
    long hours = currentVehicle.getParkingDurationHours();
    parkingFee = Payment.calculateParkingFee(currentVehicle, currentSpot);
    currentFine = Payment.calculateFine(currentVehicle, hours);
    
    // Get previous unpaid balance from payment.txt
    double previousUnpaid = Payment.getOutstandingBalance(plate);
    unpaidFines = previousUnpaid;
    
    double currentStayTotal = parkingFee + currentFine;
    double totalDue = currentStayTotal + previousUnpaid;
    currentTotal = totalDue;
    
    // Show payment summary
    String summary = String.format(
        "Current Stay:\n" +
        "  Parking Fee: RM %.2f\n" +
        "  Fine: RM %.2f\n" +
        "  Subtotal: RM %.2f\n\n" +
        "Previous Unpaid: RM %.2f\n\n" +
        "TOTAL DUE: RM %.2f",
        parkingFee, currentFine, currentStayTotal, previousUnpaid, totalDue);
    
    JOptionPane.showMessageDialog(this, summary, "Payment Summary", JOptionPane.INFORMATION_MESSAGE);
    
    // Ask for payment
    String input = JOptionPane.showInputDialog(this, 
        "Enter amount to pay (RM):", 
        String.format("%.2f", totalDue));
    
    if (input == null) return;
    
    double amountPaid;
    try {
        amountPaid = Double.parseDouble(input);
        if (amountPaid < 0) amountPaid = 0;
        if (amountPaid > totalDue) amountPaid = totalDue;
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid amount!");
        return;
    }
    
    double remainingBalance = totalDue - amountPaid;
    
    // 1. SAVE TO payment.txt
    Payment.savePaymentRecord(currentVehicle, currentSpot, amountPaid, method, previousUnpaid);
    
    // 2. SAVE TO entry_exit.txt
    Payment.saveExit(plate, amountPaid, method, remainingBalance);
    
    // 3. IMPORTANT: Remove vehicle from spot in ParkingLot singleton
    currentSpot.removeVehicle();
    ParkingLot.getInstance().decrementOccupiedSpots();
    
    // 4. CRITICAL: Also update the spot in all floor maps
    // Get the spot ID and find it in ParkingLot to ensure it's marked available
    String spotId = currentSpot.getSpotId();
    ParkingLot lot = ParkingLot.getInstance();
    for (Floor floor : lot.getFloors()) {
        for (ParkingSpot spot : floor.getSpots()) {
            if (spot.getSpotId().equals(spotId)) {
                if (!spot.isAvailable()) {
                    spot.removeVehicle();
                    System.out.println("Forcefully removed vehicle from spot: " + spotId);
                }
                break;
            }
        }
    }
    
    // 5. Generate receipt
    generateReceipt(plate, method, amountPaid, remainingBalance, previousUnpaid, currentStayTotal);
    
    // Show confirmation
    String confirmMsg;
    if (remainingBalance == 0) {
        confirmMsg = "✅ Payment complete! Vehicle has exited.\nSpot " + spotId + " is now available.";
    } else {
        confirmMsg = String.format(
            "Payment received: RM %.2f\n" +
            "Remaining balance: RM %.2f\n\n" +
            "Vehicle has exited. Spot %s is now available.\n" +
            "Balance will be added to next visit.",
            amountPaid, remainingBalance, spotId);
    }
    
    JOptionPane.showMessageDialog(this, confirmMsg);
    
    // Clear UI
    clearDisplay();
    plateSearchField.setText("");
    currentVehicle = null;
    currentSpot = null;
}

// Updated receipt method
private void generateReceipt(String plate, String method, double amountPaid, 
                             double remainingBalance, double previousUnpaid,
                             double currentStayTotal) {
    StringBuilder receipt = new StringBuilder();
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    receipt.append("# PARKING RECEIPT\n");
    receipt.append(LocalDateTime.now().format(fmt)).append("\n\n");
    
    if (currentVehicle != null && currentSpot != null) {
        LocalDateTime entryTime = java.time.Instant.ofEpochMilli(currentVehicle.getEntryTime())
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime();
        LocalDateTime exitTime = java.time.Instant.ofEpochMilli(currentVehicle.getExitTime())
                               .atZone(java.time.ZoneId.systemDefault())
                               .toLocalDateTime();
        long hours = currentVehicle.getParkingDurationHours();
        
        receipt.append("License Plate: ").append(plate).append("\n");
        receipt.append("Vehicle Type: ").append(currentVehicle.getClass().getSimpleName()).append("\n");
        receipt.append("Spot ID: ").append(currentSpot.getSpotId()).append("\n");
        receipt.append("Spot Type: ").append(currentSpot.getType()).append("\n");
        
        if (currentVehicle.getHandiCappedCard() && currentSpot instanceof Handicapped) {
            receipt.append("Handicapped Discount: FREE parking (card holder)\n");
        }
        
        receipt.append("Entry Time: ").append(entryTime.format(fmt)).append("\n");
        receipt.append("Exit Time: ").append(exitTime.format(fmt)).append("\n");
        receipt.append("Duration: ").append(hours).append(" hours\n");
        receipt.append("Parking Fee: RM ").append(String.format("%.2f", parkingFee)).append("\n");
        
        if (currentFine > 0) {
            receipt.append("Current Fine: RM ").append(String.format("%.2f", currentFine)).append("\n");
        }
        
        receipt.append("Current Stay Total: RM ").append(String.format("%.2f", currentStayTotal)).append("\n");
        
        if (previousUnpaid > 0) {
            receipt.append("Previous Unpaid: RM ").append(String.format("%.2f", previousUnpaid)).append("\n");
        }
    }
    
    receipt.append("\n---\n\n");
    receipt.append("TOTAL DUE: RM ").append(String.format("%.2f", currentTotal)).append("\n");
    receipt.append("AMOUNT PAID: RM ").append(String.format("%.2f", amountPaid)).append("\n");
    
    if (remainingBalance > 0) {
        receipt.append("REMAINING BALANCE: RM ").append(String.format("%.2f", remainingBalance)).append("\n");
        receipt.append("(Recorded in payment.txt - will be added to next visit)\n");
    } else {
        receipt.append("FULLY PAID - THANK YOU!\n");
    }
    
    receipt.append("\nPayment Method: ").append(method).append("\n");
    receipt.append("\n---\n");
    
    // Show receipt
    JTextArea textArea = new JTextArea(receipt.toString());
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    textArea.setEditable(false);
    textArea.setBackground(Color.WHITE);
    textArea.setMargin(new Insets(10, 10, 10, 10));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 650));
    
    JOptionPane.showMessageDialog(this, scrollPane, "Parking Receipt", 
                                  JOptionPane.PLAIN_MESSAGE);
}
    
private double getUnpaidFinesFromFile(String plate) {
    double unpaidFines = 0;
    try (BufferedReader br = new BufferedReader(new FileReader("entry_exit.txt"))) {
        String line;
        boolean hasUnpaidFromLastExit = false;
        
        while ((line = br.readLine()) != null) {
            if (line.startsWith("EXIT") && line.contains(plate)) {
                // Check if this exit had unpaid fines
                if (line.contains("Unpaid: RM")) {
                    String[] parts = line.split("Unpaid: RM");
                    String amount = parts[1].split("\\|")[0].trim();
                    unpaidFines += Double.parseDouble(amount);
                    hasUnpaidFromLastExit = true;
                } else {
                    hasUnpaidFromLastExit = false; // Paid in full
                }
            }
        }
    } catch (Exception e) {
        // File not found
    }
    return unpaidFines;
}
    
    
    private void clearDisplay() {
        lblEntry.setText(" Entry Time: --:--");
        lblExit.setText(" Exit Time: --:--");
        lblDuration.setText(" Duration: 0 Hours");
        lblParkingBreakdown.setText(" Parking Fee: --");
        lblFee.setText(" Subtotal Parking: RM 0.00");
        lblFineBreakdown.setText(" Fines: --");
        lblFines.setText(" Total Fines: RM 0.00");
        lblTotal.setText(" TOTAL DUE: RM 0.00");
    }

private ParkingSpot findSpotByVehiclePlate(String plate) {
    ParkingLot lot = ParkingLot.getInstance();
    
    System.out.println("Searching for vehicle: " + plate);
    
    for (Floor floor : lot.getFloors()) {
        for (ParkingSpot spot : floor.getSpots()) {
            Vehicle v = spot.getCurrentVehicle();
            if (v != null) {
                System.out.println("  Spot " + spot.getSpotId() + " has: " + v.getLicenseplate());
                if (v.getLicenseplate().equalsIgnoreCase(plate)) {
                    System.out.println("  >>> FOUND in spot: " + spot.getSpotId());
                    return spot;
                }
            } else {
                System.out.println("  Spot " + spot.getSpotId() + " is empty");
            }
        }
    }
    
    System.out.println("Vehicle not found in memory");
    return null;
}
}
    
//--------------------------------------------------------------------------------------------------


// import java.awt.*;
// import javax.swing.*;

// public class ExitPanel extends JPanel {
//     public ExitPanel() {
//         setLayout(new BorderLayout(20, 20));
        
//         // Top: Search
//         JPanel top = new JPanel();
//         top.add(new JLabel("Enter License Plate:"));
//         top.add(new JTextField(12));
//         top.add(new JButton("Find Vehicle"));
//         add(top, BorderLayout.NORTH);

//         // Center: Receipt & Fee Breakdown (Requirement 5)
//         JPanel receipt = new JPanel(new GridLayout(8, 1));
//         receipt.setBorder(BorderFactory.createTitledBorder("Payment & Receipt"));
//         receipt.add(new JLabel(" Entry Time: 10:00 AM | Exit Time: 12:00 PM"));
//         receipt.add(new JLabel(" Duration: 2 Hours (Ceiling Rounding)"));
//         receipt.add(new JLabel(" Parking Fee: 2 hrs x RM 5.00 = RM 10.00"));
//         receipt.add(new JLabel(" Fines Due: RM 0.00 (Previous unpaid fines included)"));
//         receipt.add(new JLabel(" TOTAL DUE: RM 10.00"));
        
//         JPanel payMethods = new JPanel();
//         payMethods.add(new JLabel("Payment Method:"));
//         payMethods.add(new JRadioButton("Cash"));
//         payMethods.add(new JRadioButton("Card"));
//         receipt.add(payMethods);

//         add(receipt, BorderLayout.CENTER);
//         add(new JButton("Process Payment & Mark Spot Available"), BorderLayout.SOUTH);
//     }
// }