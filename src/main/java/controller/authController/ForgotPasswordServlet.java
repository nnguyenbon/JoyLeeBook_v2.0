package controller.authController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet implementation class ForgotPasswordServlet
 * Handles password reset functionality with OTP verification
 */
@WebServlet(urlPatterns = {"/forgot", "/reset-password"})
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/forgot" -> viewForgotPassword(request, response);
            case "/reset-password" -> {
                // Check if OTP is verified
                HttpSession session = request.getSession();
                if (session.getAttribute("resetEmail") == null) {
                    response.sendRedirect(request.getContextPath() + "/forgot");
                    return;
                }
                viewResetPassword(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/forgot")) {
            String action = request.getParameter("action");
            if ("sendOtp".equals(action)) {
                sendOtpForPasswordReset(request, response);
            } else if ("verifyOtp".equals(action)) {
                verifyOtpForPasswordReset(request, response);
            }
        } else if (path.equals("/reset-password")) {
            resetPassword(request, response);
        }
    }

    /**
     * Display forgot password page
     */
    private void viewForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Forgot Password");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/ForgotPasswordPage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
    }

    /**
     * Display reset password page (after OTP verification)
     */
    private void viewResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Reset Password");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/ResetPasswordPage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
    }

    /**
     * Send OTP to user's email for password reset
     */
    private void sendOtpForPasswordReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        try {
            // Validate email format
            if (!ValidationInput.isEmptyString(email)) {
                request.setAttribute("message", "Email cannot be empty");
                request.setAttribute("messageType", "error");
                viewForgotPassword(request, response);
                return;
            }

            if (!ValidationInput.isValidEmailFormat(email)) {
                request.setAttribute("message", "Invalid email format");
                request.setAttribute("messageType", "error");
                viewForgotPassword(request, response);
                return;
            }

            // Check if email exists in database
            if (!AuthenticationUtils.checkExistEmail(email)) {
                request.setAttribute("message", "Email does not exist in our system");
                request.setAttribute("messageType", "error");
                viewForgotPassword(request, response);
                return;
            }

            // Create temporary user object for OTP sending
            User tempUser = new User();
            tempUser.setEmail(email);

            // Send OTP
            boolean sendSuccess = AuthenticationUtils.sendOTP(session, tempUser);

            if (sendSuccess) {
                session.setAttribute("resetEmail", email);
                request.setAttribute("pageTitle", "Verify OTP");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/VerifyOTPForgot.jsp");
                request.setAttribute("message", "OTP has been sent to your email");
                request.setAttribute("messageType", "success");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            } else {
                request.setAttribute("message", "Failed to send OTP. Please try again");
                request.setAttribute("messageType", "error");
                viewForgotPassword(request, response);
            }

        } catch (SQLException e) {
            System.out.println("Error in sendOtpForPasswordReset: " + e.getMessage());
            request.setAttribute("message", "Something went wrong. Please try again later");
            request.setAttribute("messageType", "error");
            viewForgotPassword(request, response);
        }
    }

    /**
     * Verify OTP entered by user
     */
    private void verifyOtpForPasswordReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String enteredOtp = request.getParameter("otp");
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/forgot");
            return;
        }

        try {
            boolean isOtpValid = AuthenticationUtils.checkOTP(session, enteredOtp);

            if (!isOtpValid) {
                request.setAttribute("pageTitle", "Verify OTP");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/VerifyOTPForgot.jsp");
                request.setAttribute("message", "Invalid OTP. Please try again");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
                return;
            }

            // OTP verified successfully, redirect to reset password page
            session.removeAttribute("otp");
            response.sendRedirect(request.getContextPath() + "/reset-password");

        } catch (Exception e) {
            System.out.println("Error in verifyOtpForPasswordReset: " + e.getMessage());
            request.setAttribute("message", "Something went wrong. Please try again");
            request.setAttribute("messageType", "error");
            request.setAttribute("pageTitle", "Verify OTP");
            request.setAttribute("contentPage", "/WEB-INF/views/auth/VerifyOTPForgot.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
        }
    }

    /**
     * Reset user password after OTP verification
     */
    private void resetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/forgot");
            return;
        }

        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate password
        if (!ValidationInput.isEmptyString(newPassword)) {
            request.setAttribute("message", "Password cannot be empty");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        if (ValidationInput.isValidLength(newPassword, 8)) {
            request.setAttribute("message", "Password must have more than 8 characters");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        if (!ValidationInput.hasLowercase(newPassword)) {
            request.setAttribute("message", "Password must contain lowercase letters");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        if (!ValidationInput.hasUppercase(newPassword)) {
            request.setAttribute("message", "Password must contain uppercase letters");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        if (!ValidationInput.hasNumber(newPassword)) {
            request.setAttribute("message", "Password must contain numeric characters");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        if (!ValidationInput.hasSpecialCharacter(newPassword)) {
            request.setAttribute("message", "Password must contain special characters");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Passwords do not match");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
            return;
        }

        // Update password in database
        boolean updateSuccess = AuthenticationUtils.updatePassword(email, newPassword);

        if (updateSuccess) {
            session.removeAttribute("resetEmail");
            request.setAttribute("message", "Password reset successfully. Please login with your new password");
            request.setAttribute("messageType", "success");
            request.setAttribute("pageTitle", "Login");
            request.setAttribute("contentPage", "/WEB-INF/views/auth/LoginPage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "Failed to reset password. Please try again");
            request.setAttribute("messageType", "error");
            viewResetPassword(request, response);
        }

    }
}