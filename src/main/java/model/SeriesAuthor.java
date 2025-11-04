package model;


import java.time.LocalDateTime;

public class SeriesAuthor {
    private int seriesId;
    private int authorId;
    private LocalDateTime addedAt;

    public SeriesAuthor(int seriesId, int authorId, LocalDateTime addedAt) {
        this.seriesId = seriesId;
        this.authorId = authorId;
        this.addedAt = addedAt;
    }

    public SeriesAuthor() {
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
