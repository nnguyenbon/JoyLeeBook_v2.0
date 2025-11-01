package controller.generalController;

import dao.*;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.UserServices;
import services.category.CategoryServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet ("/homepage")
public class HomepageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            UserServices userServices = new UserServices();
            CategoryServices categoryServices = new CategoryServices();

            SeriesDAO seriesDAO = new SeriesDAO(conn);
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            request.setAttribute("hotSeriesList", seriesDAO.getTopRatedSeries(3));
            request.setAttribute("weeklySeriesList", seriesDAO.getWeeklySeries(8));
            request.setAttribute("newReleaseSeriesList", seriesDAO.getNewReleasedSeries(4));
            request.setAttribute("recentlyUpdatedSeriesList", seriesDAO.getRecentlyUpdated(6));
            request.setAttribute("completedSeriesList", seriesDAO.getSeriesByStatus(6, "completed"));
            request.setAttribute("categoryList", categoryDAO.getCategoryTop(6));
            request.setAttribute("userList",  userDAO.selectTopUserPoints(8));

            request.setAttribute("pageTitle", "JoyLeeBook");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Homepage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
