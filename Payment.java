import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    public static final double REGULAR_RATE = 5.0;
    public static final double COMPACT_RATE = 2.0;
    public static final double RESERVED_RATE = 10.0;

    // ========== CALCULATION METHODS (Define these first) ==========


public static double calculateParkingFee(Vehicle vehicle, ParkingSpot spot) {
    int hours = (int) vehicle.getParkingDurationHours();
    
    // Debug print to see what's happening
    System.out.println("=== DEBUG Payment.calculateParkingFee ===");
    System.out.println("Vehicle type: " + vehicle.getClass().getSimpleName());
    System.out.println("Has handicapped card: " + vehicle.getHandiCappedCard());
    System.out.println("Spot type: " + spot.getType());
    System.out.println("Spot hourly rate: " + spot.getHourlyRate());
    
    double rate;
    double fee;
    
    // Check if this is a handicapped vehicle
    if (vehicle instanceof Handicapped_Vehicle) {
        Handicapped_Vehicle hv = (Handicapped_Vehicle) vehicle;
        
        if (hv.getHandiCappedCard()) {
            // Case: HAS handicapped card → MINUS RM 2 from spot rate
            rate = spot.getHourlyRate() - 2.0;
            // Ensure rate doesn't go below 0
            if (rate < 0) rate = 0;
            System.out.println("Case: Has card → Minus RM 2: " + spot.getHourlyRate() + " - 2 = RM " + rate + "/hr");
        } else {
            // Case: NO handicapped card → Pay spot's normal rate
            rate = spot.getHourlyRate();
            System.out.println("Case: No card → Pay spot rate: RM " + rate + "/hr");
        }
    } else {
        // Regular vehicle (Car, Motorcycle, SUV) - pay spot's normal rate
        rate = spot.getHourlyRate();
        System.out.println("Case: Regular vehicle → Pay spot rate: RM " + rate + "/hr");
    }
    
    fee = hours * rate;
    System.out.println("Hours: " + hours);
    System.out.println("Rate: RM " + rate);
    System.out.println("Calculated fee: RM " + fee);
    System.out.println("================================");
    
    return fee;
}


    public static double calculateFine(Vehicle vehicle, long hours) {
        double fine = 0;
        
        // Overstaying fine (>24 hours)
        if (hours > 24) {
            int overstayHours = (int) (hours - 24);
            fine = ParkingManager.getInstance().getFineStrategy().calculateFine(overstayHours);
        }
        
        // Reserved violation fine
        if (vehicle.reservedViolation()) {
            fine += 50.0;
        }
        
        return fine;
    }

    public static double calculateTotal(Vehicle vehicle, ParkingSpot spot) {
        long hours = vehicle.getParkingDurationHours();
        double parkingFee = calculateParkingFee(vehicle, spot);
        double fineAmount = calculateFine(vehicle, hours);
        return parkingFee + fineAmount;
    }

