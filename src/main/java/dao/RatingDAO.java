package dao;

import model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {
    private final Connection conn;

    public RatingDAO(Connection conn) {
        this.conn = conn;
    }

    // 🔹 Helper: Map ResultSet → Rating
    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating r = new Rating();
        r.setSeriesId(rs.getInt("series_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setRatingValue(rs.getByte("rating_value"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);

        return r;
    }

    // 🔹 CREATE
    public boolean insert(Rating rating) throws SQLException {
        String sql = "INSERT INTO ratings (series_id, user_id, rating_value, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getSeriesId());
            stmt.setInt(2, rating.getUserId());
            stmt.setInt(3, rating.getRatingValue());
            stmt.setTimestamp(4, Timestamp.valueOf(rating.getCreatedAt() != null ? rating.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(5, Timestamp.valueOf(rating.getUpdatedAt() != null ? rating.getUpdatedAt() : LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    // 🔹 READ ALL
    public List<Rating> getAll() throws SQLException {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT * FROM ratings";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToRating(rs));
            }
        }
        return list;
    }

    // 🔹 READ by user_id
    public List<Rating> getByUserId(int userId) throws SQLException {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRating(rs));
                }
            }
        }
        return list;
    }

    // 🔹 READ by series_id
    public List<Rating> getBySeriesId(int seriesId) throws SQLException {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRating(rs));
                }
            }
        }
        return list;
    }

    // 🔹 READ one (for specific user + series)
    public Rating findById(int seriesId, int userId) throws SQLException {
        String sql = "SELECT * FROM ratings WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRating(rs);
                }
            }
        }
        return null;
    }

    // 🔹 UPDATE
    public boolean update(Rating rating) throws SQLException {
        String sql = "UPDATE ratings SET rating_value = ?, updated_at = ? WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getRatingValue());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, rating.getSeriesId());
            stmt.setInt(4, rating.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    // 🔹 DELETE
    public boolean delete(int seriesId, int userId) throws SQLException {
        String sql = "DELETE FROM ratings WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // 🔹 Tính trung bình rating của 1 series
    public double getAverageRating(int seriesId) throws SQLException {
        String sql = "SELECT AVG(CAST(rating_value AS FLOAT)) AS avg_rating FROM ratings WHERE series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }

    // 🔹 Đếm số lượng người đánh giá cho series
    public int getRatingCount(int seriesId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM ratings WHERE series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
}