package controller.reportController;

import dto.report.ReportBaseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.report.ReportServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;
@WebServlet("/report-detail")
public class ReportDetailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
}
