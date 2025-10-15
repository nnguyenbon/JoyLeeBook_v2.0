package controller.chapterController;

import dao.ChapterDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;
import services.chapter.ChapterServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/navigate-chapter")
public class NavigateChapterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;

        int chapterNumber = ValidationInput.isPositiveInteger(request.getParameter("chapterNumber")) ? Integer.parseInt(request.getParameter("chapterNumber")) : 1;
        String action = request.getParameter("action");

        try {
            String redirectUrl = ChapterServices.getRedirectUrl(action, seriesId, chapterNumber);
            response.sendRedirect(redirectUrl);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
