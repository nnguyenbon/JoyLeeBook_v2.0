package controller.reactionController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import services.like.LikeService;

import java.io.IOException;

@WebServlet("/reaction")
public class ReactionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("like")){
            likeChapter(request,response);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    private void likeChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginedUser");
        String role = user.getRole();
        if (!role.equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Please login to like chapter");
            return;
        }
        try {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int chapterId = Integer.parseInt(request.getParameter("chapterId"));
                LikeService likeService = new LikeService();
                int newLikeCount = likeService.likeChapter(userId, chapterId);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\": true, \"newLikeCount\": " + newLikeCount + ", \"liked\": true }");
            }catch (Exception e) {
//                System.out.println("series " + e.getMessage());
                request.setAttribute("error", "Could not insert like data. " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
//            System.out.println("number " + e.getMessage());
            request.setAttribute("error", "Invalid userId/chapterId.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }
}
