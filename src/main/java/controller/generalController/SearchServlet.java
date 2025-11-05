package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class SearchServlet
 * Handles search requests for series based on various criteria.
 * Processes GET requests to display search results and POST requests for search submissions.
 * Utilizes SeriesServices, UserServices, and SearchServices for data retrieval and processing.
 * Forwards results to the appropriate JSP for rendering.
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    /**
     * Handles the HTTP GET method.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String statusParam = request.getParameter("status");
//        String genresParam = request.getParameter("genres");
//        String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword");
//        String searchType = request.getParameter("searchType");
//        try {
//            SeriesServices seriesServices = new SeriesServices();
//            UserServices userServices = new UserServices();
//            SearchServices searchServices = new SearchServices();
//
//            if (searchServices.handleSearchByType(searchType, keyword, searchType != null, DBConnection.getConnection(), request, response)) {
//                return;
//            }
//
//            request.setAttribute("weeklySeriesList", seriesServices.weeklySeriesList(8));
//            request.setAttribute("userList", userServices.topUsersPoints(8));
//            request.setAttribute("statusParam", searchServices.extractParameters(statusParam));
//            request.setAttribute("genresParam", searchServices.extractParameters(genresParam));
//            request.setAttribute("searchType", searchType != null ? searchType : "title");
//            request.setAttribute("keyword", keyword);
//
//            request.setAttribute("pageTitle", "Search " + (searchType != null ? searchType : "title"));
//            request.setAttribute("contentPage", "/WEB-INF/views/general/SearchPage.jsp");
//            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Handles the HTTP POST method.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
//    public List<SeriesInfoDTO> filterSeries(HttpServletRequest request, HttpServletResponse response ,List<String> statuses, List<String> genres) throws SQLException, ClassNotFoundException {
//        try (Connection connection = DBConnection.getConnection()) {
//            SeriesDAO seriesDAO = new SeriesDAO(connection);
//            CategoryDAO categoryDAO = new CategoryDAO(connection);
//            List<Series> allSeries = seriesDAO.getAll();
//            List<SeriesInfoDTO> result = new ArrayList<>();
//
//            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
//            paginationRequest.setOrderBy("series_id");
//
//            for (Series s : allSeries) {
//                boolean matchStatus = statuses.isEmpty() || statuses.contains(s.getStatus());
//                boolean matchGenre = genres.isEmpty() || categoryDAO.matchGenres(s.getSeriesId(), genres);
//
//                if (matchStatus && matchGenre) {
//                    result.add(seriesServices.buildSeriesInfoDTO(s));
//                }
//            }
//            return result;
//        }
//
//    }

    public boolean handleSearchByType(String type, String keyword, boolean isAjaxRequest, Connection connection,
                                      HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
//        try (Connection conection = DBConnection.getConnection()) {
//            CategoryDAO categoryDAO = new CategoryDAO(connection);
//            UserDAO userDAO = new UserDAO(connection);
//
//            SeriesDAO seriesDAO = new SeriesDAO(connection);
//            if ("title".equals(type) || type == null) {
//                List<Category> categories = categoryDAO.getAll();
//                List<SeriesInfoDTO> seriesInfoDTOList = seriesServices.buildSeriesInfoDTOList(seriesDAO.findByName(keyword));
//                request.setAttribute("categories", categories);
//                request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
//                if (isAjaxRequest) {
//                    request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchTitleView.jsp").forward(request, response);
//                    return true;
//                }
//            } else if ("filter".equals(type)) {
//                String statusParam = request.getParameter("status");
//                String genresParam = request.getParameter("genres");
//
//                List<String> statuses = extractParameters(statusParam);
//
//                List<String> genres = extractParameters(genresParam);
//
//                SearchServices filterServices = new SearchServices();
//                List<SeriesInfoDTO> filteredSeries = filterSeries(statuses, genres, connection);
//
//                request.setAttribute("seriesInfoDTOList", filteredSeries);
//                request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchFilterView.jsp")
//                        .forward(request, response);
//                return true;
//            } else if ("author".equals(type)) {
//                AuthorServices authorServices = new AuthorServices();
//                List<AuthorItemDTO> authorItemDTOList = authorServices.buildAuthorItemDTOList(userDAO.findByName(keyword));
//                request.setAttribute("authorItemDTOList", authorItemDTOList);
//
//                if (isAjaxRequest) {
//                    request.getRequestDispatcher("/WEB-INF/views/general/searchview/SearchAuthorView.jsp").forward(request, response);
//                    return true ;
//                }
//            }
            return false;
//        }
    }

    public List<String> extractParameters (String parametersUrl){
        return  (parametersUrl != null && !parametersUrl.isEmpty())
                ? List.of(parametersUrl.split(","))
                : new ArrayList<>();
    }
}

