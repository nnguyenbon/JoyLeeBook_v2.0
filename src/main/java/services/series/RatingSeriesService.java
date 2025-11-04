package services.series;

import dao.RatingDAO;
import db.DBConnection;
import model.Rating;
import services.general.PointServices;

import java.sql.Connection;
import java.sql.SQLException;

public class RatingSeriesService {
    private final RatingDAO ratingDAO;

    public RatingSeriesService() throws ClassNotFoundException, SQLException {
        Connection connection = DBConnection.getConnection();
        this.ratingDAO = new RatingDAO(connection);
    }
    public double getAverageRatingOfAuthor(int userId) throws SQLException {
        double rate = ratingDAO.getAverageRating(userId);
        return (double) Math.round(rate * 10) / 10;
    }

    public boolean saveOrUpdateRating(Rating rating) throws SQLException {
        if (ratingDAO.getRatingValueByUserId(rating)) {
            ratingDAO.update(rating);
        } else {
            ratingDAO.insert(rating);
            PointServices.trackAction(rating.getUserId(),1, "Rating a new chapter", "rating", Integer.parseInt(String.valueOf(rating.getUserId()) + rating.getSeriesId()) );
        }
        return true;
    }

    public double getAverageRating(int seriesId) throws SQLException {
        double rate = ratingDAO.getAverageRating(seriesId);
        return (double) Math.round(rate * 10) / 10;
    }

    public int getTotalRatings(int seriesId) throws SQLException {
        return ratingDAO.getRatingCount(seriesId);
    }

    public int getRatingByUserID(int userId, int seriesId) throws SQLException {

        return ratingDAO.getRatingValueByUserId(userId, seriesId );
    }
}
