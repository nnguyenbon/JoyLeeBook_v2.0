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
        String usernameOrEmail = request.getParameter("username");
        String seriesIdParam = request.getParameter("seriesId");

        JSONObject json = new JSONObject();

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            json.put("success", false);
            json.put("message", "Username or email is required.");
            response.getWriter().write(json.toString());
            return;
        }

        if (seriesIdParam == null || seriesIdParam.trim().isEmpty()) {
            json.put("success", false);
            json.put("message", "Series ID is required.");
            response.getWriter().write(json.toString());
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(seriesIdParam);

            UserDAO userDAO = new UserDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            // Lấy user & series
            User invitedUser = userDAO.findByEmail(usernameOrEmail);
            if (invitedUser == null) {
                invitedUser = userDAO.findByUsername(usernameOrEmail);
            }
            Series series = seriesDAO.findById(seriesId);
            SeriesAuthor existing = seriesAuthorDAO.findById(seriesId, invitedUser != null ? invitedUser.getUserId() : -1);
            int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);

            // ✅ Gọi hàm validation
            String validationError = validateAddCoAuthor(author.getUserId(), author.getRole(), invitedUser == null ? -1 : invitedUser.getUserId(),  existing != null, ownerId);
            if (validationError != null) {
                json.put("success", false);
                json.put("message", validationError);
                response.getWriter().write(json.toString());
                return;
            }

            Notification notification = new Notification();
            notification.setUserId(invitedUser.getUserId());
            notification.setTitle("Co-Author Invitation");
            notification.setMessage(author.getUsername() + " invited you to collaborate on \"" + series.getTitle() + "\"");
            notification.setUrlRedirect("/manage-coauthors/invitation?seriesId=" + seriesId + "&inviterId=" + author.getUserId());
            notification.setType("system");
            notification.setRead(false);

            boolean created = notificationsDAO.insertNotification(notification);
            json.put("success", created);
            json.put("message", created
                    ? "Invitation sent to " + invitedUser.getUsername()
                    : "Failed to send invitation.");
            response.getWriter().write(json.toString());

        } catch (NumberFormatException e) {
            json.put("success", false);
            json.put("message", "Invalid series ID format.");
            response.getWriter().write(json.toString());
        } catch (SQLException | ClassNotFoundException e) {
            json.put("success", false);
            json.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(json.toString());
            e.printStackTrace();
        }
    }

    public String validateAddCoAuthor(
            int authorId,
            String authorRole,
            int inviterId,
            boolean existing,
            int ownerId
    ) {
        if ( !"author".equalsIgnoreCase(authorRole)) {
            return "You must be logged in as an author.";
        }

        if (inviterId == -1) {
            return "User not found.";
        }

        if (ownerId != authorId) {
            return "Only the series owner can add co-authors.";
        }

        if (existing) {
            return "This user is already a co-author of this series.";
        }

        if (inviterId == authorId) {
            return "You cannot add yourself as a co-author.";
        }


        return null;
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
        String username = request.getParameter("username");

        if (username == null || username.trim().isEmpty()) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", "Username is required.");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        Connection conn = null;
        try {
            int seriesId = Integer.parseInt(seriesIdParam);

            conn = DBConnection.getConnection();
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            // Verify the current user is the owner
            int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
            if (ownerId != author.getUserId()) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "Only the series owner can remove co-authors.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Find the user to remove by username
            User removedUser = userDAO.findByUsername(username);
            if (removedUser == null) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Check if the user is actually a co-author
            SeriesAuthor existing = seriesAuthorDAO.findById(seriesId, removedUser.getUserId());
            if (existing == null || existing.getSeriesId() == 0) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "This user is not a co-author of this series.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Prevent removing the owner
            if (removedUser.getUserId() == ownerId) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("success", false);
                errorResponse.put("message", "Cannot remove the series owner.");
                response.getWriter().write(errorResponse.toString());
                return;
            }

            // Get series details
            Series series = seriesDAO.findById(seriesId);

            // Remove co-author
            seriesAuthorDAO.removeAuthorFromSeries(seriesId, removedUser.getUserId());

            // Send notification to the removed co-author
            Notification notification = new Notification();
            notification.setUserId(removedUser.getUserId());
            notification.setTitle("Removed from Co-authorship");
            notification.setMessage("You have been removed as a co-author from \"" + series.getTitle() + "\" by " + author.getUsername());
            notification.setUrlRedirect("/series/detail?seriesId=" + seriesId);
            notification.setRead(false);
            notification.setType("system");

            notificationsDAO.insertNotification(notification);

            JSONObject successResponse = new JSONObject();
            successResponse.put("success", true);
            successResponse.put("message", removedUser.getUsername() + " has been removed as co-author.");
            response.getWriter().write(successResponse.toString());

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
}