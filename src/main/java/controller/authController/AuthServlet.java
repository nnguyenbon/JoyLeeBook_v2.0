package controller.authController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import services.account.UserServices;
import services.auth.HandleOTPServices;
import services.auth.LoginServices;
import services.auth.RegisterServices;
import utils.LoginUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/login", "/logout", "/register"})
public class AuthServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/login")) {
            login(request, response);
        } else if (path.equals("/register")) {
            String action = request.getParameter("action");
            if (action.equals("sendOtp")) {
                sendOtp(request, response);
            } else if (action.equals("verifyOtp")) {
                verifyOtp(request, response);
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/login" -> {
                request.setAttribute("pageTitle", "Login");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/LoginPage.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            }
            case "/logout" -> {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
            }
            case "/register" -> {
                if (checkValidate(request, response)) {
                    return;
                }
                String action = request.getParameter("action") == null ? "" : request.getParameter("action");
                if (action.equals("sendOtp")) {
                    sendOtp(request, response);
                }
                viewRegister(request, response);
            }
        }
    }

    private void viewRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("pageTitle", "Register");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            LoginServices loginServices = new LoginServices();
            User user = loginServices.checkLogin(userName, password);
            if (user != null) {
                LoginUtils.storeLoginedUser(request.getSession(), user);
                String role = user.getRole();
                switch (role) {
                    case "admin":
                    case "staff":
                        response.sendRedirect(request.getContextPath() + "/staff");
                        break;
                    case "author":
                    case "reader":
                        response.sendRedirect(request.getContextPath() + "/homepage");
                        break;
                }

            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.setAttribute("pageTitle", "Login");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/LoginPage.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("error", "Something went wrong. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/LoginPage.jsp").forward(request, response);
        }
    }

    private void verifyOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String enterOTP = request.getParameter("otp");
            HttpSession session = request.getSession();


            RegisterServices registerServices = new RegisterServices();
            boolean checkOTP = registerServices.checkOTP(session, enterOTP);
            if (!checkOTP) {
                System.out.println("OTP is incorrect.");
                request.setAttribute("message", "OTP is incorrect.");
                request.setAttribute("pageTitle", "Register");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
                return;
            }
            User user = registerServices.createUser(session);
            if (user == null) {
                System.out.println("Something went wrong.");
                request.setAttribute("message", "Something went wrong. Please try again.");
                request.setAttribute("pageTitle", "Register");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            }

            System.out.println("Register successful.");
            session.removeAttribute("register");
            session.removeAttribute("otp");
            LoginUtils.storeLoginedUser(session, user);
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error in RegisterServlet: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User register;
        try {
            HandleOTPServices handleOTPServices = new HandleOTPServices();

            if (session.getAttribute("register") == null) {
                String userName = request.getParameter("username");
                String password = request.getParameter("password");
                String fullName = request.getParameter("fullName");
                String confirmPassword = request.getParameter("confirmPassword");
                String email = request.getParameter("email");
                if (handleOTPServices.checkExistUsername(userName)) {
                    request.setAttribute("message", "Username is already exist.");
                    request.setAttribute("fullName", fullName);
                    request.setAttribute("email", email);
                    request.setAttribute("password", password);
                    viewRegister(request, response);
                    return;
                }
                if (handleOTPServices.checkExistEmail(email)) {
                    request.setAttribute("message", "Email is already exist.");
                    request.setAttribute("fullName", fullName);
                    request.setAttribute("username", userName);
                    request.setAttribute("password", password);
                    viewRegister(request, response);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    request.setAttribute("message", "Your confirm password is not correct.");
                    request.setAttribute("fullName", fullName);
                    request.setAttribute("email", email);
                    request.setAttribute("username", userName);
                    request.setAttribute("password", password);
                    viewRegister(request, response);
                    return;
                }
                register = new User();
                register.setUsername(userName);
                register.setPasswordHash(password);
                register.setFullName(fullName);
                register.setEmail(email);
            } else {
                register = (User) session.getAttribute("register");
            }
            request.setAttribute("pageTitle", "Verify Email");
            request.setAttribute("contentPage", "/WEB-INF/views/auth/VerifyOTP.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            boolean sendSuccess = handleOTPServices.sendOTP(session, register);
            if (sendSuccess) {
                System.out.println("OTP has been sent to your email.");
                session.setAttribute("register", register);
                request.setAttribute("message", "OTP has been sent to your email.");
            } else {
                System.out.println("Something went wrong.");
                request.setAttribute("message", "Something went wrong. Please try again.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error in HandleOTPServlet: " + e.getMessage());
        }
    }

    private void validateName(HttpServletRequest request, HttpServletResponse response, String name) throws ServletException, IOException {
        String value = request.getParameter("value");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (!ValidationInput.isEmptyString(value)) {
            String message = name + " cannot empty";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (ValidationInput.isValidLength(value, 3)) {
            String message = name + " cannot contain less than 3 characters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.isValidCharacters(value)) {
            String message = name + " cannot contain special characters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        }
    }

    private void validateEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String value = request.getParameter("value");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (!ValidationInput.isEmptyString(value)) {
            String message = "Your email cannot empty";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.isValidEmailFormat(value)) {
            String message = "Your email format is invalid";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.hasValidDomain(value)) {
            String message = "Your domain is invalid";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        }
    }

    private void validatePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String value = request.getParameter("value");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (!ValidationInput.isEmptyString(value)) {
            String message = "Your password cannot empty";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (ValidationInput.isValidLength(value, 8)) {
            String message = "Your password must has more than 8 characters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.hasLowercase(value)) {
            String message = "Your password must has lower case letters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.hasUppercase(value)) {
            String message = "Your password must has upper case letters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.hasNumber(value)) {
            String message = "Your password must has numeric characters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        } else if (!ValidationInput.hasSpecialCharacter(value)) {
            String message = "Your password must contain special characters";
            String json = String.format("{\"valid\": %b, \"message\": \"%s\"}", false, message);
            response.getWriter().write(json);
        }

    }

    private boolean checkValidate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        switch (type) {
            case "username":
                validateName(request, response, "Username");
                return true;
            case "fullname":
                validateName(request, response, "Fullname");
                return true;
            case "email":
                validateEmail(request, response);
                return true;
            case "password":
                validatePassword(request, response);
                return true;
        }
        return false;
    }
}
