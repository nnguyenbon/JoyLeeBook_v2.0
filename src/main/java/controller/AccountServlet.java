package controller;

import dao.AccountDAO;
import dto.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dto.AccountDTO;
import model.Account;
import model.BanReason;
import model.Staff;
import model.User;
import services.account.AccountServices;
import utils.AuthenticationUtils;
import utils.PaginationUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/account/*")
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getPathInfo();
            if (action == null) action = "";

            switch (action) {
                case "/add" -> showAddAccount(request, response);
                case "/edit" -> showEditAccount(request, response);
                case "/detail" -> viewAccountDetail(request, response);
                case "/list" -> viewAccountList(request, response);
                default -> throw new ServletException("Invalid path or function does not exist.");
            }
        }catch (ServletException e){
            e.printStackTrace();
            throw e;
        }
    }

    private void viewAccountList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Determine user role and context
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(req.getSession());
        String role = "reader";

        if (loggedInAccount != null) {
            if (loggedInAccount instanceof User user) {
                role = user.getRole();
            } else if (loggedInAccount instanceof Staff staff) {
                role = staff.getRole();
            }
        }
        try {
            String search = req.getParameter("search");
            String filterByRole = req.getParameter("filterByRole");
            PaginationRequest paginationRequest = PaginationUtils.fromRequest(req);
            paginationRequest.setOrderBy("id");
            AccountDAO accountDAO = new AccountDAO();
            // Gọi service để lấy danh sách tài khoản phù hợp với role người đăng nhập
            List<Account> accountList = accountDAO.getAccounts(search,filterByRole, role, paginationRequest);
            int totalRecords = accountDAO.getTotalAccounts(search, filterByRole, role);

            req.setAttribute("accountList", accountList);
            req.setAttribute("size", totalRecords);
            req.setAttribute("search", search);
            PaginationUtils.sendParameter(req, paginationRequest);

            //forward theo role
            if ("reader".equalsIgnoreCase(role)) {
                req.setAttribute("contentPage", "/WEB-INF/views/_searchAuthor.jsp");
                req.setAttribute("search", search);
                req.setAttribute("pageTitle", "Search");
                req.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(req, resp);

            }
            req.setAttribute("contentPage", "/WEB-INF/views/staff/_usersListView.jsp");
            req.setAttribute("activePage", "users");
            req.setAttribute("filterByRole", filterByRole);
            req.setAttribute("pageTitle", "Manage Users");
            req.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi khi tải danh sách tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error/error.jsp").forward(req, resp);
        }
    }

    private void viewAccountDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int accountId = Integer.parseInt(req.getParameter("id"));

            AccountServices accountServices = new AccountServices();
            AccountDTO acc = accountServices.getAccountDetail(accountId);

            if (acc == null) {
                req.setAttribute("error", "Không tìm thấy tài khoản với ID: " + accountId);
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("account", acc);
            req.getRequestDispatcher("/views/account/accountDetail.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi tải thông tin tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    private void searchAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String query = req.getParameter("query");
            if (query == null || query.trim().isEmpty()) {
                req.setAttribute("error", "Query tìm kiếm không hợp lệ.");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null) {
                resp.sendRedirect("login.jsp");
                return;
            }

            AccountServices accountServices = new AccountServices();
            String role = (String) session.getAttribute("role");
            List<AccountDTO> results = accountServices.searchAccounts(query, role);

            req.setAttribute("accountList", results);
            req.setAttribute("query", query);
            req.getRequestDispatcher("/views/account-search.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi khi tìm kiếm tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    private void showAddAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null || (!"staff".equals(session.getAttribute("role")) && !"admin".equals(session.getAttribute("role")))) {
            req.setAttribute("error", "Bạn không có quyền thêm tài khoản.");
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
            return;
        }

        req.getRequestDispatcher("/views/account/addAccount.jsp").forward(req, resp);
    }

    private void showEditAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int accountId = Integer.parseInt(req.getParameter("id"));

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null || (!"staff".equals(session.getAttribute("role")) && !"admin".equals(session.getAttribute("role")))) {
                req.setAttribute("error", "Bạn không có quyền chỉnh sửa tài khoản.");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            AccountServices accountServices = new AccountServices();
            AccountDTO acc = accountServices.getAccountDetail(accountId);

            if (acc == null) {
                req.setAttribute("error", "Không tìm thấy tài khoản với ID: " + accountId);
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("account", acc);
            req.getRequestDispatcher("/views/account/editAccount.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi tải thông tin tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    private void deleteAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int accountId = Integer.parseInt(req.getParameter("id"));

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null || !"admin".equals(session.getAttribute("role"))) {
                req.setAttribute("error", "Bạn không có quyền xóa tài khoản.");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            AccountServices accountServices = new AccountServices();
            accountServices.deleteAccount(accountId);

            // Redirect back to list
            resp.sendRedirect(req.getContextPath() + "/account?action=viewAccountList");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi khi xóa tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    private void banAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int accountId = Integer.parseInt(req.getParameter("id"));
            String reason = req.getParameter("reason"); // Assume reason is passed as param, e.g., from form

            if (reason == null || reason.trim().isEmpty()) {
                req.setAttribute("error", "Lý do cấm không được để trống.");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null || (!"staff".equals(session.getAttribute("role")) && !"admin".equals(session.getAttribute("role")))) {
                req.setAttribute("error", "Bạn không có quyền cấm tài khoản.");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            AccountServices accountServices = new AccountServices();

            // Assuming BanReason is an enum; map string to enum if needed
            BanReason banReason = BanReason.valueOf(reason.toUpperCase()); // Adjust as per actual enum\
            accountServices.banAccount(accountId, banReason);

            // Redirect back to list
            resp.sendRedirect(req.getContextPath() + "/account?action=viewAccountList");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi khi cấm tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getPathInfo();
            if (action == null) action = "";

            switch (action) {
                case "/insert" -> insertAccount(request, response);
                case "/update" -> updateAccount(request, response);
                case "/delete" -> deleteAccount(request, response);
                case "/ban" -> banAccount(request, response);
                default -> throw new ServletException("Invalid path or function does not exist.");
            }
        }catch (ServletException e){
            e.printStackTrace();
            throw e;
        }
    }

    private void insertAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = req.getParameter("username");
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String role = req.getParameter("role");
            String password = req.getParameter("password"); // Assume password is hashed elsewhere

            if (username == null || fullName == null || email == null || role == null || password == null) {
                req.setAttribute("error", "Thông tin không đầy đủ.");
                showAddAccount(req, resp);
                return;
            }

            AccountDTO newAccount = new AccountDTO();
            newAccount.setUsername(username);
            newAccount.setFullName(fullName);
            newAccount.setEmail(email);
            newAccount.setRole(role);
            newAccount.setStatus("active");
            newAccount.setType("user"); // Default to user

            AccountServices accountServices = new AccountServices();
            accountServices.createAccount(newAccount, password); // Pass password separately for hashing

            resp.sendRedirect(req.getContextPath() + "/account?action=viewAccountList");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi khi tạo tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    private void updateAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int accountId = Integer.parseInt(req.getParameter("id"));
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String role = req.getParameter("role");
            String status = req.getParameter("status");

            if (fullName == null || email == null || role == null || status == null) {
                req.setAttribute("error", "Thông tin không đầy đủ.");
                showEditAccount(req, resp);
                return;
            }

            AccountDTO updatedAccount = new AccountDTO();
            updatedAccount.setId(accountId);
            updatedAccount.setFullName(fullName);
            updatedAccount.setEmail(email);
            updatedAccount.setRole(role);
            updatedAccount.setStatus(status);

            AccountServices accountServices = new AccountServices();
            accountServices.updateAccount(updatedAccount);

            resp.sendRedirect(req.getContextPath() + "/account?action=viewAccountList");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Lỗi khi cập nhật tài khoản: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
