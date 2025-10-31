package controller.seriesController;

import dto.PaginationRequest;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import model.Staff;
import model.User;
import services.chapter.ChapterServices;
import services.series.RatingSeriesService;
import services.series.SavedSeriesService;
import services.series.SeriesServices;
import utils.AuthenticationUtils;
import utils.PaginationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/series/*")
public class SeriesServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action.equals("/add")) {
            addSeries(request, response);
        } else if (action.equals("/edit")) {

        } else if (action.equals("/delete")) {
            deleteSeries(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        System.out.println(action);
        if (action.equals("/detail")) {
            viewSeriesDetail(request, response);
        } else if(action.equals("/list")) {
            viewSeriesList(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void viewSeriesDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account loginedUser = AuthenticationUtils.getLoginedUser(request.getSession());
        String role = (loginedUser != null) ? loginedUser.getRole() : "reader";
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;
        if (role.equals("admin") || role.equals("staff")) {
            try {
                SeriesServices seriesServices = new SeriesServices();
                ChapterServices chapterServices = new ChapterServices();
                request.setAttribute("series", seriesServices.buildSeriesInfoDTO(seriesId));
                request.setAttribute("chapterDetailDTOList", chapterServices.chaptersFromSeries(seriesId));
                request.setAttribute("contentPage", "/WEB-INF/views/series/SeriesDetailForStaff.jsp");
                request.setAttribute("pageTitle", "Series Detail");
                request.setAttribute("activePage", "series");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (role.equals("author")) {
            try {
                User user = (User) loginedUser;
                ChapterServices chapterServices = new ChapterServices();
                int userId = user.getUserId();
                SeriesServices seriesServices = new SeriesServices();
                request.setAttribute("userId", userId);
                request.setAttribute("seriesInfoDTO", seriesServices.buildSeriesInfoDTO(seriesId));
                request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
                request.setAttribute("mySeriesDetails", seriesServices.mySeriesDetails(seriesId));
                request.setAttribute("pageTitle", "Series Detail");
                request.setAttribute("contentPage", "/WEB-INF/views/series/SeriesDetail.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                User user = (User) loginedUser;
                SeriesServices seriesServices = new SeriesServices();
                RatingSeriesService ratingSeriesService = new RatingSeriesService();
                ChapterServices chapterServices = new ChapterServices();
                SavedSeriesService savedSeriesService = new SavedSeriesService();
                int userId = 0;
                if (loginedUser != null) {
                    userId = user.getUserId();
                }
                request.setAttribute("userId", userId);
                request.setAttribute("seriesInfoDTO", seriesServices.buildSeriesInfoDTO(seriesId));
                request.setAttribute("chapterInfoDTOList", chapterServices.chaptersFromSeries(seriesId));
                request.setAttribute("userRating", ratingSeriesService.getRatingByUserID(userId, seriesId));
                request.setAttribute("saved", savedSeriesService.hasUserSavedSeries(userId, seriesId));
                request.setAttribute("pageTitle", "Series Detail");
                request.setAttribute("contentPage", "/WEB-INF/views/series/SeriesDetail.jsp");
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void viewSeriesList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       Staff loginedStaff = (Staff) req.getSession().getAttribute("loginedUser");
       String role = (loginedStaff != null) ? loginedStaff.getRole() : "reader";
       int userId = 0;
        try {
            String search = req.getParameter("search");
            String status = req.getParameter("filterByStatus");

            List<Integer> genreIds = Optional.ofNullable(req.getParameterValues("genre"))
                    .map(Arrays::asList)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());


            SeriesServices seriesServices = new SeriesServices();
            PaginationRequest paginationRequest = PaginationUtils.fromRequest(req);
            paginationRequest.setOrderBy("series_id");
            List<SeriesInfoDTO> seriesList = seriesServices.buildSeriesList(search, genreIds, userId, status, paginationRequest);
            int totalRecords = seriesServices.getTotalSeriesCount(search, genreIds, userId, status);

            req.setAttribute("size", totalRecords);
            req.setAttribute("seriesInfoDTOList", seriesList);
            req.setAttribute("search", search);
            req.setAttribute("filterByStatus", status);
            PaginationUtils.sendParameter(req, paginationRequest);


            if (role.equals("admin") || role.equals("staff")) {
                req.setAttribute("contentPage", "/WEB-INF/views/general/staffview/SeriesListView.jsp");
                req.setAttribute("activePage", "series");
                req.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(req, resp);
                return;
            } else if (role.equals("author")) {
                req.getRequestDispatcher("/WEB-INF/views/components/SeriesListForAuthor.jsp").forward(req, resp);
                return;
            } else {
                req.setAttribute("contentPage", "/WEB-INF/views/series/SeriesList.jsp");
                req.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(req, resp);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
        try {
            String coverImgUrl = request.getParameter("coverImgUrl");
            String title = request.getParameter("title");
            String[] genre = request.getParameterValues("genres");
            String status = request.getParameter("status");
            String description = request.getParameter("description");
            SeriesServices seriesServices = new SeriesServices();
            seriesServices.createSeries(userId, coverImgUrl, title, status, description);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void editSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

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
