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

/**
 * SeriesAuthorDAO class provides CRUD operations for SeriesAuthor entities in the database.
 *
 * @author KToan, HaiDD-dev
 */
public class SeriesAuthorDAO {
    private final Connection conn;

    public SeriesAuthorDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Adds a new SeriesAuthor record to the database.
     *
     * @param sa the SeriesAuthor object to be added
     * @return true if the record was added successfully, false otherwise
     */
    public boolean add(SeriesAuthor sa) throws SQLException {
        String sql = "INSERT INTO series_author (series_id, user_id, added_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sa.getSeriesId());
            ps.setInt(2, sa.getAuthorId());
            ps.setTimestamp(3, Timestamp.valueOf(sa.getAddedAt() != null ? sa.getAddedAt() : LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Finds a SeriesAuthor record by series ID and user ID.
     *
     * @param seriesId the ID of the series
     * @param userId   the ID of the user
     * @return the SeriesAuthor object if found, null otherwise
     */
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

    /**
     * Retrieves all SeriesAuthor records from the database.
     *
     * @return a list of all SeriesAuthor objects
     */
    public List<SeriesAuthor> findAll() throws SQLException {
        String sql = "SELECT * FROM series_author";
        List<SeriesAuthor> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSeriesAuthor(rs));
            }
        }
        return list;
    }

    /**
     * Updates an existing SeriesAuthor record in the database.
     *
     * @param sa the SeriesAuthor object with updated information
     * @return true if the record was updated successfully, false otherwise
     */
    public boolean update(SeriesAuthor sa) throws SQLException {
        String sql = "UPDATE series_author SET added_at = ? WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(sa.getAddedAt()));
            ps.setInt(2, sa.getSeriesId());
            ps.setInt(3, sa.getAuthorId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a SeriesAuthor record from the database.
     *
     * @param seriesId the ID of the series
     * @param userId   the ID of the user
     * @return true if the record was deleted successfully, false otherwise
     */
    public boolean delete(int seriesId, int userId) throws SQLException {
        String sql = "DELETE FROM series_author WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Checks if a user is an author of a specific series.
     *
     * @param userId   the ID of the user
     * @param seriesId the ID of the series
     * @return true if the user is an author of the series, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isUserAuthorOfSeries(int userId, int seriesId) throws SQLException {
        String sql = "SELECT COUNT(1) FROM series_author WHERE user_id = ? AND series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) <= 0;
                }
            }
        }
        return false;
    }

    /**
     * Utility method to map a ResultSet row to a SeriesAuthor object.
     *
     * @param rs the ResultSet to map
     * @return the mapped SeriesAuthor object
     */
    private SeriesAuthor mapResultSetToSeriesAuthor(ResultSet rs) throws SQLException {
        SeriesAuthor seriesAuthor = new SeriesAuthor();
        seriesAuthor.setAuthorId(rs.getInt("user_id"));
        seriesAuthor.setSeriesId(rs.getInt("series_id"));
        Timestamp created = rs.getTimestamp("created_at");
        seriesAuthor.setAddedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());
        return seriesAuthor;
    }
}

