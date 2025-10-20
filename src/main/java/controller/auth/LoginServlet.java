package controller.auth;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import services.auth.LoginServices;
import utils.MyUltis;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("pageTitle", "Login");
        request.setAttribute("contentPage", "/WEB-INF/views/auth/LoginPage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            LoginServices loginServices = new LoginServices();
            User user = loginServices.checkLogin(userName, password);
            if (user != null) {
                System.out.println("Login successful.");
                MyUltis.storeLoginedUser(request.getSession(), user);
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
}
