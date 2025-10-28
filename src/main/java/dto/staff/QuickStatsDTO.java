package dto.staff;

public class QuickStatsDTO {
    private int reviewsCompleted;
    private int contentApproved;
    private int contentRejected;
    private int reportsResolved;

    public int getReportsResolved() {
        return reportsResolved;
    }

    public void setReportsResolved(int reportsResolved) {
        this.reportsResolved = reportsResolved;
    }

    public int getContentRejected() {
        return contentRejected;
    }

    public void setContentRejected(int contentRejected) {
        this.contentRejected = contentRejected;
    }

    public int getContentApproved() {
        return contentApproved;
    }

    public void setContentApproved(int contentApproved) {
        this.contentApproved = contentApproved;
    }

    public int getReviewsCompleted() {
        return reviewsCompleted;
    }

    public void setReviewsCompleted(int reviewsCompleted) {
        this.reviewsCompleted = reviewsCompleted;
    }
}
