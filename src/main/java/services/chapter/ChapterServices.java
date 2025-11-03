package services.chapter;

import dao.*;
import db.DBConnection;
import dto.PaginationRequest;
import dto.chapter.ChapterDetailDTO;
import dto.chapter.ChapterInfoDTO;
import dto.chapter.ChapterItemDTO;
import dto.chapter.ChapterViewDTO;
import model.Chapter;
import model.Series;
import services.general.FormatServices;
import services.general.PointServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling chapter-related operations
 *
 * @author HaiDD-dev
 */
public class ChapterServices {
    private final Connection connection;
    private final ChapterDAO chapterDAO;

    public ChapterServices() throws SQLException, ClassNotFoundException {
        this.connection = DBConnection.getConnection();
        this.chapterDAO = new ChapterDAO(connection);
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
    public ChapterViewDTO loadView(Integer chapterId, Integer seriesId, Integer number, Integer viewerUserId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            LikeDAO likesDAO = new LikeDAO(conn);
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

            Series series = seriesDAO.findById(chapter.getSeriesId(), "ongoing");

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

            ChapterViewDTO vm = new ChapterViewDTO();
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

    public List<ChapterDetailDTO> buildChapterList(String search, String status, PaginationRequest paginationRequest) throws SQLException {
        return buildChapterDetailDTOList(chapterDAO.getAll(search, status, paginationRequest));
    }

    public ChapterInfoDTO buildChapterInfoDTO(Chapter chapter) throws SQLException {
        LikeDAO likesDAO = new LikeDAO(connection);
        ChapterInfoDTO chapterInfoDTO = new ChapterInfoDTO();
        chapterInfoDTO.setChapterId(chapter.getChapterId());
        chapterInfoDTO.setTitle(chapter.getTitle());
        chapterInfoDTO.setChapterNumber(chapter.getChapterNumber());
        chapterInfoDTO.setUpdatedAt(FormatServices.calculateTimeAgo(chapter.getUpdatedAt()));
        chapterInfoDTO.setTotalLikes(likesDAO.countByChapter(chapter.getChapterId()));
        return chapterInfoDTO;
    }

    public List<ChapterInfoDTO> buildChapterInfoDTOList(List<Chapter> chapterList) throws SQLException {
        List<ChapterInfoDTO> chapterInfoDTOList = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            chapterInfoDTOList.add(buildChapterInfoDTO(chapter));
        }
        return chapterInfoDTOList;
    }

    public ChapterDetailDTO buildChapterDetailDTO(Chapter chapter) throws SQLException {
        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(connection);
        SeriesDAO seriesDAO = new SeriesDAO(connection);
        LikeDAO likesDAO = new LikeDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        ChapterDetailDTO chapterDetailDTO = new ChapterDetailDTO();
        chapterDetailDTO.setSeriesId(chapter.getSeriesId());
        chapterDetailDTO.setChapterId(chapter.getChapterId());
        chapterDetailDTO.setTitle(chapter.getTitle());
        chapterDetailDTO.setContent(chapter.getContent());
        chapterDetailDTO.setChapterNumber(chapter.getChapterNumber());
        chapterDetailDTO.setAuthorsName(userDAO.findById(chapter.getUserId()).getUsername());
        chapterDetailDTO.setSeriesTitle(seriesDAO.findById(chapter.getSeriesId(), "approved").getTitle());
        chapterDetailDTO.setTotalLike(likesDAO.countByChapter(chapter.getChapterId()));
        chapterDetailDTO.setStatus(FormatServices.formatString(chapter.getStatus()));
        chapterDetailDTO.setCreatedAt(FormatServices.formatDate(chapter.getCreatedAt()));
        chapterDetailDTO.setUpdatedAt(FormatServices.formatDate(chapter.getUpdatedAt()));
        chapterDetailDTO.setAction(FormatServices.getAction(chapterDetailDTO.getCreatedAt(), chapterDetailDTO.getUpdatedAt()));
        return chapterDetailDTO;
    }

    /**
     * Update reading history for a user and chapter
     *
     * @param userId    The ID of the user
     * @param chapterId The ID of the chapter
     */
    public void updateReadingHistory(int userId, int chapterId) throws SQLException {
        int seriesId = chapterDAO.findSeriesIdByChapter(chapterId);
        if (seriesId == -1) {
            System.out.println("Chapter ID " + chapterId + " not found.");
            return;
        }

        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);
            if (!chapterDAO.deleteOldReadingHistory(userId, seriesId)){
                return;
            }
            if (chapterDAO.insertReadingHistory(userId, chapterId)) {
                PointServices.trackAction(userId, 3, "Reading chapter", "chapter", chapterId);
            }
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Error updating reading history for user ID " + userId + " and chapter ID " + chapterId);
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction");
            }
            System.out.println(e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException setAutoCommitEx) {
                System.out.println("Error restoring auto-commit setting");
            }
        }
    }

    public List<ChapterDetailDTO> buildChapterDetailDTOList(List<Chapter> chapterList) throws SQLException {
        List<ChapterDetailDTO> chapterDetailDTOList = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            chapterDetailDTOList.add(buildChapterDetailDTO(chapter));
        }
        return chapterDetailDTOList;
    }

    public List<ChapterItemDTO> historyChaptersFromUser(int userId, int offset, int pagesize, String keyword) throws SQLException, ClassNotFoundException {
        return chapterDAO.getReadingHistoryChapters(userId, offset, pagesize, keyword);
    }

    public List<ChapterDetailDTO> chaptersFromSeries(int seriesId) throws SQLException, ClassNotFoundException {
        return buildChapterDetailDTOList(chapterDAO.findChapterBySeriesId(seriesId));
    }

    public ChapterDetailDTO buildChapterDetailDTO(int chapterId) throws SQLException {
        return buildChapterDetailDTO(chapterDAO.findById(chapterId));
    }

    public int getFirstChapterNumber(int seriesId) throws SQLException {
        return chapterDAO.getFirstChapterNumber(seriesId);
    }

    public static String getRedirectUrl(String type, int seriesId, int chapterNumber) throws SQLException, ClassNotFoundException {
        Chapter chapter = new Chapter();
        ChapterDAO chapterDAO = new ChapterDAO(DBConnection.getConnection());
        if (type.equals("next")) {
            chapter = chapterDAO.getNextChapter(seriesId, chapterNumber);
        } else if (type.equals("previous")) {
            chapter = chapterDAO.getPreviousChapter(seriesId, chapterNumber);
        }
        return String.format("/chapter/detail?seriesId=%d&chapterId=%d", seriesId, chapter.getChapterId());
    }

    public int getTotalChaptersCount(String search, String status) throws SQLException {
        return chapterDAO.getTotalChaptersCount(search, status);
    }

    public int getCountMyChapterByUserId(int userId, String status) throws SQLException {
        return chapterDAO.countChapterByUserId(userId, status);
    }
}
