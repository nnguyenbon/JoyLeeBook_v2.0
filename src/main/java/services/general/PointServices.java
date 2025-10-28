package services.general;

import dao.PointHistoryDAO;
import dao.UserDAO;
import db.DBConnection;
import model.PointHistory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PointServices {
    private static final Connection connection;

    static {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final PointHistoryDAO pointHistoryDAO = new PointHistoryDAO(connection);
    private static final UserDAO userDAO = new UserDAO(connection);

    public static void trackLogin (int userId) throws SQLException {
        if (pointHistoryDAO.trackLogin(userId, "login")){
            return;
        }
        PointHistory pointHistory = new PointHistory();
        pointHistory.setUserId(userId);
        pointHistory.setPointChange(10);
        pointHistory.setReason("login");
        pointHistory.setReferenceType("login");
        pointHistory.setReferenceId(0);
        pointHistory.setCreatedAt(LocalDateTime.now());
        if (pointHistoryDAO.insert(pointHistory)) {
            userDAO.updatePoint(userId, 10);
        }
    }

    public static void trackAction (int userId, int points, String reason, String referenceType, int referenceId) throws SQLException {
        if (pointHistoryDAO.trackAction(userId, referenceType)){
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
    }
}
