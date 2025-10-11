package dao;

import model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Comments
 *
 * @author KToan, HaiDD-dev
 */
public class CommentDAO {
    private final Connection conn;

    public CommentDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieves all Comment records from the database that are not marked as deleted.
     *
     * @return A list of all non-deleted Comment records.
     * @throws SQLException If an SQL error occurs during the retrieval.
     */
    public List<Comment> getAll() throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE is_delete = 0 ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToComment(rs));
            }
        }
        return list;
    }

    /**
     * Find comment by its ID
     *
     * @param id The ID of the comment to find
     * @return The Comment object if found, otherwise null
     * @throws SQLException If an SQL error occurs during the retrieval
     */
    public Comment findById(int id) throws SQLException {
        String sql = "SELECT * FROM comments WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find comments by chapter ID
     *
     * @param chapterId The ID of the chapter to find comments for
     * @return A list of Comment objects associated with the specified chapter
     * @throws SQLException If an SQL error occurs during the retrieval
     */
    public List<Comment> findByChapter(int chapterId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE chapter_id = ? AND is_deleted = 0 ORDER BY created_at ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Insert a new comment into the database
     *
     * @param cmt The Comment object to insert
     * @return true if the insertion was successful, false otherwise
     * @throws SQLException If an SQL error occurs during the insertion
     */
    public boolean insert(Comment cmt) throws SQLException {
        String sql = "INSERT INTO comments (user_id, chapter_id, content, is_delete, created_at, updated_at) " + "VALUES (?, ?, ?, 0, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cmt.getUserId());
            stmt.setInt(2, cmt.getChapterId());
            stmt.setString(3, cmt.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(cmt.getCreatedAt() != null ? cmt.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(5, Timestamp.valueOf(cmt.getUpdatedAt() != null ? cmt.getUpdatedAt() : LocalDateTime.now()));

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Update an existing comment in the database
     *
     * @param cmt The Comment object with updated information
     * @return true if the update was successful, false otherwise
     * @throws SQLException If an SQL error occurs during the update
     */
    public boolean update(Comment cmt) throws SQLException {
        String sql = "UPDATE comments SET content = ?, updated_at = ? WHERE comment_id = ? AND is_delete = 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cmt.getContent());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, cmt.getCommentId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Soft delete a comment by setting its is_delete flag to true
     *
     * @param commentId The ID of the comment to soft delete
     * @return true if the soft delete was successful, false otherwise
     * @throws SQLException If an SQL error occurs during the soft delete
     */
    public boolean softDelete(int commentId) throws SQLException {
        String sql = "UPDATE comments SET is_delete = 1, updated_at = ? WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, commentId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Hard delete a comment from the database
     *
     * @param commentId The ID of the comment to hard delete
     * @return true if the hard delete was successful, false otherwise
     * @throws SQLException If an SQL error occurs during the hard delete
     */
    public boolean hardDelete(int commentId) throws SQLException {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Count the number of comments for a specific chapter that are not marked as deleted
     *
     * @param chapterId The ID of the chapter to count comments for
     * @return The number of non-deleted comments for the specified chapter
     * @throws SQLException If an SQL error occurs during the count
     */
    public int countByChapter(int chapterId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM comments WHERE chapter_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Maps a ResultSet row to a Comment object
     *
     * @param rs The ResultSet to map
     * @return The mapped Comment object
     * @throws SQLException If an SQL error occurs during the mapping
     */
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment cmt = new Comment();
        cmt.setCommentId(rs.getInt("comment_id"));
        cmt.setUserId(rs.getInt("user_id"));
        cmt.setChapterId(rs.getInt("chapter_id"));
        cmt.setContent(rs.getString("content"));
        cmt.setDeleted(rs.getBoolean("is_deleted"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");

        cmt.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());
        cmt.setUpdatedAt(updated != null ? updated.toLocalDateTime() : LocalDateTime.now());
        return cmt;
    }
}
