package model;

import java.time.LocalDateTime;

public class Like {
    private int userId;
    private int chapterId;
    private LocalDateTime likedAt;

    public Like(int userId, int chapterId, LocalDateTime likedAt) {
        this.userId = userId;
        this.chapterId = chapterId;
        this.likedAt = likedAt;
    }

    public Like() {
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

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }
}
