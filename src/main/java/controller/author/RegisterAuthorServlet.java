package controller.author;

import db.DBConnection;
import model.User;
import services.account.AuthorServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "RegisterAuthorServlet", value = "/register-author")
public class RegisterAuthorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null && "reader".equals(user.getRole())) {
            request.getRequestDispatcher("/WEB-INF/views/author/register-author.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/user-profile?error=unauthorized");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null && "reader".equals(user.getRole())) {
            AuthorServices authorServices = null;
            try {
                authorServices = new AuthorServices();
                if (authorServices.registerAsAuthor(user)) {
                    user.setRole("author");
                    session.setAttribute("user", user);
                    response.sendRedirect(request.getContextPath() + "/user-profile?message=registered-as-author-successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/error/error.jsp");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}