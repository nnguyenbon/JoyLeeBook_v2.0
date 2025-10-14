package controller.chapterController;

import dao.ChapterDAO;
import db.DBConnection;
import dto.chapter.ChapterDetailDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/staff-chapter")
public class ChapterContentForStaff extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int chapterId = ValidationInput.isPositiveInteger(request.getParameter("chapterId")) ? Integer.parseInt(request.getParameter("chapterId")) : 1;
        try {
            Connection connection = DBConnection.getConnection();
            ChapterDAO chapterDAO = new ChapterDAO(connection);
            ChapterServices chapterServices = new ChapterServices();

            ChapterDetailDTO chapterDetailDTO = chapterServices.buildChapterDetailDTO(chapterDAO.findById(chapterId), connection);
            request.setAttribute("chapter", chapterDetailDTO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/chapter/ChapterContentForStaff.jsp").forward(request, response);
    }
}
