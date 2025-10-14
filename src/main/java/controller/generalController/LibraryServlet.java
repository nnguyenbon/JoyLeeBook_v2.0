package controller.generalController;

import dao.ChapterDAO;
import dao.SeriesDAO;
import db.DBConnection;
import dto.chapter.ChapterItemDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdParam = request.getParameter("userId");

        int userId = ValidationInput.isPositiveInteger(userIdParam) ? Integer.parseInt(userIdParam) : 1;
        try {
            Connection conection = DBConnection.getConnection();
            SeriesDAO  seriesDAO = new SeriesDAO(conection);
            ChapterDAO chapterDAO = new ChapterDAO(conection);

            SeriesServices seriesServices = new SeriesServices(conection);
            List<SeriesInfoDTO> savedSeries = seriesServices.buildSeriesInfoDTOList(seriesDAO.getSeriesByUserId(userId));

            List<ChapterItemDTO> historyChapters = chapterDAO.getReadingHistoryChapters(userId, 0, Integer.MAX_VALUE, null);

            request.setAttribute("savedSeries", savedSeries);
            request.setAttribute("historyChapters", historyChapters);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/general/Library.jsp").forward(request, response);
    }
}
