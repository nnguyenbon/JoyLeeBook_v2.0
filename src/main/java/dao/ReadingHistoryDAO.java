package dao;

import model.ReadingHistory;

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
     * Extracts a ReadingHistory object from the current row of the given ResultSet.
     *
     * @param rs The ResultSet to extract data from.
     * @return A ReadingHistory object populated with data from the ResultSet.
     * @throws SQLException If an SQL error occurs while accessing the ResultSet.
     */
    private ReadingHistory extractReadingHistoryFromResultSet(ResultSet rs) throws SQLException {
        ReadingHistory rh = new ReadingHistory();
        rh.setUserId(rs.getInt("user_id"));
        rh.setChapterId(rs.getInt("chapter_id"));
        Timestamp ts = rs.getTimestamp("last_read_at");
        rh.setLastReadAt(ts != null ? ts.toLocalDateTime() : null);
        return rh;
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
     * Retrieves all ReadingHistory records from the database.
     *
     * @return A list of all ReadingHistory records.
     * @throws SQLException If an SQL error occurs during the retrieval.
     */
    public List<ReadingHistory> getAll() throws SQLException {
        List<ReadingHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM reading_history";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractReadingHistoryFromResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves all ReadingHistory records for a specific user.
     *
     * @param userId The ID of the user whose reading history is to be retrieved.
     * @return A list of ReadingHistory records for the specified user.
     * @throws SQLException If an SQL error occurs during the retrieval.
     */
    public List<ReadingHistory> getByUserId(int userId) throws SQLException {
        List<ReadingHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM reading_history WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReadingHistoryFromResultSet(rs));
                }
            }
        }
        return list;
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

    public boolean deleteByChapterId(int chapterId) throws SQLException {
        String sql = "DELETE FROM reading_history WHERE chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Upserts a ReadingHistory record in the database. If a record with the given userId and chapterId exists,
     * it updates the last_read_at timestamp. If it does not exist, it inserts a new record.
     *
     * @param userId    The ID of the user.
     * @param chapterId The ID of the chapter.
     * @throws SQLException If an SQL error occurs during the upsert operation.
     */
    public void upsert(int userId, int chapterId) throws SQLException {
        String sql = """
                    MERGE reading_history AS t
                    USING (SELECT ? AS user_id, ? AS chapter_id) AS s
                    ON (t.user_id = s.user_id AND t.chapter_id = s.chapter_id)
                    WHEN MATCHED THEN UPDATE SET last_read_at = GETDATE()
                    WHEN NOT MATCHED THEN INSERT (user_id, chapter_id, last_read_at)
                         VALUES (s.user_id, s.chapter_id, GETDATE());
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, chapterId);
            ps.executeUpdate();
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