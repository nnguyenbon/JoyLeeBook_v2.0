package controller.authorController;

import dao.BadgeDAO;
import dao.SeriesAuthorDAO;
import dao.SeriesDAO;
import dao.UserDAO;
import dao.NotificationsDAO;
import db.DBConnection;
import model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Servlet implementation class CoAuthorManagementServlet
 * Handles the management of co-authors for a series.
 * Allows authors to add or remove co-authors from their series.
 */
@WebServlet(name = "CoAuthorManagementServlet", value = "/manage-coauthors/*")
public class CoAuthorManagementServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action.");
            return;
        }
        switch (action) {
            case "/users" -> getUserName(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();

        if (action == null) action = "";
        switch (action) {
            case "/add" -> addCoAuthor(request, response);
            case "/remove" -> removeCoAuthor(request, response);
            case "/accept" -> acceptInvitation(request, response);
            case "/decline" -> declineInvitation(request, response);
            default -> doGet(request, response);
        }
    }

    private void getUserName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User author = (User) AuthenticationUtils.getLoginedUser(request.getSession());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String username = request.getParameter("username");

            if (username == null || username.trim().isEmpty()) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "You must provide a username.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            UserDAO userDAO = new UserDAO(conn);
            List<User> users = userDAO.findByName(username);

            JSONArray jsonArray = new JSONArray();
            for (User user : users) {
                // Don't include the current user in suggestions
                if (author != null && user.getUserId() == author.getUserId()) {
                    continue;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", user.getUserId());
                jsonObject.put("username", user.getUsername());
                jsonObject.put("email", user.getEmail());
                jsonArray.put(jsonObject);
            }
            response.getWriter().write(jsonArray.toString());
        } catch (SQLException | ClassNotFoundException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(errorResponse.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addCoAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User author = (User) AuthenticationUtils.getLoginedUser(request.getSession());

        if (author == null || !"author".equals(author.getRole())) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "You must be logged in as an author.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        String usernameOrEmail = request.getParameter("username");
        String seriesIdParam = request.getParameter("seriesId");

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Username or email is required.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        if (seriesIdParam == null || seriesIdParam.trim().isEmpty()) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Series ID is required.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        Connection conn = null;
        try {
            int seriesId = Integer.parseInt(seriesIdParam);
            conn = DBConnection.getConnection();

            UserDAO userDAO = new UserDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            // Find the user to invite
            User invitedUser = userDAO.findByEmail(usernameOrEmail);
            if (invitedUser == null) {
                invitedUser = userDAO.findByUsername(usernameOrEmail);
            }

            if (invitedUser == null) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Check if user is already a co-author
            SeriesAuthor existing = seriesAuthorDAO.findById(seriesId, invitedUser.getUserId());
            if (existing != null && existing.getSeriesId() > 0) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "This user is already a co-author of this series.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Verify the current user is the owner
            int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
            if (ownerId != author.getUserId()) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "Only the series owner can add co-authors.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Get series details
            Series series = seriesDAO.findById(seriesId);
            if (series == null) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "Series not found.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Create notification for the invited user
            Notification notification = new Notification();
            notification.setUserId(invitedUser.getUserId());
            notification.setTitle("Co-Author Invitation");
            notification.setMessage(author.getUsername() + " invited you to collaborate on \"" + series.getTitle() + "\"");
            notification.setUrlRedirect("/series/detail?seriesId=" + seriesId);
            notification.setRead(false);
            notification.setType("coauthor_invitation");

            // Store invitation data in notification metadata (you can extend Notification model if needed)
            // For now, we'll use the URL to pass data
            notification.setUrlRedirect("/manage-coauthors/invitation?seriesId=" + seriesId + "&inviterId=" + author.getUserId());

            boolean notificationCreated = notificationsDAO.insertNotification(notification);

            if (notificationCreated) {
                JSONObject successResponse = new JSONObject();
                successResponse.put("success", true);
                successResponse.put("message", "Invitation sent to " + invitedUser.getUsername());
                response.getWriter().write(successResponse.toString());
            } else {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "Failed to send invitation.");
                response.getWriter().write(errorResponse.toString());
            }

        } catch (NumberFormatException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid series ID format.");
            response.getWriter().write(errorResponse.toString());
        } catch (SQLException | ClassNotFoundException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(errorResponse.toString());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void acceptInvitation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());

        if (user == null) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "You must be logged in.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        String seriesIdParam = request.getParameter("seriesId");
        String notificationIdParam = request.getParameter("notificationId");

        Connection conn = null;
        try {
            int seriesId = Integer.parseInt(seriesIdParam);
            int notificationId = Integer.parseInt(notificationIdParam);

            conn = DBConnection.getConnection();
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);

            // Add user as co-author
            seriesAuthorDAO.addAuthorToSeries(seriesId, user.getUserId());

            // Mark notification as read
            notificationsDAO.markAsRead(notificationId);

            // Send confirmation notification to the inviter
            int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
            Series series = seriesDAO.findById(seriesId);

            Notification confirmation = new Notification();
            confirmation.setUserId(ownerId);
            confirmation.setTitle("Co-Author Accepted");
            confirmation.setMessage(user.getUsername() + " accepted your invitation to collaborate on \"" + series.getTitle() + "\"");
            confirmation.setUrlRedirect("/series/detail?seriesId=" + seriesId);
            confirmation.setRead(false);
            confirmation.setType("system"); // Use existing type value
            notificationsDAO.insertNotification(confirmation);

            JSONObject successResponse = new JSONObject();
            successResponse.put("success", true);
            successResponse.put("message", "You are now a co-author!");
            successResponse.put("redirectUrl", request.getContextPath() + "/series/detail?seriesId=" + seriesId);
            response.getWriter().write(successResponse.toString());

        } catch (NumberFormatException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid parameters.");
            response.getWriter().write(errorResponse.toString());
        } catch (SQLException | ClassNotFoundException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(errorResponse.toString());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void declineInvitation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());

        if (user == null) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "You must be logged in.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        String notificationIdParam = request.getParameter("notificationId");
        String seriesIdParam = request.getParameter("seriesId");

        Connection conn = null;
        try {
            int notificationId = Integer.parseInt(notificationIdParam);
            int seriesId = Integer.parseInt(seriesIdParam);

            conn = DBConnection.getConnection();
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);

            // Mark notification as read
            notificationsDAO.markAsRead(notificationId);

            // Optionally notify the inviter about the decline
            int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
            Series series = seriesDAO.findById(seriesId);

            Notification declineNotification = new Notification();
            declineNotification.setUserId(ownerId);
            declineNotification.setTitle("Co-Author Invitation Declined");
            declineNotification.setMessage(user.getUsername() + " declined your invitation to collaborate on \"" + series.getTitle() + "\"");
            declineNotification.setUrlRedirect("/series/detail?seriesId=" + seriesId);
            declineNotification.setRead(false);
            declineNotification.setType("system"); // Use existing type value
            notificationsDAO.insertNotification(declineNotification);

            JSONObject successResponse = new JSONObject();
            successResponse.put("success", true);
            successResponse.put("message", "Invitation declined.");
            response.getWriter().write(successResponse.toString());

        } catch (NumberFormatException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid parameters.");
            response.getWriter().write(errorResponse.toString());
        } catch (SQLException | ClassNotFoundException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(errorResponse.toString());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void removeCoAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User author = (User) AuthenticationUtils.getLoginedUser(request.getSession());

        if (author == null || !"author".equals(author.getRole())) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "You must be logged in as an author.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        String seriesIdParam = request.getParameter("seriesId");
        String userIdParam = request.getParameter("userId");

        Connection conn = null;
        try {
            int seriesId = Integer.parseInt(seriesIdParam);
            int userId = Integer.parseInt(userIdParam);

            conn = DBConnection.getConnection();
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);

            // Verify the current user is the owner
            int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
            if (ownerId != author.getUserId()) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "Only the series owner can remove co-authors.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Remove co-author
            seriesAuthorDAO.removeAuthorFromSeries(seriesId, userId);

            JSONObject successResponse = new JSONObject();
            successResponse.put("success", true);
            successResponse.put("message", "Co-author removed successfully.");
            response.getWriter().write(successResponse.toString());

        } catch (NumberFormatException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid parameters.");
            response.getWriter().write(errorResponse.toString());
        } catch (SQLException | ClassNotFoundException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(errorResponse.toString());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}