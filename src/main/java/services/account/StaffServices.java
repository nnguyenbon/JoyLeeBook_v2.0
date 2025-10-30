package services.account;

import dao.*;
import db.DBConnection;
import dto.staff.DashboardStatsDTO;
import dto.staff.QuickStatsDTO;
import dto.staff.RecentActionDTO;
import model.Staff;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class StaffServices {

    private StaffDAO staffDAO;

    private SeriesDAO seriesDAO;
 
    private UserDAO userDAO;
 
    private ChapterDAO chapterDAO;
 
    private ReportDAO reportDAO;
 
    private ReviewChapterDAO reviewChapterDAO;

    public StaffServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.staffDAO = new StaffDAO(connection);
        this.seriesDAO = new SeriesDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.reportDAO = new ReportDAO(connection);
        this.reviewChapterDAO = new ReviewChapterDAO(connection);
    }

    // Lấy stats chính cho dashboard
    public DashboardStatsDTO getDashboardStats(int staffId) {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Total Series: COUNT(series) WHERE is_deleted = 0
        stats.setTotalSeries(seriesDAO.countAllNonDeleted());

        // Active Users: COUNT(users) WHERE status = 'active' AND role IN ('reader', 'author')
        stats.setActiveUsers(userDAO.countActiveUsers());

        // Authors: COUNT(users) WHERE role = 'author' AND status = 'active'
        stats.setAuthors(userDAO.countActiveAuthors());

        // Banned Users: COUNT(users) WHERE status = 'banned'
        stats.setBannedUsers(userDAO.countBannedUsers());

        // Total Reports: COUNT(reports) WHERE is_deleted IS NULL (tất cả)
        stats.setTotalReports(reportDAO.countAll());

        // Pending Reports: COUNT(reports) WHERE status = 'pending'
        stats.setPendingReports(reportDAO.countByStatus("pending"));

        // Reports You’ve Handled: COUNT(reports) WHERE staff_id = ? AND status = 'resolved'
        stats.setHandledReports(reportDAO.countHandledByStaff(staffId, "resolved"));

        // Total Chapters: COUNT(chapters) WHERE is_deleted = 0
        stats.setTotalChapters(chapterDAO.countAllNonDeleted());

        // Your Reviews: COUNT(review_chapter) WHERE staff_id = ?
        stats.setYourReviews(reviewChapterDAO.countByStaff(staffId));

        // Pending Chapters: COUNT(chapters) WHERE status = 'pending'
        stats.setPendingChapters(chapterDAO.countByStatus("pending"));

        // Your Rejects: COUNT(review_chapter) WHERE staff_id = ? AND status = 'rejected'
        stats.setYourRejects(reviewChapterDAO.countByStaffAndStatus(staffId, "rejected"));

        return stats;
    }

    // Lấy recent actions (kết hợp review_chapter và reports, limit 4)
    public List<RecentActionDTO> getRecentActions(int staffId, int limit) {
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime());

        // Query JOIN review_chapter và reports WHERE staff_id = ? AND created_at >= startOfDay ORDER BY created_at DESC LIMIT ?
        // Map sang RecentActionDTO, format timestamp thành "X minutes/hours ago"
        return reviewChapterDAO.getRecentActionsByStaff(staffId, startOfDay, limit);
        // Hoặc kết hợp với reportDAO nếu cần.
    }

    // Lấy quick stats hôm nay
    public QuickStatsDTO getQuickStatsToday(int staffId) {
        QuickStatsDTO stats = new QuickStatsDTO();
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime());

        // Reviews completed: COUNT(review_chapter) WHERE staff_id = ? AND created_at >= ?
        stats.setReviewsCompleted(reviewChapterDAO.countByStaffAndDate(staffId, startOfDay));

        // Content approved: COUNT(review_chapter) WHERE staff_id = ? AND status = 'approved' AND created_at >= ?
        stats.setContentApproved(reviewChapterDAO.countByStaffStatusAndDate(staffId, "approved", startOfDay));

        // Content rejected: COUNT(review_chapter) WHERE staff_id = ? AND status = 'rejected' AND created_at >= ?
        stats.setContentRejected(reviewChapterDAO.countByStaffStatusAndDate(staffId, "rejected", startOfDay));

        // Reports resolved: COUNT(reports) WHERE staff_id = ? AND status = 'resolved' AND updated_at >= ?
        stats.setReportsResolved(reportDAO.countResolvedByStaffAndDate(staffId, startOfDay));

        return stats;
    }
    
    public Staff getCurrentStaff(int staffId) throws SQLException, ClassNotFoundException {
        return staffDAO.findById(staffId);
    }
}
