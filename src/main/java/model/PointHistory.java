package model;

import java.time.LocalDateTime;

public class PointHistory {
    private int historyId;
    private int userId;
    private int pointChange;
    private String reason;
    private String referenceType;
    private int referenceId;
    private LocalDateTime createdAt;

    public PointHistory(int historyId, int userId, int pointChange, String reason, String referenceType, int referenceId, LocalDateTime createdAt) {
        this.historyId = historyId;
        this.userId = userId;
        this.pointChange = pointChange;
        this.reason = reason;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    public PointHistory() {}

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPointChange() {
        return pointChange;
    }

    public void setPointChange(int pointChange) {
        this.pointChange = pointChange;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
