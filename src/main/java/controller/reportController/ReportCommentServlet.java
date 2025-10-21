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

@WebServlet("/report-comment")
public class ReportCommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}
