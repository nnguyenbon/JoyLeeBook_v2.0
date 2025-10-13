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
        String seriesIdParam = request.getParameter("seriesId");


        int seriesId = ValidationInput.isPositiveInteger(seriesIdParam) ? Integer.parseInt(seriesIdParam) : 0;

        try {
            Connection connection = DBConnection.getConnection();
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            ChapterDAO chapterDAO = new ChapterDAO(connection);

            SeriesServices seriesServices = new SeriesServices(connection);
            SeriesInfoDTO seriesInfoDTO = seriesServices.buildSeriesInfoDTO(seriesDAO.findById(seriesId));

            ChapterServices chapterServices = new ChapterServices();
            List<ChapterInfoDTO> chapterInfoDTOList = chapterServices.buildChapterInfoDTOList(chapterDAO.findChapterBySeriesId(seriesInfoDTO.getSeriesId()), connection);

            request.setAttribute("seriesInfoDTO", seriesInfoDTO);
            request.setAttribute("chapterInfoDTOList", chapterInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/series/SeriesDetail.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }



}
