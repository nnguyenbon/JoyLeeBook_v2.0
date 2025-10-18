package controller.genreController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.category.CategoryServices;
import services.general.CommentServices;
import utils.ValidationInput;

import java.io.IOException;

@WebServlet("/edit-genre")
public class EditGenreServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            CategoryServices categoryServices = new CategoryServices();
            categoryServices.editCategory(name, description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
