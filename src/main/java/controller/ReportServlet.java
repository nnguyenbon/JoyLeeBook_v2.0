package controller;

import dao.*;
import db.DBConnection;
import dto.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
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

    /* ===========================
       ======== HTTP GET =========
       =========================== */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) action = "/list";

        try {
            switch (action) {
                case "/list" -> viewReportList(request, response);
                case "/detail" -> viewReportDetail(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleServerError(request, response, e, "Unexpected error in GET /report");
        }
    }

    /* ===========================
       ======== HTTP POST ========
       =========================== */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getPathInfo();
        if (action == null) action = "";

        try {
            switch (action) {
                case "/report-chapter" -> reportChapter(request, response);
                case "/report-comment" -> reportComment(request, response);
                case "/handle" -> handleReport(request, response);
                case "/delete" -> deleteReport(request, response);
                default -> response.sendRedirect(request.getContextPath() + "/");
            }
        } catch (Exception e) {
            handleServerError(request, response, e, "Unexpected error in POST /report");
        }
    }

    /* ===========================
       ======== VIEW LIST ========
       =========================== */
    private void viewReportList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DBConnection.getConnection()) {
            String type = request.getParameter("type");
            if (type == null || type.isEmpty()) type = "chapter";

            String statusFilter = request.getParameter("filterByStatus");

            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("report_id");
            paginationRequest.setSortDir("DESC");

            ReportDAO reportDAO = new ReportDAO(conn);

            if ("chapter".equals(type)) {
                List<ReportChapter> chapterReports = reportDAO.getReportChapterList(statusFilter, paginationRequest);
                request.setAttribute("reportList", chapterReports);
                request.setAttribute("size", chapterReports.size());
            } else if ("comment".equals(type)) {
                List<ReportComment> commentReports = reportDAO.getReportCommentList(statusFilter, paginationRequest);
                request.setAttribute("reportList", commentReports);
                request.setAttribute("size", commentReports.size());
            } else {
                handleClientError(request, response, "Invalid report type.");
                return;
            }

            PaginationUtils.sendParameter(request, paginationRequest);
            request.setAttribute("type", type);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("activePage", "reports");
            request.setAttribute("pageTitle", "Manage Reports");
            request.setAttribute("contentPage", "/WEB-INF/views/staff/_reportList.jsp");

            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while retrieving report list.");
        }
    }

    /* ===========================
       ======== VIEW DETAIL ======
       =========================== */
    private void viewReportDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DBConnection.getConnection()) {
            int reportId = ValidationInput.isPositiveInteger(request.getParameter("reportId"))
                    ? Integer.parseInt(request.getParameter("reportId"))
                    : -1;

            if (reportId == -1) {
                handleClientError(request, response, "Invalid reportId format.");
                return;
            }

            String type = request.getParameter("type");
            if (type == null || type.isEmpty()) type = "chapter";

            ReportDAO reportDAO = new ReportDAO(conn);
            Report report = reportDAO.findById(reportId);

            if (report == null) {
                request.setAttribute("error", "Report not found!");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            if ("chapter".equals(type)) {
                ReportChapter detailReport = reportDAO.getReportChapterById(reportId);
                ChapterDAO chapterDAO = new ChapterDAO(conn);
                Chapter chapter = chapterDAO.findById(report.getChapterId());
                request.setAttribute("chapter", chapter);
                request.setAttribute("report", detailReport);
            } else if ("comment".equals(type)) {
                ReportComment detailReport = reportDAO.getReportCommentById(reportId);
                request.setAttribute("report", detailReport);
            }

            request.setAttribute("type", type);
            request.setAttribute("activePage", "reports");
            request.setAttribute("pageTitle", "Report Details");
            request.setAttribute("contentPage", "/WEB-INF/views/staff/_reportDetail.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid report ID format.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while retrieving report detail.");
        }
    }

    /* ===========================
       ===== REPORT CHAPTER ======
       =========================== */
    private void reportChapter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String reason = request.getParameter("reason");

            Report report = new Report();
            report.setReporterId(user.getUserId());
            report.setChapterId(chapterId);
            report.setReason(reason);
            report.setTargetType("chapter");

            ReportDAO reportDAO = new ReportDAO(conn);
            reportDAO.insert(report);

            // Có thể trả JSON response sau này cho AJAX
            response.sendRedirect(request.getContextPath() + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid chapter ID format.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while reporting chapter.");
        }
    }

    /* ===========================
       ===== REPORT COMMENT ======
       =========================== */
    private void reportComment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            String reason = request.getParameter("reason");

            Report report = new Report();
            report.setReporterId(user.getUserId());
            report.setCommentId(commentId);
            report.setReason(reason);
            report.setTargetType("comment");

            ReportDAO reportDAO = new ReportDAO(conn);
            reportDAO.insert(report);
            response.sendRedirect(request.getContextPath() + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid comment ID format.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while reporting comment.");
        }
    }

    /* ===========================
       ===== HANDLE REPORT =======
       =========================== */
    private void handleReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object loggedUser = request.getSession().getAttribute("loginedUser");
        if (!(loggedUser instanceof Staff staff)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int reportId = Integer.parseInt(request.getParameter("reportId"));
            String status = request.getParameter("status");
            String message = request.getParameter("message");
            ReportDAO reportDAO = new ReportDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            Report report = reportDAO.findById(reportId);
            if (report == null) {
                handleClientError(request, response, "Report not found.");
                return;
            }

            boolean updated = reportDAO.updateStatus(reportId, status, staff.getStaffId());
            if (updated && "chapter".equals(report.getTargetType())) {
                ChapterDAO chapterDAO = new ChapterDAO(conn);
                chapterDAO.updateStatus(report.getChapterId(), status);
                Notification noti = createApprovalNotification(conn, report.getChapterId(), message, status);
                notificationsDAO.insertNotification(noti);
            } else if (updated && "comment".equals(report.getTargetType())) {
                CommentDAO commentDAO = new CommentDAO(conn);
                commentDAO.softDelete(report.getCommentId());
            }
            response.sendRedirect(request.getContextPath() + "/report/list?type=" + report.getTargetType() + "&filterByStatus=pending");

        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid report ID format.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while handling report.");
        }
    }

    /* ===========================
       ===== DELETE REPORT =======
       =========================== */
    private void deleteReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object loggedUser = request.getSession().getAttribute("loginedUser");
        if (!(loggedUser instanceof Staff)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int reportId = Integer.parseInt(request.getParameter("reportId"));
            ReportDAO reportDAO = new ReportDAO(conn);
            boolean success = reportDAO.deleteReport(reportId);

            if (success) {
                request.getSession().setAttribute("success", "Report deleted successfully!");
            } else {
                request.getSession().setAttribute("error", "Failed to delete report!");
            }

            response.sendRedirect(request.getContextPath() + "/report/list");
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid report ID format.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while deleting report.");
        }
    }

    /* ===========================
       ===== CREATE NOTIFY =======
       =========================== */
    private static Notification createApprovalNotification(Connection conn, int chapterId,
                                                           String message, String status)
            throws SQLException {
        ChapterDAO chapterDAO = new ChapterDAO(conn);
        Chapter chapter = chapterDAO.findById(chapterId);

        Notification notification = new Notification();
        notification.setUserId(chapter.getAuthorId());
        notification.setTitle("Chapter " + status);
        notification.setType("moderation");
        notification.setMessage(message);
        notification.setUrlRedirect("/chapter/detail?chapterId=" + chapterId);

        return notification;
    }

    /* ===========================
       ===== ERROR HANDLERS ======
       =========================== */
    private void handleClientError(HttpServletRequest req, HttpServletResponse res, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, res);
    }

    private void handleServerError(HttpServletRequest req, HttpServletResponse res, Exception e, String msg)
            throws ServletException, IOException {
        e.printStackTrace();
        req.setAttribute("error", msg + " " + e.getMessage());
        req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, res);
    }
}
