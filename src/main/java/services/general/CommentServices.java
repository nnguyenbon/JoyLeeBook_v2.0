package services.general;


import dao.UserDAO;
import dto.general.CommentDetailDTO;
import model.Comment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentServices {
    public CommentDetailDTO buildCommentDetailDTO(Comment comment, Connection connection) throws SQLException {
        UserDAO userDAO = new UserDAO(connection);
        CommentDetailDTO commentDetailDTO = new CommentDetailDTO();
        commentDetailDTO.setCommentId(comment.getCommentId());
        commentDetailDTO.setContent(comment.getContent());
        commentDetailDTO.setUserId(comment.getUserId());
        commentDetailDTO.setUpdateAt(FormatServices.calculateTimeAgo(comment.getUpdatedAt()));
        commentDetailDTO.setUsername(userDAO.findById(comment.getUserId()).getUsername());
        return commentDetailDTO;
    }

    public List<CommentDetailDTO> buildCommentDetailDTOList(List<Comment> comments, Connection connection) throws SQLException {
        List<CommentDetailDTO> commentDetailDTOList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDetailDTOList.add(buildCommentDetailDTO(comment, connection));
        }
        return commentDetailDTOList;
    }


}
