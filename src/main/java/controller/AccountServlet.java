package controller;

import dao.AccountDAO;
import dao.BadgeDAO;
import dao.SeriesDAO;
import dao.UserDAO;
import db.DBConnection;
import dto.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import utils.AuthenticationUtils;
import utils.PaginationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/account/*")
public class AccountServlet extends HttpServlet {

    /* ===========================
       ======= HTTP GET ==========
       =========================== */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) pathInfo = "/list";

        try {
            switch (pathInfo) {
                case "/list" -> viewAccountList(request, response);
                case "/detail" -> viewAccountDetail(request, response);
                case "/add" -> showAddAccount(request, response);
                case "/edit" -> showEditAccount(request, response);
                case "/ranking" -> showRanking(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleServerError(request, response, e, "Unexpected error in GET request.");
        }
    }

    /* ===========================
       ======= HTTP POST ==========
       =========================== */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            switch (pathInfo) {
                case "/insert" -> insertAccount(request, response);
                case "/update" -> updateAccount(request, response);
                case "/delete" -> deleteAccount(request, response);
                case "/update-status" -> updateStatus(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleServerError(request, response, e, "Unexpected error in POST request.");
        }
    }

    /* ===========================
       ======= VIEW LIST ==========
       =========================== */
    private void viewAccountList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String currentUserRole = getCurrentRole(request);
        String search = request.getParameter("search");
        String roleFilter = request.getParameter("roleFilter");
        // Check if this is an AJAX request
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);

        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO dao = new AccountDAO(conn);

            PaginationRequest pageRequest = PaginationUtils.fromRequest(request);
            pageRequest.setOrderBy("id");

            List<Account> accounts = dao.getAllAccounts(search, roleFilter, currentUserRole, pageRequest);
            int totalAccounts = dao.countAccounts(search, roleFilter, currentUserRole);


            request.setAttribute("size", totalAccounts);
            request.setAttribute("search", search);
            request.setAttribute("roleFilter", roleFilter);
            PaginationUtils.sendParameter(request, pageRequest);

            if ("staff".equals(currentUserRole) || "admin".equals(currentUserRole)) {
                request.setAttribute("accounts", accounts);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_accountList.jsp");
                request.setAttribute("pageTitle", "Manage Accounts");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
                if (isAjax) {
                    // Return only the author list fragment for AJAX requests
                    request.setAttribute("authorList", accounts);
                    request.getRequestDispatcher("/WEB-INF/views/author/_authorList.jsp").forward(request, response);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while listing accounts.");
        }
    }

    /* ===========================
       ====== ACCOUNT DETAIL =====
       =========================== */
    private void viewAccountDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int accountId = Integer.parseInt(request.getParameter("accountId"));
            String role = request.getParameter("role");
            try (Connection conn = DBConnection.getConnection()) {
                AccountDAO accountDAO = new AccountDAO(conn);
                Account account = accountDAO.getAccountById(accountId, role);

                if (account == null) {
                    request.setAttribute("error", "Account not found!");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                if ("author".equals(role)) {
                    SeriesDAO seriesDAO = new SeriesDAO(conn);
                    List<Series> authorSeriesList = seriesDAO.getSeriesByAuthorId(accountId);
                    request.setAttribute("authorSeriesList", authorSeriesList);
                }

                request.setAttribute("account", account);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_accountDetail.jsp");
                request.setAttribute("pageTitle", "Manage Accounts");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid accountId format.");
        } catch (SQLException e) {
            handleServerError(request, response, e, "Database error while retrieving account detail.");
        }
    }

    private void showRanking (HttpServletRequest request, HttpServletResponse response ){
        try (Connection conn = DBConnection.getConnection()){
            UserDAO userDAO = new UserDAO(conn);
            request.setAttribute("userList",  userDAO.selectTopUserPoints(8));
            request.getRequestDispatcher("/WEB-INF/views/general/_userRanking.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException | ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* ===========================
       ===== ADD ACCOUNT =========
       =========================== */
    private void showAddAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        request.setAttribute("type", "staff");
        request.setAttribute("pageTitle", "Manage Accounts");
        request.setAttribute("contentPage","/WEB-INF/views/staff/_showAddAccount.jsp");
        request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
    }

    /* ===========================
       ===== EDIT ACCOUNT ========
       =========================== */
    private void showEditAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));

            try (Connection conn = DBConnection.getConnection()) {
                AccountDAO accountDAO = new AccountDAO(conn);
                Account account = accountDAO.getStaffById(staffId);
                if (account == null) {
                    request.setAttribute("error", "Account not found!");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("staffId", staffId);
                request.setAttribute("username", account.getUsername());
                request.setAttribute("fullName", account.getFullName());
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_showEditAccount.jsp");
                request.setAttribute("pageTitle", "Manage Accounts");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid account ID.");
        } catch (SQLException e) {
            handleServerError(request, response, e, "Database error while editing account.");
        }
    }

    /* ===========================
       ===== INSERT ACCOUNT ======
       =========================== */
    private void insertAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String type = request.getParameter("type");
        boolean success = false;

        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAO(conn);
            String userName = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String confirmPassword = request.getParameter("confirmPassword");
            if (accountDAO.checkByUsername(userName)) {
                request.setAttribute("message", "Username is already exist.");
                request.setAttribute("username", userName);
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);
                request.setAttribute("password", password);
                showAddAccount(request, response);
                return;
            }
            //Check if email already exists
            if (email != null && accountDAO.checkByEmail(email)) {
                request.setAttribute("message", "Email is already exist.");
                request.setAttribute("fullName", fullName);
                request.setAttribute("username", userName);
                request.setAttribute("email", email);
                request.setAttribute("password", password);
                showAddAccount(request, response);
                return;
            }
            //Check if password and confirm password match
            if (!password.equals(confirmPassword)) {
                request.setAttribute("message", "Your confirm password is not correct.");
                request.setAttribute("fullName", fullName);
                request.setAttribute("username", userName);
                request.setAttribute("email", email);
                request.setAttribute("password", password);
                showAddAccount(request, response);
                return;
            }
            if ("reader".equalsIgnoreCase(type)) {
                User user = new User();
                user.setUsername(userName);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPasswordHash(AuthenticationUtils.hashPwd(password));
                user.setRole("reader");
                success = accountDAO.insertUser(user);
            } else if ("staff".equalsIgnoreCase(type)) {
                Staff staff = new Staff();
                staff.setUsername(userName);
                staff.setFullName(fullName);
                staff.setPasswordHash(AuthenticationUtils.hashPwd(password));
                staff.setRole("staff");
                success = accountDAO.insertStaff(staff);
            }
            if (success) {
                setFlashMessage(request, success, "Account created successfully!", "Failed to add account!");
                response.sendRedirect(request.getContextPath() + "/account/list");
                return;
            }
        } catch (SQLException e) {
            handleServerError(request, response, e, "Database error while inserting account.");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        setFlashMessage(request, success, "Account created successfully!", "Failed to create account!");
        response.sendRedirect(request.getContextPath() + "/account/list");
    }

    /* ===========================
       ===== UPDATE ACCOUNT ======
       =========================== */
    /* ===========================
   ===== UPDATE ACCOUNT (STAFF ONLY) ======
   =========================== */
    private void updateAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Kiểm tra quyền admin (chỉ admin mới được cập nhật)
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean success = false;

        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAO(conn);

            String staffIdStr = request.getParameter("staffId");
            String fullName = request.getParameter("fullName");
            String password = request.getParameter("password");

            // Kiểm tra ID hợp lệ
            int staffId;
            try {
                staffId = Integer.parseInt(staffIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("message", "Invalid staff ID format.");
                request.setAttribute("accountId", staffIdStr);
                request.setAttribute("fullName", fullName);
                showEditAccount(request, response);
                return;
            }

            // Kiểm tra xem staff có tồn tại không
            if (!accountDAO.checkStaffById(staffId)) {
                request.setAttribute("message", "Staff account does not exist.");
                request.setAttribute("accountId", staffId);
                request.setAttribute("fullName", fullName);
                showEditAccount(request, response);
                return;
            }

            // Tạo đối tượng Staff và cập nhật
            Staff staff = new Staff();
            staff.setStaffId(staffId);
            staff.setFullName(fullName);
            staff.setRole("staff");
            if (password != null && !password.isBlank()) {
                staff.setPasswordHash(AuthenticationUtils.hashPwd(password));
            }
            success = accountDAO.updateStaff(staff);

            if (success) {
                setFlashMessage(request, success, "Staff account updated successfully!", "Failed to update staff account!");
                response.sendRedirect(request.getContextPath() + "/account/list");
                return;
            }

        } catch (SQLException e) {
            handleServerError(request, response, e, "Database error while updating staff account.");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Nếu thất bại (không có ngoại lệ nhưng update trả về false)
        setFlashMessage(request, success, "Staff account updated successfully!", "Failed to update staff account!");
        response.sendRedirect(request.getContextPath() + "/account/list");
    }

    /* ===========================
       ===== DELETE ACCOUNT ======
       =========================== */
    private void deleteAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean success = false;

        try (Connection conn = DBConnection.getConnection()) {
            int id = Integer.parseInt(request.getParameter("accountId"));
            String type = request.getParameter("accountType");
            AccountDAO accountDAO = new AccountDAO(conn);
            success = accountDAO.deleteAccount(id, type);
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid account ID format.");
            return;
        } catch (SQLException e) {
            handleServerError(request, response, e, "Database error while deleting account.");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        setFlashMessage(request, success, "Account deleted successfully!", "Failed to delete account!");
        response.sendRedirect(request.getContextPath() + "/account/list");
    }

    /* ===========================
       ===== UPDATE STATUS =======
       =========================== */
    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String role = getCurrentRole(request);
        if (!"admin".equalsIgnoreCase(role) && !"staff".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean success = false;
        try (Connection conn = DBConnection.getConnection()) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String status = request.getParameter("status");
            AccountDAO dao = new AccountDAO(conn);
            success = dao.updateUserStatus(userId, status);
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid userId format.");
            return;
        } catch (SQLException e) {
            handleServerError(request, response, e, "Database error while updating status.");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        setFlashMessage(request, success, "Status updated successfully!", "Failed to update status!");
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/account/list");
    }

    /* ===========================
       ======= HELPERS ===========
       =========================== */
    private boolean isAdmin(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("loginedUser");
        if (obj instanceof Staff staff) return "admin".equalsIgnoreCase(staff.getRole());
        return false;
    }

    private String getCurrentRole(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("loginedUser");
        if (obj instanceof User user) return user.getRole();
        if (obj instanceof Staff staff) return staff.getRole();
        return "reader";
    }

    private void setFlashMessage(HttpServletRequest req, boolean success, String ok, String fail) {
        HttpSession session = req.getSession();
        session.setAttribute(success ? "success" : "error", success ? ok : fail);
    }

    private void handleClientError(HttpServletRequest req, HttpServletResponse res, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, res);
    }

    private void handleServerError(HttpServletRequest req, HttpServletResponse res, Exception e, String msg)
            throws ServletException, IOException {
        e.printStackTrace();
        req.setAttribute("error", msg + " " + e.getMessage());
        req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, res);
    }

    private String hashPassword(String password) {
        // TODO: replace with BCrypt
        return password;
    }
}
