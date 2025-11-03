package controller;

import dao.*;
import db.DBConnection;
import dto.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import utils.AuthenticationUtils;
import utils.PaginationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/report/*")
public class ReportServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for viewing report data.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/list" -> viewReportList(request, response);
            case "/detail" -> viewReportDetail(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/");
            }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/report-chapter" -> reportChapter(request, response);
            case "/report-comment" -> reportComment(request, response);
            case "/handle" -> handleReport(request, response);
            case "/delete" -> deleteReport(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private void deleteReport(HttpServletRequest request, HttpServletResponse response) {
    }

    private void handleReport(HttpServletRequest request, HttpServletResponse response) {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int staffId = ((Staff) loggedInAccount).getStaffId();

        try (Connection conn = DBConnection.getConnection()) {
            int reportId = Integer.parseInt(request.getParameter("reportId"));
            String status = request.getParameter("status");
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            ReportDAO reportDAO = new ReportDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            // Verify series exists
            Report report = reportDAO.findById(reportId);
            if (report == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Report not found.");
                return;
            }

            if (reportDAO.updateStatus(reportId, status, staffId)) {
                // Create and send notification to series owner
                if ("chapter".equals(type)) {
                Notification notification = createApprovalNotification(
                        conn, report.getChapterId(), message, status
                );
                notificationsDAO.insertNotification(notification);
                }
            }
            //tra ve json cua ajax
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error approving series", e);
        } catch (IOException e) {
            throw new RuntimeException("Error redirecting after approval", e);
        }
    }


    /**
     * Displays a paginated list of report with filtering.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void viewReportList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            String type = request.getParameter("type");
            if (type == null || type.isEmpty()) {
                type = "chapter";
            }
            String statusFilter = request.getParameter("filterByStatus");

            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("report_id");
            paginationRequest.setSortDir("DESC");
            ReportDAO reportDAO = new ReportDAO(conn);

            if ("chapter".equals(type)) {
                // Get chapter reports
                List<ReportChapter> chapterReports = reportDAO.getReportChapterList(statusFilter, paginationRequest);
                request.setAttribute("reportList", chapterReports);
            } else if ("comment".equals(type)) {
                // Get comment reports
                List<ReportComment> commentReports = reportDAO.getReportCommentList(statusFilter, paginationRequest);
                request.setAttribute("reportList", commentReports);
            }
            PaginationUtils.sendParameter(request, paginationRequest);
            request.setAttribute("type", type);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("activePage", "reports");
            request.setAttribute("pageTitle", "Manage Series");
            request.setAttribute("contentPage", "/WEB-INF/views/staff/_reportList.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void viewReportDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            int reportId = ValidationInput.isPositiveInteger(request.getParameter("reportId"))
                    ? Integer.parseInt(request.getParameter("reportId")) : -1;

            if (reportId == -1) {
                response.sendRedirect(request.getContextPath() + "/report/list");
                return;
            }

            String type = request.getParameter("type");
            if (type == null || type.isEmpty()) {
                type = "chapter";
            }

            ReportDAO reportDAO = new ReportDAO(conn);
            Report report = reportDAO.findById(reportId);

            if (report == null) {
                request.getSession().setAttribute("errorMessage", "Report not found.");
                response.sendRedirect(request.getContextPath() + "/report/list?type=" + type);
                return;
            }
            if ("chapter".equals(type)) {
                ReportChapter detailReport = reportDAO.getReportChapterById(reportId);
                request.setAttribute("report", detailReport);
                ChapterDAO chapterDAO = new ChapterDAO(conn);
                Chapter chapter = chapterDAO.findById(report.getChapterId());
                request.setAttribute("chapter", chapter);
            } else if ("comment".equals(type)) {
                ReportComment detailReport = reportDAO.getReportCommentById(reportId);
                request.setAttribute("report", detailReport);
            }
            request.setAttribute("type", type);
            request.setAttribute("contentPage", "/WEB-INF/views/staff/_reportDetail.jsp");
            request.setAttribute("activePage", "reports");
            request.setAttribute("pageTitle", "Manage Report");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
        private void reportComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
            if (user == null) throw new AssertionError();
            int userId = user.getUserId();
        try (Connection conn = DBConnection.getConnection()) {
            String type = "comment";
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            String reason = request.getParameter("reason");

            Report report = new Report();
            report.setReporterId(userId);
            report.setCommentId(commentId);
            report.setReason(reason);
            report.setTargetType(type);
            ReportDAO reportDAO = new ReportDAO(conn);
            reportDAO.insert(report);

            // tra ve json ajax
        }  catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void reportChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (user == null) throw new AssertionError();
        int userId = user.getUserId();

        try(Connection conn = DBConnection.getConnection()) {
            String type = "chapter";
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            String reason = request.getParameter("reason");

            Report report = new Report();
            report.setReporterId(userId);
            report.setChapterId(chapterId);
            report.setReason(reason);
            report.setTargetType(type);
            ReportDAO reportDAO = new ReportDAO(conn);
            reportDAO.insert(report);

            //tra ve json cho ajax
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a notification object for series approval/rejection.
     *
     * @param conn the database connection to use for queries
     * @param message the staff's feedback message
     * @param chapterId id of chapter
     * @param status status of report
     * @return a Notification object ready to be inserted into the database
     * @throws SQLException if a database access error occurs while fetching owner ID
     */
    private static Notification createApprovalNotification(Connection conn, int chapterId,
                                                           String message, String status) throws SQLException {
        ChapterDAO chapterDAO = new ChapterDAO(conn);
        Notification notification = new Notification();
        notification.setUserId(chapterDAO.findById(chapterId).getUserId());
        notification.setTitle("Chapter" + status);
        notification.setType("moderation");
        notification.setMessage(message);
        notification.setUrlRedirect("/chapter/detail?chapterId=" + chapterId);

        return notification;
    }
}
