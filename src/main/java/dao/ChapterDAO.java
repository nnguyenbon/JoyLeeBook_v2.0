/**
 * cần chỉnh sửa xem xem có cho xem các chapter chưa approved không
 */

package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.helper.PaginationDAOHelper;
import dto.PaginationRequest;
import model.Chapter;
import dto.chapter.ChapterItemDTO;
import utils.FormatUtils;

/**
 * Data Access Object (DAO) for Chapter entity.
 * Provides CRUD operations and specific queries.
 *
 * @author KToan, HaiDD-dev
 */
public class ChapterDAO {
    private final Connection conn;

    /**
     * Constructor to initialize the DAO with a database connection.
     *
     * @param conn the database connection
     */
    public ChapterDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieve all non-deleted chapters.
     *
     * @return a list of all non-deleted chapters
     * @throws SQLException if a database access error occurs
     */
    public List<Chapter> getAll() throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapters WHERE is_deleted = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                chapters.add(extractChapterFromResultSet(rs));
            }
        }
        return chapters;
    }

    public boolean checkExistChapterNumber (int chapterNumber, int series_id) throws SQLException {
        String sql = "SELECT * FROM chapters WHERE chapter_number = ? AND is_deleted = 0 AND series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterNumber);
            ps.setInt(2, series_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        }
    }
    public int countChapterByUserId(int userId, String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chapters WHERE user_id = ? AND is_deleted = 0";
        if (!status.isEmpty()) {
            sql += " AND approval_status = ?";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (!status.isEmpty()) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
    public List<Chapter> getAll(String search, String status, PaginationRequest paginationRequest) throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        PaginationDAOHelper paginationDAOHelper = new PaginationDAOHelper(paginationRequest);

        StringBuilder sql = buildChapterBaseQuery(false, search, status);
        sql.append(paginationDAOHelper.buildPaginationClause());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setChapterQueryParameters(stmt, search, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chapters.add(extractChapterFromResultSet(rs));
                }
            }
        }
        return chapters;
    }
    public List<Chapter> getAll(String search, String approvedStatus,String status, PaginationRequest paginationRequest) throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        PaginationDAOHelper paginationDAOHelper = new PaginationDAOHelper(paginationRequest);

        StringBuilder sql = buildChapterBaseQuery(false, search, approvedStatus, status);
        sql.append(paginationDAOHelper.buildPaginationClause());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setChapterQueryParameters(stmt, search, approvedStatus, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chapters.add(extractChapterFromResultSet(rs));
                }
            }
        }
        return chapters;
    }
    public int getTotalChaptersCount(String search, String approvalStatus, String status) throws SQLException {
        StringBuilder sql = buildChapterBaseQuery(true, search, approvalStatus, status);
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setChapterQueryParameters(stmt, search, approvalStatus, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }
    public int getTotalChaptersCount(int seriesId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chapters WHERE series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setInt(1, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
    private StringBuilder buildChapterBaseQuery(boolean isCount, String search, String approvalStatus) throws SQLException {
        StringBuilder sql = new StringBuilder();

        if (isCount) {
            sql.append("SELECT COUNT(DISTINCT chapter_id) AS total FROM chapters WHERE is_deleted = 0");
        } else {
            sql.append("SELECT DISTINCT * FROM chapters WHERE is_deleted = 0");
        }

        if (approvalStatus != null && !approvalStatus.trim().isEmpty()) {
            sql.append(" AND approval_status = ?");
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND title LIKE ?");
        }

        return sql;
    }

    private StringBuilder buildChapterBaseQuery(boolean isCount, String search, String approvalStatus, String status) throws SQLException {
        StringBuilder sql = new StringBuilder();

        if (isCount) {
            sql.append("SELECT COUNT(DISTINCT chapter_id) AS total FROM chapters WHERE is_deleted = 0");
        } else {
            sql.append("SELECT DISTINCT * FROM chapters WHERE is_deleted = 0");
        }

        if (approvalStatus != null && !approvalStatus.trim().isEmpty()) {
            sql.append(" AND approval_status = ?");
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND title LIKE ?");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }
        return sql;
    }
    private int setChapterQueryParameters(PreparedStatement stmt, String search, String approvalStatus) throws SQLException {
        int index = 1;

        if (approvalStatus != null && !approvalStatus.trim().isEmpty()) {
            stmt.setString(index++, approvalStatus);
        }

        if (search != null && !search.trim().isEmpty()) {
            stmt.setString(index, "%" + search + "%");
        }

        return index;
    }
    private int setChapterQueryParameters(PreparedStatement stmt, String search, String approvalStatus, String status) throws SQLException {
        int index = 1;

        if (approvalStatus != null && !approvalStatus.trim().isEmpty()) {
            stmt.setString(index++, approvalStatus);
        }

        if (search != null && !search.trim().isEmpty()) {
            stmt.setString(index++, "%" + search + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            stmt.setString(index++, status);
        }

        return index;
    }
    /**
     * Find a chapter by its ID.
     *
     * @param chapterId the ID of the chapter
     * @return the Chapter object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findById(int chapterId) throws SQLException {
        String sql = "SELECT * FROM chapters WHERE chapter_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractChapterFromResultSet(rs);
                }
            }
        }
        return null;
    } /**
     * Find a chapter by its ID.
     *
     * @param chapterId the ID of the chapter
     * @return the Chapter object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findById(int chapterId, String approval_status) throws SQLException {
        String sql = "SELECT * FROM chapters WHERE chapter_id = ? AND is_deleted = 0";

        if (approval_status != null && !approval_status.trim().isEmpty()) {
            sql += " AND approval_status = ?";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            if (approval_status != null && !approval_status.trim().isEmpty()) {
                ps.setString(2, approval_status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return  extractChapterFromResultSet(rs);
                }
            }
        }
        return null;
    }


    /**
     * Insert a new chapter into the database.
     *
     * @param chapter  the Chapter object to insert
     * @param seriesId the ID of the series the chapter belongs to
     * @param authorId the ID of the author creating the chapter
     * @return true if insertion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(Chapter chapter, int seriesId, int authorId) throws SQLException {
        String sql = "INSERT INTO chapters " + "(series_id, user_id, chapter_number, title, content, status, approval_status, is_deleted, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());

            ps.setInt(1, seriesId);
            ps.setInt(2, authorId);
            ps.setInt(3, chapter.getChapterNumber());
            ps.setString(4, chapter.getTitle());
            ps.setString(5, chapter.getContent());
            ps.setString(6, chapter.getStatus());
            ps.setString(7, chapter.getApprovalStatus());
            ps.setBoolean(8, false);
            ps.setTimestamp(9, now);
            ps.setTimestamp(10, now);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) chapter.setChapterId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Update an existing chapter in the database.
     *
     * @param chapter the Chapter object with updated data
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean update(Chapter chapter) throws SQLException {
        String sql = "UPDATE chapters SET " + "chapter_number = ?, title = ?, content = ?, status = ?, updated_at = ? "
                + "WHERE chapter_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapter.getChapterNumber());
            ps.setString(2, chapter.getTitle());
            ps.setString(3, chapter.getContent());
            ps.setString(4, chapter.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(6, chapter.getChapterId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int chapterId, String approvalStatus) throws SQLException {
        String sql = "UPDATE chapters SET approval_status = ?, updated_at = ? WHERE chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, approvalStatus);
            ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(3, chapterId);
            return ps.executeUpdate() > 0;
        }
    }
    /**
     * Soft delete a chapter by setting its is_deleted flag to true.
     *
     * @param chapterId the ID of the chapter to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(int chapterId) throws SQLException {
        String sql = "UPDATE chapters SET is_deleted = 1, updated_at = ? WHERE chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(2, chapterId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteBySeriesId(int seriesId) throws SQLException {
        String sql = "UPDATE chapters SET is_deleted = 1, updated_at = ? WHERE series_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(2, seriesId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieve a paginated list of chapters authored by a specific user with optional filters.
     * Author mode
     *
     * @param userId       the ID of the author
     * @param offset       the starting point for pagination
     * @param pageSize     the number of records to retrieve
     * @param statusFilter optional status filter (e.g., "published", "draft")
     * @param keyword      optional keyword to search in series or chapter titles
     * @return a list of ChapterListItem objects matching the criteria
     * @throws SQLException if a database access error occurs
     */
    public List<ChapterItemDTO> getAuthoredChapters(int userId, int offset, int pageSize, String statusFilter, String keyword) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT c.chapter_id, c.series_id, s.title AS series_title, c.chapter_number, c.title AS chapter_title, c.status, c.updated_at FROM chapters c " + "JOIN series s ON c.series_id = s.series_id " + "JOIN series_author sa ON sa.series_id = s.series_id " + "WHERE sa.user_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append("AND c.status = ? ");
            params.add(statusFilter);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (s.title LIKE ? OR c.title LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        sql.append("ORDER BY c.updated_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<ChapterItemDTO> list = new ArrayList<>();
                while (rs.next()) {
                    ChapterItemDTO it = new ChapterItemDTO();
                    it.setChapterId(rs.getInt("chapter_id"));
                    it.setSeriesId(rs.getInt("series_id"));
                    it.setSeriesTitle(rs.getString("series_title"));
                    it.setChapterNumber(rs.getInt("chapter_number"));
                    it.setTitle(rs.getString("chapter_title"));
                    it.setStatus(rs.getString("status"));
                    String up = rs.getString("updated_at");
                    it.setUpdatedAt(up != null ? up : null);
                    list.add(it);
                }
                return list;
            }
        }
    }

    /**
     * Count the total number of chapters authored by a specific user with optional filters.
     *
     * @param userId       the ID of the author
     * @param statusFilter optional status filter (e.g., "published", "draft")
     * @param keyword      optional keyword to search in series or chapter titles
     * @return the total count of chapters matching the criteria
     * @throws SQLException if a database access error occurs
     */
    public int countAuthoredChapters(int userId, String statusFilter, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) " + "FROM chapters c " + "JOIN series s ON c.series_id = s.series_id " + "JOIN series_author sa ON sa.series_id = s.series_id " + "WHERE sa.user_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (statusFilter != null && !statusFilter.isBlank()) {
            sql.append("AND c.status = ? ");
            params.add(statusFilter);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (s.title LIKE ? OR c.title LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Retrieve a paginated list of chapters from a user's reading history with optional keyword filter.
     * History mode
     *
     * @param userId   the ID of the user
     * @param offset   the starting point for pagination
     * @param pageSize the number of records to retrieve
     * @param keyword  optional keyword to search in series or chapter titles
     * @return a list of ChapterListItem objects from the user's reading history matching the criteria
     * @throws SQLException if a database access error occurs
     */
    public List<ChapterItemDTO> getReadingHistoryChapters(int userId, int offset, int pageSize, String keyword) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT c.chapter_id, c.series_id, s.title AS series_title, " + "       c.chapter_number, c.title AS chapter_title, c.status, c.updated_at, h.last_read_at, cover_image_url " + "FROM reading_history h " + "JOIN chapters c ON c.chapter_id = h.chapter_id " + "JOIN series s ON s.series_id = c.series_id " + "WHERE h.user_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (s.title LIKE ? OR c.title LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        sql.append("ORDER BY h.last_read_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<ChapterItemDTO> list = new ArrayList<>();
                while (rs.next()) {
                    ChapterItemDTO it = new ChapterItemDTO();
                    it.setChapterId(rs.getInt("chapter_id"));
                    it.setSeriesId(rs.getInt("series_id"));
                    it.setSeriesTitle(rs.getString("series_title"));
                    it.setChapterNumber(rs.getInt("chapter_number"));
                    it.setTitle(rs.getString("chapter_title"));
                    it.setStatus(rs.getString("status"));
                    it.setCoverImgUrl("img/" + rs.getString("cover_image_url"));
                    String up = rs.getString("updated_at");
                    it.setUpdatedAt(up != null ? up : null);
                    String lr =  FormatUtils.formatDate(rs.getTimestamp("last_read_at").toLocalDateTime());
                    it.setLastReadAt(lr != null ?lr : null);
                    list.add(it);
                }
                return list;
            }
        }
    }

    /**
     * Count the total number of chapters in a user's reading history with optional keyword filter.
     *
     * @param userId  the ID of the user
     * @param keyword optional keyword to search in series or chapter titles
     * @return the total count of chapters in the user's reading history matching the criteria
     * @throws SQLException if a database access error occurs
     */
    public int countReadingHistoryChapters(int userId, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) " + "FROM reading_history h " + "JOIN chapters c ON c.chapter_id = h.chapter_id " + "JOIN series s ON s.series_id = c.series_id " + "WHERE h.user_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (s.title LIKE ? OR c.title LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Get the latest chapter number of a series.
     *
     * @param seriesId ID of the series.
     * @return number of the latest chapter. If no chapters exist, returns 0.
     */
    public int getLatestChapterNumber(int seriesId) throws SQLException {
        String sql = "SELECT MAX(chapter_number) FROM chapters WHERE series_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }



    /**
     * Add a new chapter and return the Chapter object with the generated ID.
     *
     * @param chapter the Chapter object to add (without chapterId)
     * @return the Chapter object with the generated chapterId, or null if insertion failed
     */
    public Chapter addChapter(Chapter chapter) throws SQLException {
        String sql = "INSERT INTO chapters (series_id, chapter_number, title, content, status, created_at, updated_at) " + "VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, chapter.getSeriesId());
            ps.setInt(2, chapter.getChapterNumber());
            ps.setString(3, chapter.getTitle());
            ps.setString(4, chapter.getContent());
            ps.setString(5, chapter.getStatus());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        chapter.setChapterId(generatedKeys.getInt(1));
                        return chapter;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find an approved chapter by its ID.
     *
     * @param chapterId the ID of the chapter
     * @return the Chapter object if found and approved, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findApprovedById(int chapterId) throws SQLException {
        String sql = """
                    SELECT * FROM chapters
                    WHERE chapter_id = ? AND is_deleted = 0 AND status = 'approved'
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Find an approved chapter by its series ID and chapter number.
     *
     * @param seriesId      the ID of the series
     * @param chapterNumber the chapter number within the series
     * @return the Chapter object if found and approved, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findApprovedBySeriesAndNumber(int seriesId, int chapterNumber) throws SQLException {
        String sql = """
                    SELECT * FROM chapters
                    WHERE series_id = ? AND chapter_number = ? AND is_deleted = 0 AND status = 'approved'
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, chapterNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Find the next approved chapter in a series after the given chapter number.
     *
     * @param seriesId      the ID of the series
     * @param currentNumber the current chapter number
     * @return the next approved Chapter object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findPrevApproved(int seriesId, int currentNumber) throws SQLException {
        String sql = """
                    SELECT TOP 1 * FROM chapters
                    WHERE series_id = ? AND is_deleted = 0 AND status = 'approved'
                      AND chapter_number < ?
                    ORDER BY chapter_number DESC
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, currentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Find the next approved chapter in a series after the current chapter number.
     *
     * @param seriesId      the ID of the series
     * @param currentNumber the current chapter number
     * @return the next approved Chapter object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findNextApproved(int seriesId, int currentNumber) throws SQLException {
        String sql = """
                    SELECT TOP 1 * FROM chapters
                    WHERE series_id = ? AND is_deleted = 0 AND status = 'approved'
                      AND chapter_number > ?
                    ORDER BY chapter_number ASC
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, currentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int findSeriesIdByChapter(int chapterId) {
        String findSeriesSql = "SELECT series_id FROM chapters WHERE chapter_id = ?";
        try (PreparedStatement psFind = conn.prepareStatement(findSeriesSql)) {
            psFind.setInt(1, chapterId);
            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("series_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding series ID for chapter ID " + chapterId);
        }
        return -1;
    }

    public boolean deleteOldReadingHistory(int userId, int seriesId) throws SQLException {
        String deleteSql = "DELETE FROM reading_history WHERE user_id = ? AND chapter_id IN (SELECT chapter_id FROM chapters WHERE series_id = ?)";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
            psDelete.setInt(1, userId);
            psDelete.setInt(2, seriesId);
            return psDelete.executeUpdate() > 0;
        }
    }

    public boolean insertReadingHistory(int userId, int chapterId) throws SQLException {
        String insertSql = "INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES (?, ?, GETDATE())";
        try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
            psInsert.setInt(1, userId);
            psInsert.setInt(2, chapterId);
            return  psInsert.executeUpdate() > 0;
        }
    }

    /**
     * Map the current row of the ResultSet to a Chapter object.
     *
     * @param rs the ResultSet positioned at the desired row
     * @return a Chapter object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Chapter map(ResultSet rs) throws SQLException {
        Chapter c = new Chapter();

        c.setChapterId(rs.getInt("chapter_id"));
        c.setSeriesId(rs.getInt("series_id"));
        c.setChapterNumber(rs.getInt("chapter_number"));
        c.setTitle(rs.getString("title"));
        c.setContent(rs.getString("content"));
        c.setStatus(rs.getString("status"));
        c.setDeleted(rs.getBoolean("is_deleted"));
        c.setApprovalStatus(rs.getString("approval_status"));
        c.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        c.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));
        return c;
    }

    /**
     * Find a chapter by its ID if it is not marked as deleted.
     *
     * @param chapterId the ID of the chapter
     * @return the Chapter object if found and not deleted, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findByIdIfNotDeleted(int chapterId) throws SQLException {
        String sql = "SELECT * FROM chapters WHERE chapter_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Find a chapter by its series ID and chapter number if it is not marked as deleted.
     *
     * @param seriesId      the ID of the series
     * @param chapterNumber the chapter number within the series
     * @return the Chapter object if found and not deleted, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Chapter findBySeriesAndNumberIfNotDeleted(int seriesId, int chapterNumber) throws SQLException {
        String sql = """
                    SELECT * FROM chapters
                    WHERE series_id = ? AND chapter_number = ? AND is_deleted = 0
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, chapterNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int countChapterBySeriesId(int seriesId) throws SQLException {

        String sql = "SELECT COUNT(*) FROM chapters WHERE series_id = ? AND is_deleted = 0 AND status = 'published' AND approval_status = 'approved'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
    public int countChapterBySeriesId(int seriesId, String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chapters WHERE series_id = ? AND is_deleted = 0 ";
        if (role.equals("reader")) {
            sql += "AND status = 'published' AND approval_status = 'approved'";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<Chapter> findChapterBySeriesId(int seriesId, String approvalStatus) throws SQLException {
        List<Chapter> chapterList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM chapters WHERE series_id = ? AND is_deleted = 0");
        if (approvalStatus != null && !approvalStatus.trim().isEmpty()) {
            sql.append(" AND approval_status = ?  AND status = 'published'");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, seriesId);
            if (approvalStatus != null && !approvalStatus.trim().isEmpty()) {
                ps.setString(2, approvalStatus);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                chapterList.add(extractChapterFromResultSet(rs));
                }
            }
            return chapterList;
        }
    }

    /**
     * Retrieves the next chapter in a series based on the chapter index.
     *
     * @param seriesId     the ID of the series
     * @param chapterNumber the index of the current chapter
     * @return the next Chapter object if it exists, or null if there is no next
     *         chapter.
     * @throws SQLException If a database access error occurs.
     */
    public Chapter getNextChapter(int seriesId, int chapterNumber) throws SQLException {
        String sql = "SELECT TOP 1 * FROM Chapters WHERE series_id = ? AND chapter_number > ? AND status = 'published' AND approval_status = 'approved' AND is_deleted = 0 ORDER BY chapter_number ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, chapterNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractChapterFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the previous chapter in a series based on the chapter index.
     *
     * @param seriesId     the ID of the series
     * @param chapterNumber the index of the current chapter
     * @return the previous Chapter object if it exists, or null if there is no
     *         previous chapter.
     * @throws SQLException If a database access error occurs.
     */
    public Chapter getPreviousChapter(int seriesId, int chapterNumber) throws SQLException {
        String sql = "SELECT TOP 1 * FROM Chapters WHERE series_id = ? AND chapter_number < ? AND status = 'published' AND approval_status = 'approved' AND is_deleted = 0 ORDER BY chapter_number DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, chapterNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractChapterFromResultSet(rs);
                }
            }
        }
        return null;
    }


    /**
     * Get the latest chapter number of a series.
     *
     * @param seriesId ID of the series.
     * @return number of the latest chapter. If no chapters exist, returns 0.
     */
    public int getFirstChapterNumber(int seriesId) throws SQLException {
        String sql = "SELECT TOP 1 chapter_id  FROM chapters WHERE series_id = ?  AND status = 'published' AND approval_status = 'approved' AND is_deleted = 0 ORDER BY chapter_number ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    /**
     * Extract a Chapter object from the current row of the ResultSet.
     *
     * @param rs the ResultSet positioned at the desired row
     * @return a Chapter object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Chapter extractChapterFromResultSet(ResultSet rs) throws SQLException {
        Chapter chapter = new Chapter();
        chapter.setChapterId(rs.getInt("chapter_id"));
        chapter.setSeriesId(rs.getInt("series_id"));
        chapter.setAuthorId(rs.getInt("user_id"));
        chapter.setChapterNumber(rs.getInt("chapter_number"));
        chapter.setTitle(rs.getString("title"));
        chapter.setContent(rs.getString("content"));
        chapter.setStatus(rs.getString("status"));
        chapter.setApprovalStatus(rs.getString("approval_status"));
        chapter.setDeleted(rs.getBoolean("is_deleted"));
        chapter.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        chapter.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));
        return chapter;
    }

    public int countAllNonDeleted() {
        String sql = "SELECT COUNT(*) AS total FROM chapters WHERE is_deleted = 0";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStatus(String pending) {
        String sql = "SELECT COUNT(*) AS total FROM review_chapter WHERE status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pending);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }




}
