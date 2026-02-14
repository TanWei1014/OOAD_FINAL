// Behavioral Pattern: Strategy
// File: FineStrategy.java
public interface Fine {
    double calculateFine(int hoursOverdue);
}

class FixedFine implements Fine {
    public double calculateFine(int hours) { return 50.0; } // Flat RM50
}

class ProgressiveFine implements Fine {
    public double calculateFine(int hours) { 
        return hours * 15.0; // RM15 per hour overdue
    } 
}

class HourlyFine implements Fine {
    public double calculateFine(int hours) { return hours * 5.0; } 
}