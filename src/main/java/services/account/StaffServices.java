package services.account;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.SeriesDAO;
import dto.category.CategoryInfoDTO;
import dto.chapter.ChapterDetailDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.category.CategoryServices;
import services.chapter.ChapterServices;
import services.general.PaginationServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StaffServices {
    public boolean handleRedirect (String type, Connection connection, HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
        PaginationServices paginationServices = new PaginationServices();
        SeriesDAO seriesDAO = new SeriesDAO(connection);
        ChapterDAO chapterDAO = new ChapterDAO(connection);
        if ("series".equals(type)) {
            SeriesServices seriesServices = new SeriesServices(connection);
            List<SeriesInfoDTO> seriesList = seriesServices.buildSeriesInfoDTOList(seriesDAO.getAll());
            List<SeriesInfoDTO> seriesInfoDTOList = paginationServices.handleParameterPage(seriesList, request);
            request.setAttribute("size", seriesList.size());
            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/SeriesListView.jsp").forward(request, response);
            return true;
        } else if ("chapter".equals(type)) {
            ChapterServices chapterServices = new ChapterServices();
            List<ChapterDetailDTO> chapterList = chapterServices.buildChapterDetailDTOList(chapterDAO.getAll(), connection);
            List<ChapterDetailDTO> chapterDetailDTOList = paginationServices.handleParameterPage(chapterList, request);
            request.setAttribute("size", chapterList.size());
            request.setAttribute("chapterDetailDTOList", chapterDetailDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/ChaptersListView.jsp").forward(request, response);
            return true;
        } else if ("genres".equals(type)) {
            CategoryDAO categoryDAO = new  CategoryDAO(connection);
            CategoryServices categoryServices = new CategoryServices();
            List<CategoryInfoDTO> categoryList = categoryServices.buildCategoryInfoDTOList(categoryDAO.getAll());
            List<CategoryInfoDTO> categoryInfoDTOList = paginationServices.handleParameterPage(categoryList, request);
            request.setAttribute("size", categoryList.size());
            request.setAttribute("categoryInfoDTOList", categoryInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/GenresListView.jsp").forward(request, response);
            return true;
        }
        return false;
    }
}
