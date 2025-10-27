package controller.generalController;

import db.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SavedSeries;
import model.User;
import services.chapter.ChapterServices;
import services.series.SavedSeriesService;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "save":
                saveSeries(request,response);
                break;
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "view":
                viewLibrary(request, response);
                break;
        }
    }

    private void viewLibrary(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId;
        User user = (User) request.getSession().getAttribute("loginedUser");
        if (user == null || user.getRole() == null || !user.getRole().equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Please login to view yours library");
            return;
        }
        userId = user.getUserId();
        try {
            ChapterServices chapterServices = new ChapterServices();
            SeriesServices seriesServices = new SeriesServices();

            request.setAttribute("savedSeries", seriesServices.savedSeriesFromUser(userId));
            request.setAttribute("historyChapters", chapterServices.historyChaptersFromUser(userId, 0, Integer.MAX_VALUE, null));

            request.setAttribute("pageTitle", "Library");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Library.jsp");

            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginedUser");
        String role = user.getRole();
        if (!role.equals("reader")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("Please login to save series");
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        try {
            SavedSeriesService saveSeriesService = new SavedSeriesService();

            int userId = user.getUserId();
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String action = request.getParameter("type");

            boolean saved;
            SavedSeries savedSeries = new SavedSeries();
            savedSeries.setUserId(userId);
            savedSeries.setSeriesId(seriesId);

            if ("save".equalsIgnoreCase(action)) {
                saveSeriesService.saveSeries(savedSeries);
                saved = true;
            } else {
                saveSeriesService.unSaveSeries(savedSeries);
                saved = false;
            }

            response.getWriter().write("{\"success\": true, \"saved\": " + saved + "}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().write("{\"success\": false}");
        }
    }
}
