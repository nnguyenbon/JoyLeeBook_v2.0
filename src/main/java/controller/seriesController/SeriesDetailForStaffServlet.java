package controller.seriesController;

import dao.ChapterDAO;
import dao.SeriesDAO;
import db.DBConnection;
import dto.chapter.ChapterDetailDTO;
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

@WebServlet("/staff-series")
public class SeriesDetailForStaffServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;
        try {
            Connection connection = DBConnection.getConnection();
            SeriesServices seriesServices = new SeriesServices(connection);
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            ChapterDAO chapterDAO = new ChapterDAO(connection);
            ChapterServices chapterServices = new ChapterServices();
            SeriesInfoDTO  seriesInfoDTO = seriesServices.buildSeriesInfoDTO(seriesDAO.findById(seriesId));
            List<ChapterDetailDTO>  chapterDetailDTOList = chapterServices.buildChapterDetailDTOList(chapterDAO.findChapterBySeriesId(seriesId),connection);
            request.setAttribute("series", seriesInfoDTO);
            request.setAttribute("chapterDetailDTOList", chapterDetailDTOList);
            request.getRequestDispatcher("/WEB-INF/views/series/SeriesDetailForStaff.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
