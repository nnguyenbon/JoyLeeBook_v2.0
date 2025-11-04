package controller.reactionController;

import dao.LikeDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Like;
import model.Rating;
import model.User;
import services.general.PointServices;
import services.series.RatingSeriesService;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet implementation class ReactionServlet
 * Handles user reactions such as likes and ratings.
 */
@WebServlet("/reaction/*")
public class ReactionServlet extends HttpServlet {
    /**
     * Handles POST requests for user reactions.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        //Determine the action based on the URL path
        switch (action) {
            case "/like":
                likeChapter(request, response);
                break;
            case "/rate":
                ratingSeries(request, response);
                break;
        }
    }

    /**
     * Handles GET requests for user reactions.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * Handles liking a chapter.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void likeChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        try {
            try {
                int userId = loginedUser != null ? loginedUser.getUserId() : 0;
                int chapterId = Integer.parseInt(request.getParameter("chapterId"));
                int newLikeCount = likeChapter(userId, chapterId);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\": true, \"newLikeCount\": " + newLikeCount + ", \"liked\": true }");
            } catch (Exception e) {
                request.setAttribute("error", "Could not insert like data. " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid userId/chapterId.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles rating a series.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void ratingSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedUser == null || loginedUser.getRole() == null || !loginedUser.getRole().equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "You must be logged in as a reader to rate a series."
                    }
                    """);
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        try {
            int userId = loginedUser.getUserId();
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            int ratingValue = Integer.parseInt(request.getParameter("rating"));

            Rating rating = new Rating();
            rating.setUserId(userId);
            rating.setSeriesId(seriesId);
            rating.setRatingValue(ratingValue);

            RatingSeriesService ratingService = new RatingSeriesService();
            ratingService.saveOrUpdateRating(rating);

            double avgRating = ratingService.getAverageRating(seriesId);
            int totalRatings = ratingService.getTotalRatings(seriesId);

            String json = String.format(
                    "{\"success\": true, \"rating\": %d, \"avgRating\": %.2f, \"totalRatings\": %d}",
                    ratingValue, avgRating, totalRatings
            );
            response.getWriter().write(json);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.getWriter().write("{\"success\": false}");
        }
    }

    /**
     * Inserts a like for a chapter by a user.
     * @param userId ID of the user liking the chapter.
     * @param chapterId ID of the chapter being liked.
     * @return The new total like count for the chapter.
     * @throws SQLException
     */
    public int likeChapter(int userId, int chapterId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()
        ) {
            LikeDAO likeDAO = new LikeDAO(connection);
            Like like = new Like();
            like.setUserId(userId);
            like.setChapterId(chapterId);
            if (likeDAO.isLikedByUser(like.getUserId(), like.getChapterId())) {
                return likeDAO.countByChapter(like.getChapterId());
            }
            likeDAO.insert(like);
            PointServices.trackAction(userId, 2, "Like new chapter", "like", likeDAO.findById(userId, chapterId).getChapterId());
            return likeDAO.countByChapter(like.getChapterId());
        } catch (Exception exception) {

        }
        return 0;
    }

}
