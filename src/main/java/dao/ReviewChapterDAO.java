package dao;

import model.staff.RecentAction;
import model.ReviewChapter;
import model.ReviewSeries;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ReviewChapterDAO {
    private final Connection conn;

    /**
     * Constructs a ReviewSeriesDAO with an active database connection.
     *
     * @param conn the database connection to be used by this DAO
     */
    public ReviewChapterDAO(Connection conn) {
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
                list.add(mapResultSetToReviewSeries(rs));
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
                    return mapResultSetToReviewSeries(rs);
                }
            }
        }
        return null;
    }


    public ReviewChapter findById(int chapterId) throws SQLException {
        String sql = "SELECT * FROM review_chapter WHERE chapter_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReviewChapter(rs);
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
                    list.add(mapResultSetToReviewSeries(rs));
                }
            }
        }
        return list;
    }


    public boolean insert(ReviewChapter review) throws SQLException {
        String sql = "INSERT INTO review_chapter (chapter_id, staff_id, status, comment, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getChapterId());
            stmt.setInt(2, review.getStaffId());
            stmt.setString(3, review.getStatus());
            stmt.setString(4, review.getComment());
            stmt.setTimestamp(5, Timestamp.valueOf(
                    review.getCreatedAt() != null ? review.getCreatedAt() : LocalDateTime.now()
            ));

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
     * Updates an existing review record in the {@code review_series} table.
     *
     * @param review the {@link ReviewSeries} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean update(ReviewChapter review) throws SQLException {
        String sql = "UPDATE review_chapter SET status = ?, comment = ? WHERE chapter_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getStatus());
            stmt.setString(2, review.getComment());
            stmt.setInt(3, review.getChapterId());
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

    /**
     * Maps a {@link ResultSet} row to a {@link ReviewSeries} object.
     *
     * @param rs the {@link ResultSet} containing review data
     * @return a populated {@link ReviewSeries} object
     * @throws SQLException if a database access error occurs
     */
    private ReviewSeries mapResultSetToReviewSeries(ResultSet rs) throws SQLException {
        ReviewSeries review = new ReviewSeries();
        review.setSeriesId(rs.getInt("series_id"));
        review.setStaffId(rs.getInt("staff_id"));
        review.setStatus(rs.getString("status"));
        review.setComment(rs.getString("comment"));

        Timestamp created = rs.getTimestamp("created_at");
        review.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return review;
    }

    private ReviewChapter mapResultSetToReviewChapter(ResultSet rs) throws SQLException {
        ReviewChapter review = new ReviewChapter();
        review.setChapterId(rs.getInt("chapter_id"));
        review.setStaffId(rs.getInt("staff_id"));
        review.setStatus(rs.getString("status"));
        review.setComment(rs.getString("comment"));

        Timestamp created = rs.getTimestamp("created_at");
        review.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return review;
    }
    public int countByStaff(int staffId) {
        String sql = "SELECT COUNT(*) AS total FROM review_chapter WHERE staff_id = ?";
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
        String sql = "SELECT COUNT(*) AS total FROM review_chapter WHERE staff_id = ? AND status = ?";
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

    public List<RecentAction> getRecentActionsByStaff(int staffId, Timestamp startOfDay, int limit) {
        String sql = " SELECT TOP " + limit + " rc.chapter_id, rc.status, rc.comment, rc.created_at, " +
                "c.title AS chapter_title, s.title AS series_title " +
                "FROM review_chapter rc " +
                "JOIN chapters c ON rc.chapter_id = c.chapter_id " +
                "JOIN series s ON c.series_id = s.series_id " +
                "WHERE rc.staff_id = ? AND rc.created_at >= ? " +
                "ORDER BY rc.created_at DESC";

        List<RecentAction> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setTimestamp(2, startOfDay);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RecentAction dto = new RecentAction();
                String chapterTitle = rs.getString("chapter_title");
                String seriesTitle = rs.getString("series_title");
                String status = rs.getString("status");
                Timestamp createdAt = rs.getTimestamp("created_at");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int countByStaffAndDate(int staffId, Timestamp startOfDay) {
        String sql = "SELECT COUNT(*) AS total FROM review_chapter WHERE staff_id = ? AND created_at >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setTimestamp(2, startOfDay);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStaffStatusAndDate(int staffId, String status, Timestamp startOfDay) {
        String sql = "SELECT COUNT(*) AS total FROM review_chapter WHERE staff_id = ? AND status = ? AND created_at >= ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, status);
            stmt.setTimestamp(3, startOfDay);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
