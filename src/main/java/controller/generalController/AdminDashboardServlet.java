package controller.generalController;

import dao.*;
import db.DBConnection;
import model.*;
import model.staff.DashboardStats;
import model.staff.InteractionStats;
import model.staff.GenreStats;
import model.staff.WeeklyUserStats;
import model.staff.WeeklyReportStats;
import utils.AuthenticationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Timestamp;
import java.util.List;

/**
 * Servlet implementation class AdminDashboardServlet
 * Handles requests for the admin dashboard page.
 */
@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Not implemented
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());

        if (loggedInAccount == null || !(loggedInAccount instanceof Staff)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Staff staff = (Staff) loggedInAccount;
        String role = staff.getRole();

        try (Connection conn = DBConnection.getConnection()) {
            // Initialize DAOs
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            StaffDAO staffDAO = new StaffDAO(conn);
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            ReportDAO reportDAO = new ReportDAO(conn);
            InteractionDAO interactionDAO = new InteractionDAO(conn);
            CategoryDAO categoryDAO = new CategoryDAO(conn);

            // Main Dashboard Stats
            DashboardStats dashboardStats = new DashboardStats();

            // Series Statistics
            dashboardStats.setTotalSeries(seriesDAO.countAllNonDeleted());
            List<User> userList = userDAO.getAll();

            dashboardStats.setActiveUsers(countActiveUsers(userList));
            dashboardStats.setAuthors(countActiveAuthors(userList));
            dashboardStats.setBannedUsers(countBannedUsers(userList));
            dashboardStats.setStaffs(staffDAO.countAllActive());

            // Chapter Statistics
            List<Chapter> chapterList = chapterDAO.getAll();
            dashboardStats.setTotalChapters(chapterList.size());
            dashboardStats.setPendingChapters(countChapterByApprovalStatus(chapterList, "pending"));
            dashboardStats.setRejectedChapters(countChapterByApprovalStatus(chapterList, "rejected"));

            // Report Statistics
            List<Report> reports = reportDAO.getAll();
            dashboardStats.setTotalReports(reports.size());
            dashboardStats.setPendingReports(countReportByStatus(reports, "pending"));

            // Interaction Statistics (for bar chart)
            InteractionStats interactionStats = interactionDAO.getTotalInteractions();

            // Genre Statistics (for pie chart)
            List<GenreStats> genreStatsList = categoryDAO.getGenreDistribution();

            // Weekly User Statistics (for area chart)
            LocalDate today = LocalDate.now();
            LocalDate weekAgo = today.minusDays(30); // Last 7 days including today
            Timestamp startOfWeek = Timestamp.valueOf(weekAgo.atStartOfDay());

            WeeklyUserStats weeklyUserStats = userDAO.getWeeklyUserStats(startOfWeek);

            // Weekly Report Statistics (for area chart)
            WeeklyReportStats weeklyReportStats = reportDAO.getWeeklyReportStats(startOfWeek);

            // Set attributes for JSP
            request.setAttribute("dashboardStats", dashboardStats);
            request.setAttribute("interactionStats", interactionStats);
            request.setAttribute("genreStatsList", genreStatsList);
            request.setAttribute("weeklyUserStats", weeklyUserStats);
            request.setAttribute("weeklyReportStats", weeklyReportStats);
            request.setAttribute("staffName", staff.getFullName());

            // Layout attributes
            request.setAttribute("pageTitle", "Admin Dashboard");
            request.setAttribute("contentPage", "/WEB-INF/views/staff/_viewStatistics.jsp");

            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error loading admin dashboard", e);
        }
    }
    private int countActiveUsers(List<User> userList) throws SQLException {
        int count = 0;
        for (User user : userList) {
            if (user.getStatus().equals("active") && user.getRole().equals("reader")) {
                count++;
            }
        }
        return count;
    }

    private int countBannedUsers(List<User> userList) throws SQLException {
        int count = 0;
        for (User user : userList) {
            if (user.getStatus().equals("banned")) {
                count++;
            }
        }
        return count;
    }

    private int countActiveAuthors(List<User> userList) throws SQLException {
        int count = 0;
        for (User user : userList) {
            if (user.getStatus().equals("active") && user.getRole().equals("author")) {
                count++;
            }
        }
        return count;
    }

    private int countReportByStatus(List<Report> reportList, String status) throws SQLException {
        int count = 0;
        for (Report report : reportList) {
            if (report.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    private int countChapterByApprovalStatus (List<Chapter> chapterList, String approvalStatus) throws SQLException {
        int count = 0;
        for (Chapter chapter : chapterList) {
            if (chapter.getApprovalStatus().equals(approvalStatus)) {
                count++;
            }
        }
        return count;
    }
}