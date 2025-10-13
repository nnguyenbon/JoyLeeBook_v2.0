package dto.chapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * ChapterListItem class represents a chapter item in a list view.
 * It includes details about the chapter and its associated series.
 *
 * @author HaiDD-dev
 */
public class ChapterItemDTO {
    private int chapterId;
    private int seriesId;
    private String seriesTitle;
    private int chapterNumber;
    private String chapterTitle;
    private String status;
    private LocalDateTime updatedAt;
    private LocalDateTime lastReadAt;

    // getters/setters
    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    public Date getUpdatedAtAsDate() {
        return updatedAt != null ? Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public Date getLastReadAtAsDate() {
        return lastReadAt != null ? Date.from(lastReadAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }


    public String getLastReadAtFormatted() {
        return lastReadAt != null
                ? lastReadAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
    }

}
