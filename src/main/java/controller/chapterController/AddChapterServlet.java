package controller.chapterController;

import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Chapter;
import model.Series;
import services.ChapterManagementService;

import java.io.IOException;
import java.sql.Connection;

/**
 * AddChapterServlet handles requests to add a new chapter to a series.
 * It supports displaying the form for adding a chapter and processing the form submission.
 * <p>
 * URL: /add-chapter
 * Methods: GET, POST
 * <p>
 * Query Parameters:
 * - seriesId: The ID of the series to which the chapter will be added (required)
 * <p>
 * Session:
 * - Requires userId in session to identify the logged-in user
 * <p>
 * Forwards to:
 * - /WEB-INF/views/add-chapter.jsp on GET request
 * - /manage-series?id={seriesId}&success=true on successful POST request
 * - /WEB-INF/views/error.jsp on error
 *
 * @author HaiDD-dev
 */
@WebServlet("/add-chapter")
public class AddChapterServlet extends HttpServlet {

    /**
     * Handles GET requests to display the add chapter form.
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

        try {
            int seriesId = Integer.parseInt(req.getParameter("seriesId"));

            try (Connection conn = DBConnection.getConnection()) {
                ChapterManagementService service = new ChapterManagementService(conn);

                // check if user is author of the series
                if (new dao.SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                    req.setAttribute("error", "You do not have permission to access this page.");
                    req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
                    return;
                }

                // get series details
                Series series = service.getSeriesById(seriesId);
                req.setAttribute("series", series);
                req.getRequestDispatcher("/WEB-INF/views/chapter/add-chapter.jsp").forward(req, resp);

            } catch (Exception e) {
                System.out.println("series " + e.getMessage());
                req.setAttribute("error", "Could not load series data. " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            System.out.println("number " + e.getMessage());
            req.setAttribute("error", "Invalid Series ID.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }

    /**
     * Handles POST requests to process the add chapter form submission.
     * Validates user session and permissions, then creates the new chapter.
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

        try {
            int seriesId = Integer.parseInt(req.getParameter("seriesId"));
            String title = req.getParameter("title");
            String content = req.getParameter("content");

            try (Connection conn = DBConnection.getConnection()) {
                ChapterManagementService service = new ChapterManagementService(conn);
                Chapter newChapter = service.createChapter(userId, seriesId, title, content);

                if (newChapter != null) {
                    // forward to manage series page with success message
                    resp.sendRedirect(req.getContextPath() + "/manage-series?id=" + seriesId + "&success=true");
                } else {
                    req.setAttribute("error", "Failed to create new chapter.");
                    req.getRequestDispatcher("/WEB-INF/views/chapter/add-chapter.jsp").forward(req, resp);
                }

            } catch (IllegalAccessException e) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                req.setAttribute("error", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
            } catch (Exception e) {
                req.setAttribute("error", "An error occurred: " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/error/add-chapter.jsp").forward(req, resp);
            }

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid Series ID.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}