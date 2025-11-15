package dao;

import model.ReadingHistory;
import utils.FormatUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ReadingHistory
 *
 * @author KToan, HaiDD-dev
 */
public class ReadingHistoryDAO {
    private final Connection conn;

    public ReadingHistoryDAO(Connection conn) {
        this.conn = conn;
    }


    /**
     * Inserts a new ReadingHistory record into the database.
     *
     * @param rh The ReadingHistory object to insert.
     * @return true if the insertion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the insertion.
     */
    public boolean insert(ReadingHistory rh) throws SQLException {
        String sql = "INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rh.getUserId());
            ps.setInt(2, rh.getChapterId());
            ps.setTimestamp(3, Timestamp.valueOf(rh.getLastReadAt()));
            return ps.executeUpdate() > 0;
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
    public List<ReadingHistory> getReadingHistoryChapters(int userId, int offset, int pageSize, String keyword) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT c.chapter_id, c.series_id, s.title AS series_title, " + " c.chapter_number, c.title AS chapter_title, c.status, c.updated_at, h.last_read_at, cover_image_url " + "FROM reading_history h " + "JOIN chapters c ON c.chapter_id = h.chapter_id " + "JOIN series s ON s.series_id = c.series_id " + "WHERE h.user_id = ? ");

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
                List<ReadingHistory> list = new ArrayList<>();
                while (rs.next()) {
                    ReadingHistory it = new ReadingHistory();
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
     * Updates an existing ReadingHistory record in the database.
     *
     * @param rh The ReadingHistory object containing updated data.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the update.
     */
    public boolean update(ReadingHistory rh) throws SQLException {
        String sql = "UPDATE reading_history SET last_read_at = ? WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(rh.getLastReadAt()));
            ps.setInt(2, rh.getUserId());
            ps.setInt(3, rh.getChapterId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a ReadingHistory record from the database.
     *
     * @param userId    The ID of the user.
     * @param chapterId The ID of the chapter.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the deletion.
     */
    public boolean delete(int userId, int chapterId) throws SQLException {
        String sql = "DELETE FROM reading_history WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, chapterId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes all ReadingHistory records for a specific user.
     *
     * @param userId The ID of the user.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the deletion.
     */
    public boolean deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM reading_history WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }


    public void updateReadingHistory(int userId, int chapterId) throws SQLException {
        int seriesId = -1;
        String findSeriesSql = "SELECT series_id FROM chapters WHERE chapter_id = ?";
        try (PreparedStatement psFind = conn.prepareStatement(findSeriesSql)) {
            psFind.setInt(1, chapterId);
            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    seriesId = rs.getInt("series_id");
                } else {
                    System.out.println("Chapter ID " + chapterId + " not found.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding series ID for chapter ID " + chapterId);
        }

        String deleteSql = "DELETE FROM reading_history WHERE user_id = ? AND chapter_id IN (SELECT chapter_id FROM chapters WHERE series_id = ?)";
        String insertSql = "INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES (?, ?, GETDATE())";

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, userId);
                psDelete.setInt(2, seriesId);
                psDelete.executeUpdate();
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, userId);
                psInsert.setInt(2, chapterId);
                psInsert.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Error updating reading history for user ID " + userId + " and chapter ID " + chapterId);
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction");
            }
            System.out.println(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException setAutoCommitEx) {
                System.out.println("Error restoring auto-commit setting");
            }
        }
    }
}