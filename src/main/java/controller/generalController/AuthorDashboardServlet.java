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

@WebServlet("/author")
public class AuthorDashboardServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

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
