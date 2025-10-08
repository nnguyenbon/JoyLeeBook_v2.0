package model;

import java.time.LocalDateTime;

public class ReadingHistory {
    private int userId;
    private int chapterId;
    private LocalDateTime lastReadAt;

    public ReadingHistory(int userId, int chapterId, LocalDateTime lastReadAt) {
        this.userId = userId;
        this.chapterId = chapterId;
        this.lastReadAt = lastReadAt;
    }

    public ReadingHistory() {}
    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }
}
