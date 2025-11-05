package utils;

import dao.PointHistoryDAO;
import dao.UserDAO;
import db.DBConnection;
import model.PointHistory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TrackPointUtils {

    public static void trackAction(int userId, int points, String reason, String referenceType, int referenceId, int limit) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            PointHistoryDAO pointHistoryDAO = new PointHistoryDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            if (pointHistoryDAO.trackAction(userId, referenceType, limit)) {
                return;
            }
            PointHistory pointHistory = new PointHistory();
            pointHistory.setUserId(userId);
            pointHistory.setPointChange(points);
            pointHistory.setReason(reason);
            pointHistory.setReferenceType(referenceType);
            pointHistory.setReferenceId(referenceId);
            pointHistory.setCreatedAt(LocalDateTime.now());
            if (pointHistoryDAO.insert(pointHistory)) {
                userDAO.updatePoint(userId, points);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
