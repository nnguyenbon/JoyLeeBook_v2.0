package controller.chapterController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import services.general.CommentServices;
import services.chapter.LikeChapterService;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/chapter-content")
public class ChapterContentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 0;

        try {
            ChapterServices chapterServices = new ChapterServices();
            CommentServices commentServices = new CommentServices();
            LikeChapterService likeService = new LikeChapterService();
            int userId = 10;
            String chapterIdParam = request.getParameter("chapterId");
            int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : chapterServices.getFirstChapterNumber(seriesId);

            request.setAttribute("chapterDetailDTO", chapterServices.buildChapterDetailDTO(chapterId));
            request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
            request.setAttribute("commentDetailDTOList", commentServices.commentsFromChapter(chapterId));
            request.setAttribute("seriesId", seriesId);
            request.setAttribute("chapterId", chapterId);

            request.setAttribute("liked", likeService.hasUserLiked(userId, chapterId));
            request.setAttribute("pageTitle","Chapter Content");
            request.setAttribute("contentPage", "/WEB-INF/views/chapter/ChapterContent.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
