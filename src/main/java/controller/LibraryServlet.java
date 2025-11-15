package controller;

import dao.*;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SavedSeries;
import model.Series;
import model.User;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet implementation class LibraryServlet
 * Handles viewing and managing the user's library, including saved series and reading history.
 * Supports actions to save/unsave series, delete individual history items, and clear all history.
 * Requires user authentication for library access and modifications.
 */
@WebServlet("/library/*")
public class LibraryServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LibraryServlet.class.getName()); //Logger for logging errors and information

    /**
     * Handles POST requests for saving/unsaving series and managing reading history.
     *
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action.");
            return;
        }
        //Determine action based on path info
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

    /**
     * Handles GET requests to view the user's library.
     *
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
        viewLibrary(request, response);
    }

    /**
     * Displays the user's library, including saved series and reading history.
     *
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
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

        try (Connection conn = DBConnection.getConnection()) {
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            ReadingHistoryDAO readingHistoryDAO = new ReadingHistoryDAO(conn);
            List<Series> savedSeriesList = new ArrayList<>();
            for (Series series : seriesDAO.getSeriesByUserId(userId)) {
                savedSeriesList.add(buildSeries(conn, series));
            }

            request.setAttribute("savedSeries", savedSeriesList);
            request.setAttribute("historyChapters", readingHistoryDAO.getReadingHistoryChapters(userId, 0, Integer.MAX_VALUE, null));

            request.setAttribute("pageTitle", "Library");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Library.jsp");
            request.setAttribute("mode", mode);

            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves or unsaves a series for the logged-in user.
     *
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws IOException if an I/O error occurs
     */
    private void saveSeries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null || !loginedUser.getRole().equals("reader")) {
            //User is not logged in or not a reader
            response.getWriter().print("""
                        {
                        "success": false,
                        "message": "You need to login to save series."
                        }
                    """);
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            SavedSeriesDAO savedSeriesDAO = new SavedSeriesDAO(conn);

            int userId = loginedUser.getUserId();
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String action = request.getParameter("type");

            boolean saved;
            String message;
            SavedSeries savedSeries = new SavedSeries();
            savedSeries.setUserId(userId);
            savedSeries.setSeriesId(seriesId);
            if ("save".equalsIgnoreCase(action)) {
                savedSeriesDAO.insert(savedSeries);
                saved = true;
                message = "Your series has been saved successfully";
            } else {
                savedSeriesDAO.delete(savedSeries.getUserId(), savedSeries.getSeriesId());
                saved = false;
                message = "Your series has been unsaved successfully";
            }
            //Return JSON response indicating success
            response.getWriter().print("{\"success\": true, \"saved\": " + saved + ", \"message\": \"" + message + "\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().write("{\"success\": false}");
        }
    }

    /**
     * Deletes a specific reading history item for the logged-in user.
     *
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void deleteHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int userId = loginedUser.getUserId();

        //Get chapterId parameter and validate
        Integer chapterId = parseIntOrNull(request.getParameter("chapterId"));
        if (chapterId == null) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        //Delete the reading history item
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

    /**
     * Clears all reading history for the logged-in user.
     *
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void clearAllHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int userId = loginedUser.getUserId();

        //Clear all reading history for the user
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

    /**
     * Enriches a Series object with additional related data from the database.
     *
     * @param conn   the database connection to use for queries
     * @param series the Series object to enrich with additional data, or null
     * @return the enriched Series object, or null if the input was null
     * @throws SQLException if a database access error occurs
     */
    private static Series buildSeries(Connection conn, Series series) throws SQLException {
        ChapterDAO chapterDAO = new ChapterDAO(conn);
        RatingDAO ratingDAO = new RatingDAO(conn);
        CategoryDAO categoryDAO = new CategoryDAO(conn);
        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
        UserDAO userDAO = new UserDAO(conn);
        series.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
        series.setTotalRating(ratingDAO.getRatingCount(series.getSeriesId()));
        series.setCategoryList(categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
        series.setAuthorList(userDAO.getAuthorList(series.getSeriesId()));
        series.setAvgRating(Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10.0) / 10.0);
        return series;
    }

    /**
     * Parses a string into an Integer, returning null if parsing fails.
     *
     * @param s the string to parse
     * @return the parsed Integer, or null if parsing fails
     */
    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}