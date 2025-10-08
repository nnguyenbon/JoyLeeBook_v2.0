package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Series;

public class SeriesDAO {
    private final Connection conn;

    // ✅ Nhận connection từ bên ngoài
    public SeriesDAO(Connection conn) {
        this.conn = conn;
    }

    // 🔹 Lấy tất cả series chưa bị xóa
    public List<Series> getAll() throws SQLException {
        List<Series> list = new ArrayList<>();
        String sql = "SELECT * FROM series WHERE is_deleted = false";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractSeriesFromResultSet(rs));
            }
        }
        return list;
    }

    public Series findById(int seriesId) throws SQLException {
        String sql = "SELECT * FROM series WHERE series_id = ? AND is_deleted = false";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractSeriesFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(Series series, int authorId) throws SQLException {
        String sql = "INSERT INTO series (title, author_id, description, cover_image_url, status, created_at, updated_at, is_deleted)VALUES (?, ?, ?, ?, ?, ?, ?, false)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, series.getTitle());
            ps.setInt(2, authorId);
            ps.setString(3, series.getDescription());
            ps.setString(4, series.getCoverImgUrl());
            ps.setInt(5, series.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        series.setSeriesId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Series series) throws SQLException {
        String sql = "UPDATE series SET title = ?, description = ?, cover_image_url = ?, status = ?, updated_at = ? WHERE series_id = ? AND is_deleted = false";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, series.getTitle());
            ps.setString(2, series.getDescription());
            ps.setString(3, series.getCoverImgUrl());
            ps.setInt(4, series.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, series.getSeriesId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int seriesId) throws SQLException {
        String sql = "UPDATE series SET is_deleted = true, updated_at = ? WHERE series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, seriesId);
            return ps.executeUpdate() > 0;
        }
    }

    private Series extractSeriesFromResultSet(ResultSet rs) throws SQLException {
        Series s = new Series();
        s.setSeriesId(rs.getInt("series_id"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setCoverImgUrl(rs.getString("cover_image_url"));
        s.setStatus(rs.getInt("status"));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        s.setDeleted(rs.getBoolean("is_deleted"));
        return s;
    }
}
