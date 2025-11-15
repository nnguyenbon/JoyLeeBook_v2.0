package controller;

import dao.CategoryDAO;
import dao.SeriesCategoriesDAO;
import db.DBConnection;
import model.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Category;
import model.Staff;
import utils.PaginationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/category/*")
public class CategoryServlet extends HttpServlet {

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
                case "/list" -> viewCategoryList(request, response);
                case "/detail" -> viewCategoryDetail(request, response);
                case "/add" -> showAddCategory(request, response);
                case "/edit" -> showEditCategory(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleServerError(request, response, e, "Unexpected error in GET request.");
        }
    }

    /* ===========================
       ======= HTTP POST =========
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
                case "/insert" -> insertCategory(request, response);
                case "/update" -> updateCategory(request, response);
                case "/delete" -> deleteCategory(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleServerError(request, response, e, "Unexpected error in POST request.");
        }
    }

    /* ===========================
       ====== VIEW LIST ==========
       =========================== */
    private void viewCategoryList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);

        try (Connection conn = DBConnection.getConnection()) {
            CategoryDAO dao = new CategoryDAO(conn);

            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("updated_at");
            paginationRequest.setSortDir("desc");

            List<Category> categories = dao.getAll();
            int totalCategories = categories.size();

            request.setAttribute("size", totalCategories);
            request.setAttribute("search", search);
            request.setAttribute("categories", categories);
            PaginationUtils.sendParameter(request, paginationRequest);

            if (isAjax) {
                request.getRequestDispatcher("/WEB-INF/views/category/_categoryList.jsp").forward(request, response);
            } else {
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_categoryList.jsp");
                request.setAttribute("pageTitle", "Manage Categories");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            }

        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while listing categories.");
        }
    }

    /* ===========================
       ===== VIEW DETAIL =========
       =========================== */
    private void viewCategoryDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int categoryId = Integer.parseInt(request.getParameter("id"));
            try (Connection conn = DBConnection.getConnection()) {
                CategoryDAO dao = new CategoryDAO(conn);
                Category category = dao.findById(categoryId);
                if (category == null) {
                    request.setAttribute("error", "Category not found!");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("category", category);
                request.setAttribute("contentPage", "/WEB-INF/views/category/_categoryDetail.jsp");
                request.setAttribute("pageTitle", "Category Detail");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid category ID format.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while retrieving category detail.");
        }
    }

    /* ===========================
       ===== ADD CATEGORY ========
       =========================== */
    private void showAddCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        request.setAttribute("contentPage", "/WEB-INF/views/category/_addCategory.jsp");
        request.setAttribute("pageTitle", "Add Category");
        request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
    }

    private void insertCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean success = false;
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        try (Connection conn = DBConnection.getConnection()) {
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("message", "Category name cannot be empty!");
                showAddCategory(request, response);
                return;
            }

            CategoryDAO dao = new CategoryDAO(conn);
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);

            success = dao.insert(category);

        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while inserting category.");
            return;
        }

        setFlashMessage(request, success, "Category added successfully!", "Failed to add category!");
        response.sendRedirect(request.getContextPath() + "/category/list");
    }

    /* ===========================
       ===== EDIT CATEGORY =======
       =========================== */
    private void showEditCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            try (Connection conn = DBConnection.getConnection()) {
                CategoryDAO dao = new CategoryDAO(conn);
                Category category = dao.findById(id);

                if (category == null) {
                    request.setAttribute("error", "Category not found!");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("category", category);
                request.setAttribute("contentPage", "/WEB-INF/views/category/_editCategory.jsp");
                request.setAttribute("pageTitle", "Edit Category");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid category ID.");
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while loading edit form.");
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean success = false;

        try (Connection conn = DBConnection.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            CategoryDAO dao = new CategoryDAO(conn);
            Category category = new Category();
            category.setCategoryId(id);
            category.setName(name);
            category.setDescription(description);

            success = dao.update(category);
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while updating category.");
            return;
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid category ID format.");
            return;
        }

        setFlashMessage(request, success, "Category updated successfully!", "Failed to update category!");
        response.sendRedirect(request.getContextPath() + "/category/list");
    }

    /* ===========================
       ===== DELETE CATEGORY =====
       =========================== */
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        boolean success = false;
        try (Connection conn = DBConnection.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));
            SeriesCategoriesDAO mapDao = new SeriesCategoriesDAO(conn);
            CategoryDAO dao = new CategoryDAO(conn);

            mapDao.deleteByCategoryId(id);
            success = dao.delete(id);
        } catch (SQLException | ClassNotFoundException e) {
            handleServerError(request, response, e, "Database error while deleting category.");
            return;
        } catch (NumberFormatException e) {
            handleClientError(request, response, "Invalid category ID format.");
            return;
        }

        setFlashMessage(request, success, "Category deleted successfully!", "Failed to delete category!");
        response.sendRedirect(request.getContextPath() + "/category/list");
    }

    /* ===========================
       ======= HELPERS ===========
       =========================== */
    private boolean isAdmin(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("loginedUser");
        if (obj instanceof Staff staff) return "admin".equalsIgnoreCase(staff.getRole());
        return false;
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
}
