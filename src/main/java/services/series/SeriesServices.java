package services.series;

import dao.*;
import db.DBConnection;
import dto.series.SeriesInfoDTO;
import model.Category;
import model.Chapter;
import model.Series;
import model.SeriesAuthor;
import services.general.FormatServices;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SeriesServices {
    private final CategoryDAO categoryDAO;
    private final RatingDAO ratingDAO ;
    private final ChapterDAO chapterDAO ;
    private final SeriesAuthorDAO seriesAuthorDAO ;
    private final SeriesDAO seriesDAO;
    private final ReadingHistoryDAO readingHistoryDAO;
    private final SavedSeriesDAO savedSeriesDAO;
    private final SeriesCategoriesDAO seriesCategoriesDAO;

    public SeriesServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.categoryDAO = new CategoryDAO(connection);
        this.ratingDAO = new RatingDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.seriesAuthorDAO = new SeriesAuthorDAO(connection);
        this.seriesDAO = new SeriesDAO(connection);
        this.readingHistoryDAO = new ReadingHistoryDAO(connection);
        this.savedSeriesDAO = new SavedSeriesDAO(connection);
        this.seriesCategoriesDAO = new SeriesCategoriesDAO(connection);
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
        dto.setCoverImgUrl("img/" + series.getCoverImgUrl());
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

    public SeriesInfoDTO buildSeriesInfoDTO(int seriesId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTO(seriesDAO.findById(seriesId));
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

    public List<SeriesInfoDTO> seriesFromAuthor (int authorId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getSeriesByAuthorId(authorId));
    }

    public List<SeriesInfoDTO> mySeriesList (int userId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getSeriesByAuthorId(userId));
    }

    public List<SeriesInfoDTO> savedSeriesFromUser (int userId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTOList(seriesDAO.getSeriesByUserId(userId));
    }

    public SeriesInfoDTO mySeriesDetails(int seriesId) throws SQLException, ClassNotFoundException {
        return buildSeriesInfoDTO(seriesDAO.findById(seriesId));
    }

    public Series createSeries(int userId, String coverImgUrl, String title, String status, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be blank");
        }

        if(title.trim().length() > 50) {
            throw new IllegalArgumentException("Title cannot be longer than 50 characters");
        }

        Series series = new Series();
        series.setAuthorId(userId);
        series.setCoverImgUrl(coverImgUrl);
        series.setTitle(title);
        series.setDescription(description.trim());
        series.setStatus(status.trim());

        try {
            boolean success = seriesDAO.insert(series);
            if (!success) {
                throw new SQLException("Failed to insert series into database.");
            }
            return series;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating series", e);
        }
    }

    public void deleteSeries(String seriesIdparam) {
        int seriesId;
        try {
            seriesId = Integer.parseInt(seriesIdparam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Series ID must be an integer");
        }

        if (seriesId <= 0) {
            throw new IllegalArgumentException("Series is invalid");
        }
        try {
            boolean deleteMapGenre = seriesCategoriesDAO.deleteBySeriesId(seriesId);
            boolean deleteSaved = savedSeriesDAO.deleteBySeriesId(seriesId);
            List<Chapter> chapterList = chapterDAO.findChapterBySeriesId(seriesId);
            for (Chapter chapter : chapterList) {
                boolean deleteHistory = readingHistoryDAO.deleteByChapterId(chapter.getChapterId());
                if (!deleteHistory) {
                    throw new SQLException("Failed to delete reading history for chapter " + chapter.getChapterId());
                }
            }
            boolean deleteChapters = chapterDAO.deleteBySeriesId(seriesId);
            if (!deleteChapters) {
                throw new SQLException("Failed to delete chapters of the series in database.");
            }
            Series series = seriesDAO.findById(seriesId);
            if (series != null) {
                String coverImgUrl = series.getCoverImgUrl();
                if (coverImgUrl != null && !coverImgUrl.trim().isEmpty()) {
                    File file = new File(coverImgUrl);
                    if (file.exists() && !file.isDirectory()) {
                        if (!file.delete()) {
                            System.err.println("Cannot delete file image: " + coverImgUrl);
                        }
                    }
                }
            }
            boolean deletedSeries = seriesDAO.delete(seriesId);
            if (!deletedSeries) {
                throw new SQLException("Failed to delete series from database.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while deleting series", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while deleting series", e);
        }
    }

}
