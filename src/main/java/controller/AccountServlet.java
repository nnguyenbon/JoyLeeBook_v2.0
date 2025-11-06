package controller;

import dao.AccountDAO;
import dao.SeriesDAO;
import db.DBConnection;
import dto.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
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

        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO dao = new AccountDAO(conn);

            PaginationRequest pageRequest = PaginationUtils.fromRequest(request);
            pageRequest.setOrderBy("id");

            List<Account> accounts = dao.getAllAccounts(search, roleFilter, currentUserRole, pageRequest);
            int totalAccounts = dao.countAccounts(search, roleFilter, currentUserRole);

            request.setAttribute("accounts", accounts);
            request.setAttribute("size", totalAccounts);
            request.setAttribute("search", search);
            request.setAttribute("roleFilter", roleFilter);
            PaginationUtils.sendParameter(request, pageRequest);

            if ("staff".equals(currentUserRole) || "admin".equals(currentUserRole)) {
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_accountList.jsp");
                request.setAttribute("pageTitle", "Manage Accounts");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
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

            if (role == null) {
                response.sendRedirect(request.getContextPath() + "/account/list");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                AccountDAO dao = new AccountDAO(conn);
                Account account = dao.getAccountById(accountId, role);

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
                request.setAttribute("pageTitle", "Account Details");
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

    /* ===========================
       ===== ADD ACCOUNT =========
       =========================== */
    private void showAddAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/account-add.jsp").forward(request, response);
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
            int accountId = Integer.parseInt(request.getParameter("id"));
            String type = request.getParameter("type");

            try (Connection conn = DBConnection.getConnection()) {
                AccountDAO dao = new AccountDAO(conn);
                Account account = dao.getAccountById(accountId, type);

                if (account == null) {
                    request.setAttribute("error", "Account not found!");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("account", account);
                request.getRequestDispatcher("/WEB-INF/views/account-edit.jsp").forward(request, response);
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

        String type = request.getParameter("accountType");
        boolean success = false;

        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO dao = new AccountDAO(conn);

            if ("user".equalsIgnoreCase(type)) {
                User u = new User();
                u.setUsername(request.getParameter("username"));
                u.setFullName(request.getParameter("fullName"));
                u.setEmail(request.getParameter("email"));
                u.setPasswordHash(hashPassword(request.getParameter("password")));
                u.setRole(request.getParameter("role"));
                u.setStatus(request.getParameter("status"));
                u.setBio(request.getParameter("bio"));
                success = dao.insertUser(u);
            } else if ("staff".equalsIgnoreCase(type)) {
                Staff s = new Staff();
                s.setUsername(request.getParameter("username"));
                s.setFullName(request.getParameter("fullName"));
                s.setPasswordHash(hashPassword(request.getParameter("password")));
                s.setRole(request.getParameter("role"));
                success = dao.insertStaff(s);
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
    private void updateAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String type = request.getParameter("accountType");
        boolean success = false;

        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO dao = new AccountDAO(conn);

            if ("user".equalsIgnoreCase(type)) {
                User user = new User();
                user.setUserId(Integer.parseInt(request.getParameter("accountId")));
                user.setFullName(request.getParameter("fullName"));
                user.setEmail(request.getParameter("email"));
                user.setRole(request.getParameter("role"));
                user.setStatus(request.getParameter("status"));
                user.setBio(request.getParameter("bio"));
                success = dao.updateUser(user);
            } else if ("staff".equalsIgnoreCase(type)) {
                Staff staff = new Staff();
                staff.setStaffId(Integer.parseInt(request.getParameter("accountId")));
                staff.setFullName(request.getParameter("fullName"));
                staff.setRole(request.getParameter("role"));
                success = dao.updateStaff(staff);
            }

        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid account ID format.");
            return;
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while updating account.");
            return;
        }

        setFlashMessage(request, success, "Account updated successfully!", "Failed to update account!");
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
            AccountDAO dao = new AccountDAO(conn);
            success = dao.deleteAccount(id, type);
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
