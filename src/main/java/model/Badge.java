package model;

import java.time.LocalDateTime;

public class Badge {
    private int badgeId;
    private String name;
    private String iconUrl;
    private String description;
    private LocalDateTime createdAt;

    public Badge(int badgeId, String name, String iconUrl, String description, LocalDateTime createdAt) {
        this.badgeId = badgeId;
        this.name = name;
        this.iconUrl = iconUrl;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Badge() {
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
