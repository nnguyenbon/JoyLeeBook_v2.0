package dao;

import model.SeriesAuthor;
import model.User;

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
     * @param seriesAuthor the SeriesAuthor object to be added
     * @return true if the record was added successfully, false otherwise
     */
    public boolean insertSeriesAuthor(SeriesAuthor seriesAuthor) throws SQLException {
        String sql = "INSERT INTO series_author (series_id, user_id, is_owner) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesAuthor.getSeriesId());
            ps.setInt(2, seriesAuthor.getAuthorId());
            ps.setBoolean(3, seriesAuthor.isOwner());
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
        String sql = "SELECT series_author.series_id, users.user_id, users.username, series_author.is_owner FROM series_author INNER JOIN users ON series_author.user_id = users.user_id WHERE series_author.series_id = ? AND series_author.user_id = ?";
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

    public List<SeriesAuthor> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM series_author WHERE user_id = ?";
        List<SeriesAuthor> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    list.add(mapResultSetToSeriesAuthor(rs));
                }
            }
        }
        return list;
    }
    public SeriesAuthor findOwnerIdBySeriesId(int seriesId) throws SQLException {
        String sql = "SELECT series_author.series_id, users.user_id, users.username, series_author.is_owner\n" +
                "FROM   series_author \n" +
                "INNER JOIN users ON series_author.user_id = users.user_id \n" +
                "WHERE series_author.series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeriesAuthor(rs);
                }
            }
        }
        return null;
    }
    public List<SeriesAuthor> findBySeriesId(int seriesId) throws SQLException {
        String sql = "SELECT * FROM series_author WHERE series_id = ?";
        List<SeriesAuthor> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSeriesAuthor(rs));
                }
            }
        }
        return list;
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

    public List<String> authorsOfSeries(int seriesId) throws SQLException {
        String sql = "SELECT DISTINCT username FROM users u JOIN series_author sa ON u.user_id = sa.user_id AND series_id = ?";
        List<String> authorsName = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authorsName.add(rs.getString("username"));
                }
                return authorsName;
            }
        }
    }

    /**
     * Finds all users associated with a given series.
     *
     * @param seriesId The ID of the series.
     * @return A list of User objects.
     */
    public List<User> findUsersBySeriesId(int seriesId) throws SQLException {
        List<User> users = new ArrayList<>();
        // Assumes a JOIN between SeriesAuthors and Users tables
        String sql = "SELECT u.* FROM Users u JOIN series_author sa ON u.user_id = sa.user_id WHERE sa.series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    /**
     * Adds an author to a series.
     *
     * @param seriesId The ID of the series.
     * @param userId   The ID of the user to add.
     */
    public void addAuthorToSeries(int seriesId, int userId) throws SQLException {
        String sql = "INSERT INTO series_author (series_id, user_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Removes an author from a series.
     *
     * @param seriesId The ID of the series.
     * @param userId   The ID of the user to remove.
     */
    public void removeAuthorFromSeries(int seriesId, int userId) throws SQLException {
        String sql = "DELETE FROM series_author WHERE series_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
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
        seriesAuthor.setOwner(rs.getBoolean("is_owner"));
        seriesAuthor.setAuthorName(rs.getString("username"));
        return seriesAuthor;
    }
}

