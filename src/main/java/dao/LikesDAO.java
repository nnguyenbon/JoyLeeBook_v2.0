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

public class LikesDAO {
    private final Connection conn;

    public LikesDAO(Connection conn) {
        this.conn = conn;
    }

    // Lấy tất cả Like
    public List<Like> getAll() throws SQLException {
        List<Like> list = new ArrayList<>();
        String sql = "SELECT * FROM likes";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToLike(rs));
            }
        }
        return list;
    }

    // Kiểm tra xem user đã like chapter chưa
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

    // Thêm like
    public boolean insert(Like like) throws SQLException {
        String sql = "INSERT INTO likes (user_id, chapter_id, liked_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, like.getUserId());
            stmt.setInt(2, like.getChapterId());
            stmt.setTimestamp(3, Timestamp.valueOf(
                    like.getLikedAt() != null ? like.getLikedAt() : LocalDateTime.now()
            ));
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa like
    public boolean delete(int userId, int chapterId) throws SQLException {
        String sql = "DELETE FROM likes WHERE user_id = ? AND chapter_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, chapterId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy tất cả likes của 1 user
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

    // Đếm tổng số like của 1 chapter
    public int countLikesByChapter(int chapterId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM likes WHERE chapter_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    // Ánh xạ ResultSet → Likes
    private Like mapResultSetToLike(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setUserId(rs.getInt("user_id"));
        like.setChapterId(rs.getInt("chapter_id"));
        Timestamp ts = rs.getTimestamp("liked_at");
        like.setLikedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return like;
    }
}
