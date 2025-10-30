package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import services.chapter.ChapterServices;
import services.chapter.LikeServices;
import services.series.RatingSeriesService;
import services.series.SeriesServices;
import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;
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

        try {
            SeriesServices seriesServices = new SeriesServices();
            ChapterServices chapterServices = new ChapterServices();
            LikeServices likeService = new LikeServices();
            RatingSeriesService ratingService = new RatingSeriesService();
            request.setAttribute("totalChapters", chapterServices.getCountMyChapterByUserId(userId, "approved"));
            request.setAttribute("pendingChapters", chapterServices.getCountMyChapterByUserId(userId, "pending"));
            request.setAttribute("totalLikes", likeService.countLikesOfAuthor(userId));
            request.setAttribute("avgRating", ratingService.getAverageRatingOfAuthor(userId));
            request.setAttribute("mySeriesList", seriesServices.seriesFromAuthor(userId));
            request.setAttribute("pageTitle", "AuthorDashboard");
            request.setAttribute("contentPage", "/WEB-INF/views/general/AuthorDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
