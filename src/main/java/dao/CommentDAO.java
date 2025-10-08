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

public class CommentDAO {
    private final Connection conn;

    public CommentDAO(Connection conn) {
        this.conn = conn;
    }

    // ✅ Lấy tất cả comment (trừ comment đã xóa)
    public List<Comment> getAll() throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE is_delete = 0 ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToComment(rs));
            }
        }
        return list;
    }

    // ✅ Lấy comment theo ID
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

    // ✅ Lấy tất cả comment của 1 chương
    public List<Comment> findByChapter(int chapterId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE chapter_id = ? AND is_delete = 0 ORDER BY created_at ASC";

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

    // ✅ Thêm comment mới
    public boolean insert(Comment cmt) throws SQLException {
        String sql = "INSERT INTO comments (user_id, chapter_id, content, is_delete, created_at, updated_at) " +
                "VALUES (?, ?, ?, 0, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cmt.getUserId());
            stmt.setInt(2, cmt.getChapterId());
            stmt.setString(3, cmt.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(
                    cmt.getCreatedAt() != null ? cmt.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(5, Timestamp.valueOf(
                    cmt.getUpdatedAt() != null ? cmt.getUpdatedAt() : LocalDateTime.now()));

            return stmt.executeUpdate() > 0;
        }
    }

    // ✅ Cập nhật nội dung comment
    public boolean update(Comment cmt) throws SQLException {
        String sql = "UPDATE comments SET content = ?, updated_at = ? WHERE comment_id = ? AND is_delete = 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cmt.getContent());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, cmt.getCommentId());
            return stmt.executeUpdate() > 0;
        }
    }

    // ✅ Đánh dấu xóa mềm
    public boolean softDelete(int commentId) throws SQLException {
        String sql = "UPDATE comments SET is_delete = 1, updated_at = ? WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, commentId);
            return stmt.executeUpdate() > 0;
        }
    }

    // ✅ Xóa vĩnh viễn
    public boolean hardDelete(int commentId) throws SQLException {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        }
    }

    // ✅ Map ResultSet → Comment
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment cmt = new Comment();
        cmt.setCommentId(rs.getInt("comment_id"));
        cmt.setUserId(rs.getInt("user_id"));
        cmt.setChapterId(rs.getInt("chapter_id"));
        cmt.setContent(rs.getString("content"));
        cmt.setDeleted(rs.getBoolean("is_delete"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");

        cmt.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());
        cmt.setUpdatedAt(updated != null ? updated.toLocalDateTime() : LocalDateTime.now());
        return cmt;
    }
}
