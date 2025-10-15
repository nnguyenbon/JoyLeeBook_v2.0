package controller.seriesController;

import dao.*;
import db.DBConnection;
import dto.chapter.ChapterInfoDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.chapter.ChapterServices;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/series-detail")
public class SeriesDetailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 0;

        try {
            SeriesServices seriesServices = new SeriesServices();
            ChapterServices chapterServices = new ChapterServices();

            request.setAttribute("seriesInfoDTO", seriesServices.buildSeriesInfoDTO(seriesId));
            request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
            request.setAttribute("pageTitle","Series Detail");
            request.setAttribute("contentPage", "/WEB-INF/views/series/SeriesDetail.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
