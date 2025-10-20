package services.series;

import dao.SavedSeriesDAO;
import db.DBConnection;
import model.SavedSeries;

import java.sql.Connection;
import java.sql.SQLException;

public class SavedSeriesService {
    private final Connection connection;
    private final SavedSeriesDAO savedSeriesDAO;

    public SavedSeriesService() throws ClassNotFoundException, SQLException {
        this.connection = DBConnection.getConnection();
        this.savedSeriesDAO = new SavedSeriesDAO(connection);
    }

    public void saveSeries(SavedSeries savedSeries) throws SQLException {
        savedSeriesDAO.insert(savedSeries);
    }

    public void unSaveSeries(SavedSeries savedSeries) throws SQLException {
        savedSeriesDAO.delete(savedSeries.getUserId(), savedSeries.getSeriesId());
    }

    public boolean hasUserSavedSeries(int userId, int seriesId) throws SQLException {
        return savedSeriesDAO.isSaved(userId, seriesId);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
