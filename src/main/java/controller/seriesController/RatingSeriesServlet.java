package controller.seriesController;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Rating;
import services.series.RatingSeriesService;

import java.io.IOException;

@WebServlet("/rate-series")
public class RatingSeriesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
