package controller.generalController;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.UserServices;
import services.category.CategoryServices;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet ("/homepage")
public class HomepageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserServices userServices = new UserServices();
//            SeriesServices seriesServices = new SeriesServices();
//            CategoryServices categoryServices = new CategoryServices();
//
//            request.setAttribute("hotSeriesList", seriesServices.hotSeriesList(3));
//
//            request.setAttribute("weeklySeriesList", seriesServices.weeklySeriesList(8));
//
//            request.setAttribute("newReleaseSeriesList", seriesServices.newReleaseSeries(4));
//            request.setAttribute("recentlyUpdatedSeriesList", seriesServices.recentlyUpdatedSeries(6));
//            request.setAttribute("completedSeriesList", seriesServices.completedSeries(6, "completed"));
//            request.setAttribute("categoryList", categoryServices.topCategories(6));

            request.setAttribute("userList", userServices.topUsersPoints(8));

            request.setAttribute("pageTitle", "JoyLeeBook");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Homepage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
