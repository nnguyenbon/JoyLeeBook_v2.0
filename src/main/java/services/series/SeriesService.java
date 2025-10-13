package services.series;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.RatingDAO;
import dao.SeriesAuthorDAO;
import db.DBConnection;
import dto.series.SeriesInfoDTO;
import model.Category;
import model.Series;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeriesService {
    private final CategoryDAO categoryDAO;
    private final RatingDAO ratingDAO ;
    private final ChapterDAO chapterDAO ;
    private final SeriesAuthorDAO seriesAuthorDAO ;
    

    public SeriesService(Connection connection) throws SQLException, ClassNotFoundException {
        this.categoryDAO = new CategoryDAO(connection);
        this.ratingDAO = new RatingDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.seriesAuthorDAO = new SeriesAuthorDAO(connection);
    }

    public SeriesInfoDTO buildSeriesInfoDTO(Series series) throws SQLException {
        SeriesInfoDTO dto = new SeriesInfoDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        dto.setSeriesId(series.getSeriesId());
        dto.setTitle(series.getTitle());
        dto.setDescription(series.getDescription());
        dto.setCoverImgUrl(series.getCoverImgUrl());
        dto.setStatus(series.getStatus());
        dto.setUpdatedAt(series.getUpdatedAt().format(formatter));

        // Categories
        List<String> categories = new ArrayList<>();
        for (Category category : categoryDAO.getCategoryBySeriesId(series.getSeriesId())) {
            categories.add(category.getName());
        }
        dto.setCategories(categories);

        // Ratings
        dto.setAvgRating(Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10.0) / 10.0);
        dto.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));

        // Chapters
        dto.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));

        // Authors
        dto.setAuthorsName(seriesAuthorDAO.authorsOfSeries(series.getSeriesId()));

        return dto;
    }
}
