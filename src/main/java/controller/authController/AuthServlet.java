package controller.authController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import services.auth.LoginServices;
import services.auth.RegisterServices;
import utils.LoginUtils;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/login", "/logout", "/register"})
public class AuthServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/login")) {
            login(request, response);
        } else if (path.equals("/register")) {
            register(request, response);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/login")) {
            request.setAttribute("pageTitle", "Login");
            request.setAttribute("contentPage", "/WEB-INF/views/auth/LoginPage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
        } else if (path.equals("/logout")) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } else if (path.equals("/register")) {
            request.setAttribute("pageTitle", "Register");
            request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            LoginServices loginServices = new LoginServices();
            User user = loginServices.checkLogin(userName, password);
            if (user != null) {
                System.out.println("Login successful.");
                LoginUtils.storeLoginedUser(request.getSession(), user);
                response.sendRedirect(request.getContextPath() + "/homepage");
            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/WEB-INF/views/auth/LoginPage.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("error", "Something went wrong. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/LoginPage.jsp").forward(request, response);
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
}
