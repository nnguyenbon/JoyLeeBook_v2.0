package controller.chapterController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/staff-chapter")
public class ChapterContentForStaffServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int chapterId = ValidationInput.isPositiveInteger(request.getParameter("chapterId")) ? Integer.parseInt(request.getParameter("chapterId")) : 1;
        try {
            ChapterServices chapterServices = new ChapterServices();
            request.setAttribute("chapter", chapterServices.buildChapterDetailDTO(chapterId));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/chapter/ChapterContentForStaff.jsp").forward(request, response);
    }
}
