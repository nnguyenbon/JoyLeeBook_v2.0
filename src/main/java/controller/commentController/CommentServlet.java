package controller.commentController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.general.CommentServices;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("create")){
            createComment(request, response);
        } else if (action.equals("edit")){
            updateComment(request, response);
        } else  if (action.equals("delete")){
            deleteComment(request, response);
        }

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    private void createComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String content = request.getParameter("content");
            String chapterId = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");

            CommentServices commentServices = new CommentServices();
            commentServices.createComment(1, chapterId, content);

            response.sendRedirect(request.getContextPath()
                    + "/chapter-content?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String commentId = request.getParameter("commentId");
            String seriesId = request.getParameter("seriesId");
            String chapterId = request.getParameter("chapterId");

            CommentServices commentService = new CommentServices();
            commentService.deleteComment(1, commentId);

            response.sendRedirect(request.getContextPath()
                    + "/chapter-content?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

}
