package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import services.auth.RegisterServices;
import utils.LoginUtils;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("pageTitle", "Register");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/RegisterPage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
