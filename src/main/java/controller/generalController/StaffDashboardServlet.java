package controller.generalController;

import dto.staff.DashboardStatsDTO;
import dto.staff.QuickStatsDTO;
import dto.staff.RecentActionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.StaffServices;

import java.io.IOException;

import java.util.List;


@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Staff loginedStaff = (Staff) request.getSession().getAttribute("loginedStaff");
        try {
            StaffServices staffServices = new StaffServices();
//            Staff currentStaff = staffServices.getCurrentStaff(loginedStaff.getStaffId());

//            int staffId = currentStaff.getStaffId();
            int staffId = 1;
            DashboardStatsDTO stats = staffServices.getDashboardStats(staffId);
            List<RecentActionDTO> recentActions = staffServices.getRecentActions(staffId, 4);
            QuickStatsDTO quickStats = staffServices.getQuickStatsToday(staffId);

            // Set attributes cho JSP
//            request.setAttribute("currentStaff", currentStaff);
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
