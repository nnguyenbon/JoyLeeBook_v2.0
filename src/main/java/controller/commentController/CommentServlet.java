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
import utils.TrackPointUtils;
import utils.AuthenticationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet implementation class CommentServlet
 * Handles creation, updating, and deletion of comments.
 */
@WebServlet("/comment/*")
public class CommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        //Determine action based on the path
        switch (action) {
            case "/create" -> createComment(request, response);
            case "/edit" -> updateComment(request, response);
            case "/delete" -> deleteComment(request, response);
        }

    }

    /**
     * Handles GET requests (not used in this servlet).
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * Handles the creation of a new comment.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void createComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : 0;
        try (Connection conn = DBConnection.getConnection()) {
            CommentDAO commentDAO = new CommentDAO(conn);
            String content = request.getParameter("content");
            String chapterIdParam = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");

            //Validate input
            if (content == null || content.trim().isEmpty() || chapterIdParam == null) {
                throw new IllegalArgumentException("No content for this comment");
            }

            //Parse chapterId
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
            TrackPointUtils.trackAction(userId, 2, "Comment  a chapter", "comment", commentDAO.findByUserIdAndChapterId(userId, chapterId).getCommentId(), 5);

            response.sendRedirect(request.getContextPath()
                    + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the deletion of a comment.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
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
            //Send JSON response
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"Your comment has been successfully deleted\" }");
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Your comment has been unsuccessfully deleted\" }");
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the updating of a comment.
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
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
