package dao;

import model.Like;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Likes
 *
 * @author KToan, HaiDD-dev
 */
public class LikesDAO {
    private final Connection conn;

    public LikesDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieves all Like records from the database.
     *
     * @return A list of all Like records.
     * @throws SQLException If an SQL error occurs during the retrieval.
     */
    public List<Like> getAll() throws SQLException {
        List<Like> list = new ArrayList<>();
        String sql = "SELECT * FROM likes";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToLike(rs));
            }
        }
        return list;
    }

    /**
     * Checks if a like exists for a given user and chapter.
     *
     * @param userId    The ID of the user.
     * @param chapterId The ID of the chapter.
     * @return true if the like exists, false otherwise.
     * @throws SQLException If an SQL error occurs during the check.
     */
    public boolean exists(int userId, int chapterId) throws SQLException {
        String sql = "SELECT 1 FROM likes WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, chapterId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts a new Like record into the database.
     *
     * @param like The Like object to insert.
     * @return true if the insertion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the insertion.
     */
    public boolean insert(Like like) throws SQLException {
        String sql = "INSERT INTO likes (user_id, chapter_id, liked_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, like.getUserId());
            stmt.setInt(2, like.getChapterId());
            stmt.setTimestamp(3, Timestamp.valueOf(like.getLikedAt() != null ? like.getLikedAt() : LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a Like record from the database.
     *
     * @param userId    The ID of the user.
     * @param chapterId The ID of the chapter.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the deletion.
     */
    public boolean delete(int userId, int chapterId) throws SQLException {
        String sql = "DELETE FROM likes WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, chapterId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Additional Methods
     */
    public List<Like> findByUser(int userId) throws SQLException {
        List<Like> list = new ArrayList<>();
        String sql = "SELECT * FROM likes WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLike(rs));
                }
            }
        }
        return list;
    }

    /**
     * Counts the number of likes for a specific chapter.
     *
     * @param chapterId The ID of the chapter.
     * @return The number of likes for the chapter.
     * @throws SQLException If an SQL error occurs during the count.
     */
    public int countByChapter(int chapterId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM likes WHERE chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Checks if a specific user has liked a specific chapter.
     *
     * @param userId    The ID of the user.
     * @param chapterId The ID of the chapter.
     * @return true if the user has liked the chapter, false otherwise.
     * @throws SQLException If an SQL error occurs during the check.
     */
    public boolean isLikedByUser(int userId, int chapterId) throws SQLException {
        String sql = "SELECT 1 FROM likes WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Utility method to map ResultSet to Like object
     */
    private Like mapResultSetToLike(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setUserId(rs.getInt("user_id"));
        like.setChapterId(rs.getInt("chapter_id"));
        Timestamp ts = rs.getTimestamp("liked_at");
        like.setLikedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return like;
    }
}
