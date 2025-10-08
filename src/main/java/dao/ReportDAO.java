package dao;

import model.Report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        int staffId = rs.getInt("staff_id");
        r.setStaffId(rs.wasNull() ? null : staffId);
        r.setType(rs.getString("type"));
        r.setReportTypeId(rs.getInt("report_type_id"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        return r;
    }

    public boolean insert(Report report) throws SQLException {
        String sql = "INSERT INTO reports (reporter_id, staff_id, type, report_type_id, reason, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, report.getReporterId());
            if (report.getStaffId() != 0)
                ps.setInt(2, report.getStaffId());
            ps.setString(3, report.getType());
            ps.setInt(4, report.getReportTypeId());
            ps.setString(5, report.getReason());
            ps.setString(6, report.getStatus());
            ps.setTimestamp(7, Timestamp.valueOf(report.getCreatedAt()));
            ps.setTimestamp(8, report.getUpdatedAt() != null ? Timestamp.valueOf(report.getUpdatedAt()) : null);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Report> getAll() throws SQLException {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM reports";
        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractReportFromResultSet(rs));
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

    public boolean update(Report report) throws SQLException {
        String sql = "UPDATE reports SET staff_id=?, type=?, report_type_id=?, reason=?, status=?, updated_at=? WHERE report_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (report.getStaffId() != 0)
                ps.setInt(1, report.getStaffId());
            ps.setString(2, report.getType());
            ps.setInt(3, report.getReportTypeId());
            ps.setString(4, report.getReason());
            ps.setString(5, report.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(report.getUpdatedAt()));
            ps.setInt(7, report.getReportId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int reportId) throws SQLException {
        String sql = "DELETE FROM reports WHERE report_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;
        }
    }
}
