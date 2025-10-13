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
import model.User;
import services.category.CategoryServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet ("/homepage")
public class HomepageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            SeriesDAO seriesDAO = new SeriesDAO(DBConnection.getConnection());
            CategoryDAO categoryDAO = new CategoryDAO(DBConnection.getConnection());
            UserDAO userDAO = new UserDAO(DBConnection.getConnection());

            List<SeriesInfoDTO> hotSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> weeklySeriesList = new ArrayList<>();
            List<SeriesInfoDTO> newReleaseSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> recentlyUpdatedSeriesList = new ArrayList<>();
            List<SeriesInfoDTO> completedSeriesList = new ArrayList<>();
            List<CategoryInfoDTO> categoryList = new ArrayList<>();
            List<User>  userList = userDAO.selectTopUserPoints(8);

            CategoryServices categoryServices = new CategoryServices();
            List<CategoryInfoDTO> categoryList = categoryServices.buildCategoryInfoDTOList(categoryDAO.getAll());

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
}
