package controller.generalController;

import dao.ReadingHistoryDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SavedSeries;
import model.User;
import services.chapter.ChapterServices;
import services.series.SavedSeriesService;

import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/library/*")
public class LibraryServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LibraryServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action.");
            return;
        }
        switch (action) {
            case "/save":
                saveSeries(request, response);
                break;
            case "/deleteHistory":
                deleteHistory(request, response);
                break;
            case "/clearHistory":
                clearAllHistory(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
        viewLibrary(request, response);
    }

    private void viewLibrary(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null || loginedUser.getRole() == null || !loginedUser.getRole().equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Please login to view yours library");
            return;
        }
        int userId = loginedUser.getUserId();

        String mode = request.getParameter("mode");
        if (mode == null || (!mode.equals("saved") && !mode.equals("history"))) {
            mode = "saved";
        }

        try {
            ChapterServices chapterServices = new ChapterServices();
//            SeriesServices seriesServices = new SeriesServices();

//            request.setAttribute("savedSeries", seriesServices.savedSeriesFromUser(userId));
            request.setAttribute("historyChapters", chapterServices.historyChaptersFromUser(userId, 0, Integer.MAX_VALUE, null));

            request.setAttribute("pageTitle", "Library");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Library.jsp");
            request.setAttribute("mode", mode);

            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSeries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null || !loginedUser.getRole().equals("reader")) {
            response.getWriter().print("""
                        {
                        "success": false,
                        "message": "You need to login to save series."
                        }
                    """);
            return;
        }
        try {
            SavedSeriesService saveSeriesService = new SavedSeriesService();

            int userId = loginedUser.getUserId();
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String action = request.getParameter("type");

            boolean saved;
            String message;
            SavedSeries savedSeries = new SavedSeries();
            savedSeries.setUserId(userId);
            savedSeries.setSeriesId(seriesId);
            if ("save".equalsIgnoreCase(action)) {
                saveSeriesService.saveSeries(savedSeries);
                saved = true;
                message = "Your series has been saved successfully";
            } else {
                saveSeriesService.unSaveSeries(savedSeries);
                saved = false;
                message = "Your series has been unsaved successfully";

            }
            response.getWriter().print("{\"success\": true, \"saved\": " + saved + ", \"message\": \"" + message + "\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().write("{\"success\": false}");
        }
    }

    private void deleteHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int userId = loginedUser.getUserId();

        Integer chapterId = parseIntOrNull(request.getParameter("chapterId"));
        if (chapterId == null) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ReadingHistoryDAO dao = new ReadingHistoryDAO(conn);
            dao.delete(userId, chapterId);
            response.sendRedirect(request.getContextPath() + "/library?action=view&mode=history&deleted=true");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error deleting reading history item", e);
            request.setAttribute("error", "Unable to delete history item.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void clearAllHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int userId = loginedUser.getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            ReadingHistoryDAO dao = new ReadingHistoryDAO(conn);
            dao.deleteByUserId(userId);
            response.sendRedirect(request.getContextPath() + "/library?action=view&mode=history&cleared=true");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error clearing reading history", e);
            request.setAttribute("error", "Unable to clear history.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}