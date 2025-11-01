package controller.seriesController;

import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import services.category.CategoryServices;
import services.series.SeriesServices;
import utils.ValidationInput;
import utils.WebpConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/add-series")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class AddSeriesServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            CategoryServices categoryServices = new CategoryServices();
            request.setAttribute("categoryList", categoryServices.buildCategoryInfoDTOList());
            request.setAttribute("pageTitle", "Add Series");
            request.setAttribute("contentPage",  "/WEB-INF/views/author/add-series.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
        }  catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;
//        try {
//            String title = request.getParameter("title");
//            String[] genresIdParam = request.getParameterValues("selectedGenres");
//            String status = request.getParameter("status");
//            String description = request.getParameter("description");
//
//            List<String> genreIds = new ArrayList<>();
//            if (genresIdParam != null && genresIdParam.length > 0) {
//                genreIds = Arrays.asList(genresIdParam);
//            }
//            Part filePart = request.getPart("coverImgUrl");
//            if (filePart == null || filePart.getSubmittedFileName().trim().isEmpty()) {
//                throw new IOException("Please select a cover image.");
//            }
//
//            SeriesInfoDTO seriesInfoDTO = new SeriesInfoDTO();
//            seriesInfoDTO.setTitle(title);
//            seriesInfoDTO.setCoverImgUrl( WebpConverter.convertToWebp(filePart, getServletContext()));
//            seriesInfoDTO.setStatus(status);
//            seriesInfoDTO.setDescription(description);
//            seriesInfoDTO.setCategories(genreIds);
//
//            SeriesServices seriesServices = new SeriesServices();
//            seriesServices.createSeries(seriesInfoDTO, userId);
//
//            response.sendRedirect(request.getContextPath() + "/author?userId=" + userId);
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }}

}