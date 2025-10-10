package model;

import java.time.LocalDateTime;

public class Series {
    private int seriesId;
    private String title;
    private String description;
    private String coverImgUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int authorId;
    private boolean isDeleted;
    private int rating_points;

    public Series(int seriesId, String title, String description, String coverImgUrl, String status, LocalDateTime createdAt, LocalDateTime updatedAt, int authorId, boolean isDeleted, int rating_points) {
        this.seriesId = seriesId;
        this.title = title;
        this.description = description;
        this.coverImgUrl = coverImgUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authorId = authorId;
        this.isDeleted = isDeleted;
        this.rating_points = rating_points;
    }

    public Series() {}

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getRating_points() {
        return rating_points;
    }

    public void setRating_points(int rating_points) {
        this.rating_points = rating_points;
    }
}
