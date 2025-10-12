package controller.generalController;

import dao.*;
import db.DBConnection;
import dto.CategoryInfoDTO;
import dto.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Series;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet ("/homepage")
public class HomepageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            SeriesDAO seriesDAO = new SeriesDAO(DBConnection.getConnection());
            CategoryDAO categoryDAO = new CategoryDAO(DBConnection.getConnection());
            ChapterDAO chapterDAO = new ChapterDAO(DBConnection.getConnection());
            RatingDAO ratingDAO = new RatingDAO(DBConnection.getConnection());
            SeriesCategoriesDAO seriesCategoriesDAO = new SeriesCategoriesDAO(DBConnection.getConnection());
            UserDAO userDAO = new UserDAO(DBConnection.getConnection());

            List<SeriesInfoDTO> hotSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> weeklySeriesList = new ArrayList<>();
            List<SeriesInfoDTO> newReleaseSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> recentlyUpdatedSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> completedSeriesList = new ArrayList<>();
            List<CategoryInfoDTO> categoryList = new ArrayList<>();
            List<User>  userList = userDAO.selectTopUserPoints(10);
            for (Series series : seriesDAO.getTopRatedSeries(3)){
                SeriesInfoDTO seriesInfoDTO = setSeriesInfoDTO(series, categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
                hotSeriesList.add(seriesInfoDTO);
            }
            for (Series series : seriesDAO.getWeeklySeries(8)){
                SeriesInfoDTO seriesInfoDTO = setSeriesInfoDTO(series, categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
                weeklySeriesList.add(seriesInfoDTO);
                seriesInfoDTO.setAvgRating(series.getRating_points());
            }
            for (Series series : seriesDAO.getNewReleasedSeries(4)){
                SeriesInfoDTO seriesInfoDTO = setSeriesInfoDTO(series, categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
                seriesInfoDTO.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
                seriesInfoDTO.setAvgRating((double) Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10) /10);
                seriesInfoDTO.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));
                newReleaseSeriesList.add(seriesInfoDTO);
            }
            for (Series series : seriesDAO.getRecentlyUpdated(5)){
                SeriesInfoDTO seriesInfoDTO = setSeriesInfoDTO(series, categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
                seriesInfoDTO.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
                seriesInfoDTO.setAvgRating((double) Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10) /10);
                seriesInfoDTO.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));
                recentlyUpdatedSeriesList.add(seriesInfoDTO);
            }
            for (Series series : seriesDAO.getSeriesByStatus(6, "completed")){
                SeriesInfoDTO seriesInfoDTO = setSeriesInfoDTO(series, categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
                seriesInfoDTO.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
                seriesInfoDTO.setAvgRating((double) Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10) /10);
                seriesInfoDTO.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));
                completedSeriesList.add(seriesInfoDTO);
            }
            for (Category category : categoryDAO.getAll()){
                CategoryInfoDTO categoryInfoDTO = new CategoryInfoDTO();
                categoryInfoDTO.setCategoryId(category.getCategoryId());
                categoryInfoDTO.setName(category.getName());
                int totalSeries = seriesCategoriesDAO.countSeriesByCategoryId(category.getCategoryId());
                categoryInfoDTO.setTotalSeries(totalSeries);
                categoryList.add(categoryInfoDTO);
            }
            categoryList.sort((a, b) -> Integer.compare(b.getTotalSeries(), a.getTotalSeries()));

            request.setAttribute("hotSeriesList", hotSeriesList);
            request.setAttribute("weeklySeriesList", weeklySeriesList);
            request.setAttribute("newReleaseSeriesList", newReleaseSeriesList);
            request.setAttribute("recentlyUpdatedSeriesList", recentlyUpdatedSeriesList);
            request.setAttribute("completedSeriesList", completedSeriesList);
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("userList", userList);
            request.getRequestDispatcher("Homepage.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public SeriesInfoDTO setSeriesInfoDTO(Series series, List<Category> categories) {
        SeriesInfoDTO seriesInfoDTO = new SeriesInfoDTO();
        seriesInfoDTO.setSeriesId(series.getSeriesId());
        seriesInfoDTO.setTitle(series.getTitle());
        seriesInfoDTO.setDescription(series.getDescription());
        seriesInfoDTO.setCoverImgUrl(series.getCoverImgUrl());
        List<String> categoriesName = new ArrayList<>();
        for (Category category : categories) {
            categoriesName.add(category.getName());
        }
        seriesInfoDTO.setCategories(categoriesName);
        return seriesInfoDTO;
    }
}
