package controller.chapterController;

import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import services.chapter.ChapterManagementService;

import java.io.IOException;
import java.sql.Connection;

/**
 * DeleteChapterServlet handles requests to delete a chapter.
 * It supports displaying a confirmation page and processing the deletion.
 * <p>
 * URL: /delete-chapter
 * Methods: GET, POST
 * <p>
 * Query Parameters:
 * - id: The ID of the chapter to be deleted (required)
 * <p>
 * Session:
 * - Requires userId in session to identify the logged-in user
 * <p>
 * Forwards to:
 * - /WEB-INF/views/chapter/confirm-delete.jsp on GET request
 * - /my-chapters?deleted=1 on successful POST request
 * - /WEB-INF/views/error.jsp on error
 *
 * @author HaiDD-dev
 */
@WebServlet("/delete-chapter")
public class DeleteChapterServlet extends HttpServlet {

    /**
     * Handles GET requests to display the delete confirmation page.
     * Validates user session and permissions before displaying the confirmation.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Integer chapterId = parseIntOrNull(req.getParameter("id"));
        if (chapterId == null) {
            req.setAttribute("error", "Missing chapter id.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
            return;
        }

        Integer userId = getUserId(req.getSession());

        // testing
        // userId = 3;

        String role = getUserRole(req.getSession());

        // testing
        // role = "author";

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);

            var chapter = svc.getChapterById(chapterId);
            if (chapter == null) {
                req.setAttribute("error", "Chapter not found or already deleted.");
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
                return;
            }

            // check permission
            if (!svc.canDeleteChapter(userId, role, chapter.getSeriesId())) {
                req.setAttribute("error", "You do not have permission to delete this chapter.");
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("chapter", chapter);
            req.getRequestDispatcher("/WEB-INF/views/chapter/confirm-delete.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Unable to load delete confirmation.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }

    /**
     * Execute soft delete
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Integer chapterId = parseIntOrNull(req.getParameter("id"));
        if (chapterId == null) {
            req.setAttribute("error", "Missing chapter id.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
            return;
        }

        Integer userId = getUserId(req.getSession());

        // testing
        // userId = 3;

        String role = getUserRole(req.getSession());

        // testing
        // role = "author";

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);
            svc.deleteChapter(userId, role, chapterId);

            // redirect to author's chapter list with success message
            resp.sendRedirect(req.getContextPath() + "/my-chapters?deleted=1");
        } catch (IllegalAccessException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to delete chapter.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }

    /**
     * Utility methods
     */
    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Try to get userId from session.
     * Supports both "userId" attribute (Integer) and "authUser" attribute (User).
     * Returns null if not found or invalid.
     */
    private static Integer getUserId(HttpSession session) {
        Object u1 = session.getAttribute("userId");
        if (u1 instanceof Integer) return (Integer) u1;
        Object u2 = session.getAttribute("authUser");
        if (u2 instanceof User) return ((User) u2).getUserId();
        return null;
    }

    /**
     * Try to get user role from session.
     * Supports both "authUser" attribute (User) and "role" attribute (String).
     * Defaults to "reader" if not found or invalid.
     */
    private static String getUserRole(HttpSession session) {
        Object u2 = session.getAttribute("authUser");
        if (u2 instanceof User) {
            String role = ((User) u2).getRole();
            return role != null ? role : "reader";
        }
        Object r = session.getAttribute("role");
        return (r instanceof String) ? (String) r : "reader";
    }
}
