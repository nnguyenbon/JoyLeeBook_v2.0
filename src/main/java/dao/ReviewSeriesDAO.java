package dao;

import model.ReviewSeries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewSeriesDAO {
    private final Connection conn;

    public ReviewSeriesDAO(Connection conn) {
        this.conn = conn;
    }

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

    public ReviewSeries findById(int chapterId, int staffId) throws SQLException {
        String sql = "SELECT * FROM review_series WHERE chapter_id = ? AND staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            stmt.setInt(2, staffId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }
        return null;
    }

    public List<ReviewSeries> findByChapter(int chapterId) throws SQLException {
        List<ReviewSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM review_series WHERE chapter_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReview(rs));
                }
            }
        }
        return list;
    }

    public boolean insert(ReviewSeries review) throws SQLException {
        String sql = "INSERT INTO review_series (chapter_id, staff_id, status, comment, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getChapterId());
            stmt.setInt(2, review.getStaffId());
            stmt.setString(3, review.getStatus());
            stmt.setString(4, review.getComment());
            stmt.setTimestamp(5, Timestamp.valueOf(review.getCreatedAt() != null ? review.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(6, Timestamp.valueOf(review.getUpdatedAt() != null ? review.getUpdatedAt() : LocalDateTime.now()));

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(ReviewSeries review) throws SQLException {
        String sql = "UPDATE review_series SET status = ?, comment = ?, updated_at = ? WHERE chapter_id = ? AND staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getStatus());
            stmt.setString(2, review.getComment());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, review.getChapterId());
            stmt.setInt(5, review.getStaffId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int chapterId, int staffId) throws SQLException {
        String sql = "DELETE FROM review_series WHERE chapter_id = ? AND staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            stmt.setInt(2, staffId);
            return stmt.executeUpdate() > 0;
        }
    }

    private ReviewSeries mapResultSetToReview(ResultSet rs) throws SQLException {
        ReviewSeries review = new ReviewSeries();
        review.setChapterId(rs.getInt("chapter_id"));
        review.setStaffId(rs.getInt("staff_id"));
        review.setStatus(rs.getString("status"));
        review.setComment(rs.getString("comment"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        review.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());
        review.setUpdatedAt(updated != null ? updated.toLocalDateTime() : LocalDateTime.now());

        return review;
    }
}
