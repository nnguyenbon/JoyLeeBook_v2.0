package controller.seriesController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import services.like.LikeService;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/author")
public class AuthorDashboard extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;

        try {
            SeriesServices seriesServices = new SeriesServices();
            ChapterServices chapterServices = new ChapterServices();
            LikeService likeService = new LikeService();

            request.setAttribute("totalChapters", chapterServices.getCountMyChapterByUserId(userId, "approved"));
            request.setAttribute("pendingChapters", chapterServices.getCountMyChapterByUserId(userId, "pending"));
            request.setAttribute("totalLikes", likeService.countLikesOfAuthor(userId));

            request.setAttribute("mySeriesList", seriesServices.seriesFromAuthor(userId));
            request.setAttribute("pageTitle", "AuthorDashboard");
            request.setAttribute("contentPage", "/WEB-INF/views/author/AuthorDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}