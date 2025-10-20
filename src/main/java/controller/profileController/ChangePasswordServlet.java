package controller.profileController;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.UserServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;


        try {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            UserServices userServices = new UserServices();
            String message = userServices.editPassword(userId, oldPassword, newPassword, confirmPassword);

            request.getSession().setAttribute("message", message);
            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
