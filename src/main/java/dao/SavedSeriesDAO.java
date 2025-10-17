package dao;

import model.SavedSeries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SavedSeriesDAO {
    private final Connection conn;

    public SavedSeriesDAO(Connection conn) {
        this.conn = conn;
    }

    private SavedSeries mapResultSetToSavedSeries(ResultSet rs) throws SQLException {
        SavedSeries ss = new SavedSeries();
        ss.setUserId(rs.getInt("user_id"));
        ss.setSeriesId(rs.getInt("series_id"));

        Timestamp ts = rs.getTimestamp("save_at");
        ss.setSavedAt(ts != null ? ts.toLocalDateTime() : null);

        return ss;
    }

    public boolean insert(SavedSeries ss) throws SQLException {
        String sql = "INSERT INTO saved_series (user_id, series_id, saved_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ss.getUserId());
            stmt.setInt(2, ss.getSeriesId());
            stmt.setTimestamp(3, Timestamp.valueOf(ss.getSavedAt() != null ? ss.getSavedAt() : LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    public List<SavedSeries> getAll() throws SQLException {
        List<SavedSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM saved_series ORDER BY saved_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSavedSeries(rs));
            }
        }
        return list;
    }

    public List<SavedSeries> getByUserId(int userId) throws SQLException {
        List<SavedSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM saved_series WHERE user_id = ? ORDER BY saved_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSavedSeries(rs));
                }
            }
        }
        return list;
    }

    public boolean isSaved(int userId, int seriesId) throws SQLException {
        String sql = "SELECT 1 FROM saved_series WHERE user_id = ? AND series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean delete(int userId, int seriesId) throws SQLException {
        String sql = "DELETE FROM saved_series WHERE user_id = ? AND series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, seriesId);
            return stmt.executeUpdate() > 0;
        }
    }
}
