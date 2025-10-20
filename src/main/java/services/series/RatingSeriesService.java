package services.series;

import dao.RatingDAO;
import db.DBConnection;
import model.Rating;

import java.sql.Connection;
import java.sql.SQLException;

public class RatingSeriesService {
    private final RatingDAO ratingDAO;

    public RatingSeriesService() throws ClassNotFoundException, SQLException {
        Connection connection = DBConnection.getConnection();
        this.ratingDAO = new RatingDAO(connection);
    }

    public boolean saveOrUpdateRating(Rating rating) throws SQLException {
        if (ratingDAO.getRatingValueByUserId(rating)) {
            ratingDAO.update(rating);
        } else {
            ratingDAO.insert(rating);
        }
        return true;
    }

    public double getAverageRating(int seriesId) throws SQLException {
        return ratingDAO.getAverageRating(seriesId);
    }

    public double getAverageRatingOfAuthor(int userId) throws SQLException {
        return ratingDAO.getAverageRating(userId);
    }

    public int getTotalRatings(int seriesId) throws SQLException {
        return ratingDAO.getRatingCount(seriesId);
    }

    public int getRatingByUserID(int userId, int seriesId) throws SQLException {

        return ratingDAO.getRatingValueByUserId(userId, seriesId );
    }
}
