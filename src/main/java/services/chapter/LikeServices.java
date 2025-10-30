package services.chapter;

import dao.LikeDAO;
import db.DBConnection;
import model.Like;
import services.general.PointServices;

import java.sql.Connection;
import java.sql.SQLException;

public class LikeServices {
    private final Connection connection;
    private final LikeDAO likesDAO;

    public LikeServices() throws SQLException, ClassNotFoundException {
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
    public int countLikesOfAuthor(int userId) throws SQLException {
        return likesDAO.countLikesOfAuthor(userId);
    }

    public int likeChapter(int userId, int chapterId) throws SQLException {

        Like like = new Like();
        like.setUserId(userId);
        like.setChapterId(chapterId);
        if (likesDAO.isLikedByUser(like.getUserId(), like.getChapterId())) {
            return likesDAO.countByChapter(like.getChapterId());
        }
        likesDAO.insert(like);
        PointServices.trackAction(userId, 2, "Like new chapter", "like", likesDAO.findById(userId, chapterId).getChapterId());
        return likesDAO.countByChapter(like.getChapterId());
    }

    public boolean hasUserLiked(int userId, int chapterId) throws SQLException {
        return likesDAO.isLikedByUser(userId, chapterId);
    }
}
