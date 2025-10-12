package controller.seriesController;

import dao.*;
import db.DBConnection;
import dto.ChapterInfoDTO;
import dto.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Chapter;
import model.Series;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/series-detail")
public class SeriesDetailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String seriesIdParam = request.getParameter("seriesId");


        int seriesId = 0;
        if (seriesIdParam != null && !seriesIdParam.isEmpty()) {
            try {
                seriesId = Integer.parseInt(seriesIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid seriesId");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing seriesId");
            return;
        }

        try {
            LikesDAO likesDAO = new LikesDAO(DBConnection.getConnection());
            SeriesDAO seriesDAO = new SeriesDAO(DBConnection.getConnection());
            CategoryDAO categoryDAO = new CategoryDAO(DBConnection.getConnection());
            ChapterDAO chapterDAO = new ChapterDAO(DBConnection.getConnection());
            RatingDAO ratingDAO = new RatingDAO(DBConnection.getConnection());
            SeriesAuthorDAO  seriesAuthorDAO = new SeriesAuthorDAO(DBConnection.getConnection());
            Series series = seriesDAO.findById(seriesId);
            SeriesInfoDTO seriesInfoDTO = new SeriesInfoDTO();
            seriesInfoDTO.setSeriesId(seriesId);
            seriesInfoDTO.setDescription(series.getDescription());
            seriesInfoDTO.setTitle(series.getTitle());
            seriesInfoDTO.setCoverImgUrl(series.getCoverImgUrl());
            List<String> categories = new ArrayList<>();
            for (Category category : categoryDAO.getCategoryBySeriesId(seriesId)){
                categories.add(category.getName());
            }
            seriesInfoDTO.setCategories(categories);
//            seriesInfoDTO.setAuthorsName();
            seriesInfoDTO.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
            seriesInfoDTO.setAvgRating((double) Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10) /10);
            seriesInfoDTO.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));
            seriesInfoDTO.setAuthorsName(seriesAuthorDAO.authorsOfSeries(series.getSeriesId()));

            List<ChapterInfoDTO> chapterInfoDTOList = new ArrayList<>();
            for (Chapter chapter : chapterDAO.findChapterBySeriesId(series.getSeriesId())) {
                ChapterInfoDTO chapterInfoDTO = new ChapterInfoDTO();
                chapterInfoDTO.setChapterId(chapter.getChapterId());
                chapterInfoDTO.setTitle(chapter.getTitle());
                chapterInfoDTO.setChapterNumber(chapter.getChapterNumber());
                chapterInfoDTO.setUpdatedAt(calculateTimeAgo(chapter.getUpdatedAt()));
                chapterInfoDTO.setTotalLikes(likesDAO.countByChapter(chapter.getChapterId()));
                chapterInfoDTOList.add(chapterInfoDTO);
            }
            request.setAttribute("seriesInfoDTO", seriesInfoDTO);
            request.setAttribute("chapterInfoDTOList", chapterInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/series/SeriesDetail.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    private String calculateTimeAgo(LocalDateTime updatedAt) {
        Duration duration = Duration.between(updatedAt, LocalDateTime.now());

        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "just now";
        }
    }
}
