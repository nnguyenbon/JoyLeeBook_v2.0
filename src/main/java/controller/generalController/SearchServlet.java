package controller.generalController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
//            request.setAttribute("contentPage", "/WEB-INF/views/general/_search.jsp");
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
}

