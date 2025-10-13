package controller.chapterController;

import dto.chapter.ChapterViewDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.chapter.ChapterServices;
import model.User;

import java.io.IOException;

/**
 * Servlet for viewing a chapter
 *
 * @author HaiDD-dev
 */
@WebServlet("/chapter")
public class ViewChapterServlet extends HttpServlet {

    /**
     * Handles GET requests to view a chapter by ID or by series ID and chapter number.
     * If the chapter is found and approved, it forwards to the chapter view JSP.
     * If not found or an error occurs, it forwards to an error JSP.
     *
     * @param req  The HttpServletRequest object that contains the request the client made to the servlet
     * @param resp The HttpServletResponse object that contains the response the servlet returns to the client
     * @throws ServletException If an input or output error is detected when the servlet handles the GET request
     * @throws IOException      If the request for the GET could not be handled
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String idStr = req.getParameter("id");
            String seriesStr = req.getParameter("seriesId");
            String numberStr = req.getParameter("no");

            Integer chapterId = (idStr != null && !idStr.isBlank()) ? Integer.valueOf(idStr) : null;
            Integer seriesId = (seriesStr != null && !seriesStr.isBlank()) ? Integer.valueOf(seriesStr) : null;
            Integer number = (numberStr != null && !numberStr.isBlank()) ? Integer.valueOf(numberStr) : null;

            Integer viewerUserId = null;
            Object u = req.getSession().getAttribute("authUser");
            if (u instanceof User) {
                viewerUserId = ((User) u).getUserId();
            }

            ChapterServices svc = new ChapterServices();
            ChapterViewDTO chapterViewDTO = svc.loadView(chapterId, seriesId, number, viewerUserId);

            if (chapterViewDTO == null) {
                req.setAttribute("error", "Chapter not found or you don't have permission.");
                req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("vm", chapterViewDTO);
            req.getRequestDispatcher("/WEB-INF/views/chapter/chapter-view.jsp").forward(req, resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("error", "Unable to load chapter.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}
