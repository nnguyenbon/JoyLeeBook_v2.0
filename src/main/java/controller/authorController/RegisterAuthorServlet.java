package controller.authorController;

import model.User;
import services.account.AuthorServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "RegisterAuthorServlet", value = "/register-author")
public class RegisterAuthorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        try {
            AuthorServices authorServices = new AuthorServices();
            String email = user != null ? user.getEmail() : null;
            if (authorServices.isAuthor(email)){
                user.setRole("author");
                response.sendRedirect(request.getContextPath() + "/author");
            }else {
                response.sendRedirect(request.getContextPath() + "/homepage");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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