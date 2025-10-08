package model;

import java.time.LocalDateTime;

public class Rating {
    private int ratingValue;
    private int userId;
    private int seriesId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rating(int ratingValue, int userId, int seriesId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.ratingValue = ratingValue;
        this.userId = userId;
        this.seriesId = seriesId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public Rating(){}

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
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
