package controller.chapterController;

import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Chapter;
import model.Series;
import services.chapter.ChapterManagementService;

import java.io.IOException;
import java.sql.Connection;

/**
 * UpdateChapterServlet handles requests to update an existing chapter.
 * It supports displaying the edit form and processing the form submission.
 * <p>
 * URL: /update-chapter
 * Methods: GET, POST
 * <p>
 * Query Parameters:
 * - id: The ID of the chapter to be updated (required)
 * <p>
 * Session:
 * - Requires userId in session to identify the logged-in user
 * <p>
 * Forwards to:
 * - /WEB-INF/views/chapter/edit-chapter.jsp on GET request
 * - /chapter?id={chapterId}&updated=true on successful POST request
 * - /WEB-INF/views/error.jsp on error
 *
 * @author HaiDD-dev
 */
@WebServlet("/update-chapter")
public class UpdateChapterServlet extends HttpServlet {

    /**
     * Handles GET requests to display the edit chapter form.
     * Validates user session and permissions before displaying the form.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Integer userId = (Integer) req.getSession().getAttribute("userId");

        // testing
        // userId = 3;

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            req.setAttribute("error", "Missing chapter id.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);

            int chapterId = Integer.parseInt(idStr);
            Chapter chapter = svc.getChapterById(chapterId);
            if (chapter == null) {
                req.setAttribute("error", "Chapter not found.");
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
                return;
            }

            // check permission
            int seriesId = chapter.getSeriesId();
            if (new dao.SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                req.setAttribute("error", "You do not have permission to edit this chapter.");
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
                return;
            }

            Series series = svc.getSeriesById(seriesId);

            req.setAttribute("series", series);
            req.setAttribute("chapter", chapter);
            req.getRequestDispatcher("/WEB-INF/views/chapter/edit-chapter.jsp").forward(req, resp);

        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("error", "Unable to load chapter for editing. " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }

    /**
     * Handles POST requests to process the edit chapter form submission.
     * Validates user session and permissions, then updates the chapter.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Integer userId = (Integer) req.getSession().getAttribute("userId");

        // testing
        // userId = 3;

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);

            int chapterId = Integer.parseInt(req.getParameter("id"));
            String title = trim(req.getParameter("title"));
            String content = req.getParameter("content");
            String numberStr = req.getParameter("chapterNumber");
            Integer chapterNumber = (numberStr == null || numberStr.isBlank()) ? null : Integer.parseInt(numberStr);
            String status = trim(req.getParameter("status"));

            Chapter updated = svc.updateChapter(userId, chapterId, title, content, chapterNumber, status);

            // not done yet
            // redirect to view page
            resp.sendRedirect(req.getContextPath() + "/chapter?id=" + updated.getChapterId() + "&updated=true");

        } catch (IllegalAccessException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid chapter id or number.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to update chapter: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }

    /**
     * Utility to trim a string and convert empty to null
     */
    private static String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}