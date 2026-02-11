// Behavioral Pattern: Strategy
// File: FineStrategy.java
public interface FineStrategy {
    double calculateFine(int hoursOverdue);
}

class FixedFine implements FineStrategy {
    public double calculateFine(int hours) { return 50.0; } // Flat RM50
}

class ProgressiveFine implements FineStrategy {
    public double calculateFine(int hours) { 
        return hours * 15.0; // RM15 per hour overdue
    } 
}

class HourlyFine implements FineStrategy {
    public double calculateFine(int hours) { return hours * 5.0; } 
}