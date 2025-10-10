package services;

import dao.ChapterDAO;
import dao.SeriesAuthorDAO;
import dao.SeriesDAO;
import model.Chapter;
import model.Series;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ChapterManagementService provides functionalities for managing chapters within a series.
 *
 * @author HaiDD-dev
 */
public class ChapterManagementService {
    private final ChapterDAO chapterDAO;
    private final SeriesDAO seriesDAO;
    private final SeriesAuthorDAO seriesAuthorDAO;

    public ChapterManagementService(Connection conn) {
        this.chapterDAO = new ChapterDAO(conn);
        this.seriesDAO = new SeriesDAO(conn);
        this.seriesAuthorDAO = new SeriesAuthorDAO(conn);
    }

    /**
     * Creates a new chapter in the specified series if the user is an author of that series.
     *
     * @param userId   The ID of the user creating the chapter.
     * @param seriesId The ID of the series to which the chapter belongs.
     * @param title    The title of the new chapter.
     * @param content  The content of the new chapter.
     * @return The created Chapter object.
     * @throws SQLException           If a database access error occurs.
     * @throws IllegalAccessException If the user is not an author of the series.
     */
    public Chapter createChapter(int userId, int seriesId, String title, String content) throws SQLException, IllegalAccessException {
        // 1. Check if the user is an author of the series
        if (seriesAuthorDAO.isUserAuthorOfSeries(userId, seriesId)) {
            throw new IllegalAccessException("User does not have permission to add a chapter to this series.");
        }

        // 2. Get the latest chapter number and increment it
        int latestChapterNumber = chapterDAO.getLatestChapterNumber(seriesId);
        int newChapterNumber = latestChapterNumber + 1;

        // 3. Create and save the new chapter
        Chapter newChapter = new Chapter();
        newChapter.setSeriesId(seriesId);
        newChapter.setChapterNumber(newChapterNumber);
        newChapter.setTitle(title);
        newChapter.setContent(content);
        newChapter.setStatus("pending");

        // 4. Save the chapter to the database
        return chapterDAO.addChapter(newChapter);
    }

    /**
     * Retrieves a series by its ID.
     *
     * @param seriesId The ID of the series to retrieve.
     * @return The Series object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Series getSeriesById(int seriesId) throws SQLException {
        return seriesDAO.findById(seriesId);
    }
}