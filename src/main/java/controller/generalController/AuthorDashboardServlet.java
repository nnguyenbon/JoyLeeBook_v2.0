package controller.generalController;

import dao.ChapterDAO;
import dao.LikeDAO;
import dao.SeriesDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import services.series.RatingSeriesService;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet implementation class AuthorDashboardServlet
 * This servlet handles the author dashboard page, displaying statistics and series information for the logged-in author.
 * It checks if the user is logged in and has the "author" role before fetching data from the database.
 * If the user is not logged in or does not have the correct role, they are redirected to the login page.
 * The servlet retrieves the total number of approved and pending chapters, total likes, average rating, and the list of series created by the author.
 * Finally, it forwards the request to the appropriate JSP page for rendering the dashboard.
 */
@WebServlet("/author")
public class AuthorDashboardServlet extends HttpServlet {
    /**
     * Handles the HTTP POST method.
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * Handles the HTTP GET method.
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId;
        if (user == null || !"author".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } else {
            userId = user.getUserId();
        }

        try (Connection conn = DBConnection.getConnection()) {
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            ChapterDAO chapterDAO = new  ChapterDAO(conn);
            LikeDAO likeDAO = new LikeDAO(conn);
            RatingSeriesService ratingService = new RatingSeriesService();
            //Fetching statistics and series list for the author
            request.setAttribute("totalChapters", chapterDAO.countChapterByUserId(userId, "approved"));
            request.setAttribute("pendingChapters", chapterDAO.countChapterByUserId(userId, "pending"));
            request.setAttribute("totalLikes", likeDAO.countLikesOfAuthor(userId));
            request.setAttribute("avgRating", ratingService.getAverageRatingOfAuthor(userId));
            request.setAttribute("mySeriesList", seriesDAO.getSeriesByAuthorId(userId));
            request.setAttribute("pageTitle", "AuthorDashboard");
            request.setAttribute("contentPage", "/WEB-INF/views/general/AuthorDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
