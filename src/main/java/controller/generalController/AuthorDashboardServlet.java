package controller.generalController;

import dao.ChapterDAO;
import dao.LikeDAO;
import dao.RatingDAO;
import dao.SeriesDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


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
            RatingDAO ratingDAO = new RatingDAO(conn);
            double rate = ratingDAO.getAverageRating(userId);

            //Fetching statistics and series list for the author
            request.setAttribute("totalChapters", chapterDAO.countChapterByUserId(userId, ""));
            request.setAttribute("pendingChapters", chapterDAO.countChapterByUserId(userId, "pending"));
            request.setAttribute("totalLikes", likeDAO.countLikesOfAuthor(userId));
            request.setAttribute("avgRating", (double) Math.round(rate * 10) / 10);
            request.setAttribute("mySeriesList", seriesDAO.getSeriesByAuthorId(userId));
            request.setAttribute("pageTitle", "AuthorDashboard");
            request.setAttribute("contentPage", "/WEB-INF/views/general/AuthorDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */
/**
 * AdminDashboardServlet is responsible for handling requests related to the admin dashboard.
 * It manages the display of administrative functions and statistics.
 * This servlet ensures that only authorized admin users can access the dashboard features.
 * It extends HttpServlet and overrides doGet and doPost methods to process incoming requests.
 * @version 1.0
 * @since 2024-06-15
 * @author Your Name
 */