package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Staff;
import services.account.StaffServices;

import utils.ValidationInput;

import java.io.IOException;

import java.sql.SQLException;


@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int staffId = ValidationInput.isPositiveInteger(request.getParameter("staffId")) ? Integer.parseInt(request.getParameter("staffId")) : 1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");

        try {
            StaffServices staffServices = new StaffServices();
            Staff staff = staffServices.getStaffAccount(staffId);
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
