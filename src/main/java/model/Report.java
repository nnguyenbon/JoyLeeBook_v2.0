package model;

import java.time.LocalDateTime;

public class Report {
    private int reportId;
    private int reporterId;
    private int staffId;
    private String type;
    private int reportTypeId;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Report(int reportId, int reporterId, int staffId, String type, int reportTypeId, String reason, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.staffId = staffId;
        this.type = type;
        this.reportTypeId = reportTypeId;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Report() {
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getReportTypeId() {
        return reportTypeId;
    }

    public void setReportTypeId(int reportTypeId) {
        this.reportTypeId = reportTypeId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
