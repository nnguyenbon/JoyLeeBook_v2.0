package controller.adminController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.Staff;
import dao.UserDAO;
import dao.StaffDAO;
import db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * UserListServlet handles requests to display a list of all users.
 * It ensures that only authenticated staff members can access the user list.
 * <p>
 * URL: /admin/users
 * Method: GET
 * <p>
 * Session:
 * - Requires userId and username in session to identify the logged-in staff member
 * <p>
 * Forwards to:
 * - /WEB-INF/views/admin/UserList.jsp on successful GET request for staff members
 * - Redirects to /login if not logged in
 * - Sends 403 Forbidden if the user does not have staff permissions
 *
 * @author HaiDD-dev
 */
@WebServlet(name = "UserListServlet", value = "/admin/users")
public class UserListServlet extends HttpServlet {

    /**
     * Handles GET requests to display the user list.
     * Validates user session and permissions before displaying the list.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        // cần xem xét lại
        Integer staffId = (Integer) request.getSession().getAttribute("staffId");

        if (staffId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            StaffDAO staffDAO = new StaffDAO(conn);

            Staff currentStaff = staffDAO.findByUsernameForLogin(username);

            if (currentStaff != null) {
                UserDAO userDAO = new UserDAO(conn);
                List<User> users = userDAO.getAll();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/views/admin/UserList.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
        }
    }
}