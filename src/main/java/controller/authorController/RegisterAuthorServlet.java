package controller.authorController;

import dao.UserDAO;
import db.DBConnection;
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
@WebServlet(name = "RegisterAuthorServlet", value = "/register-author")
public class RegisterAuthorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());

        // thêm badge cho user nếu register thành công

        try (Connection conn = DBConnection.getConnection()) {
            //If user is a reader, attempt to register as author
            if (user != null && "reader".equals(user.getRole())) {
                UserDAO userDAO = new UserDAO(conn);
                boolean isAuthor = userDAO.isAuthor(user.getEmail()) ? true : userDAO.updateUserRoleToAuthor(user.getUsername(), user.getFullName(), user.getEmail());
                if (isAuthor) {
                    user.setRole("author");
                    response.sendRedirect(request.getContextPath() + "/author");
                } else {
                    response.sendRedirect(request.getContextPath() + "/error/error.jsp");
                }
            } else if ("author".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/author");
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

