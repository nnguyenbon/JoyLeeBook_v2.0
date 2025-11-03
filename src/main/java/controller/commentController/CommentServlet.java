package controller.commentController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import model.User;
import services.general.CommentServices;
import services.general.PointServices;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.SQLException;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    private void createComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : 0;
        try {
            String content = request.getParameter("content");
            String chapterId = request.getParameter("chapterId");
            String seriesId = request.getParameter("seriesId");

            CommentServices commentServices = new CommentServices();
            commentServices.createComment(userId, chapterId, content);
            response.sendRedirect(request.getContextPath()
                    + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteComment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = loginedUser != null ? loginedUser.getUserId() : 0;
        try {
            String commentId = request.getParameter("commentId");
            String seriesId = request.getParameter("seriesId");
            String chapterId = request.getParameter("chapterId");

            CommentServices commentService = new CommentServices();
            commentService.deleteComment(userId, commentId);
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
        try {
            String content = request.getParameter("content");
            String commentId = request.getParameter("commentId");
            String seriesId = request.getParameter("seriesId");
            String chapterId = request.getParameter("chapterId");


            CommentServices commentService = new CommentServices();
            commentService.editComment(userId, commentId, content);

            response.sendRedirect(request.getContextPath()
                    + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapterId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
