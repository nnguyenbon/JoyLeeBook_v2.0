package dto.chapter;

import model.Chapter;
import model.Series;

public class ChapterViewDTO {
    private Chapter chapter;
    private Series series;
    private Chapter prev;
    private Chapter next;
    private int likeCount;
    private int commentCount;
    private boolean userLiked;

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Chapter getPrev() {
        return prev;
    }

    public void setPrev(Chapter prev) {
        this.prev = prev;
    }

    public Chapter getNext() {
        return next;
    }

    public void setNext(Chapter next) {
        this.next = next;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isUserLiked() {
        return userLiked;
    } // EL map tới ${vm.userLiked}

    public void setUserLiked(boolean userLiked) {
        this.userLiked = userLiked;
    }

}
