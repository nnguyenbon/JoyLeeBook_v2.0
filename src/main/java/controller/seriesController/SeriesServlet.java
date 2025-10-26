package controller.seriesController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/series")
public class SeriesServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("add")) {
            addSeries(request, response);
        } else if (action.equals("edit")){

        } else if (action.equals("delete")){
            deleteSeries(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action.equals("detail")) {
            viewSeriesDetail(request, response);
        } else {
            viewSeriesList(request, response);
        }
    }

    private void viewSeriesDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getSession().getAttribute("role").toString() == null ? "reader" : request.getSession().getAttribute("role").toString();
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;

        if (role.equals("admin") || role.equals("staff")) {
            try {
                SeriesServices seriesServices = new SeriesServices();
                ChapterServices chapterServices = new ChapterServices();
                request.setAttribute("series", seriesServices.buildSeriesInfoDTO(seriesId));
                request.setAttribute("chapterDetailDTOList", chapterServices.chaptersFromSeries(seriesId));
                request.getRequestDispatcher("/WEB-INF/views/series/SeriesDetailForStaff.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (role.equals("author")) {
            try {
                SeriesServices seriesServices = new SeriesServices();
                request.setAttribute("mySeriesDetails", seriesServices.mySeriesDetails(seriesId));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                SeriesServices seriesServices = new SeriesServices();
                ChapterServices chapterServices = new ChapterServices();
                request.setAttribute("seriesInfoDTO", seriesServices.buildSeriesInfoDTO(seriesId));
                request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
                request.setAttribute("pageTitle", "Series Detail");
                request.setAttribute("contentPage", "/WEB-INF/views/series/SeriesDetail.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void viewSeriesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getSession().getAttribute("role").toString() == null ? "reader" : request.getSession().getAttribute("role").toString();
        if (role.equals("admin") || role.equals("staff")) {

        } else if (role.equals("author")) {
            try {
                int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
                SeriesServices seriesServices = new SeriesServices();
                request.setAttribute("mySeriesList", seriesServices.mySeriesList(userId));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {

        }
    }

    private void addSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            String coverImgUrl = request.getParameter("coverImgUrl");
            String title =  request.getParameter("title");
            String[] genre = request.getParameterValues("genres");
            String status = request.getParameter("status");
            String description = request.getParameter("description");

            SeriesServices seriesServices = new SeriesServices();
            seriesServices.createSeries(userId, coverImgUrl, title, status, description);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void editSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    private void deleteSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String seriesId = request.getParameter("seriesId");

            SeriesServices seriesServices = new SeriesServices();
            seriesServices.deleteSeries(seriesId);
            response.sendRedirect(request.getContextPath()
                    + "/series-detail?seriesId=" + seriesId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
