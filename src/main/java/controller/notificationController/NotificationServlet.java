package controller.notificationController;

import dao.NotificationsDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import model.Notification;
import model.User;
import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet implementation class NotificationServlet
 * Handles notification-related actions such as marking notifications as read
 * and displaying all notifications for the logged-in user.
 */
@WebServlet(urlPatterns = {
        "/notification/mark-read",
        "/notifications/all",
        "/notifications/mark-all"
})
public class NotificationServlet extends HttpServlet {

    /**
     * Handles GET requests for displaying all notifications and marking all as read.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/notifications/all".equals(path)) {
            showAllNotificationsPage(request, response);
        } else if ("/notifications/mark-all".equals(path)) {
            markAllAsRead(request, response);
        }
    }

    /**
     * Handles POST requests for marking a single notification as read.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/notification/mark-read".equals(path)) {
            markOneAsRead(request, response);
        } else if ("/notifications/mark-all".equals(path)) {
            markAllAsRead(request, response);
        }
    }

    /**
     * Displays the page with all notifications for the logged-in user.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void showAllNotificationsPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account loginedAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedAccount == null || !(loginedAccount instanceof User)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User loginedUser = (User) loginedAccount;

        //Pagination parameters
        int page = ValidationInput.isPositiveInteger(request.getParameter("page")) ? Integer.parseInt(request.getParameter("page")) : 1;
        int size = 20;

        try (Connection conn = DBConnection.getConnection()) {
            NotificationsDAO dao = new NotificationsDAO(conn);

            //Fetch paginated notifications
            List<Notification> notifications = dao.getPaginatedByUserId(loginedUser.getUserId(), page, size);

            //Calculate total pages
            int totalNotifications = dao.getTotalCountByUserId(loginedUser.getUserId());
            int totalPages = (int) Math.ceil((double) totalNotifications / size);

            request.setAttribute("notificationList", notifications);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalNotifications", totalNotifications);

            request.setAttribute("pageTitle", "All Notifications");
            request.setAttribute("contentPage", "/WEB-INF/views/notification/all-notifications.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load notifications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    /**
     * Marks a single notification as read based on the provided notification ID.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    private void markOneAsRead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account loginedAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedAccount == null || !(loginedAccount instanceof User)) {
            //Not logged in as user
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in as a user.");
            return;
        }

        try {
            int notificationId = Integer.parseInt(request.getParameter("id"));
            try (Connection conn = DBConnection.getConnection()) {
                NotificationsDAO dao = new NotificationsDAO(conn);

                //Mark notification as read
                boolean success = dao.markAsRead(notificationId);
                if (success) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true}");
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not mark as read in DB");
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request: " + e.getMessage());
        }
    }

    /**
     * Marks all notifications as read for the logged-in user.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    private void markAllAsRead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Account loginedAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        if (loginedAccount == null || !(loginedAccount instanceof User)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User loginedUser = (User) loginedAccount;

        try (Connection conn = DBConnection.getConnection()) {
            NotificationsDAO dao = new NotificationsDAO(conn);
            dao.markAllAsRead(loginedUser.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to mark all as read: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/notifications/all");
    }
}