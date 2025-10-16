package controller.profileController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Badge;
import model.User;
import services.account.UserServices;
import services.general.BadgesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/profile")
public class UserProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;

        try {
            UserServices userServices = new UserServices();
            BadgesServices badgesServices = new BadgesServices();

            User user = userServices.getUser(userId);
            List<Badge> bagetList = badgesServices.badgeListFromUser(userId);
            request.setAttribute("user", user);
            request.setAttribute("badges", bagetList);

            request.setAttribute("pageTitle", "My Profile");
            request.setAttribute("contentPage", "/WEB-INF/views/profile/MyProfile.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