public static double getRateForSpot(String type) {
    switch (type) {
        case "Compact": return 2.0;
        case "Regular": return 5.0;
        case "Reserved": return 10.0;
        default: return 5.0;
    }
}

    // These are now just wrappers that call calculateFine if you need them separately
    public static double getOnlyFine(long hours) {
        // This only calculates overstaying fine, not reserved violation
        if (hours <= 24) return 0.0;
        int overstayHours = (int)(hours - 24);
        return ParkingManager.getInstance().getFineStrategy().calculateFine(overstayHours);
    }
    
    public static double getReservedViolationFine(Vehicle vehicle) {
        if (vehicle.reservedViolation()) {
            return 50.0;
        }
        return 0.0;
    }

    // ========== ENTRY/EXIT FILE METHODS (entry_exit.txt) ==========
    
    public static void saveEntry(Vehicle vehicle, ParkingSpot spot) {
        String filename = "entry_exit.txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            String record = String.format("ENTRY | %s | %s | %s | %s | HandicappedCard: %s | ReservedViolation: %s",
                vehicle.getLicenseplate(),
                vehicle.getClass().getSimpleName(),
                spot.getSpotId(),
                LocalDateTime.now().format(dtf),
                vehicle.getHandiCappedCard(),
                vehicle.reservedViolation()
            );
            
            pw.println(record);
            
        } catch (Exception e) {
            System.err.println("Error saving entry: " + e.getMessage());
        }
    }

    public static void saveExit(String plate, double amountPaid, String method, double remainingBalance) {
        String filename = "entry_exit.txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            String record = "EXIT | " + plate + " | Paid: RM" + String.format("%.2f", amountPaid);
            if (remainingBalance > 0) {
                record += " | Unpaid: RM" + String.format("%.2f", remainingBalance);
            }
            record += " | Method: " + method + " | Time: " + LocalDateTime.now().format(dtf);
            
            pw.println(record);
            
        } catch (Exception e) {
            System.err.println("Error saving exit: " + e.getMessage());
        }
    }

    // ========== PAYMENT FILE METHODS (payment.txt) ==========

    public static void savePaymentRecord(Vehicle vehicle, ParkingSpot spot, 
                                         double amountPaid, String paymentMethod,
                                         double previousUnpaid) {
        String filename = "payment.txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            long hours = vehicle.getParkingDurationHours();
            double parkingFee = calculateParkingFee(vehicle, spot);
            double fineAmount = calculateFine(vehicle, hours);
            double totalDue = parkingFee + fineAmount + previousUnpaid;
            double remainingBalance = totalDue - amountPaid;
            
            // Format: Plate | VehicleType | EntryTime | ExitTime | Hours | SpotType | ParkingFee | Fine | PreviousUnpaid | TotalDue | AmountPaid | Remaining | Method | DateTime
            String record = String.format("%s | %s | %d | %d | %d | %s | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f | %s | %s",
                vehicle.getLicenseplate(),
                vehicle.getClass().getSimpleName(),
                vehicle.getEntryTime(),
                System.currentTimeMillis(),
                hours,
                spot.getType(),
                parkingFee,
                fineAmount,
                previousUnpaid,
                totalDue,
                amountPaid,
                remainingBalance,
                paymentMethod,
                LocalDateTime.now().format(dtf)
            );
            
            pw.println(record);
            pw.flush();

            // Add to revenue only what was actually paid
            ParkingManager.getInstance().addRevenue(amountPaid);

        } catch (Exception e) {
            System.err.println("Error saving payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

public static double getOutstandingBalance(String plate) {
    File file = new File("payment.txt");
    if (!file.exists()) return 0;
    
    double latestOutstanding = 0;
    
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        String lastRecord = null;
        
        // Find the most recent record for this plate
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" \\| ");
            if (parts.length >= 14 && parts[0].equalsIgnoreCase(plate)) {
                lastRecord = line; // Keep updating to get the last one
            }
        }
        
        // Get the remaining balance from the most recent record only
        if (lastRecord != null) {
            String[] parts = lastRecord.split(" \\| ");
            latestOutstanding = Double.parseDouble(parts[11]); // Remaining balance (index 11)
            System.out.println("Most recent outstanding for " + plate + ": RM " + latestOutstanding);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return latestOutstanding > 0 ? latestOutstanding : 0;
}

    // ========== REPORTING METHODS ==========

    public static double getTotalRevenue() {
        double total = 0;
        File file = new File("payment.txt");
        if (!file.exists()) return 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length >= 12) {
                    total += Double.parseDouble(parts[10]); // amount paid (index 10)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static double getTotalOutstandingFines() {
        double total = 0;
        File file = new File("payment.txt");
        if (!file.exists()) return 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length >= 13) {
                    total += Double.parseDouble(parts[11]); // remaining balance (index 11)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static void displayPaymentReport() {
        System.out.println("\n=== PAYMENT RECORDS (payment.txt) ===");
        System.out.println("Plate\t\tType\tHours\tSpot\tParking\tFine\tPrevious\tTotal\tPaid\tRemaining\tMethod");
        System.out.println("------------------------------------------------------------------------------------------");
        
        File file = new File("payment.txt");
        if (!file.exists()) {
            System.out.println("No payment records yet");
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length >= 14) {
                    System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
                        parts[0], parts[1], parts[4], parts[5], 
                        parts[6], parts[7], parts[8], parts[9], 
                        parts[10], parts[11], parts[12]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("\nTotal Revenue: RM " + getTotalRevenue());
        System.out.println("Total Outstanding: RM " + getTotalOutstandingFines());
    }
}