package controller.generalController;

import dao.*;
import db.DBConnection;

import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.User;
import services.general.SearchServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private static boolean isFirstDirect = true;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String statusParam = request.getParameter("status");
        String genresParam = request.getParameter("genres");
        List<String> statuses = (statusParam != null && !statusParam.isEmpty())
                ? List.of(statusParam.split(","))
                : new ArrayList<>();

        List<String> genres = (genresParam != null && !genresParam.isEmpty())
                ? List.of(genresParam.split(","))
                : new ArrayList<>();
        String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword");
        String searchType = request.getParameter("searchType");
        boolean isAjaxRequest = searchType != null;

            try {
                Connection connection = DBConnection.getConnection();
                SeriesDAO seriesDAO = new SeriesDAO(connection);
                UserDAO userDAO = new UserDAO(connection);

                SeriesServices seriesServices = new SeriesServices(connection);
                List<SeriesInfoDTO> weeklySeriesList = seriesServices.buildSeriesInfoDTOList(seriesDAO.getWeeklySeries(8));

                List<User> userList = userDAO.selectTopUserPoints(8);

                SearchServices searchServices = new SearchServices();
                if (searchServices.handleSearchByType(searchType, keyword, isAjaxRequest, connection, request, response)){
                    return;
                }

                request.setAttribute("weeklySeriesList", weeklySeriesList);
                request.setAttribute("userList", userList);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            request.setAttribute("statusParam", statuses);
            request.setAttribute("genresParam", genres);
            request.setAttribute("searchType", searchType != null ? searchType : "title");
            request.setAttribute("keyword", keyword);
            request.getRequestDispatcher("/WEB-INF/views/general/SearchPage.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}

