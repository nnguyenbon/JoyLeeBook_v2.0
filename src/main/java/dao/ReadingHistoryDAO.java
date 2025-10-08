package dao;

import model.ReadingHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReadingHistoryDAO {
    private final Connection conn;

    public ReadingHistoryDAO(Connection conn) {
        this.conn = conn;
    }

    private ReadingHistory extractReadingHistoryFromResultSet(ResultSet rs) throws SQLException {
        ReadingHistory rh = new ReadingHistory();
        rh.setUserId(rs.getInt("user_id"));
        rh.setChapterId(rs.getInt("chapter_id"));
        Timestamp ts = rs.getTimestamp("last_read_at");
        rh.setLastReadAt(ts != null ? ts.toLocalDateTime() : null);
        return rh;
    }

    // CREATE (insert)
    public boolean insert(ReadingHistory rh) throws SQLException {
        String sql = "INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rh.getUserId());
            ps.setInt(2, rh.getChapterId());
            ps.setTimestamp(3, Timestamp.valueOf(rh.getLastReadAt()));
            return ps.executeUpdate() > 0;
        }
    }

    // READ (get all)
    public List<ReadingHistory> getAll() throws SQLException {
        List<ReadingHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM reading_history";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractReadingHistoryFromResultSet(rs));
            }
        }
        return list;
    }

    // READ by user_id
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

    // UPDATE
    public boolean update(ReadingHistory rh) throws SQLException {
        String sql = "UPDATE reading_history SET last_read_at = ? WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(rh.getLastReadAt()));
            ps.setInt(2, rh.getUserId());
            ps.setInt(3, rh.getChapterId());
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean delete(int userId, int chapterId) throws SQLException {
        String sql = "DELETE FROM reading_history WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, chapterId);
            return ps.executeUpdate() > 0;
        }
    }
}
