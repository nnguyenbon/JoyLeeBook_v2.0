package controller.authController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Staff;
import model.User;
import utils.TrackPointUtils;
import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet implementation class AuthServlet
 * This servlet handles user authentication including login, logout, and registration with OTP verification.
 * It supports both GET and POST requests for different authentication actions.
 */
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

    /**
     * Handles GET requests for login, logout, and registration pages.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath(); //Get the servlet path to determine the action

        //Route to the appropriate handler based on the servlet path
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
                //Default action for register page
                String action = request.getParameter("action") == null ? "" : request.getParameter("action");
                if (action.equals("sendOtp")) {
                    sendOtp(request, response);
                }
                viewRegister(request, response);
            }
        }
    }

    /**
     * Renders the registration page.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void viewRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("pageTitle", "Register");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
    }

    /**
     * Processes user login.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = AuthenticationUtils.checkLoginUser(userName, password);
            Staff staff = AuthenticationUtils.checkLoginStaff(userName, password);
            if (user != null) {
                AuthenticationUtils.storeLoginedUser(request.getSession(), user);
                String role = user.getRole();
                switch (role) {
                    case "author":
                        if (user.getStatus().equals("banned")) {
                            response.sendRedirect(request.getContextPath() + "/login");
                            return;
                        }
                        //Track login for point system
                        TrackPointUtils.trackAction(user.getUserId(), 10, "Login with form", "login", 0, 1);
                        response.sendRedirect(request.getContextPath() + "/author");
                        break;
                    case "reader":
                        if (user.getStatus().equals("banned")) {
                            response.sendRedirect(request.getContextPath() + "/login");
                            return;
                        }
                        //Track login for point system
                        TrackPointUtils.trackAction(user.getUserId(), 10, "Login with form", "login", 0, 1);
                        response.sendRedirect(request.getContextPath() + "/homepage");
                        break;
                }
            } else if (staff != null) {
                AuthenticationUtils.storeLoginedUser(request.getSession(), staff);
                String role = staff.getRole();
                switch (role) {
                    case "admin":
                        response.sendRedirect(request.getContextPath() + "/admin");
                        break;
                    case "staff":
                        response.sendRedirect(request.getContextPath() + "/staff");
                        break;
                }
            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.setAttribute("pageTitle", "Login");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/LoginPage.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Something went wrong. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/LoginPage.jsp").forward(request, response);
        }
    }

    /**
     * Verifies the OTP entered by the user during registration.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void verifyOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String enterOTP = request.getParameter("otp");
            HttpSession session = request.getSession();


            boolean checkOTP = AuthenticationUtils.checkOTP(session, enterOTP);
            if (!checkOTP) {
                System.out.println("OTP is incorrect.");
                request.setAttribute("message", "OTP is incorrect.");
                request.setAttribute("pageTitle", "Register");
                request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
                return;
            }
            User user = AuthenticationUtils.createUser(session);
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
            AuthenticationUtils.storeLoginedUser(session, user);
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (SQLException e) {
            System.out.println("Error in RegisterServlet: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an OTP to the user's email during registration.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void sendOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User register; //Temporary user object to hold registration data
        try {


            if (session.getAttribute("register") == null) {
                String userName = request.getParameter("username");
                String password = request.getParameter("password");
                String fullName = request.getParameter("fullName");
                String confirmPassword = request.getParameter("confirmPassword");
                String email = request.getParameter("email");
                if (AuthenticationUtils.checkExistUsername(userName)) {
                    request.setAttribute("message", "Username is already exist.");
                    request.setAttribute("fullName", fullName);
                    request.setAttribute("email", email);
                    request.setAttribute("password", password);
                    viewRegister(request, response);
                    return;
                }
                //Check if email already exists
                if (AuthenticationUtils.checkExistEmail(email)) {
                    request.setAttribute("message", "Email is already exist.");
                    request.setAttribute("fullName", fullName);
                    request.setAttribute("username", userName);
                    request.setAttribute("password", password);
                    viewRegister(request, response);
                    return;
                }
                //Check if password and confirm password match
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
            boolean sendSuccess = AuthenticationUtils.sendOTP(session, register);
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


    /**
     * Checks which field to validate based on the "type" parameter in the request.
     *
     * @param request
     * @param response
     * @return true if a validation was performed, false otherwise
     * @throws ServletException
     * @throws IOException
     */
    private boolean checkValidate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        String value = request.getParameter("value");

        // Gọi logic validate riêng (độc lập với request/response)
        String jsonResult = validateInput(type, value);

        if (jsonResult.isEmpty()) return false;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResult);
        return true;
    }

    public String validateInput(String type, String value) {
        switch (type) {
            case "username":
                return validateName("Username", value);
            case "fullname":
                return validateName("Fullname", value);
            case "email":
                return validateEmail(value);
            case "password":
                return validatePassword(value);
            default:
                return "";
        }
    }
    private String validateName(String name, String value) {
        if (!ValidationInput.isEmptyString(value)) {
            return json(false, name + " cannot empty");
        } else if (ValidationInput.isValidLength(value, 3)) {
            return json(false, name + " cannot contain less than 3 characters");
        } else if (!ValidationInput.isValidCharacters(value)) {
            return json(false, name + " cannot contain special characters");
        }
        return json(true, "Valid " + name);
    }

    private String validateEmail(String value) {
        if (!ValidationInput.isEmptyString(value)) {
            return json(false, "Your email cannot empty");
        } else if (!ValidationInput.isValidEmailFormat(value)) {
            return json(false, "Your email format is invalid");
        } else if (!ValidationInput.hasValidDomain(value)) {
            return json(false, "Your domain is invalid");
        }
        return json(true, "Valid email");
    }

    private String validatePassword(String value) {
        if (!ValidationInput.isEmptyString(value)) {
            return json(false, "Your password cannot empty");
        } else if (ValidationInput.isValidLength(value, 8)) {
            return json(false, "Your password must has more than 8 characters");
        } else if (!ValidationInput.hasLowercase(value)) {
            return json(false, "Your password must has lower case letters");
        } else if (!ValidationInput.hasUppercase(value)) {
            return json(false, "Your password must has upper case letters");
        } else if (!ValidationInput.hasNumber(value)) {
            return json(false, "Your password must has numeric characters");
        } else if (!ValidationInput.hasSpecialCharacter(value)) {
            return json(false, "Your password must contain special characters");
        }
        return json(true, "Valid password");
    }

    /** Hàm tạo JSON dùng chung */
    private String json(boolean valid, String message) {
        return String.format("{\"valid\": %b, \"message\": \"%s\"}", valid, message);
    }
}