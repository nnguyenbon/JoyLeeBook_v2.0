package model.staff;

public class RecentAction {
    private String actionDescription;  // e.g., "Approved 'The Magic Quest' Chapter"
    private String timestamp;          // e.g., "15 minutes ago"
    private String statusColor;        // e.g., "green", "yellow", "red" cho bullet point

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }
}
