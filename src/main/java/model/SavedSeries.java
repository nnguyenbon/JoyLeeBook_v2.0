package model;

import java.time.LocalDateTime;

public class SavedSeries {
    private int seriesId;
    private int userId;
    private LocalDateTime savedAt;

    public SavedSeries(int seriesId, int userId, LocalDateTime savedAt) {
        this.seriesId = seriesId;
        this.userId = userId;
        this.savedAt = savedAt;
    }

    public SavedSeries() {}

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
