package model;

import java.time.LocalDateTime;

public class BadgesUser {
    private int badgeId;
    private int userId;
    private LocalDateTime awardedAt;

    public BadgesUser(int badgeId, int userId, LocalDateTime awardedAt) {
        this.badgeId = badgeId;
        this.userId = userId;
        this.awardedAt = awardedAt;
    }

    public BadgesUser() {
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }
}
