package controller.chapterController;

import dao.SeriesAuthorDAO;
import dao.UserDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;
import model.Series;
import model.User;
import services.ChapterManagementService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * AddChapterServlet handles requests to add new chapters to a series.
 * It ensures that only authenticated users with the "author" role and
 * who are authors of the specific series can add chapters.
 * <p>
 * URL: /add-chapter
 * Methods: GET, POST
 * <p>
 * Session:
 * - Requires userId in session to identify the logged-in user
 * <p>
 * Forwards to:
 * - /WEB-INF/views/chapter/AddChapter.jsp on successful GET request for authors
 * - Redirects to /login if not logged in
 * - Sends 403 Forbidden if the user does not have author permissions or is not the author of the series
 * - Redirects to /manage-series?id={seriesId}&success=true on successful chapter creation
 * - Forwards back to AddChapter.jsp with error message on failure
 *
 * @author HaiDD-dev
 */
@WebServlet(name = "AddChapterServlet", value = "/add-chapter")
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
                UserDAO userDAO = new UserDAO(conn);
                SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);

                User currentUser = userDAO.findById(userId);

                if (currentUser == null || !"author".equals(currentUser.getRole()) || !seriesAuthorDAO.isUserAuthorOfSeries(userId, seriesId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to perform this action.");
                    return;
                }

                ChapterManagementService service = new ChapterManagementService(conn);
                Series series = service.getSeriesById(seriesId);
                req.setAttribute("series", series);
                req.getRequestDispatcher("/WEB-INF/views/chapter/AddChapter.jsp").forward(req, resp);

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Series ID.");
        }
    }

    /**
     * Handles POST requests to create a new chapter.
     * Validates user session and permissions before creating the chapter.
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
                UserDAO userDAO = new UserDAO(conn);
                SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);

                User currentUser = userDAO.findById(userId);

                if (currentUser == null || !"author".equals(currentUser.getRole()) || !seriesAuthorDAO.isUserAuthorOfSeries(userId, seriesId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to perform this action.");
                    return;
                }

                ChapterManagementService service = new ChapterManagementService(conn);
                Chapter newChapter = service.createChapter(userId, seriesId, title, content);

                if (newChapter != null) {
                    resp.sendRedirect(req.getContextPath() + "/manage-series?id=" + seriesId + "&success=true");
                } else {
                    req.setAttribute("error", "Failed to create new chapter.");
                    req.getRequestDispatcher("/WEB-INF/views/chapter/AddChapter.jsp").forward(req, resp);
                }

            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("error", "An error occurred: " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/chapter/AddChapter.jsp").forward(req, resp);
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Series ID.");
        }
    }
}