package controller.generalController;

import dao.*;
import db.DBConnection;
import dto.category.CategoryInfoDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Series;
import model.User;
import services.series.SeriesServices;

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
            SeriesCategoriesDAO seriesCategoriesDAO = new SeriesCategoriesDAO(DBConnection.getConnection());
            UserDAO userDAO = new UserDAO(DBConnection.getConnection());

            List<SeriesInfoDTO> hotSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> weeklySeriesList = new ArrayList<>();
            List<SeriesInfoDTO> newReleaseSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> recentlyUpdatedSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> completedSeriesList = new ArrayList<>();
            List<CategoryInfoDTO> categoryList = new ArrayList<>();
            List<User>  userList = userDAO.selectTopUserPoints(8);
            SeriesServices seriesServices = new SeriesServices(DBConnection.getConnection());
            for (Series series : seriesDAO.getTopRatedSeries(3)){
                hotSeriesList.add(seriesServices.buildSeriesInfoDTO(series));
            }
            for (Series series : seriesDAO.getWeeklySeries(8)){
                SeriesInfoDTO seriesInfoDTO = seriesServices.buildSeriesInfoDTO(series);
                weeklySeriesList.add(seriesInfoDTO);
                seriesInfoDTO.setAvgRating(series.getRating_points());
            }
            for (Series series : seriesDAO.getNewReleasedSeries(4)){
                newReleaseSeriesList.add(seriesServices.buildSeriesInfoDTO(series));
            }
            for (Series series : seriesDAO.getRecentlyUpdated(6)){
                recentlyUpdatedSeriesList.add(seriesServices.buildSeriesInfoDTO(series));
            }
            for (Series series : seriesDAO.getSeriesByStatus(6, "completed")){
                completedSeriesList.add(seriesServices.buildSeriesInfoDTO(series));
            }
            for (Category category : categoryDAO.getAll()){
                CategoryInfoDTO categoryInfoDTO = new CategoryInfoDTO();
                categoryInfoDTO.setCategoryId(category.getCategoryId());
                categoryInfoDTO.setName(category.getName());
                int totalSeries = seriesCategoriesDAO.countSeriesByCategoryId(category.getCategoryId());
                categoryInfoDTO.setTotalSeries(totalSeries);
                categoryList.add(categoryInfoDTO);
            }

            request.setAttribute("hotSeriesList", hotSeriesList);
            request.setAttribute("weeklySeriesList", weeklySeriesList);
            request.setAttribute("newReleaseSeriesList", newReleaseSeriesList);
            request.setAttribute("recentlyUpdatedSeriesList", recentlyUpdatedSeriesList);
            request.setAttribute("completedSeriesList", completedSeriesList);
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("userList", userList);

            request.setAttribute("pageTitle", "JoyLeeBook");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Homepage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
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
