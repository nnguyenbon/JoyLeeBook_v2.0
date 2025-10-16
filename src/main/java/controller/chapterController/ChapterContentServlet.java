package controller.chapterController;

import dao.*;
import db.DBConnection;
import dto.chapter.ChapterDetailDTO;
import dto.chapter.ChapterInfoDTO;
import dto.general.CommentDetailDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.chapter.ChapterServices;
import services.general.CommentServices;
import services.like.LikeService;
import utils.ValidationInput;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/chapter-content")
public class ChapterContentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 0;

        try {
            ChapterServices chapterServices = new ChapterServices();
            CommentServices commentServices = new CommentServices();
            LikeService likeService = new LikeService();
            int userId = 10;
            String chapterIdParam = request.getParameter("chapterId");
            int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : chapterServices.getFirstChapterNumber(seriesId);

            request.setAttribute("chapterId", chapterId);
            request.setAttribute("chapterDetailDTO", chapterServices.buildChapterDetailDTO(chapterId));
            request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
            request.setAttribute("commentDetailDTOList", commentServices.commentsFromChapter(chapterId));
            request.setAttribute("liked", likeService.hasUserLiked(userId, chapterId));
            request.setAttribute("pageTitle","Chapter Content");
            request.setAttribute("contentPage", "/WEB-INF/views/chapter/ChapterContent.jsp");
            request.setAttribute("seriesId", seriesId);
            request.setAttribute("chapterId", chapterId);
            System.out.println("ChapterId = " + chapterId);
            System.out.println("SeriesId = " + seriesId);

//            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
    }
}
