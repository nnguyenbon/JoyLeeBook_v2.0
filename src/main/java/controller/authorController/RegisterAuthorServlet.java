package controller.authorController;

import dao.UserDAO;
import db.DBConnection;
import model.Account;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet to handle author registration requests.
 * Allows a logged-in reader to register as an author.
 * Redirects to the author dashboard upon successful registration.
 * If the user is already an author, redirects to the author dashboard directly.
 */
@WebServlet(name = "RegisterAuthorServlet", value = "/author/*")
public class RegisterAuthorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        switch (path) {
            case "/register" -> RegsiterAuthor(request, response);
            case "/check"-> CheckRole(request, response);
        }
    }

    private void CheckRole(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Account account = AuthenticationUtils.getLoginedUser(request.getSession());
        boolean isAuthor = false;

        if (account != null) {
            try (Connection conn = DBConnection.getConnection()) {
                UserDAO userDAO = new UserDAO(conn);
                isAuthor = userDAO.isAuthor(account.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.getWriter().write("{\"isAuthor\": " + isAuthor + "}");
    }

    private void RegsiterAuthor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            if (!userDAO.isAuthor(user.getEmail())) {
                userDAO.updateUserRoleToAuthor(user.getUsername(), user.getFullName(), user.getEmail());
            }
            if (user.getRole().equals("reader")) {
                user.setRole("author");
                response.sendRedirect(request.getContextPath() + "/author");
            } else if (user.getRole().equals("author")) {
                user.setRole("reader");
                response.sendRedirect(request.getContextPath() + "/homepage");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    //    public void extractDataFromAuthorId(List<Series> seriesList, HttpServletRequest request) throws SQLException {
//        try {
//            LikeDAO likesDAO = new LikeDAO(connection);
//            RatingDAO ratingDAO = new RatingDAO(connection);
//            ChapterDAO chapterDAO = new ChapterDAO(connection);
//            int totalLike = 0;
//            double avgRating;
//            int totalRating = 0;
//            int ratingCount = 0;
//            for (Series series : seriesList) {
//                for (Chapter chapter : chapterDAO.findChapterBySeriesId(series.getSeriesId())) {
//                    totalLike += likesDAO.countByChapter(chapter.getChapterId());
//                }
//                totalRating += ratingDAO.getRatingSumBySeriesId(series.getSeriesId());
//                ratingCount += ratingDAO.getRatingCount(series.getSeriesId());
//            }
//            if (ratingCount > 0) {
//                avgRating = (double) Math.round(((double) totalRating / ratingCount) * 10) / 10;
//            } else {
//                avgRating = 0.0;
//            }
//            request.setAttribute("totalLike", totalLike);
//            request.setAttribute("avgRating", avgRating);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}

