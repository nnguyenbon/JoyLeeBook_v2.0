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
import services.account.UserServices;
import services.general.SearchServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String statusParam = request.getParameter("status");
        String genresParam = request.getParameter("genres");
        String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword");
        String searchType = request.getParameter("searchType");
        try {
            SeriesServices seriesServices = new SeriesServices();
            UserServices userServices = new UserServices();
            SearchServices searchServices = new SearchServices();

            if (searchServices.handleSearchByType(searchType, keyword, searchType != null, DBConnection.getConnection(), request, response)) {
                return;
            }

            request.setAttribute("weeklySeriesList", seriesServices.weeklySeriesList(8));
            request.setAttribute("userList", userServices.topUsersPoints(8));
            request.setAttribute("statusParam", searchServices.extractParameters(statusParam));
            request.setAttribute("genresParam", searchServices.extractParameters(genresParam));
            request.setAttribute("searchType", searchType != null ? searchType : "title");
            request.setAttribute("keyword", keyword);
            request.getRequestDispatcher("/WEB-INF/views/general/SearchPage.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}

