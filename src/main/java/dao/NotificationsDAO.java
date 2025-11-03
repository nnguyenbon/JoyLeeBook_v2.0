package dao;

import model.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {
    private final Connection conn;

    public NotificationsDAO(Connection conn) {
        this.conn = conn;
    }

    // Lấy toàn bộ thông báo
    public List<Notification> getAll() throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToNotification(rs));
            }
        }
        return list;
    }

    // Tìm thông báo theo ID
    public Notification findById(int id) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                }
            }
        }
        return null;
    }

    // Lấy tất cả thông báo của một user
    public List<Notification> findByUserId(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNotification(rs));
                }
            }
        }
        return list;
    }

    // Thêm thông báo mới
    public boolean insertNotification(Notification noti) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, type, title, message, is_read, url_redirect, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noti.getUserId());
            stmt.setString(2, noti.getType());
            stmt.setString(3, noti.getTitle());
            stmt.setString(4, noti.getMessage());
            stmt.setBoolean(5, false);
            stmt.setString(6, noti.getUrlRedirect());
            stmt.setTimestamp(7, Timestamp.valueOf(
                    noti.getCreatedAt() != null ? noti.getCreatedAt() : LocalDateTime.now()
            ));
            return stmt.executeUpdate() > 0;
        }
    }

    // Cập nhật nội dung thông báo (hoặc trạng thái đã đọc)
    public boolean update(Notification noti) throws SQLException {
        String sql = "UPDATE notifications SET " +
                "user_id = ?, type = ?, title = ?, message = ?, is_read = ?, url_redirect = ?, created_at = ? " +
                "WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noti.getUserId());
            stmt.setString(2, noti.getType());
            stmt.setString(3, noti.getTitle());
            stmt.setString(4, noti.getMessage());
            stmt.setBoolean(5, noti.isRead());
            stmt.setString(6, noti.getUrlRedirect());
            stmt.setTimestamp(7, Timestamp.valueOf(noti.getCreatedAt()));
            stmt.setInt(8, noti.getNotificationId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Đánh dấu đã đọc
    public boolean markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa thông báo
    public boolean delete(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Hàm ánh xạ dữ liệu từ ResultSet
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("notification_id"));
        n.setUserId(rs.getInt("user_id"));
        n.setType(rs.getString("type"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setUrlRedirect(rs.getString("url_redirect"));

        Timestamp created = rs.getTimestamp("created_at");
        n.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return n;
    }
}
