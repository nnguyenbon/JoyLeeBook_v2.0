package controller.generalController;

import dto.staff.DashboardStatsDTO;
import dto.staff.QuickStatsDTO;
import dto.staff.RecentActionDTO;
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

import java.util.List;


@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int staffId = ValidationInput.isPositiveInteger(request.getParameter("staffId")) ? Integer.parseInt(request.getParameter("staffId")) : 1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");

        try {
            StaffServices staffServices = new StaffServices();
            DashboardStatsDTO stats = staffServices.getDashboardStats(staffId);
            List<RecentActionDTO> recentActions = staffServices.getRecentActions(staffId, 4);
            QuickStatsDTO quickStats = staffServices.getQuickStatsToday(staffId);

            // Set attributes cho JSP
            request.setAttribute("dashboardStats", stats);
            request.setAttribute("recentActions", recentActions);
            request.setAttribute("quickStats", quickStats);

            request.setAttribute("pageTitle", "Staff Dashboard");
            request.setAttribute("activePage", "overview");
            request.setAttribute("contentPage", "/WEB-INF/views/general/StaffDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
        }catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
