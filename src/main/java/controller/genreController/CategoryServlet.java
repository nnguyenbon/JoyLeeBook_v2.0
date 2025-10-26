package controller.genreController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.category.CategoryServices;
import utils.ValidationInput;

import java.io.IOException;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action  = request.getParameter("action");
        if (action.equals("add")){
            addCategory(request,response);
        } else if (action.equals("edit")){
            editCategory(request,response);
        } else if (action.equals("delete")){
            deleteCategory(request,response);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            CategoryServices categoryServices = new CategoryServices();
            categoryServices.createCategory(name, description);
//            response.sendRedirect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void editCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            CategoryServices categoryServices = new CategoryServices();
            categoryServices.editCategory(name, description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String categoryId = request.getParameter("categoryId");
            CategoryServices categoryServices = new CategoryServices();
            categoryServices.deleteCategory(categoryId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
