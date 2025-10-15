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
import services.chapter.ChapterServices;
import services.general.CommentServices;
import utils.ValidationInput;

import java.io.IOException;
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
            String chapterIdParam = request.getParameter("chapterId");
            int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : chapterServices.getFirstChapterNumber(seriesId);


            request.setAttribute("chapterDetailDTO", chapterServices.buildChapterDetailDTO(chapterId));
            request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
            request.setAttribute("commentDetailDTOList", commentServices.commentsFromChapter(chapterId));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
    }
}
