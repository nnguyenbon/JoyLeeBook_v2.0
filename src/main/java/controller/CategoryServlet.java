package controller;

import dao.CategoryDAO;
import dao.SeriesCategoriesDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet implementation class CategoryServlet
 * Handles CRUD operations for Category entities.
 * Supports adding, editing, and deleting categories via POST requests.
 * Expects parameters: action (add, edit, delete), name, description, categoryId.
 * Utilizes CategoryDAO and SeriesCategoriesDAO for database interactions.
 * Validates input parameters and manages database connections.
 * Throws RuntimeException on errors for higher-level handling.
 */
@WebServlet("/category")
public class CategoryServlet extends HttpServlet {
    /**
     * Handles POST requests for adding, editing, and deleting categories.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("add")) {
            addCategory(request, response);
        } else if (action.equals("edit")) {
            editCategory(request, response);
        } else if (action.equals("delete")) {
            deleteCategory(request, response);
        }
    }

    /**
     * Handles GET requests. Currently not implemented.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     * Adds a new category to the database.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void addCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }

            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            boolean success = categoryDAO.insert(category);
            if (!success) {
                throw new SQLException("Failed to update genre into database.");
            }
//            response.sendRedirect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Edits an existing category in the database.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void editCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }

            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            boolean success = categoryDAO.update(category);
            if (!success) {
                throw new SQLException("Failed to update genre into database.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a category from the database.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            int categoryId = ValidationInput.isPositiveInteger(request.getParameter("categoryId")) ? Integer.parseInt(request.getParameter("categoryId")) : -1;
            SeriesCategoriesDAO seriesCategoriesDAO = new SeriesCategoriesDAO(conn);
            CategoryDAO categoryDAO = new CategoryDAO(conn);

            //delete mappings in series_categories table
            boolean deleteMap = seriesCategoriesDAO.deleteByCategoryId(categoryId);
            if (!deleteMap) {
                throw new SQLException("Failed to delete mapping category in database.");
            }

            boolean deleteCategory = categoryDAO.delete(categoryId);
            if (!deleteCategory) {
                throw new SQLException("Failed to delete category in database.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
