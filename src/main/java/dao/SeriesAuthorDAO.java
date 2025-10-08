package dao;

import model.SeriesAuthor;
import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeriesAuthorDAO {
    private final Connection conn;

    public SeriesAuthorDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean add(SeriesAuthor sa) throws SQLException {
        String sql = "INSERT INTO series_author (series_id, user_id, added_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sa.getSeriesId());
            ps.setInt(2, sa.getAuthorId());
            ps.setTimestamp(3, Timestamp.valueOf(sa.getAddedAt() != null ? sa.getAddedAt() : LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        }
    }

    public SeriesAuthor findById(int seriesId, int userId) throws SQLException {
        String sql = "SELECT * FROM series_author WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeriesAuthor(rs);
                }
            }
        }
        return null;
    }

    public List<SeriesAuthor> findAll() throws SQLException {
        String sql = "SELECT * FROM series_author";
        List<SeriesAuthor> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSeriesAuthor(rs));
            }
        }
        return list;
    }

    public boolean update(SeriesAuthor sa) throws SQLException {
        String sql = "UPDATE series_author SET added_at = ? WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(sa.getAddedAt()));
            ps.setInt(2, sa.getSeriesId());
            ps.setInt(3, sa.getAuthorId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int seriesId, int userId) throws SQLException {
        String sql = "DELETE FROM series_author WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private SeriesAuthor mapResultSetToSeriesAuthor(ResultSet rs) throws SQLException {
        SeriesAuthor seriesAuthor = new SeriesAuthor();
        seriesAuthor.setAuthorId(rs.getInt("user_id"));
        seriesAuthor.setSeriesId(rs.getInt("series_id"));
        Timestamp created = rs.getTimestamp("created_at");
        seriesAuthor.setAddedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());
        return seriesAuthor;
    }
}

