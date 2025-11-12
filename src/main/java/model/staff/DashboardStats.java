package model.staff;

public class DashboardStats {
    // Left group
    private int totalSeries;
    private int yourReviewSeries;
    private int pendingSeries;
    private int yourRejectSeries;
    private int activeUsers;
    private int authors;
    private int bannedUsers;
    private int staffs;

    // Right group
    private int totalChapters;
    private int yourReviews;
    private int pendingChapters;
    private int rejectedChapters;
    private int yourRejects;
    private int totalReports;
    private int pendingReports;
    private int handledReports;


    public int getTotalSeries() {
        return totalSeries;
    }

    public int getYourReviewSeries() {
        return yourReviewSeries;
    }

    public void setYourReviewSeries(int yourReviewSeries) {
        this.yourReviewSeries = yourReviewSeries;
    }

    public int getPendingSeries() {
        return pendingSeries;
    }

    public void setPendingSeries(int pendingSeries) {
        this.pendingSeries = pendingSeries;
    }

    public int getYourRejectSeries() {
        return yourRejectSeries;
    }

    public void setYourRejectSeries(int yourRejectSeries) {
        this.yourRejectSeries = yourRejectSeries;
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

    public int getRejectedChapters() {
        return rejectedChapters;
    }

    public void setRejectedChapters(int rejectedChapters) {
        this.rejectedChapters = rejectedChapters;
    }

    public int getStaffs() {
        return staffs;
    }

    public void setStaffs(int staffs) {
        this.staffs = staffs;
    }
}
