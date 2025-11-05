package controller.generalController;

import dao.ReportDAO;
import dao.ReviewChapterDAO;
import db.DBConnection;
import dto.staff.DashboardStatsDTO;
import dto.staff.QuickStatsDTO;
import dto.staff.RecentActionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Staff;
import model.User;
import services.account.StaffServices;

import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
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
        int staffId = ValidationInput.isPositiveInteger(request.getParameter("staffId")) ? Integer.parseInt(request.getParameter("staffId")) : 1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");

        try (Connection connection = DBConnection.getConnection()) {
            //Check authentication
            StaffServices staffServices = new StaffServices();
            DashboardStatsDTO stats = staffServices.getDashboardStats(staffId);
            List<RecentActionDTO> recentActions = staffServices.getRecentActions(staffId, 4);
            QuickStatsDTO quickStats = getQuickStatsToday(staffId, connection);

            // Set attributes cho JSP
            request.setAttribute("dashboardStats", stats);
            request.setAttribute("recentActions", recentActions);
            request.setAttribute("quickStats", quickStats);

            request.setAttribute("pageTitle", "Staff Dashboard");
            request.setAttribute("activePage", "overview");
            request.setAttribute("contentPage", "/WEB-INF/views/general/StaffDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
        }catch (Exception e) {
            throw new ServletException(e);
        }
    }
    public QuickStatsDTO getQuickStatsToday(int staffId, Connection conn) throws SQLException {
        try {
            ReviewChapterDAO reviewChapterDAO = new ReviewChapterDAO(conn);
            ReportDAO reportDAO = new ReportDAO(conn);
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
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
