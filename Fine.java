public interface Fine {
    double calculateFine(int hoursOverdue);
    String getName();
}

class FixedFine implements Fine {
    @Override
    public double calculateFine(int hoursOverdue) {
        return (hoursOverdue > 0) ? 50.0 : 0;
    }
    
    @Override
    public String getName() {
        return "Fixed (RM 50 flat)";
    }
}

class ProgressiveFine implements Fine {
    @Override
    public double calculateFine(int hoursOverdue) {
        if (hoursOverdue <= 0) return 0;
        if (hoursOverdue <= 24) return 50.0;
        if (hoursOverdue <= 48) return 150.0; // 50 + 100
        if (hoursOverdue <= 72) return 300.0; // 50 + 100 + 150
        return 500.0; // 50 + 100 + 150 + 200
    }
    
    @Override
    public String getName() {
        return "Progressive";
    }
}

class HourlyFine implements Fine {
    @Override
    public double calculateFine(int hoursOverdue) {
        return hoursOverdue * 20.0;
    }
    
    @Override
    public String getName() {
        return "Hourly (RM 20/hr)";
    }
}