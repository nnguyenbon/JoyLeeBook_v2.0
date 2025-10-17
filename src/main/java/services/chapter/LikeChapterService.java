package services.chapter;

import dao.LikeDAO;
import db.DBConnection;
import model.Like;

import java.sql.Connection;
import java.sql.SQLException;

public class LikeChapterService {
    private final Connection connection;
    private final LikeDAO likesDAO;
    public LikeChapterService() throws SQLException, ClassNotFoundException {
        this.connection = DBConnection.getConnection();
        this.likesDAO = new LikeDAO(connection);
    }

//    public int likeChapter(Like like) throws SQLException {
//
//        if (likesDAO.isLikedByUser(like.getUserId(), like.getChapterId())) {
//            return likesDAO.countByChapter(like.getChapterId());
//        }
//
//        likesDAO.insert(like);
//
//        return  likesDAO.countByChapter(like.getChapterId());
//    }

    public int likeChapter(Like like) throws SQLException {

        if (likesDAO.isLikedByUser(like.getUserId(), like.getChapterId())) {
            return likesDAO.countByChapter(like.getChapterId());
        }
        likesDAO.insert(like);
        return likesDAO.countByChapter(like.getChapterId());
    }

    public boolean hasUserLiked(int userId, int chapterId) throws SQLException {
        return  likesDAO.isLikedByUser(userId, chapterId);
    }
}
