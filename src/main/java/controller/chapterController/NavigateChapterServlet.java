package controller.chapterController;

import dao.ChapterDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/navigate-chapter")
public class NavigateChapterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String seriesIdParam = request.getParameter("seriesId");


        int seriesId = ValidationInput.isPositiveInteger(seriesIdParam) ? Integer.parseInt(seriesIdParam) : 1;

        String chapterNumberParam = request.getParameter("chapterNumber");
        int chapterNumber = ValidationInput.isPositiveInteger(chapterNumberParam) ? Integer.parseInt(chapterNumberParam) : 1;
        String action = request.getParameter("action");

        try {
            String redirectUrl = getRedirectUrl(action, seriesId, chapterNumber);
            response.sendRedirect(redirectUrl);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getRedirectUrl(String action, int seriesId, int chapterNumber) throws SQLException, ClassNotFoundException {
        Chapter chapter = new Chapter();
        ChapterDAO chapterDAO = new ChapterDAO(DBConnection.getConnection());
        if (action.equals("next")) {
            chapter = chapterDAO.getNextChapter(seriesId, chapterNumber);
        } else if (action.equals("previous")){
             chapter = chapterDAO.getPreviousChapter(seriesId, chapterNumber);
        }
        return String.format("chapter-content?seriesId=%d&chapterId=%d", seriesId, chapter.getChapterId());
    }
}
