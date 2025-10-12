package services.chapter;

import dao.*;
import db.DBConnection;
import model.Chapter;
import model.Series;

import java.sql.Connection;

/**
 * Service class for handling chapter-related operations
 *
 * @author HaiDD-dev
 */
public class ChapterService {

    /**
     * View model for chapter details
     */
    public static class ChapterView {
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

    /**
     * Load chapter view by chapter ID or series ID and chapter number
     *
     * @param chapterId    The ID of the chapter to load (optional if seriesId and number are provided)
     * @param seriesId     The ID of the series (required if chapterId is not provided)
     * @param number       The chapter number within the series (required if chapterId is not provided)
     * @param viewerUserId The ID of the user viewing the chapter (optional, for tracking reading history and likes)
     * @return A ChapterView object containing chapter details, series info, navigation links, like and comment counts, and user like status; or null if not found
     * @throws Exception If an error occurs during database access
     */
    public ChapterView loadView(Integer chapterId, Integer seriesId, Integer number, Integer viewerUserId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            SeriesDAO  seriesDAO  = new SeriesDAO(conn);
            LikesDAO   likesDAO   = new LikesDAO(conn);
            CommentDAO commentDAO = new CommentDAO(conn);
            SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);

            Chapter chapter = null;

            // 1) Thử lấy chương đã APPROVED (công khai)
            if (chapterId != null) {
                chapter = chapterDAO.findApprovedById(chapterId);
            } else if (seriesId != null && number != null) {
                chapter = chapterDAO.findApprovedBySeriesAndNumber(seriesId, number);
            }

            // 2) Nếu không thấy, thử “My Chapter”: cho author xem bản chưa approved
            if (chapter == null && viewerUserId != null) {
                Chapter any = null;
                if (chapterId != null) {
                    any = chapterDAO.findByIdIfNotDeleted(chapterId);
                } else if (seriesId != null && number != null) {
                    any = chapterDAO.findBySeriesAndNumberIfNotDeleted(seriesId, number);
                }
                if (any != null && saDAO.isUserAuthorOfSeries(any.getSeriesId(), viewerUserId)) {
                    chapter = any; // cho phép author xem
                }
            }

            if (chapter == null) return null; // không có quyền hoặc không tồn tại

            Series series = seriesDAO.findById(chapter.getSeriesId());

            // Prev/Next vẫn theo approved để không lộ chương người khác
            Chapter prev = chapterDAO.findPrevApproved(chapter.getSeriesId(), chapter.getChapterNumber());
            Chapter next = chapterDAO.findNextApproved(chapter.getSeriesId(), chapter.getChapterNumber());

            int likeCount = likesDAO.countByChapter(chapter.getChapterId());
            int commentCount = commentDAO.countByChapter(chapter.getChapterId());
            boolean userLiked = viewerUserId != null && likesDAO.isLikedByUser(viewerUserId, chapter.getChapterId());

            // Ghi lịch sử đọc: chỉ khi chương approved (tuỳ bạn, nhưng đây là thay đổi tối thiểu)
            if (viewerUserId != null && "approved".equalsIgnoreCase(chapter.getStatus())) {
                new ReadingHistoryDAO(conn).upsert(viewerUserId, chapter.getChapterId());
            }

            ChapterView vm = new ChapterView();
            vm.setChapter(chapter);
            vm.setSeries(series);
            vm.setPrev(prev);
            vm.setNext(next);
            vm.setLikeCount(likeCount);
            vm.setCommentCount(commentCount);
            vm.setUserLiked(userLiked);
            return vm;
        }
    }
}
