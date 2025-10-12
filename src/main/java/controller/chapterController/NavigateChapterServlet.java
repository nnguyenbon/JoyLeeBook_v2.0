package controller.chapterController;

import dao.ChapterDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/navigate-chapter")
public class NavigateChapterServlet extends HttpServlet {
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

        String chapterNumberParam = request.getParameter("chapterNumber");
        int chapterNumber = 0;
        if (chapterNumberParam != null && !chapterNumberParam.isEmpty()) {
            try {
                chapterNumber = Integer.parseInt(chapterNumberParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid chapterId");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing chapterId");
            return;
        }
        String action = request.getParameter("action");

        try {
            int newChapterId = 0;
            Chapter chapter = new Chapter();
            ChapterDAO chapterDAO = new ChapterDAO(DBConnection.getConnection());
            if (action.equals("next")) {
                chapter = chapterDAO.getNextChapter(seriesId, chapterNumber);
            } else if (action.equals("previous")){
                 chapter = chapterDAO.getPreviousChapter(seriesId, chapterNumber);
            }
            newChapterId = chapter.getChapterId();
            String redirectUrl = String.format("chapter-content?seriesId=%d&chapterId=%d", seriesId, newChapterId);
            response.sendRedirect(redirectUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
