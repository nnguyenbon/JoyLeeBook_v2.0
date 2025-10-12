package controller.generalController;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.RatingDAO;
import dao.SeriesDAO;
import db.DBConnection;
import dto.chapter.ChapterItemDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Series;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String userIdParam = request.getParameter("userId");
        String userIdParam = "1";
        int userId = 0;
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                userId = Integer.parseInt(userIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userId");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            Connection conection = DBConnection.getConnection();
            SeriesDAO  seriesDAO = new SeriesDAO(conection);
            ChapterDAO chapterDAO = new ChapterDAO(conection);
            CategoryDAO categoryDAO = new CategoryDAO(conection);
            RatingDAO ratingDAO = new RatingDAO(conection);
            List<SeriesInfoDTO> savedSeries = new ArrayList<>();
            for (Series series : seriesDAO.getSeriesByUserId(userId)) {
                SeriesInfoDTO seriesInfoDTO = new SeriesInfoDTO();
                seriesInfoDTO.setSeriesId(series.getSeriesId());
                seriesInfoDTO.setTitle(series.getTitle());
                seriesInfoDTO.setUpdatedAt(series.getUpdatedAt().format(formatter));
                seriesInfoDTO.setCoverImgUrl(series.getCoverImgUrl());
                seriesInfoDTO.setStatus(series.getStatus());
                seriesInfoDTO.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
                List<String> categoriesName = new ArrayList<>();
                for (Category category : categoryDAO.getCategoryBySeriesId(series.getSeriesId())) {
                    categoriesName.add(category.getName());
                }
                seriesInfoDTO.setCategories(categoriesName);
                seriesInfoDTO.setAvgRating((double) Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10) / 10);
                seriesInfoDTO.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));
                savedSeries.add(seriesInfoDTO);
            }
            List<ChapterItemDTO> historyChapters = chapterDAO.getReadingHistoryChapters(userId, 0, Integer.MAX_VALUE, null);


            request.setAttribute("savedSeries", savedSeries);
            request.setAttribute("historyChapters", historyChapters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/general/Library.jsp").forward(request, response);
    }
}
