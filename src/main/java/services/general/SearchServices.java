package services.general;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.SeriesDAO;
import dao.UserDAO;
import dto.author.AuthorItemDTO;
import dto.category.CategoryInfoDTO;
import dto.chapter.ChapterDetailDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Series;
import services.account.AuthorServices;
import services.category.CategoryServices;
import services.chapter.ChapterServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchServices {
    public List<SeriesInfoDTO> filterSeries(List<String> statuses, List<String> genres, Connection connection) throws SQLException, ClassNotFoundException {
        SeriesDAO seriesDAO = new SeriesDAO(connection);
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        List<Series> allSeries = seriesDAO.getAll();
        List<SeriesInfoDTO> result = new ArrayList<>();

        SeriesServices seriesServices = new SeriesServices();
        for (Series s : allSeries) {
            boolean matchStatus = statuses.isEmpty() || statuses.contains(s.getStatus());
            boolean matchGenre = genres.isEmpty() || categoryDAO.matchGenres(s.getSeriesId(), genres);

            if (matchStatus && matchGenre) {
                result.add(seriesServices.buildSeriesInfoDTO(s));
            }
        }
        return result;
    }

    public boolean handleSearchByType(String searchType, String keyword, boolean isAjaxRequest, Connection connection,
                                     HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        SeriesServices seriesServices = new SeriesServices();
        SeriesDAO seriesDAO = new SeriesDAO(connection);
        if ("title".equals(searchType) || searchType == null) {
            List<Category> categories = categoryDAO.getAll();
            List<SeriesInfoDTO> seriesInfoDTOList = seriesServices.buildSeriesInfoDTOList(seriesDAO.findByName(keyword));


            request.setAttribute("categories", categories);
            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);

            if (isAjaxRequest) {
                request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchTitleView.jsp").forward(request, response);
                return true;
            }
        } else if ("filter".equals(searchType)) {
            String statusParam = request.getParameter("status");
            String genresParam = request.getParameter("genres");

            List<String> statuses = (statusParam != null && !statusParam.isEmpty())
                    ? List.of(statusParam.split(","))
                    : new ArrayList<>();

            List<String> genres = (genresParam != null && !genresParam.isEmpty())
                    ? List.of(genresParam.split(","))
                    : new ArrayList<>();

            SearchServices filterServices = new SearchServices();
            List<SeriesInfoDTO> filteredSeries = filterServices.filterSeries(statuses, genres, connection);

            request.setAttribute("seriesInfoDTOList", filteredSeries);
            request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchFilterView.jsp")
                    .forward(request, response);
            return true;
        } else if ("author".equals(searchType)) {
            AuthorServices authorServices = new AuthorServices();
            List<AuthorItemDTO> authorItemDTOList = authorServices.buildAuthorItemDTOList(userDAO.findByName(keyword));
            request.setAttribute("authorItemDTOList", authorItemDTOList);

            if (isAjaxRequest) {
                request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchAuthorView.jsp").forward(request, response);
                return true ;
            }
        }
        return false;
    }

    public List<String> extractParameters (String parametersUrl){
       return  (parametersUrl != null && !parametersUrl.isEmpty())
                ? List.of(parametersUrl.split(","))
                : new ArrayList<>();
    }
}
