package controller.seriesController;

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

@WebServlet("/staff-series")
public class SeriesDetailForStaffServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;
        try {
            SeriesServices seriesServices = new SeriesServices();
            ChapterServices chapterServices = new ChapterServices();

            request.setAttribute("series", seriesServices.buildSeriesInfoDTO(seriesId));
            request.setAttribute("chapterDetailDTOList", chapterServices.chaptersFromSeries(seriesId));
            request.getRequestDispatcher("/WEB-INF/views/series/SeriesDetailForStaff.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
