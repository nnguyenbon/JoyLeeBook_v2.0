package services.account;

import dao.ChapterDAO;
import dao.LikesDAO;
import dao.RatingDAO;
import dao.SeriesDAO;
import dto.author.AuthorItemDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import model.Chapter;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorServices {
    private final Connection connection;

    public AuthorServices() throws SQLException, ClassNotFoundException {
        this.connection = DBConnection.getConnection();
    }

    public void extractDataFromAuthorId(List<SeriesInfoDTO> seriesList, HttpServletRequest request) throws SQLException {
        try {
            LikesDAO likesDAO = new LikesDAO(connection);
            RatingDAO ratingDAO = new RatingDAO(connection);
            ChapterDAO chapterDAO = new ChapterDAO(connection);
            int totalLike = 0;
            double avgRating;
            int totalRating = 0;
            int ratingCount = 0;
            for (SeriesInfoDTO series : seriesList) {
                for (Chapter chapter : chapterDAO.findChapterBySeriesId(series.getSeriesId())) {
                    totalLike += likesDAO.countByChapter(chapter.getChapterId());
                }
                totalRating += ratingDAO.getRatingSumBySeriesId(series.getSeriesId());
                ratingCount += ratingDAO.getRatingCount(series.getSeriesId());
            }
            if (ratingCount > 0) {
                avgRating = (double) Math.round(((double) totalRating / ratingCount) * 10) / 10;
            } else {
                avgRating = 0.0;
            }
            request.setAttribute("totalLike", totalLike);
            request.setAttribute("avgRating", avgRating);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AuthorItemDTO buildAuthorItemDTO(User author) throws SQLException {
        SeriesDAO seriesDAO = new SeriesDAO(connection);
        AuthorItemDTO authorItemDTO = new AuthorItemDTO();
        authorItemDTO.setAuthorId(author.getUserId());
        authorItemDTO.setUserName(author.getUsername());
        authorItemDTO.setTotalChapters(seriesDAO.getSeriesByAuthorId(author.getUserId()).size());
        return authorItemDTO;
    }

    public List<AuthorItemDTO> buildAuthorItemDTOList(List<User> authors) throws SQLException {
        List<AuthorItemDTO> authorItemDTOList = new ArrayList<>();
        for (User author : authors) {
            authorItemDTOList.add(buildAuthorItemDTO(author));
        }
        return authorItemDTOList;
    }

    public boolean registerAsAuthor(User user) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO(connection);
        return userDAO.updateUserRoleToAuthor(user.getUserId());
    }
}