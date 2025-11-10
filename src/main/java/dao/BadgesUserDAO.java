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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String sql = "SELECT * FROM badges_users bu RIGHT JOIN badges b ON bu.badge_id = b.badge_id AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Badge badge = new Badge();
                    badge.setBadgeId(rs.getInt("badge_id"));
                    badge.setDescription(rs.getString("description"));
                    badge.setName(rs.getString("name"));
                    badge.setIconUrl("img/Badges/" + rs.getString("icon_url"));
                    badge.setUnlocked(rs.getInt("user_id") != 0);
                    badgeList.add(badge);
                }
                return badgeList;
            }
        }
    }

    public List<String> checkAndSaveBadges(int userId) throws Exception {
        List<String> newlyUnlocked = new ArrayList<>();

        try {
            conn.setAutoCommit(false);

            // check author ở phần servlet register author
            // check series completed ở servlet
            int comments = scalarInt(conn,
                    "SELECT COUNT(*) FROM comments WHERE user_id=? AND (is_deleted=0 OR is_deleted IS NULL)",
                    userId);

            int likesReceived = scalarInt(conn,
                    "SELECT COUNT(*) FROM likes where likes.user_id = ?",
                    userId);

            long points = scalarLong(conn,
                    "SELECT COALESCE(points,0) FROM users WHERE user_id=?",
                    userId);

            maybeUnlock(conn, userId, newlyUnlocked, "Post 100 comments", comments >= 100);
            maybeUnlock(conn, userId, newlyUnlocked, "Got 100 Likes", likesReceived >= 100);
            maybeUnlock(conn, userId, newlyUnlocked, "Got 1000 points", points >= 1000);
            maybeUnlock(conn, userId, newlyUnlocked, "Got 1500 Points", points >= 1500);
            maybeUnlock(conn, userId, newlyUnlocked, "Got 3000 Points", points >= 3000);
            maybeUnlock(conn, userId, newlyUnlocked, "Got 9999 Points", points >= 9999);

            conn.commit();
        } catch (Exception e) {
            throw e;
        }

        return newlyUnlocked;
    }

    private void maybeUnlock(Connection c, int userId, List<String> out, String name, boolean condition) throws SQLException {
        if (!condition) return;

        final String sql =
                "INSERT INTO badges_users(user_id, badge_id) " +
                "SELECT ?, b.badge_id " +
                "FROM badges b " +
                "WHERE b.name = ? " +
                "  AND NOT EXISTS (" +
                "        SELECT 1 FROM badges_users bu " +
                "        WHERE bu.user_id = ? AND bu.badge_id = b.badge_id" +
                "      )";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setInt(3, userId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                out.add(name);
            }
        }
    }

    private static int scalarInt(Connection c, String sql, int param) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private static long scalarLong(Connection c, String sql, int param) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
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
