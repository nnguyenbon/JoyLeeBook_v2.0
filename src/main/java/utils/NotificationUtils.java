package utils;

import dao.NotificationsDAO;
import dao.SeriesAuthorDAO;
import model.Notification;

import java.sql.Connection;
import java.sql.SQLException;

public class NotificationUtils {
    /**
     * Creates a notification object for series approval/rejection.
     *
     * @param conn          the database connection to use for queries
     * @param seriesId      the ID of the series that was reviewed
     * @param comment       the staff's feedback comment
     * @param approveStatus the approval decision ("approved" or "rejected")
     * @return a Notification object ready to be inserted into the database
     * @throws SQLException if a database access error occurs while fetching owner ID
     */
    public static Notification createApprovalNotification(Connection conn, int seriesId,
                                                          String comment, String approveStatus) throws SQLException {
        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
        NotificationsDAO notificationsDAO = new NotificationsDAO(conn);
        Notification notification = new Notification();
        notification.setUserId(seriesAuthorDAO.findOwnerIdBySeriesId(seriesId).getAuthorId());
        notification.setTitle("Series " + approveStatus);
        notification.setType("submission_status");
        notification.setMessage(comment);
        notification.setUrlRedirect("/series/detail?seriesId=" + seriesId);
        notificationsDAO.insertNotification(notification);
        return notification;
    }
}
