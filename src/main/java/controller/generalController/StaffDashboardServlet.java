package controller.generalController;

import dao.*;
import db.DBConnection;
import model.Account;
import model.staff.DashboardStats;
import model.staff.QuickStats;
import model.staff.RecentAction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Staff;
import utils.AuthenticationUtils;

import java.io.IOException;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Servlet implementation class StaffDashboardServlet
 * Handles requests for the staff dashboard page.
 * Retrieves dashboard statistics, recent actions, and quick stats for the logged-in staff member.
 * Forwards the data to the StaffDashboard JSP for rendering.
 */
@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {

    /**
     * Handles the HTTP POST method.
     * Currently not implemented.
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * Handles the HTTP GET method.
     * Retrieves dashboard statistics, recent actions, and quick stats for the logged-in staff member.
     * Forwards the data to the StaffDashboard JSP for rendering.
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        assert loggedInAccount != null;
        int staffId = ((Staff) loggedInAccount).getStaffId();
        String role = ((Staff) loggedInAccount).getRole();
        int staffIdParam = 0;
        if ("admin".equals(role)) {
            staffIdParam = Integer.parseInt(request.getParameter("staffId"));
        }
        else {
            staffIdParam = staffId;
        }
        try (Connection conn = DBConnection.getConnection()) {
            //Check authentication
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            ReportDAO reportDAO = new ReportDAO(conn);
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            ReviewSeriesDAO reviewSeriesDAO = new ReviewSeriesDAO(conn);
            ReviewChapterDAO reviewChapterDAO = new ReviewChapterDAO(conn);
            DashboardStats stats = new DashboardStats();

            // Total Series: COUNT(series) WHERE is_deleted = 0
            stats.setTotalSeries(seriesDAO.countAllNonDeleted());

            stats.setPendingSeries(seriesDAO.countByStatus("pending"));

            stats.setYourReviewSeries(reviewSeriesDAO.countByStaff(staffIdParam));

            stats.setYourRejectSeries(reviewSeriesDAO.countByStaffAndStatus(staffIdParam, "rejected"));
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
            stats.setHandledReports(reportDAO.countHandledByStaff(staffIdParam, "resolved"));

            // Total Chapters: COUNT(chapters) WHERE is_deleted = 0
            stats.setTotalChapters(chapterDAO.countAllNonDeleted());

            // Your Reviews: COUNT(review_chapter) WHERE staff_id = ?
            stats.setYourReviews(reviewChapterDAO.countByStaff(staffIdParam));

            // Pending Chapters: COUNT(chapters) WHERE status = 'pending'
            stats.setPendingChapters(chapterDAO.countByStatus("pending"));

            // Your Rejects: COUNT(review_chapter) WHERE staff_id = ? AND status = 'rejected'
            stats.setYourRejects(reviewChapterDAO.countByStaffAndStatus(staffIdParam, "rejected"));

            LocalDate today = LocalDate.now();
            Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime());

            List<RecentAction> recentActions = reviewChapterDAO.getRecentActionsByStaff(staffIdParam, startOfDay, 4);;
            QuickStats quickStats = new QuickStats();

            // Reviews completed: COUNT(review_chapter) WHERE staff_id = ? AND created_at >= ?
            quickStats.setReviewsCompleted(reviewChapterDAO.countByStaffAndDate(staffIdParam, startOfDay));

            // Content approved: COUNT(review_chapter) WHERE staff_id = ? AND status = 'approved' AND created_at >= ?
            quickStats.setContentApproved(reviewChapterDAO.countByStaffStatusAndDate(staffIdParam, "approved", startOfDay));

            // Content rejected: COUNT(review_chapter) WHERE staff_id = ? AND status = 'rejected' AND created_at >= ?
            quickStats.setContentRejected(reviewChapterDAO.countByStaffStatusAndDate(staffIdParam, "rejected", startOfDay));

            // Reports resolved: COUNT(reports) WHERE staff_id = ? AND status = 'resolved' AND updated_at >= ?
            quickStats.setReportsResolved(reportDAO.countResolvedByStaffAndDate(staffIdParam, startOfDay));

            // Set attributes cho JSP
            request.setAttribute("dashboardStats", stats);
            request.setAttribute("recentActions", recentActions);
            request.setAttribute("quickStats", quickStats);


            if ("staff".equals(role)) {
                request.setAttribute("pageTitle", "Staff Dashboard");
                request.setAttribute("contentPage", "/WEB-INF/views/general/StaffDashboard.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/general/StaffDashboard.jsp").forward(request, response);
            }
        }catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
