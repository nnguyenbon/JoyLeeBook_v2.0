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
        String seriesIdParam = request.getParameter("seriesId");
        int seriesId = ValidationInput.isPositiveInteger(seriesIdParam) ? Integer.parseInt(seriesIdParam) : 0;

        try {
            Connection connection = DBConnection.getConnection();
            ChapterDAO chapterDAO = new ChapterDAO(connection);
            CommentDAO commentDAO = new CommentDAO(connection);


            String chapterIdParam = request.getParameter("chapterId");
            int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : chapterDAO.getFirstChapterNumber(seriesId);

            ChapterServices chapterServices = new ChapterServices();
            ChapterDetailDTO chapterDetailDTO = chapterServices.buildChapterDetailDTO(chapterDAO.findById(chapterId), connection);
            List<ChapterInfoDTO> chapterInfoDTOList = chapterServices.buildChapterInfoDTOList(chapterDAO.findChapterBySeriesId(seriesId), connection);

            CommentServices commentServices = new CommentServices();
            List<CommentDetailDTO>  commentDetailDTOList = commentServices.buildCommentDetailDTOList(commentDAO.findByChapter(chapterId), connection);

            request.setAttribute("chapterDetailDTO", chapterDetailDTO);
            request.setAttribute("chapterInfoDTOList", chapterInfoDTOList);
            request.setAttribute("commentDetailDTOList", commentDetailDTOList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
    }
}
