package controller.reportController;

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
public class ViewReportDetailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        int type_id = ValidationInput.isPositiveInteger(request.getParameter("type_id")) ?  Integer.parseInt(request.getParameter("type_id")) : 1;
        try {
            ReportServices reportServices = new ReportServices();
            request.getRequestDispatcher("/WEB-INF/views/report/ChapterReportDetail.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
