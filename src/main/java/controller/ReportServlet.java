package controller;

import dao.ChapterDAO;
import dao.CommentDAO;
import dao.ReportDAO;
import db.DBConnection;
import dto.PaginationRequest;
import dto.report.ReportBaseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Report;
import model.ReportChapter;
import model.ReportComment;
import model.User;
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
            String statusFilter = request.getParameter("status");

            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("report_id");
            paginationRequest.setSortDir("DESC");
            ReportDAO reportDAO = new ReportDAO(conn);

            request.setAttribute("type", type);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("activePage", "reports");

            if ("chapter".equals(type)) {
                // Get chapter reports
                List<ReportChapter> chapterReports = reportDAO.getReportChapterList(statusFilter, paginationRequest);
                PaginationUtils.sendParameter(request, paginationRequest);

                request.setAttribute("reportList", chapterReports);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_reportChapterList.jsp");
            } else if ("comment".equals(type)) {
                // Get comment reports
                List<ReportComment> commentReports = reportDAO.getReportCommentList(statusFilter, paginationRequest);

                request.setAttribute("reportList", commentReports);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_reportCommentList.jsp");
            } else {
                // Default: show both
                List<ReportChapter> chapterReports = reportDAO.getReportChapterList();
                List<ReportComment> commentReports = reportDAO.getReportCommentList();
                request.setAttribute("chapterReports", chapterReports);
                request.setAttribute("commentReports", commentReports);
                request.getRequestDispatcher("/WEB-INF/views/reports.jsp")
                        .forward(request, response);
            }
            PaginationUtils.sendParameter(request, paginationRequest);
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void viewReportDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //            ReportServices reportServices = new ReportServices();
        int reportId = ValidationInput.isPositiveInteger(request.getParameter("reportId")) ? Integer.parseInt(request.getParameter("reportId")) : -1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
//            ReportBaseDTO reportBaseDTO = reportServices.getReportById(reportId, type);
//            request.setAttribute("reportBaseDTO", reportBaseDTO);
        request.setAttribute("contentPage", "/WEB-INF/views/report/ChapterReportDetail.jsp");
        request.setAttribute("activePage", "series");
        request.setAttribute("pageTitle", "Report Detail");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
    }

    private void reportComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user.getUserId();

        //            ReportServices reportServices = new ReportServices();
        String commentId = request.getParameter("commentId");
        String reason = request.getParameter("reason");
        String description = request.getParameter("description");
        String type = "comment";
        String chapterId = request.getParameter("chapterId");
        String seriesId = request.getParameter("seriesId");
//            reportServices.createReport(userId, type, commentId, reason, description);
        request.setAttribute("seriesId", seriesId);
        request.setAttribute("chapterId", chapterId);

        request.getSession().setAttribute("successReportMessage", "Report submitted successfully.");
        response.sendRedirect(request.getContextPath()
                + "/chapter?action=detail&seriesId=" + seriesId + "&chapterId=" + chapterId);
    }

    private void reportChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user.getUserId();

        try {
            String reason = request.getParameter("reason");
            String description = request.getParameter("description");
            String type = "chapter";

            String chapterId = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");
            request.setAttribute("seriesId", seriesId);
            request.setAttribute("chapterId", chapterId);

//            ReportServices reportServices = new ReportServices();
//            reportServices.createReport(userId, type, chapterId, reason, description);

            request.getSession().setAttribute("successReportMessage", "Report submitted successfully.");
            response.sendRedirect(request.getContextPath()
                    + "/chapter?action=detail&seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
