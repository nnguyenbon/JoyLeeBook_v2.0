package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Comment;
import services.general.CommentServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/edit-comment")
public class EditCommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            String content = request.getParameter("content");
            String commentId = request.getParameter("commentId");
            String seriesId = request.getParameter("seriesId");
            String chapterId = request.getParameter("chapterId");

            System.out.println("Content = " + content);
            System.out.println("ChapterId = " + chapterId);
            System.out.println("SeriesId = " + seriesId);

            CommentServices commentService = new CommentServices();
            commentService.editComment(1, commentId, content);

            response.sendRedirect(request.getContextPath()
                    + "/chapter-content?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
