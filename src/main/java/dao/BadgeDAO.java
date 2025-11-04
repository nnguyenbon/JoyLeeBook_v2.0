package dao;

import model.Badge;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BadgeDAO {
    private final Connection conn;

    public BadgeDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Badge> getAll() throws SQLException {
        List<Badge> list = new ArrayList<>();
        String sql = "SELECT * FROM badges";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToBadges(rs));
            }
        }
        return list;
    }

    public Badge findById(int badgeId) throws SQLException {
        String sql = "SELECT * FROM badges WHERE badge_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, badgeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBadges(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(Badge badge) throws SQLException {
        String sql = "INSERT INTO badges (icon_url, name, description, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, badge.getIconUrl());
            stmt.setString(2, badge.getName());
            stmt.setString(3, badge.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(
                    badge.getCreatedAt() != null ? badge.getCreatedAt() : LocalDateTime.now()
            ));

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Badge badge) throws SQLException {
        String sql = "UPDATE badges SET icon_url = ?, name = ?, description = ?, created_at = ? WHERE badge_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, badge.getIconUrl());
            stmt.setString(2, badge.getName());
            stmt.setString(3, badge.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(badge.getCreatedAt()));
            stmt.setInt(5, badge.getBadgeId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int badgeId) throws SQLException {
        String sql = "DELETE FROM badges WHERE badge_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, badgeId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Badge mapResultSetToBadges(ResultSet rs) throws SQLException {
        Badge badge = new Badge();
        badge.setBadgeId(rs.getInt("badge_id"));
        badge.setIconUrl("img/Badges/" + rs.getString("icon_url"));
        badge.setName(rs.getString("name"));
        badge.setDescription(rs.getString("description"));

        Timestamp created = rs.getTimestamp("created_at");
        badge.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return badge;
    }
}
