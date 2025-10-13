package controller.chapterController;

import db.DBConnection;
import services.MyChapterService;
import services.MyChapterService.PagedResult;
import dto.chapter.ChapterItemDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MyChapterListServlet handles requests to view the list of chapters
 * authored by the logged-in user or their reading history.
 * It supports pagination, searching, and filtering by status.
 * <p>
 * URL: /my-chapters
 * Method: GET
 * <p>
 * Query Parameters:
 * - mode: "author" (default) to view authored chapters, "history" to view reading history
 * - page: page number (default 1)
 * - size: number of items per page (default 10)
 * - q: search keyword (optional)
 * - status: filter by chapter status (optional, only for author mode)
 * <p>
 * Session:
 * - Requires userId in session to identify the logged-in user
 * <p>
 * Forwards to:
 * - /WEB-INF/views/MyChapters.jsp on success
 * - /WEB-INF/views/error.jsp on error
 *
 * @author HaiDD-dev
 */
@WebServlet("/my-chapters")
public class MyChapterListServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(MyChapterListServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // get userId from session
        Integer userId = (Integer) req.getSession().getAttribute("userId");

        // testing
        // userId = 3;

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String mode = req.getParameter("mode");            // "author" | "history" (default author)
        if (mode == null || mode.isBlank()) mode = "author";

        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), 10);
        String keyword = trimToNull(req.getParameter("q"));
        String status = trimToNull(req.getParameter("status")); // only for author mode

        try (Connection conn = DBConnection.getConnection()) {
            MyChapterService service = new MyChapterService(conn);

            PagedResult<ChapterItemDTO> result;
            if ("history".equalsIgnoreCase(mode)) {
                result = service.getReadingHistoryChapters(userId, page, size, keyword);
            } else {
                result = service.getAuthoredChapters(userId, page, size, status, keyword);
            }

            req.setAttribute("mode", mode);
            req.setAttribute("result", result);
            req.setAttribute("items", result.getItems());
            req.setAttribute("page", result.getPage());
            req.setAttribute("totalPages", result.getTotalPages());
            req.setAttribute("total", result.getTotal());
            req.setAttribute("q", keyword);
            req.setAttribute("status", status);

            // forward to JSP
            req.getRequestDispatcher("/WEB-INF/views/chapter/MyChapters.jsp").forward(req, resp);

        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error loading My Chapter List", e);
            req.setAttribute("error", "Unable to load your chapters.");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
