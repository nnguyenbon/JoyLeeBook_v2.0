package services;

import dao.ChapterDAO;
import dao.SeriesAuthorDAO;
import dao.SeriesDAO;
import model.Chapter;
import model.Series;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * ChapterManagementService provides methods to manage chapters within series.
 * It includes functionalities to create and update chapters, ensuring proper
 * validation and permission checks.
 * <p>
 * This service interacts with the database through DAO classes and handles
 * business logic related to chapter management.
 * </p>
 *
 * @author HaiDD-dev
 */
public class ChapterManagementService {
    private final Connection conn;

    /**
     * Allowed status values for chapters.
     */
    private static final Set<String> ALLOWED_STATUS = new HashSet<>(Arrays.asList("draft", "pending", "rejected", "approved"));

    public ChapterManagementService(Connection conn) {
        this.conn = conn;
    }

    /**
     * Fetch series by ID.
     *
     * @param seriesId The ID of the series to fetch.
     * @return The Series object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Series getSeriesById(int seriesId) throws SQLException {
        return new SeriesDAO(conn).findById(seriesId);
    }

    /**
     * Fetch chapter by ID, only if not deleted.
     *
     * @param chapterId The ID of the chapter to fetch.
     * @return The Chapter object if found and not deleted, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Chapter getChapterById(int chapterId) throws SQLException {
        return new ChapterDAO(conn).findByIdIfNotDeleted(chapterId);
    }

    /**
     * Create a new chapter in a series.
     * Validates input, checks permissions, and inserts the chapter into the database.
     *
     * @param authorUserId The ID of the user creating the chapter (must be an author of the series).
     * @param seriesId     The ID of the series to which the chapter will be added.
     * @param title        The title of the chapter (required, max 255 chars).
     * @param content      The content of the chapter (required).
     * @return The created Chapter object.
     * @throws IllegalArgumentException If validation fails or series not found.
     * @throws IllegalAccessException   If the user does not have permission to add chapters to the series.
     * @throws Exception                If a database error occurs during insertion.
     */
    public Chapter createChapter(int authorUserId, int seriesId, String title, String content) throws Exception {
        // Validate
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        title = title.trim();
        if (title.length() > 255) {
            throw new IllegalArgumentException("Title is too long (max 255 chars).");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required.");
        }

        // check series exists
        Series series = new SeriesDAO(conn).findById(seriesId);
        if (series == null) {
            throw new IllegalArgumentException("Series not found.");
        }

        // must be authored of the series
        SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);
        // returns TRUE if user is NOT author
        if (saDAO.isUserAuthorOfSeries(authorUserId, seriesId)) {
            throw new IllegalAccessException("You do not have permission to add chapter to this series.");
        }

        // calculate next chapter number
        ChapterDAO chapterDAO = new ChapterDAO(conn);
        int lastNo = 0;
        try {
            lastNo = chapterDAO.getLatestChapterNumber(seriesId);
        } catch (SQLException ignore) {
        }
        int nextNo = Math.max(1, lastNo + 1);

        // create object
        Chapter ch = new Chapter();
        ch.setSeriesId(seriesId);
        ch.setChapterNumber(nextNo);
        ch.setTitle(title);
        ch.setContent(content);
        ch.setStatus("pending");

        boolean ok = chapterDAO.insert(ch, seriesId, authorUserId);
        if (!ok) {
            throw new RuntimeException("Database insert failed.");
        }

