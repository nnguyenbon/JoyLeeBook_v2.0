package controller.reactionController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Rating;
import model.User;
import services.chapter.LikeServices;
import services.series.RatingSeriesService;

import java.io.IOException;

@WebServlet("/reaction")
public class ReactionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "like":
                likeChapter(request,response);
                break;
            case "rate":
                ratingSeries(request,response);
                break;
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    private void likeChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginedUser");
        if (user == null || user.getRole() == null || !user.getRole().equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Please login to like chapter");
            return;
        }
        try {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int chapterId = Integer.parseInt(request.getParameter("chapterId"));
                LikeServices likeService = new LikeServices();
                int newLikeCount = likeService.likeChapter(userId, chapterId);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\": true, \"newLikeCount\": " + newLikeCount + ", \"liked\": true }");
            }catch (Exception e) {
//                System.out.println("series " + e.getMessage());
                request.setAttribute("error", "Could not insert like data. " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
//            System.out.println("number " + e.getMessage());
            request.setAttribute("error", "Invalid userId/chapterId.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void ratingSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginedUser");
        if (user == null || user.getRole() == null || !user.getRole().equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Please login to rating series");
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
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
            e.printStackTrace();
            response.getWriter().write("{\"success\": false}");
        }
    }
}
