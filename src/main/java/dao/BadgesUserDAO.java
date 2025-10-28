package dao;

import model.Badge;
import model.BadgesUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BadgesUserDAO {
    private final Connection conn;

    public BadgesUserDAO(Connection conn) {
        this.conn = conn;
    }

    // Lấy toàn bộ bản ghi
    public List<BadgesUser> getAll() throws SQLException {
        List<BadgesUser> list = new ArrayList<>();
        String sql = "SELECT * FROM badges_users";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToBadgesUsers(rs));
            }
        }
        return list;
    }

    // Tìm theo khóa chính
    public BadgesUser findById(int badgeId, int userId) throws SQLException {
        String sql = "SELECT * FROM badges_users WHERE badge_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, badgeId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBadgesUsers(rs);
                }
            }
        }
        return null;
    }

    // Thêm mới
    public boolean insert(BadgesUser bu) throws SQLException {
        String sql = "INSERT INTO badges_users (badge_id, user_id, awarded_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bu.getBadgeId());
            stmt.setInt(2, bu.getUserId());
            stmt.setTimestamp(3, Timestamp.valueOf(
                    bu.getAwardedAt() != null ? bu.getAwardedAt() : LocalDateTime.now()
            ));
            return stmt.executeUpdate() > 0;
        }
    }

    // Cập nhật (ví dụ cập nhật thời điểm trao huy hiệu)
    public boolean update(BadgesUser bu) throws SQLException {
        String sql = "UPDATE badges_users SET awarded_at = ? WHERE badge_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(bu.getAwardedAt()));
            stmt.setInt(2, bu.getBadgeId());
            stmt.setInt(3, bu.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa một dòng (gỡ huy hiệu khỏi user)
    public boolean delete(int badgeId, int userId) throws SQLException {
        String sql = "DELETE FROM badges_users WHERE badge_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, badgeId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Badge> getBadgesByUserId(int userId) throws SQLException {
        List<Badge> badgeList = new ArrayList<>();
        String sql = "SELECT * FROM badges_users bu JOIN badges b ON bu.badge_id = b.badge_id WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Badge badge = new Badge();
                    badge.setBadgeId(rs.getInt("badge_id"));
                    badge.setDescription(rs.getString("description"));
                    badge.setName(rs.getString("name"));
                    badge.setIconUrl("img/" + rs.getString("icon_url"));
                    badgeList.add(badge);
                }
                return  badgeList;
            }
        }
    }

    // Hàm ánh xạ từ ResultSet sang đối tượng
    private BadgesUser mapResultSetToBadgesUsers(ResultSet rs) throws SQLException {
        BadgesUser bu = new BadgesUser();
        bu.setBadgeId(rs.getInt("badge_id"));
        bu.setUserId(rs.getInt("user_id"));

        Timestamp awarded = rs.getTimestamp("awarded_at");
        bu.setAwardedAt(awarded != null ? awarded.toLocalDateTime() : LocalDateTime.now());

        return bu;
    }
}
