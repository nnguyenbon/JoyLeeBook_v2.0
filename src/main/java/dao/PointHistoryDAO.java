package dao;

import model.PointHistory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PointHistoryDAO {
    private final Connection conn;

    public PointHistoryDAO(Connection conn) {
        this.conn = conn;
    }

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


    public boolean delete(int historyId) throws SQLException {
        String sql = "DELETE FROM point_history WHERE history_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, historyId);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM point_history WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }


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
