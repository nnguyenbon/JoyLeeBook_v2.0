package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.report.ReportServices;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        try {
            ReportServices reportServices = new ReportServices();
            if (reportServices.handleRedirect(type, request, response)) {
                return;
            }

            request.setAttribute("type", type != null ? type : "");
            request.getRequestDispatcher("/WEB-INF/views/general/ReportList.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
