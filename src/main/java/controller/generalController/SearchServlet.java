package controller.generalController;

import dao.*;
import db.DBConnection;
import dto.author.AuthorItemDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Series;
import model.User;
import services.series.SeriesService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }

        String searchType = request.getParameter("searchType");
        boolean isAjaxRequest = searchType != null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<SeriesInfoDTO> weeklySeriesList = new ArrayList<>();
        try {
            Connection connection = DBConnection.getConnection();
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            CategoryDAO categoryDAO = new CategoryDAO(connection);
            UserDAO userDAO = new UserDAO(connection);
            SeriesService seriesService = new SeriesService(connection);
            List<User>  userList = userDAO.selectTopUserPoints(8);
            if ("title".equals(searchType) || searchType == null) {
                List<SeriesInfoDTO> seriesInfoDTOList = new ArrayList<>();
                for (Series series : seriesDAO.findByName(keyword)) {
                    seriesInfoDTOList.add(seriesService.buildSeriesInfoDTO(series));
                }
                request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);

                if (isAjaxRequest && "title".equals(searchType)) {
                    request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchTitleView.jsp").forward(request, response);
                    return;
                }

            } else if ("author".equals(searchType)) {
                List<AuthorItemDTO> authorItemDTOList = new ArrayList<>();
                for (User author : userDAO.findByName(keyword)) {
                    AuthorItemDTO authorItemDTO = new AuthorItemDTO();
                    authorItemDTO.setAuthorId(author.getUserId());
                    authorItemDTO.setUserName(author.getUsername());
                    authorItemDTO.setTotalChapters(seriesDAO.getSeriesByAuthorId(author.getUserId()).size());
                    authorItemDTOList.add(authorItemDTO);
                }
                request.setAttribute("authorItemDTOList", authorItemDTOList);


                if (isAjaxRequest) {
                    request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchAuthorView.jsp").forward(request, response);
                    return;
                }
            }

            for (Series series : seriesDAO.getWeeklySeries(8)){
                SeriesInfoDTO seriesInfoDTO = setSeriesInfoDTO(series, categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
                weeklySeriesList.add(seriesInfoDTO);
                seriesInfoDTO.setAvgRating(series.getRating_points());
            }
            request.setAttribute("weeklySeriesList", weeklySeriesList);
            request.setAttribute("userList", userList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("searchType", searchType != null ? searchType : "title");
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/WEB-INF/views/general/SearchPage.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

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

