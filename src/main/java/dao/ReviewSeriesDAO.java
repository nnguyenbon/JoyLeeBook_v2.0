package dao;

import model.ReviewSeries;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ReviewSeriesDAO {
    private final Connection conn;

    /**
     * Constructs a ReviewSeriesDAO with an active database connection.
     *
     * @param conn the database connection to be used by this DAO
     */
    public ReviewSeriesDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieves all review records from the {@code review_series} table.
     *
     * @return a list of all {@link ReviewSeries} objects, ordered by {@code created_at} descending
     * @throws SQLException if a database access error occurs
     */
    public List<ReviewSeries> getAll() throws SQLException {
        List<ReviewSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM review_series ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToReview(rs));
            }
        }
        return list;
    }

    /**
     * Finds a review record by series ID and staff ID.
     *
     * @param seriesId the ID of the series
     * @param staffId  the ID of the staff member
     * @return the matching {@link ReviewSeries} object, or {@code null} if not found
     * @throws SQLException if a database access error occurs
     */
    public ReviewSeries findById(int seriesId, int staffId) throws SQLException {
        String sql = "SELECT * FROM review_series WHERE series_id = ? AND staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            stmt.setInt(2, staffId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all review records for a specific series.
     *
     * @param seriesId the ID of the series
     * @return a list of {@link ReviewSeries} objects associated with the given series
     * @throws SQLException if a database access error occurs
     */
    public List<ReviewSeries> findBySeries(int seriesId) throws SQLException {
        List<ReviewSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM review_series WHERE series_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReview(rs));
                }
            }
        }
        return list;
    }

    /**
     * Inserts a new review record into the {@code review_series} table.
     *
     * @param review the {@link ReviewSeries} object to insert
     * @return {@code true} if the insertion was successful, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(ReviewSeries review) throws SQLException {
        String sql = "INSERT INTO review_series (series_id, staff_id, status, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getSeriesId());
            stmt.setInt(2, review.getStaffId());
            stmt.setString(3, review.getStatus());
            stmt.setString(4, review.getComment());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing review record in the {@code review_series} table.
     *
     * @param review the {@link ReviewSeries} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean update(ReviewSeries review) throws SQLException {
        String sql = "UPDATE review_series SET status = ?, comment = ? WHERE series_id = ? AND staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getStatus());
            stmt.setString(2, review.getComment());
            stmt.setInt(3, review.getSeriesId());
            stmt.setInt(4, review.getStaffId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a review record from the {@code review_series} table.
     *
     * @param seriesId the ID of the series
     * @param staffId  the ID of the staff member
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(int seriesId, int staffId) throws SQLException {
        String sql = "DELETE FROM review_series WHERE series_id = ? AND staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            stmt.setInt(2, staffId);
            return stmt.executeUpdate() > 0;
        }
    }

    public int countByStaff(int staffId) {
        String sql = "SELECT COUNT(*) AS total FROM review_series WHERE staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStaffAndStatus(int staffId, String status) {
        String sql = "SELECT COUNT(*) AS total FROM review_series WHERE staff_id = ? AND status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Maps a {@link ResultSet} row to a {@link ReviewSeries} object.
     *
     * @param rs the {@link ResultSet} containing review data
     * @return a populated {@link ReviewSeries} object
     * @throws SQLException if a database access error occurs
     */
    private ReviewSeries mapResultSetToReview(ResultSet rs) throws SQLException {
        ReviewSeries review = new ReviewSeries();
        review.setSeriesId(rs.getInt("series_id"));
        review.setStaffId(rs.getInt("staff_id"));
        review.setStatus(rs.getString("status"));
        review.setComment(rs.getString("comment"));

        Timestamp created = rs.getTimestamp("created_at");
        review.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return review;
    }
}
