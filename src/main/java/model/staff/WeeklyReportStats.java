package model.staff;

import java.util.List;

public class WeeklyReportStats {
    private int thisWeekTotal;
    private double growthRate;
    private List<Integer> dailyCounts; // Mon, Tue, Wed, Thu, Fri, Sat, Sun

    public WeeklyReportStats() {}

    public WeeklyReportStats(int thisWeekTotal, double growthRate, List<Integer> dailyCounts) {
        this.thisWeekTotal = thisWeekTotal;
        this.growthRate = growthRate;
        this.dailyCounts = dailyCounts;
    }

    // Getters and Setters
    public int getThisWeekTotal() { return thisWeekTotal; }
    public void setThisWeekTotal(int thisWeekTotal) { this.thisWeekTotal = thisWeekTotal; }

    public double getGrowthRate() { return growthRate; }
    public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }

    public List<Integer> getDailyCounts() { return dailyCounts; }
    public void setDailyCounts(List<Integer> dailyCounts) { this.dailyCounts = dailyCounts; }
}
