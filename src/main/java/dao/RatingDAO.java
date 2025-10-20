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

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating r = new Rating();
        r.setSeriesId(rs.getInt("series_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setRatingValue(rs.getByte("score"));
        Timestamp updated = rs.getTimestamp("rated_at");
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);

        return r;
    }

    public boolean insert(Rating rating) throws SQLException {
        String sql = "INSERT INTO ratings (series_id, user_id, score, rated_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getSeriesId());
            stmt.setInt(2, rating.getUserId());
            stmt.setInt(3, rating.getRatingValue());
            stmt.setTimestamp(4, Timestamp.valueOf(rating.getCreatedAt() != null ? rating.getCreatedAt() : LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

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

    public boolean getRatingValueByUserId(Rating rating) throws SQLException {
        String sql = "SELECT * FROM ratings WHERE user_id = ? AND series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getUserId());
            stmt.setInt(2, rating.getSeriesId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public int getRatingValueByUserId(int userId, int seriesId) throws SQLException {
        String sql = "SELECT score FROM ratings WHERE user_id = ? AND series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, seriesId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("score") : 0;
        }
    }

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

    public boolean update(Rating rating) throws SQLException {
        String sql = "UPDATE ratings SET score = ?, rated_at = ? WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getRatingValue());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, rating.getSeriesId());
            stmt.setInt(4, rating.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int seriesId, int userId) throws SQLException {
        String sql = "DELETE FROM ratings WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public double getAverageRating(int seriesId) throws SQLException {
        String sql = "SELECT AVG(CAST(score AS FLOAT)) AS avg_rating FROM ratings WHERE series_id = ?";
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
    public int getAverageRatingOfAuthor(int userId) throws SQLException {
        String sql = "SELECT AVG(CAST(r.score AS FLOAT)) AS avg_rating FROM ratings r JOIN series_author sa ON r.series_id = sa.series_id WHERE sa.user_id = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("avg_rating");
                }
            }
        }
        return 0;
    }

    public int getRatingSumBySeriesId(int seriesId) throws SQLException {
        String sql = "SELECT SUM(score) AS total FROM ratings WHERE series_id = ?";
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