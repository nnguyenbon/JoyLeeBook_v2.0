package dao;

import dto.staff.RecentActionDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewChapterDAO {
    private final Connection conn;

    public ReviewChapterDAO(Connection conn) {
        this.conn = conn;
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

    public List<RecentActionDTO> getRecentActionsByStaff(int staffId, Timestamp startOfDay, int limit) {
        String sql = " SELECT TOP "+ limit + " rc.chapter_id, rc.status, rc.comment, rc.created_at, " +
                "c.title AS chapter_title, s.title AS series_title " +
                "FROM review_chapter rc " +
                "JOIN chapters c ON rc.chapter_id = c.chapter_id " +
                "JOIN series s ON c.series_id = s.series_id " +
                "WHERE rc.staff_id = ? AND rc.created_at >= ? " +
                "ORDER BY rc.created_at DESC";

        List<RecentActionDTO> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setTimestamp(2, startOfDay);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RecentActionDTO dto = new RecentActionDTO();
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
