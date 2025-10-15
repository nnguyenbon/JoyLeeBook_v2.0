package services.series;

import dao.*;
import db.DBConnection;
import dto.series.SeriesInfoDTO;
import model.Category;
import model.Series;
import services.general.FormatServices;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeriesServices {
    private final CategoryDAO categoryDAO;
    private final RatingDAO ratingDAO ;
    private final ChapterDAO chapterDAO ;
    private final SeriesAuthorDAO seriesAuthorDAO ;
    private final SeriesDAO seriesDAO;
    

    public SeriesServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.categoryDAO = new CategoryDAO(connection);
        this.ratingDAO = new RatingDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.seriesAuthorDAO = new SeriesAuthorDAO(connection);
        this.seriesDAO = new SeriesDAO(connection);
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
        dto.setStatus(FormatServices.formatString(series.getStatus()));
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

    public List<SeriesInfoDTO> hotSeriesList (int limit) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getTopRatedSeries(limit));
    }

    public List<SeriesInfoDTO> weeklySeriesList (int limit) throws SQLException, ClassNotFoundException {
        List<SeriesInfoDTO> seriesList = buildSeriesInfoDTOList(seriesDAO.getWeeklySeries(limit));
        for (SeriesInfoDTO series : seriesList){
            series.setAvgRating(Math.round(series.getAvgRating()*series.getCountRatings()));
        }
        return seriesList;
    }

    public List<SeriesInfoDTO> newReleaseSeries (int limit) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getNewReleasedSeries(limit));
    }

    public List<SeriesInfoDTO> recentlyUpdatedSeries (int limit) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getRecentlyUpdated(limit));
    }

    public List<SeriesInfoDTO> completedSeries (int limit, String status ) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getSeriesByStatus(limit, status));
    }

    public List<SeriesInfoDTO> savedSeriesFromUser (int userId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getSeriesByUserId(userId));
    }

    public List<SeriesInfoDTO> seriesFromAuthor (int authorId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getSeriesByAuthorId(authorId));
    }

    public SeriesInfoDTO buildSeriesInfoDTO(int seriesId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTO(seriesDAO.findById(seriesId));
    }
}
