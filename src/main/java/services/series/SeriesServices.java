package services.series;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.RatingDAO;
import dao.SeriesAuthorDAO;
import dto.series.SeriesInfoDTO;
import model.Category;
import model.Series;
import services.general.FormatServices;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeriesServices {
    private final CategoryDAO categoryDAO;
    private final RatingDAO ratingDAO ;
    private final ChapterDAO chapterDAO ;
    private final SeriesAuthorDAO seriesAuthorDAO ;
    

    public SeriesServices(Connection connection) throws SQLException, ClassNotFoundException {
        this.categoryDAO = new CategoryDAO(connection);
        this.ratingDAO = new RatingDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.seriesAuthorDAO = new SeriesAuthorDAO(connection);
    }

    public List<SeriesInfoDTO> buildSeriesInfoDTOList(List<Series> seriesList) throws SQLException, ClassNotFoundException {
        List<SeriesInfoDTO> seriesInfoDTOList = new ArrayList<>();
        for (Series series : seriesList) {
            seriesInfoDTOList.add(buildSeriesInfoDTO(series));
        }
        return seriesInfoDTOList;
    }


    public SeriesInfoDTO buildSeriesInfoDTO(Series series) throws SQLException {
        SeriesInfoDTO dto = new SeriesInfoDTO();
        dto.setSeriesId(series.getSeriesId());
        dto.setTitle(series.getTitle());
        dto.setDescription(series.getDescription());
        dto.setCoverImgUrl(series.getCoverImgUrl());
        dto.setStatus(FormatServices.formatStatus(series.getStatus()));
        dto.setUpdatedAt(FormatServices.formatDate(series.getUpdatedAt()));

        List<String> categories = new ArrayList<>();
        for (Category category : categoryDAO.getCategoryBySeriesId(series.getSeriesId())) {
            categories.add(category.getName());
        }
        dto.setCategories(categories);

        dto.setAvgRating(Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10.0) / 10.0);
        dto.setCountRatings(ratingDAO.getRatingCount(series.getSeriesId()));

        dto.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));

        dto.setAuthorsName(seriesAuthorDAO.authorsOfSeries(series.getSeriesId()));

        return dto;
    }
}
