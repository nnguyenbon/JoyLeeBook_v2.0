package controller.profileController;


import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Badge;
import model.User;
import services.account.AuthorServices;
import services.account.UserServices;
import services.general.BadgesServices;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        viewProfile(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("edit")) {
            editProfile(request, response);
        } else {
            changePassword(request, response);
        }
    }
    private void viewProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 0;
//        Dùng session bắt xem đây là xem profile bản thân hay tác giả, bắt luôn role
//        int accountId = request.getSession().getAttribute("accountId") == null ? Integer.parseInt(request.getParameter("accountId")) : Integer.parseInt(request.getParameter("accountId"));
        int accountId = 1;
        String role = "reader";
        try {
            UserServices userServices = new UserServices();
            BadgesServices badgesServices = new BadgesServices();

            request.setAttribute("user", userServices.getUser(userId));
            request.setAttribute("badgeList", badgesServices.badgeListFromUser(userId));

            if (accountId == userId && role.equals("reader")) {
                request.setAttribute("pageTitle", "My Profile");
                request.setAttribute("contentPage", "/WEB-INF/views/profile/MyProfile.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
            } else {
                SeriesServices seriesServices = new SeriesServices();
                AuthorServices authorServices = new AuthorServices();

                List<SeriesInfoDTO> seriesInfoDTOList = seriesServices.seriesFromAuthor(userId);
                authorServices.extractDataFromAuthorId(seriesInfoDTOList,request);

                request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
                request.setAttribute("totalSeriesCount", seriesInfoDTOList.size());
                request.getRequestDispatcher("WEB-INF/views/profile/AuthorProfile.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void editProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;

        try {
            String userName = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String bio = request.getParameter("bio");

            User user = new User();
            user.setUserId(userId);
            user.setUsername(userName);
            user.setFullName(fullName);
            user.setBio(bio);

            UserServices userServices = new UserServices();
            boolean isSuccess = userServices.editProfile(user);
            if(isSuccess) {
                request.getSession().setAttribute("message", "Update user successfully!");
            } else {
                request.getSession().setAttribute("message", "Something went wrong. Please try again.");
            }
            response.sendRedirect(request.getContextPath() + "/profile?userId=" + userId);
        } catch(SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;

        try {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            UserServices userServices = new UserServices();
            String message = userServices.editPassword(userId, oldPassword, newPassword, confirmPassword);
            request.getSession().setAttribute("message", message);
            response.sendRedirect(request.getContextPath() + "/profile?userId=" + userId );
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
