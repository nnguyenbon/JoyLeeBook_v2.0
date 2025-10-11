package controller.profileController;

import dao.*;
import db.DBConnection;
import dto.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/author-profile")
public class AuthorProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorIdParam = request.getParameter("authorId");

        int authorId = 0;
        if (authorIdParam != null && !authorIdParam.isEmpty()) {
            try {
                authorId = Integer.parseInt(authorIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid seriesId");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing seriesId");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy ");
        try {
            Connection connection = DBConnection.getConnection();
            UserDAO userDAO = new UserDAO(connection);
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            LikesDAO likesDAO = new LikesDAO(connection);
            CategoryDAO categoryDAO = new CategoryDAO(connection);
            RatingDAO ratingDAO = new RatingDAO(connection);
            ChapterDAO chapterDAO = new ChapterDAO(connection);
            BadgesUserDAO badgesUserDAO = new BadgesUserDAO(connection);
            User user = userDAO.findById(authorId);
            List<Badge> badgeList = badgesUserDAO.getBadgesByUserId(authorId);
            List<Series> seriesList = seriesDAO.getSeriesByAuthorId(authorId);
            int totalSeriesCount = seriesList.size();
            int totalLike = 0;
            double avgRating = 0;
            int totalRating = 0;
            int ratingCount = 0;
            List<SeriesInfoDTO> seriesInfoDTOList = new ArrayList<>();
            for (Series series : seriesList) {
                SeriesInfoDTO seriesInfoDTO = new SeriesInfoDTO();
                seriesInfoDTO.setSeriesId(series.getSeriesId());
                seriesInfoDTO.setTitle(series.getTitle());
                seriesInfoDTO.setUpdatedAt(series.getUpdatedAt().format(formatter));
                seriesInfoDTO.setCoverImgUrl(series.getCoverImgUrl());
                seriesInfoDTO.setStatus(series.getStatus());
                List<String> categoriesName = new ArrayList<>();
                for (Category category : categoryDAO.getCategoryBySeriesId(series.getSeriesId())) {
                    categoriesName.add(category.getName());
                }
                seriesInfoDTO.setCategories(categoriesName);
                seriesInfoDTO.setAvgRating((double) Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10) /10);
                seriesInfoDTO.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));
                seriesInfoDTOList.add(seriesInfoDTO);

                for (Chapter chapter : chapterDAO.findChapterBySeriesId(series.getSeriesId())) {
                    totalLike += likesDAO.countByChapter(chapter.getChapterId());
                }

                totalRating += ratingDAO.getRatingSumBySeriesId(series.getSeriesId());
                ratingCount += ratingDAO.getRatingCount(series.getSeriesId());
            }
            avgRating = (double) Math.round((totalRating / ratingCount) * 10) / 10;
            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.setAttribute("totalSeriesCount", totalSeriesCount);
            request.setAttribute("totalLike", totalLike);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("user", user);
            request.setAttribute("badgeList", badgeList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/profile/AuthorProfile.jsp").forward(request, response);
    }
}
