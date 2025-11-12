package controller.authController;

import dao.BadgesUserDAO;
import dao.SeriesDAO;
import dao.UserDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Series;
import model.User;
import utils.AuthenticationUtils;
import utils.FormatUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/profile/*")
public class ProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        viewProfile(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action.equals("/edit")) {
            editProfile(request, response);
        } else if (action.equals("/changePassword")) {
            changePassword(request, response);
        } else if (action.equals("/sendEmailOtp")) {
            sendEmailOtp(request, response);
        } else if (action.equals("/verifyEmailOtp")) {
            verifyEmailOtp(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action.");
        }
    }

    private void viewProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 0;
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int accountId = loginedUser != null ? loginedUser.getUserId() : -1;
        String role = loginedUser != null ? loginedUser.getRole() : null;
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            BadgesUserDAO badgesUserDAO = new BadgesUserDAO(conn);
            User user = userDAO.findById(userId);
            user.setRole(FormatUtils.formatString(user.getRole()));

            badgesUserDAO.checkAndSaveBadges(userId);

            request.setAttribute("user", user);
            request.setAttribute("badgeList", badgesUserDAO.getBadgesByUserId(userId));

            if (accountId == userId && role.equals("reader")) {
                request.setAttribute("pageTitle", "My Profile");
                request.setAttribute("contentPage", "/WEB-INF/views/profile/MyProfile.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
            } else {
                SeriesDAO seriesDAO = new SeriesDAO(conn);
                List<Series> series = seriesDAO.getSeriesByAuthorId(userId);
                request.setAttribute("seriesInfoDTOList", series);
                request.setAttribute("totalSeriesCount", series.size());
                request.getRequestDispatcher("WEB-INF/views/profile/AuthorProfile.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void editProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : -1;
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            String userName = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String bio = request.getParameter("bio");

            User user = new User();
            user.setUserId(userId);
            user.setUsername(userName);
            user.setFullName(fullName);
            user.setBio(bio);

            boolean isSuccess = userDAO.updateProfile(user);
            if (isSuccess) {
                request.getSession().setAttribute("message", "Update user successfully!");
            } else {
                request.getSession().setAttribute("message", "Something went wrong. Please try again.");
            }
            response.sendRedirect(request.getContextPath() + "/profile?userId=" + userId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : -1;
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            String currentPass = request.getParameter("currentPass");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            String message = "";

            User user = userDAO.findById(userId);

            if (!newPassword.equals(confirmPassword)) {
                message = "Your password and confirm password do not match";
            } else if (!AuthenticationUtils.checkPwd(currentPass, user.getPasswordHash())) {
                message = "Your current password is incorrect";
            } else if (userDAO.updatePassword(userId, newPassword)) {
                message = "Update password successfully!";
            } else {
                message = "Something went wrong. Please try again.";
            }
            request.getSession().setAttribute("message", message);
            response.sendRedirect(request.getContextPath() + "/profile?userId=" + userId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send OTP to new email address
     */
    private void sendEmailOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        String newEmail = request.getParameter("newEmail");
        HttpSession session = request.getSession();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);

            // Check if email already exists
            if (userDAO.checkByEmail(newEmail)) {
                response.getWriter().write("{\"success\": false, \"message\": \"Email is already in use\"}");
                return;
            }

            // Validate email format
            if (!ValidationInput.isValidEmailFormat(newEmail)) {
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid email format\"}");
                return;
            }

            // Store new email in session
            session.setAttribute("newEmail", newEmail);

            // Send OTP
            User tempUser = new User();
            tempUser.setEmail(newEmail);
            boolean sent = AuthenticationUtils.sendOTP(session, tempUser);

            if (sent) {
                response.getWriter().write("{\"success\": true, \"message\": \"OTP has been sent to your new email\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Failed to send OTP. Please try again\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Something went wrong. Please try again\"}");
        }
    }

    /**
     * Verify OTP and update email
     */
    private void verifyEmailOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : -1;
        String otp = request.getParameter("otp");
        HttpSession session = request.getSession();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check OTP
        boolean isValidOtp = AuthenticationUtils.checkOTP(session, otp);

        if (!isValidOtp) {
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid OTP\"}");
            return;
        }

        String newEmail = (String) session.getAttribute("newEmail");

        if (newEmail == null) {
            response.getWriter().write("{\"success\": false, \"message\": \"Session expired. Please try again\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            boolean updated = userDAO.updateEmail(userId, newEmail);

            if (updated) {
                // Update session user
                loginedUser.setEmail(newEmail);
                session.setAttribute("loginedUser", loginedUser);

                // Clean up session
                session.removeAttribute("newEmail");
                session.removeAttribute("otp");

                response.getWriter().write("{\"success\": true, \"message\": \"Email updated successfully!\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Failed to update email. Please try again\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Something went wrong. Please try again\"}");
        }
    }
}