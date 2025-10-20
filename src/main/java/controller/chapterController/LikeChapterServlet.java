package controller.chapterController;

import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Like;
import services.chapter.LikeChapterService;

import java.io.IOException;

@WebServlet("/like-chapter")
public class LikeChapterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       try {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int chapterId = Integer.parseInt(request.getParameter("chapterId"));

                LikeChapterService likeService = new LikeChapterService();
                Like like = new Like();
                like.setUserId(userId);
                like.setChapterId(chapterId);

                int newLikeCount = likeService.likeChapter(like);

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
