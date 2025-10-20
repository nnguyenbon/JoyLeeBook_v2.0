package controller.reportController;

import dto.report.ReportBaseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.general.PaginationServices;
import services.report.ReportServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "" : request.getParameter("action");
        try {
            ReportServices reportServices = new ReportServices();
            switch (action) {
                case "send":
                    sendReport(request, response, reportServices);
                    break;
                case "delete":
                    break;
                case "update":
                    break;
                default:
                    viewReportList(request, response, reportServices);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "" : request.getParameter("action");
        try {
            ReportServices reportServices = new ReportServices();
            switch (action) {
                case "detail":
                    viewReportDetail(request, response, reportServices);
                    break;
                default:
                    viewReportList(request, response, reportServices);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewReportList(HttpServletRequest request, HttpServletResponse response, ReportServices reportServices) throws ServletException, IOException, SQLException, ClassNotFoundException {
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        PaginationServices paginationServices = new PaginationServices();
        List<ReportBaseDTO> reportList;
        if (type.equals("chapter")) {
            reportList = reportServices.handleRedirect(type);
            request.setAttribute("size", reportList.size());
            request.setAttribute("reportChapterDTOList", paginationServices.handleParameterPage(reportList, request));
            request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportChapterView.jsp").forward(request, response);
            return;
        } else if (type.equals("comment")) {
            reportList = reportServices.handleRedirect(type);
            request.setAttribute("size", reportList.size());
            request.setAttribute("reportCommentDTOList", paginationServices.handleParameterPage(reportList, request));
            request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportCommentView.jsp").forward(request, response);
            return;
        }
        request.setAttribute("type", type);
        request.getRequestDispatcher("/WEB-INF/views/report/ReportList.jsp").forward(request, response);
    }

    public void viewReportDetail(HttpServletRequest request, HttpServletResponse response, ReportServices reportServices) throws ServletException, IOException, SQLException, ClassNotFoundException {
        int reportId = ValidationInput.isPositiveInteger(request.getParameter("reportId")) ? Integer.parseInt(request.getParameter("reportId")):-1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        ReportBaseDTO reportBaseDTO = reportServices.getReportById(reportId, type); // Chưa xây dựng để lấy ra đầy đủ
        request.setAttribute("reportBaseDTO", reportBaseDTO);
        request.getRequestDispatcher("/WEB-INF/views/report/ChapterReportDetail.jsp").forward(request, response);
    }

    public void sendReport(HttpServletRequest request, HttpServletResponse response, ReportServices reportServices) throws ServletException, IOException, SQLException, ClassNotFoundException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        String commentId = request.getParameter("commentId");
        String reason = request.getParameter("reason");
        String description = request.getParameter("description");
        String type = "comment";
        String chapterId = request.getParameter("chapterId");
        String seriesId = request.getParameter("seriesId");
        reportServices.createReport(userId, type, commentId, reason, description);
        request.setAttribute("seriesId", seriesId);
        request.setAttribute("chapterId", chapterId);
        request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
    }
}
