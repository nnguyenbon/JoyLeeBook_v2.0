package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.general.CommentServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/create-comment")
public class CreateCommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            String content = request.getParameter("content");
            String chapterId = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");

            System.out.println("Content = " + content);
            System.out.println("ChapterId = " + chapterId);
            System.out.println("SeriesId = " + seriesId);

            CommentServices commentServices = new CommentServices();
            commentServices.createComment(1, chapterId, content);

            response.sendRedirect(request.getContextPath()
                    + "/chapter-content?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