        Chapter saved = chapterDAO.findBySeriesAndNumberIfNotDeleted(seriesId, nextNo);
        if (saved == null) {
            saved = ch;
        }
        return saved;
    }

    /**
     * Update an existing chapter.
     * Validates input, checks permissions, and updates the chapter in the database.
     *
     * @param userId        The ID of the user attempting to update the chapter (must be an author of the series).
     * @param chapterId     The ID of the chapter to update.
     * @param title         The new title of the chapter (optional, max 255 chars).
     * @param content       The new content of the chapter (optional).
     * @param chapterNumber The new chapter number (optional, must be > 0).
     * @param desiredStatus The desired status of the chapter (optional, must be one of "draft", "pending", "rejected", "approved").
     *                      If "approved" is requested, it will be set to "pending" instead.
     * @return The updated Chapter object.
     * @throws IllegalArgumentException If validation fails or chapter not found.
     * @throws IllegalAccessException   If the user does not have permission to edit the chapter.
     * @throws Exception                If a database error occurs during update.
     */
    public Chapter updateChapter(int userId, int chapterId, String title, String content, Integer chapterNumber, String desiredStatus) throws Exception {

        ChapterDAO chapterDAO = new ChapterDAO(conn);
        Chapter chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
        if (chapter == null) {
            throw new IllegalArgumentException("Chapter not found.");
        }

        int seriesId = chapter.getSeriesId();

        SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);
        if (saDAO.isUserAuthorOfSeries(userId, seriesId)) {
            throw new IllegalAccessException("You do not have permission to edit this chapter.");
        }

        // Validate
        if (title != null) {
            title = title.trim();
            if (title.isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty.");
            }
            if (title.length() > 255) {
                throw new IllegalArgumentException("Title is too long (max 255 chars).");
            }
            chapter.setTitle(title);
        }
        if (content != null) {
            if (content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be empty.");
            }
            chapter.setContent(content);
        }
        if (chapterNumber != null) {
            if (chapterNumber <= 0) {
                throw new IllegalArgumentException("Chapter number must be > 0.");
            }
            chapter.setChapterNumber(chapterNumber);
        }

        // Status
        if (desiredStatus != null) {
            if (!ALLOWED_STATUS.contains(desiredStatus)) {
                throw new IllegalArgumentException("Invalid status.");
            }
            if ("approved".equalsIgnoreCase(desiredStatus)) {
                chapter.setStatus("pending");
            } else {
                chapter.setStatus(desiredStatus);
            }
        } else {
            chapter.setStatus("pending");
        }

        // Update DB
        boolean ok = chapterDAO.update(chapter);
        if (!ok) {
            throw new RuntimeException("Database update failed.");
        }

        return chapterDAO.findByIdIfNotDeleted(chapterId);
    }

    /**
     * Soft-delete a chapter by marking it as deleted in the database.
     * Validates permissions before performing the deletion.
     *
     * @param userId    The ID of the user attempting to delete the chapter (must be an author of the series or staff/admin).
     * @param role      The role of the user (e.g., "admin", "staff", "author").
     * @param chapterId The ID of the chapter to delete.
     * @throws IllegalArgumentException If the chapter is not found.
     * @throws IllegalAccessException   If the user does not have permission to delete the chapter.
     * @throws Exception                If a database access error occurs during deletion.
     */
    public void deleteChapter(int userId, String role, int chapterId) throws Exception {
        ChapterDAO chapterDAO = new ChapterDAO(conn);
        Chapter chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
        if (chapter == null) {
            throw new IllegalArgumentException("Chapter not found or already deleted.");
        }

        int seriesId = chapter.getSeriesId();
        if (!canDeleteChapter(userId, role, seriesId)) {
            throw new IllegalAccessException("You do not have permission to delete this chapter.");
        }

        boolean ok = chapterDAO.delete(chapterId);
        if (!ok) {
            throw new RuntimeException("Database delete failed.");
        }
    }

    /**
     * Check if a user has permission to delete a chapter in a series.
     * A user can delete a chapter if they are an author of the series or have a role of "admin" or "staff".
     *
     * @param userId   The ID of the user.
     * @param role     The role of the user (e.g., "admin", "staff", "author").
     * @param seriesId The ID of the series containing the chapter.
     * @return True if the user can delete chapters in the series, otherwise false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean canDeleteChapter(int userId, String role, int seriesId) throws SQLException {
        // Staff/Admin always can
        if (role != null) {
            String r = role.toLowerCase();
            if (r.equals("admin") || r.equals("staff")) return true;
        }
        // Author of the series can
        SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);
        // returns TRUE if user is NOT author
        return !saDAO.isUserAuthorOfSeries(userId, seriesId);
    }
}
