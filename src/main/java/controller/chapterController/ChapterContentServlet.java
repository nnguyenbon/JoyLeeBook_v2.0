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
import model.Chapter;
import model.Comment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/chapter-content")
public class ChapterContentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String seriesIdParam = request.getParameter("seriesId");


        int seriesId = 0;
        if (seriesIdParam != null && !seriesIdParam.isEmpty()) {
            try {
                seriesId = Integer.parseInt(seriesIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid seriesId");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing seriesId");
            return;
        }




        try {
            Connection connection = DBConnection.getConnection();
            ChapterDAO chapterDAO = new ChapterDAO(connection);
            SeriesAuthorDAO  seriesAuthorDAO = new SeriesAuthorDAO(connection);
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            LikesDAO likesDAO = new LikesDAO(connection);
            CommentDAO commentDAO = new CommentDAO(connection);
            UserDAO userDAO = new UserDAO(connection);

            String chapterIdParam = request.getParameter("chapterId");
            int chapterId = (chapterIdParam != null && !chapterIdParam.isEmpty()) ? Integer.parseInt(chapterIdParam) : chapterDAO.getFirstChapterNumber(seriesId);

            Chapter chapter = chapterDAO.findById(chapterId);
            ChapterDetailDTO chapterDetailDTO = new ChapterDetailDTO();
            chapterDetailDTO.setSeriesId(seriesId);
            chapterDetailDTO.setChapterId(chapter.getChapterId());
            chapterDetailDTO.setTitle(chapter.getTitle());
            chapterDetailDTO.setContent(chapter.getContent());
            chapterDetailDTO.setChapterNumber(chapter.getChapterNumber());
            chapterDetailDTO.setAuthorsName(seriesAuthorDAO.authorsOfSeries(seriesId));
            chapterDetailDTO.setSeriesTitle(seriesDAO.findById(seriesId).getTitle());
            chapterDetailDTO.setTotalLike(likesDAO.countByChapter(chapter.getChapterId()));
            List<ChapterInfoDTO> chapterInfoDTOList = new ArrayList<>();
            for (Chapter chapterItem : chapterDAO.findChapterBySeriesId(seriesId)) {
                ChapterInfoDTO chapterInfoDTO = new ChapterInfoDTO();
                chapterInfoDTO.setChapterId(chapterItem.getChapterId());
                chapterInfoDTO.setTitle(chapterItem.getTitle());
                chapterInfoDTO.setChapterNumber(chapterItem.getChapterNumber());
                chapterInfoDTO.setUpdatedAt(calculateTimeAgo(chapterItem.getUpdatedAt()));
                chapterInfoDTO.setTotalLikes(likesDAO.countByChapter(chapterItem.getChapterId()));
                chapterInfoDTOList.add(chapterInfoDTO);
            }
            List<CommentDetailDTO>  commentDetailDTOList = new ArrayList<>();
            for (Comment comment : commentDAO.findByChapter(chapterId)) {
                CommentDetailDTO commentDetailDTO = new CommentDetailDTO();
                commentDetailDTO.setCommentId(comment.getCommentId());
                commentDetailDTO.setContent(comment.getContent());
                commentDetailDTO.setUserId(comment.getUserId());
                commentDetailDTO.setUpdateAt(calculateTimeAgo(comment.getUpdatedAt()));
                commentDetailDTO.setUsername(userDAO.findById(comment.getUserId()).getUsername());
                commentDetailDTOList.add(commentDetailDTO);
            }
            request.setAttribute("chapterDetailDTO", chapterDetailDTO);
            request.setAttribute("chapterInfoDTOList", chapterInfoDTOList);
            request.setAttribute("commentDetailDTOList", commentDetailDTOList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
    }

    private String calculateTimeAgo(LocalDateTime updatedAt) {
        Duration duration = Duration.between(updatedAt, LocalDateTime.now());

        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "just now";
        }
    }
}
