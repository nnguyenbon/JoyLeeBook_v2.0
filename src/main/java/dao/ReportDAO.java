package dao;

import dao.helper.PaginationDAOHelper;
import model.PaginationRequest;
import model.Report;
import model.ReportChapter;
import model.ReportComment;
import model.staff.WeeklyReportStats;
import utils.FormatUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private final Connection conn;

    public ReportDAO(Connection conn) {
        this.conn = conn;
    }

    // ============= HELPER METHODS =============

    private Report extractReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("report_id"));
        report.setReporterId(rs.getInt("reporter_id"));
        report.setStaffId((Integer) rs.getObject("staff_id"));
        report.setTargetType(rs.getString("target_type"));
        report.setCommentId((Integer) rs.getObject("comment_id"));
        report.setChapterId((Integer) rs.getObject("chapter_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(rs.getString("status"));
        report.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        report.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));
        return report;
    }

    private ReportChapter extractReportChapterFromResultSet(ResultSet rs) throws SQLException {
        ReportChapter report = new ReportChapter();
        report.setReportId(rs.getInt("report_id"));
        report.setReporterId(rs.getInt("reporter_id"));
        report.setStaffId((Integer) rs.getObject("staff_id"));
        report.setChapterId(rs.getInt("chapter_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(rs.getString("status"));

        report.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        report.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));
        // Extended fields
        report.setChapterTitle(rs.getString("chapter_title"));
        report.setChapterNumber(rs.getInt("chapter_number"));
        report.setSeriesName(rs.getString("series_name"));
        report.setReporterUsername(rs.getString("reporter_username"));
        report.setStaffUsername(rs.getString("staff_username"));

        return report;
    }

    private ReportComment extractReportCommentFromResultSet(ResultSet rs) throws SQLException {
        ReportComment report = new ReportComment();
        report.setReportId(rs.getInt("report_id"));
        report.setReporterId(rs.getInt("reporter_id"));
        report.setStaffId((Integer) rs.getObject("staff_id"));
        report.setCommentId(rs.getInt("comment_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(rs.getString("status"));

        report.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        report.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));

        // Extended fields
        report.setCommentContent(rs.getString("comment_content"));
        report.setCommenterUsername(rs.getString("commenter_username"));
        report.setChapterTitle(rs.getString("chapter_title"));
        report.setReporterUsername(rs.getString("reporter_username"));
        report.setStaffUsername(rs.getString("staff_username"));

        return report;
    }

    // ============= CRUD OPERATIONS =============

    public boolean insert(Report report) throws SQLException {
        String sql = "INSERT INTO reports (reporter_id, target_type, comment_id, chapter_id, reason, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, report.getReporterId());
            ps.setString(2, report.getTargetType());

            if ("comment".equalsIgnoreCase(report.getTargetType())) {
                ps.setInt(3, report.getCommentId());
                ps.setNull(4, Types.INTEGER);
            } else if ("chapter".equalsIgnoreCase(report.getTargetType())) {
                ps.setNull(3, Types.INTEGER);
                ps.setInt(4, report.getChapterId());
            } else {
                ps.setNull(3, Types.INTEGER);
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, report.getReason());
            ps.setString(6, report.getStatus() != null ? report.getStatus() : "pending");
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        }
    }

    public Report findById(int reportId) throws SQLException {
        String sql = "SELECT * FROM reports WHERE report_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractReportFromResultSet(rs);
            }
        }
        return null;
    }

    public boolean updateStatus(int reportId, String status, Integer staffId) throws SQLException {
        String sql = "UPDATE reports SET status = ?, staff_id = ?, updated_at = ? WHERE report_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            if (staffId != null) {
                ps.setInt(2, staffId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    // ============= GET LISTS WITH FILTERS =============

    public List<Report> getAllWithType(String type, PaginationRequest paginationRequest) throws SQLException {
        List<Report> list = new ArrayList<>();
        PaginationDAOHelper helper = new PaginationDAOHelper(paginationRequest);
        String sql = "SELECT * FROM reports WHERE target_type = ? " + helper.buildPaginationClause();

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

    public List<Report> getAllByTypeAndStatus(String type, String status, PaginationRequest paginationRequest) throws SQLException {
        List<Report> list = new ArrayList<>();
        PaginationDAOHelper helper = new PaginationDAOHelper(paginationRequest);
        String sql = "SELECT * FROM reports WHERE target_type = ? AND status = ? " + helper.buildPaginationClause();

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, type);
            st.setString(2, status);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReportFromResultSet(rs));
                }
            }
        }
        return list;
    }

    public List<Report> getAll() throws SQLException {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM reports";
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()){
            while (rs.next()) {
                list.add(extractReportFromResultSet(rs));
            }
            return list;
        }
    }
    // ============= GET DETAILED REPORT LISTS =============

    public List<ReportChapter> getReportChapterList() throws SQLException {
        return getReportChapterList(null, null);
    }

    public List<ReportChapter> getReportChapterList(String statusFilter, PaginationRequest pagination) throws SQLException {
        List<ReportChapter> reports = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.report_id, r.reporter_id, r.staff_id, r.chapter_id, " +
                        "r.reason, r.status, r.created_at, r.updated_at, " +
                        "c.title AS chapter_title, c.chapter_number, " +
                        "s.title AS series_name, " +
                        "u.username AS reporter_username, " +
                        "st.username AS staff_username " +
                        "FROM reports r " +
                        "INNER JOIN chapters c ON r.chapter_id = c.chapter_id " +
                        "INNER JOIN series s ON c.series_id = s.series_id " +
                        "INNER JOIN users u ON r.reporter_id = u.user_id " +
                        "LEFT JOIN staffs st ON r.staff_id = st.staff_id " +
                        "WHERE r.target_type = 'chapter'"
        );

        // Add status filter if provided
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sql.append(" AND r.status = ? ");
        }

        PaginationDAOHelper helper = new PaginationDAOHelper(pagination);
        sql.append(helper.buildPaginationClause());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                stmt.setString(1, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(extractReportChapterFromResultSet(rs));
                }
            }
        }
        return reports;
    }

    public List<ReportComment> getReportCommentList() throws SQLException {
        return getReportCommentList(null, null);
    }

    public List<ReportComment> getReportCommentList(String statusFilter, PaginationRequest pagination) throws SQLException {
        List<ReportComment> reports = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.report_id, r.reporter_id, r.staff_id, r.comment_id, " +
                        "r.reason, r.status, r.created_at, r.updated_at, " +
                        "cm.content AS comment_content, " +
                        "cu.username AS commenter_username, " +
                        "c.title AS chapter_title, " +
                        "u.username AS reporter_username, " +
                        "st.username AS staff_username " +
                        "FROM reports r " +
                        "INNER JOIN comments cm ON r.comment_id = cm.comment_id " +
                        "INNER JOIN users cu ON cm.user_id = cu.user_id " +
                        "INNER JOIN chapters c ON cm.chapter_id = c.chapter_id " +
                        "INNER JOIN users u ON r.reporter_id = u.user_id " +
                        "LEFT JOIN staffs st ON r.staff_id = st.staff_id " +
                        "WHERE r.target_type = 'comment'"
        );

        // Add status filter if provided
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sql.append(" AND r.status = ? ");
        }

        PaginationDAOHelper helper = new PaginationDAOHelper(pagination);
        sql.append(helper.buildPaginationClause());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                stmt.setString(1, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(extractReportCommentFromResultSet(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Get chapter report detail by ID
     */
    public ReportChapter getReportChapterById(int reportId) throws SQLException {
        String sql = "SELECT r.report_id, r.reporter_id, r.staff_id, r.chapter_id, " +
                "r.reason, r.status, r.created_at, r.updated_at, " +
                "c.title AS chapter_title, c.chapter_number, c.content AS chapter_content, " +
                "s.title AS series_name, s.series_id, " +
                "u.username AS reporter_username, u.email AS reporter_email, " +
                "st.username AS staff_username " +
                "FROM reports r " +
                "INNER JOIN chapters c ON r.chapter_id = c.chapter_id " +
                "INNER JOIN series s ON c.series_id = s.series_id " +
                "INNER JOIN users u ON r.reporter_id = u.user_id " +
                "LEFT JOIN staffs st ON r.staff_id = st.staff_id " +
                "WHERE r.report_id = ? AND r.target_type = 'chapter'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractReportChapterFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get comment report detail by ID
     */
    public ReportComment getReportCommentById(int reportId) throws SQLException {
        String sql = "SELECT r.report_id, r.reporter_id, r.staff_id, r.comment_id, " +
                "r.reason, r.status, r.created_at, r.updated_at, " +
                "cm.content AS comment_content, cm.created_at AS comment_created_at, " +
                "cu.username AS commenter_username, " +
                "c.title AS chapter_title, c.chapter_id, " +
                "s.title AS series_name, s.series_id, " +
                "u.username AS reporter_username, u.email AS reporter_email, " +
                "st.username AS staff_username " +
                "FROM reports r " +
                "INNER JOIN comments cm ON r.comment_id = cm.comment_id " +
                "INNER JOIN users cu ON cm.user_id = cu.user_id " +
                "INNER JOIN chapters c ON cm.chapter_id = c.chapter_id " +
                "INNER JOIN series s ON c.series_id = s.series_id " +
                "INNER JOIN users u ON r.reporter_id = u.user_id " +
                "LEFT JOIN staffs st ON r.staff_id = st.staff_id " +
                "WHERE r.report_id = ? AND r.target_type = 'comment'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractReportCommentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public WeeklyReportStats getWeeklyReportStats(Timestamp startOfWeek) throws SQLException {
        WeeklyReportStats stats = new WeeklyReportStats();
        List<Integer> dailyCounts = new ArrayList<>();

        // Get this week's total
        String thisWeekSQL = "SELECT COUNT(*) FROM reports WHERE created_at >= ?";
        try (PreparedStatement ps = conn.prepareStatement(thisWeekSQL)) {
            ps.setTimestamp(1, startOfWeek);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setThisWeekTotal(rs.getInt(1));
                }
            }
        }

        // Get last week's total for growth rate calculation
        LocalDate weekAgoDate = startOfWeek.toLocalDateTime().toLocalDate();
        LocalDate twoWeeksAgoDate = weekAgoDate.minusDays(7);
        Timestamp startOfLastWeek = Timestamp.valueOf(twoWeeksAgoDate.atStartOfDay());

        String lastWeekSQL = "SELECT COUNT(*) FROM reports WHERE created_at >= ? AND created_at < ?";
        int lastWeekTotal = 0;
        try (PreparedStatement ps = conn.prepareStatement(lastWeekSQL)) {
            ps.setTimestamp(1, startOfLastWeek);
            ps.setTimestamp(2, startOfWeek);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lastWeekTotal = rs.getInt(1);
                }
            }
        }

        // Calculate growth rate
        if (lastWeekTotal > 0) {
            double growthRate = ((stats.getThisWeekTotal() - lastWeekTotal) * 100.0) / lastWeekTotal;
            stats.setGrowthRate(growthRate);
        } else {
            stats.setGrowthRate(stats.getThisWeekTotal() > 0 ? 100.0 : 0.0);
        }

        // Get daily counts for the past 7 days
        String dailySQL = "SELECT DATEPART(WEEKDAY, created_at) as day_of_week, COUNT(*) as count " +
                "FROM reports " +
                "WHERE created_at >= ? " +
                "GROUP BY DATEPART(WEEKDAY, created_at) " +
                "ORDER BY DATEPART(WEEKDAY, created_at)";

        // Initialize all days to 0
        for (int i = 0; i < 7; i++) {
            dailyCounts.add(0);
        }

        try (PreparedStatement ps = conn.prepareStatement(dailySQL)) {
            ps.setTimestamp(1, startOfWeek);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dayOfWeek = rs.getInt("day_of_week") - 2; // Adjust to Monday = 0
                    if (dayOfWeek < 0) dayOfWeek = 6; // Sunday
                    dailyCounts.set(dayOfWeek, rs.getInt("count"));
                }
            }
        }

        stats.setDailyCounts(dailyCounts);
        return stats;
    }
    // ============= COUNT METHODS =============

    public int countReports(String type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reports WHERE target_type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM reports";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM reports WHERE status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public int countHandledByStaff(int staffId, String status) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM reports WHERE staff_id = ? AND status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public int countResolvedByStaffAndDate(int staffId, Timestamp startOfDay) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM reports " +
                "WHERE staff_id = ? AND status IN ('approved', 'rejected', 'resolved') " +
                "AND updated_at >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setTimestamp(2, startOfDay);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public boolean deleteReport(int reportId) {
        return true;
    }
}