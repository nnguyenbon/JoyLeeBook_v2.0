package services.account;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.SeriesDAO;
import dao.StaffDAO;
import db.DBConnection;
import dto.category.CategoryInfoDTO;
import dto.chapter.ChapterDetailDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Staff;
import services.category.CategoryServices;
import services.chapter.ChapterServices;
import services.general.PaginationServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StaffServices {
    private final StaffDAO staffDAO;
    private final SeriesDAO seriesDAO;
    private final ChapterDAO chapterDAO;
    private final CategoryDAO categoryDAO;
    public StaffServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.staffDAO = new StaffDAO(connection);
        this.seriesDAO = new SeriesDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.categoryDAO = new CategoryDAO(connection);
    }
    public boolean  handleRedirect (String type, HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
        PaginationServices paginationServices = new PaginationServices();
        if ("series".equals(type)) {
            SeriesServices seriesServices = new SeriesServices();
            List<SeriesInfoDTO> seriesList = seriesServices.buildSeriesInfoDTOList(seriesDAO.getAll());
            List<SeriesInfoDTO> seriesInfoDTOList = paginationServices.handleParameterPage(seriesList, request);
            request.setAttribute("size", seriesList.size());
            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/SeriesListView.jsp").forward(request, response);
            return true;
        } else if ("chapter".equals(type)) {
            ChapterServices chapterServices = new ChapterServices();
            List<ChapterDetailDTO> chapterList = chapterServices.buildChapterDetailDTOList(chapterDAO.getAll());
            List<ChapterDetailDTO> chapterDetailDTOList = paginationServices.handleParameterPage(chapterList, request);
            request.setAttribute("size", chapterList.size());
            request.setAttribute("chapterDetailDTOList", chapterDetailDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/ChaptersListView.jsp").forward(request, response);
            return true;
        } else if ("genres".equals(type)) {
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
    public Staff getStaffAccount(int staffId) throws SQLException, ClassNotFoundException {
        return staffDAO.findById(staffId);
    }
}
