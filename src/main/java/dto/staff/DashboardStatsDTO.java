package dto.staff;

public class DashboardStatsDTO {
    // Left group
    private int totalSeries;
    private int activeUsers;
    private int authors;
    private int bannedUsers;
    private int totalReports;
    private int pendingReports;
    private int handledReports;

    // Right group
    private int totalChapters;
    private int yourReviews;
    private int pendingChapters;
    private int yourRejects;

    // Constructors, getters, setters
    public DashboardStatsDTO() {}

    public int getTotalSeries() {
        return totalSeries;
    }

    public void setTotalSeries(int totalSeries) {
        this.totalSeries = totalSeries;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getAuthors() {
        return authors;
    }

    public void setAuthors(int authors) {
        this.authors = authors;
    }

    public int getBannedUsers() {
        return bannedUsers;
    }

    public void setBannedUsers(int bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public int getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(int totalReports) {
        this.totalReports = totalReports;
    }

    public int getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(int pendingReports) {
        this.pendingReports = pendingReports;
    }

    public int getHandledReports() {
        return handledReports;
    }

    public void setHandledReports(int handledReports) {
        this.handledReports = handledReports;
    }

    public int getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(int totalChapters) {
        this.totalChapters = totalChapters;
    }

    public int getYourReviews() {
        return yourReviews;
    }

    public void setYourReviews(int yourReviews) {
        this.yourReviews = yourReviews;
    }

    public int getPendingChapters() {
        return pendingChapters;
    }

    public void setPendingChapters(int pendingChapters) {
        this.pendingChapters = pendingChapters;
    }

    public int getYourRejects() {
        return yourRejects;
    }

    public void setYourRejects(int yourRejects) {
        this.yourRejects = yourRejects;
    }
}
