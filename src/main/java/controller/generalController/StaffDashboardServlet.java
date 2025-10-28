package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Staff;
import model.User;
import services.account.StaffServices;

import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;

import java.sql.SQLException;


@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Staff loginedStaff = (Staff) AuthenticationUtils.getLoginedUser(request.getSession());
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");

        try {
            StaffServices staffServices = new StaffServices();
            Staff staff = staffServices.getStaffAccount(loginedStaff == null ? -1 : loginedStaff.getStaffId());
            if (staffServices.handleRedirect(type, request, response)) {
                return;
            }

            request.setAttribute("type", type != null ? type : "series");
            request.setAttribute("staffId", staff.getStaffId());
            request.setAttribute("staffName", staff.getFullName());
            request.getRequestDispatcher("/WEB-INF/views/general/StaffDashboard.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
