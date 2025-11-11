package dao;

import model.PointHistory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PointHistory
 * Handles CRUD operations and specific queries related to point history records.
 * Assumes a database table 'point_history' with columns:
 */
public class PointHistoryDAO {
    private final Connection conn;

    public PointHistoryDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new point history record into the database.
     *
     * @param history PointHistory object containing the details to insert.
     * @return true if insertion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean insert(PointHistory history) throws SQLException {
        String sql = """
            INSERT INTO point_history (user_id, points_change, reason, reference_type, reference_id, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, history.getUserId());
            ps.setInt(2, history.getPointChange());
            ps.setString(3, history.getReason());
            ps.setString(4, history.getReferenceType());
            if (history.getReferenceId() != 0) {
                ps.setInt(5, history.getReferenceId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setTimestamp(6, Timestamp.valueOf(
                    history.getCreatedAt() != null ? history.getCreatedAt() : LocalDateTime.now()
            ));
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves a point history record by its ID.
     *
     * @param historyId The ID of the point history record.
     * @return PointHistory object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public PointHistory getById(int historyId) throws SQLException {
        String sql = "SELECT * FROM point_history WHERE history_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all point history records.
     *
     * @return List of PointHistory objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<PointHistory> getAll() throws SQLException {
        List<PointHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM point_history ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves all point history records for a specific user.
     *
     * @param userId The ID of the user.
     * @return List of PointHistory objects for the user.
     * @throws SQLException if a database access error occurs.
     */
    public List<PointHistory> getByUserId(int userId) throws SQLException {
        List<PointHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM point_history WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        }
        return list;
    }

    /**
     * Deletes a point history record by its ID.
     *
     * @param historyId The ID of the point history record to delete.
     * @return true if deletion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean delete(int historyId) throws SQLException {
        String sql = "DELETE FROM point_history WHERE history_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, historyId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes all point history records for a specific user.
     *
     * @param userId The ID of the user whose point history records are to be deleted.
     * @return true if deletion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM point_history WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Tracks if a user has performed a specific action today.
     *
     * @param userId The ID of the user.
     * @param action The action type to track.
     * @return true if the user has performed the action today, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean trackLogin(int userId, String action) throws SQLException {
        String sql = "SELECT 1 FROM point_history WHERE user_id = ? AND reference_type = ? AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Tracks if a user has exceeded a limit of specific actions today.
     *
     * @param userId The ID of the user.
     * @param type The action type to track.
     * @param limit The maximum allowed number of actions.
     * @return true if the user has exceeded the limit, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean trackAction (int userId, String type, int limit) throws SQLException {
        String sql = "SELECT COUNT(*) FROM point_history WHERE user_id = ? AND reference_type = ? AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > limit;
                }
            }
            return true;
        }
    }

    /**
     * Extracts a PointHistory object from the current row of the given ResultSet.
     *
     * @param rs The ResultSet to extract data from.
     * @return A PointHistory object populated with data from the ResultSet.
     * @throws SQLException if a database access error occurs.
     */
    private PointHistory extractFromResultSet(ResultSet rs) throws SQLException {
        PointHistory ph = new PointHistory();
        ph.setHistoryId(rs.getInt("history_id"));
        ph.setUserId(rs.getInt("user_id"));
        ph.setPointChange(rs.getInt("point_change"));
        ph.setReason(rs.getString("reason"));
        ph.setReferenceType(rs.getString("reference_type"));
        int refId = rs.getInt("reference_id");
        ph.setReferenceId(rs.wasNull() ? 0 : refId);
        Timestamp ts = rs.getTimestamp("created_at");
        ph.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return ph;
    }
}
