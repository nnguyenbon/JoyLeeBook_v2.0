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

/**
 * Servlet implementation class StaffDashboardServlet
 * Handles requests for the staff dashboard page.
 * Retrieves dashboard statistics, recent actions, and quick stats for the logged-in staff member.
 * Forwards the data to the StaffDashboard JSP for rendering.
 */
@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {

    /**
     * Handles the HTTP POST method.
     * Currently not implemented.
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * Handles the HTTP GET method.
     * Retrieves dashboard statistics, recent actions, and quick stats for the logged-in staff member.
     * Forwards the data to the StaffDashboard JSP for rendering.
     * @param request  the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int staffId = ValidationInput.isPositiveInteger(request.getParameter("staffId")) ? Integer.parseInt(request.getParameter("staffId")) : 1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");

        try {
            //Check authentication
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
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
        }catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
