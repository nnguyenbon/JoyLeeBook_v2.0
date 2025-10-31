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

        if (user != null && "reader".equals(user.getRole())) {
            AuthorServices authorServices = null;
            try {
                authorServices = new AuthorServices();
                if (authorServices.registerAsAuthor(user)) {
                    response.sendRedirect(request.getContextPath() + "/author");
                } else {
                    response.sendRedirect(request.getContextPath() + "/error/error.jsp");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if ("author".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/author");
        }
    }
}