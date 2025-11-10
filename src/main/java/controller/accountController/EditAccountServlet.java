package controller.accountController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.StaffServices;
import services.account.UserServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/edit-account")
public class EditAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;

        try {
            String role = request.getParameter("role");
            String username =  request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String password = request.getParameter("password");

            if(role.equalsIgnoreCase("staff")){
                StaffServices staffServices = new StaffServices();
                staffServices.editStaff(username, fullName, password);
            } else {
                UserServices userServices = new UserServices();
                userServices.editUser(username, fullName);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
