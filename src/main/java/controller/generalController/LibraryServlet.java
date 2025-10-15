package controller.generalController;

import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            ChapterServices chapterServices = new ChapterServices(DBConnection.getConnection());
            SeriesServices seriesServices = new SeriesServices(DBConnection.getConnection());

            request.setAttribute("savedSeries", seriesServices.savedSeriesFromUser(userId));
            request.setAttribute("historyChapters", chapterServices.historyChaptersFromUser(userId, 0, Integer.MAX_VALUE, null));
            request.getRequestDispatcher("/WEB-INF/views/general/Library.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
