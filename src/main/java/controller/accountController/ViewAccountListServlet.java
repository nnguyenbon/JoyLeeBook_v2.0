package controller.accountController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.AuthorServices;
import services.account.StaffServices;
import services.category.CategoryServices;
import services.general.CommentServices;
import utils.ValidationInput;

import java.io.IOException;

@WebServlet("/view-account-list")
public class ViewAccountListServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            StaffServices staffServices = new StaffServices();
            AuthorServices authorServices = new AuthorServices();
            request.setAttribute("accountList", userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
