package controller.reportController;

import dto.PaginationRequest;
import dto.report.ReportBaseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.general.PaginationServices;
import services.report.ReportServices;
import utils.PaginationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("report")){
            String type = request.getParameter("type");
            if (type.equals("comment")) {
                reportComment(request, response);
            } else if (type.equals("chapter")){
                reportChapter(request, response);
            }
        } else if (action.equals("handle")){

        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") != null ? request.getParameter("action") : "";
        if (action.equals("detail")) {
            viewDetail(request, response);
        } else {
            viewReportList(request, response);
        }
    }

    private void viewReportList (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String type = request.getParameter("type") == null ? "" : request.getParameter("type");

            ReportServices reportServices = new ReportServices();
            List<ReportBaseDTO> reportList;

            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("report_id");
            reportList = reportServices.buildReportList(type, paginationRequest);
            if (type.equals("chapter")) {
                request.setAttribute("size", reportServices.countReports("chapter"));
                request.setAttribute("reportChapterDTOList", reportList);
                PaginationUtils.sendParameter(request, paginationRequest);
                request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportChapterView.jsp").forward(request, response);
                return;
            } else if (type.equals("comment")) {
                request.setAttribute("size", reportServices.countReports("comment"));
                request.setAttribute("reportCommentDTOList", reportList);
                PaginationUtils.sendParameter(request, paginationRequest);
                request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportCommentView.jsp").forward(request, response);
                return;
            }
            request.setAttribute("type", type);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/report/ReportList.jsp").forward(request, response);
    }

    private void viewDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ReportServices reportServices = new ReportServices();
            int reportId = ValidationInput.isPositiveInteger(request.getParameter("reportId")) ? Integer.parseInt(request.getParameter("reportId")):-1;
            String type = request.getParameter("type") == null ? "" : request.getParameter("type");
            ReportBaseDTO reportBaseDTO = reportServices.getReportById(reportId, type);
            request.setAttribute("reportBaseDTO", reportBaseDTO);
            request.getRequestDispatcher("/WEB-INF/views/report/ChapterReportDetail.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void reportComment (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            ReportServices reportServices = new ReportServices();
            String commentId = request.getParameter("commentId");
            String reason = request.getParameter("reason");
            String description = request.getParameter("description");
            String type = "comment";
            String chapterId = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");
            reportServices.createReport(userId, type, commentId, reason, description);
            request.setAttribute("seriesId", seriesId);
            request.setAttribute("chapterId", chapterId);
            response.sendRedirect(request.getContextPath()
                    + "/chapter-content?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void reportChapter (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            String reason = request.getParameter("reason");
            String description = request.getParameter("description");
            String type = "chapter";

            String chapterId = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");
            request.setAttribute("seriesId", seriesId);
            request.setAttribute("chapterId", chapterId);

            ReportServices reportServices = new ReportServices();
            reportServices.createReport(userId, type, chapterId, reason, description);

            response.sendRedirect(request.getContextPath()
                    + "/chapter-content?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
