package controller.commentController;

import dao.CommentDAO;
import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Comment;
import model.User;
import services.general.PointServices;
import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/comment/*")
public class CommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        switch (action) {
            case "/create" -> createComment(request, response);
            case "/edit" -> updateComment(request, response);
            case "/delete" -> deleteComment(request, response);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    private void createComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : 0;
        try (Connection conn = DBConnection.getConnection()) {
            CommentDAO commentDAO = new CommentDAO(conn);
            String content = request.getParameter("content");
            String chapterIdParam = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");

            if (content == null || content.trim().isEmpty() || chapterIdParam == null) {
                throw new IllegalArgumentException("No content for this comment");
            }

            int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : 0;

            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setChapterId(chapterId);
            comment.setContent(content);
            comment.setDeleted(false);

            boolean success = commentDAO.insert(comment);
            if (!success) {
                throw new SQLException("Failed to insert comment into database.");
            }
            PointServices.trackAction(userId, 2, "Comment  a chapter", "comment", commentDAO.findByUserIdAndChapterId(userId, chapterId).getCommentId());

            response.sendRedirect(request.getContextPath()
                    + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : 0;
        try (Connection conn = DBConnection.getConnection()) {
            CommentDAO commentDAO = new CommentDAO(conn);
            String commentId = request.getParameter("commentId");
            String seriesId = request.getParameter("seriesId");
            String chapterId = request.getParameter("chapterId");

            if (commentId == null || !commentId.matches("\\d+")) {
                throw new IllegalArgumentException("Invalid comment ID.");
            }

            int commentIdParam = Integer.parseInt(commentId);
            boolean success = commentDAO.softDelete(commentIdParam);
            if (!success) {
                throw new SQLException("Failed to delete comment in database.");
            }
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"Your comment has been successfully deleted\" }");
//            response.sendRedirect(request.getContextPath()
//                    + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Your comment has been unsuccessfully deleted\" }");
            throw new RuntimeException(e);
        }
    }

    private void updateComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : 0;
        try (Connection conn = DBConnection.getConnection()) {
            CommentDAO commentDAO = new CommentDAO(conn);
            String content = request.getParameter("content");
            String commentId = request.getParameter("commentId");
            String seriesId = request.getParameter("seriesId");
            String chapterId = request.getParameter("chapterId");


            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be empty.");
            }
            if (commentId == null || !commentId.matches("\\d+")) {
                throw new IllegalArgumentException("Invalid comment ID.");
            }

            int commentIdParam = ValidationInput.isPositiveInteger(commentId) ? Integer.parseInt(commentId) : 0;

            Comment comment = new Comment();
            comment.setCommentId(commentIdParam);
            comment.setUserId(userId);
            comment.setContent(content);

            boolean success = commentDAO.update(comment);
            if (!success) {
                throw new SQLException("Failed to update comment into database.");
            }
            response.sendRedirect(request.getContextPath()
                    + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
