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
import java.util.List;

@WebServlet("/report")
public class ViewReportsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ReportServices reportServices = new ReportServices();
            String type = request.getParameter("type") == null ? "" : request.getParameter("type");
            PaginationServices paginationServices = new PaginationServices();
            List<ReportBaseDTO> reportList;
            reportList = reportServices.handleRedirect(type);
            if (type.equals("chapter")) {
                request.setAttribute("size", reportList.size());
                request.setAttribute("reportChapterDTOList", paginationServices.handleParameterPage(reportList, request));
                request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportChapterView.jsp").forward(request, response);
                return;
            } else if (type.equals("comment")) {
                request.setAttribute("size", reportList.size());
                request.setAttribute("reportCommentDTOList", paginationServices.handleParameterPage(reportList, request));
                request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportCommentView.jsp").forward(request, response);
                return;
            }
            request.setAttribute("type", type);
            request.getRequestDispatcher("/WEB-INF/views/report/ReportList.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
