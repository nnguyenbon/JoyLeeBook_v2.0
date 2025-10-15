package services.general;


import dao.CommentDAO;
import dao.UserDAO;
import db.DBConnection;
import dto.general.CommentDetailDTO;
import model.Comment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentServices {
    private final Connection connection;
    private final CommentDAO commentDAO;
    private final UserDAO userDAO;
    public CommentServices () throws SQLException, ClassNotFoundException {
        this.connection = DBConnection.getConnection();
        this.commentDAO = new CommentDAO(connection);
        this.userDAO = new UserDAO(connection);
    }
    public CommentDetailDTO buildCommentDetailDTO(Comment comment) throws SQLException {
        CommentDetailDTO commentDetailDTO = new CommentDetailDTO();
        commentDetailDTO.setCommentId(comment.getCommentId());
        commentDetailDTO.setContent(comment.getContent());
        commentDetailDTO.setUserId(comment.getUserId());
        commentDetailDTO.setUpdateAt(FormatServices.calculateTimeAgo(comment.getUpdatedAt()));
        commentDetailDTO.setUsername(userDAO.findById(comment.getUserId()).getUsername());
        return commentDetailDTO;
    }

    public List<CommentDetailDTO> buildCommentDetailDTOList(List<Comment> comments) throws SQLException {
        List<CommentDetailDTO> commentDetailDTOList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDetailDTOList.add(buildCommentDetailDTO(comment));
        }
        return commentDetailDTOList;
    }

    public List<CommentDetailDTO> commentsFromChapter (int chapterId) throws SQLException {
        return buildCommentDetailDTOList(commentDAO.findByChapter(chapterId));
    }

}
