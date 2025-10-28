package dao;

import dao.helper.PaginationDAOHelper;
import dto.PaginationRequest;
import model.Report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private final Connection conn;

    public ReportDAO(Connection conn) {
        this.conn = conn;
    }

    private Report extractReportFromResultSet(ResultSet rs) throws SQLException {
        Report r = new Report();

        r.setReportId(rs.getInt("report_id"));
        r.setReporterId(rs.getInt("reporter_id"));

        Integer staffId = (Integer) rs.getObject("staff_id");
        r.setStaffId(staffId != null ? staffId : 0);

        r.setTargetType(rs.getString("target_type"));

        Integer commentId = (Integer) rs.getObject("comment_id");
        Integer chapterId = (Integer) rs.getObject("chapter_id");
        r.setCommentId(commentId);
        r.setChapterId(chapterId);

        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        return r;
    }


    public boolean insert(Report report) throws SQLException {
        String sql = "INSERT INTO reports (reporter_id, target_type, comment_id, chapter_id, reason, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, report.getReporterId());
            ps.setString(2, report.getTargetType());

            if ("comment".equalsIgnoreCase(report.getTargetType())) {
                ps.setInt(3, report.getCommentId());
                ps.setNull(4, java.sql.Types.INTEGER);
            } else if ("chapter".equalsIgnoreCase(report.getTargetType())) {
                ps.setNull(3, java.sql.Types.INTEGER);
                ps.setInt(4, report.getChapterId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setString(5, report.getReason());
            ps.setString(6, report.getStatus() != null ? report.getStatus() : "pending");
            ps.setTimestamp(7, Timestamp.valueOf(report.getCreatedAt() != null ? report.getCreatedAt() : LocalDateTime.now()));
            ps.setTimestamp(8, Timestamp.valueOf(report.getUpdatedAt() != null ? report.getUpdatedAt() : LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        }
    }


    public List<Report> getAllWithType(String type, PaginationRequest paginationRequest) throws SQLException {
        List<Report> list = new ArrayList<>();
        PaginationDAOHelper paginationDAOHelper = new PaginationDAOHelper(paginationRequest);
        String sql = "SELECT * FROM reports WHERE target_type = ?" + paginationDAOHelper.buildPaginationClause();
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, type);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReportFromResultSet(rs));
                }
            }
        }
        return list;
    }

    public Report getById(int reportId) throws SQLException {
        String sql = "SELECT * FROM reports WHERE report_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractReportFromResultSet(rs);
            }
        }
        return null;
    }

    public int countReports(String type) throws SQLException {
        String sql = "SELECT count(*) FROM reports WHERE target_type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) AS total FROM reports";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStatus(String pending) {
        String sql = "SELECT COUNT(*) AS total FROM series WHERE status='pending'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countHandledByStaff(int staffId, String resolved) {
        String sql = "SELECT COUNT(*) AS total FROM reports WHERE staff_id=? AND status=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, resolved);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countResolvedByStaffAndDate(int staffId, Timestamp startOfDay) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM review_chapter
            WHERE staff_id = ?
              AND status IN ('approved', 'rejected', 'resolved')
              AND created_at >= ?
            """;

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

//    public boolean update(Report report) throws SQLException {
//        String sql = "UPDATE reports SET staff_id, target_id=?, target_type=?, reason=?, status=?, updated_at=? WHERE report_id=?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            if (report.getStaffId() != 0)
//                ps.setInt(1, report.getStaffId());
//            ps.setInt(2, report.getTargetId());
//            ps.setString(3, report.getTargetType());
//            ps.setString(4, report.getReason());
//            ps.setString(5, report.getStatus());
//            ps.setTimestamp(6, Timestamp.valueOf(report.getUpdatedAt()));
//            ps.setInt(7, report.getReportId());
//            return ps.executeUpdate() > 0;
//        }
//    }
//
//    public boolean delete(int reportId) throws SQLException {
//        String sql = "DELETE FROM reports WHERE report_id = ?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, reportId);
//            return ps.executeUpdate() > 0;
//        }
//    }
}
