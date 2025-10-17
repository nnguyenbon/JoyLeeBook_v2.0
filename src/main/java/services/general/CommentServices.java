package services.general;


import dao.CommentDAO;
import dao.UserDAO;
import db.DBConnection;
import dto.general.CommentDetailDTO;
import model.Comment;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    public Comment createComment(int userId, String chapterIdParam, String content) {
        if (content == null || content.trim().isEmpty() || chapterIdParam == null) {
            throw new IllegalArgumentException("No content for this comment");
        }

        int chapterId;
        try {
            chapterId = Integer.parseInt(chapterIdParam.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid chapter ID");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setChapterId(chapterId);
        comment.setContent(content);
        comment.setDeleted(false);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        try {
            boolean success = commentDAO.insert(comment);
            if (!success) {
                throw new SQLException("Failed to insert comment into database.");
            }
            return comment;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating comment", e);
        }
    }

    public Comment editComment(int userId, String commentId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty.");
        }
        if (commentId == null || !commentId.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid comment ID.");
        }

        int commentIdParam = Integer.parseInt(commentId);

        Comment comment = new Comment();
        comment.setCommentId(commentIdParam);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        try {
            boolean success = commentDAO.update(comment);
            if (!success) {
                throw new SQLException("Failed to update comment into database.");
            }
            return comment;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while editing comment", e);
        }
    }

    public void deleteComment(int userId, String commentId) {
        if (commentId == null || !commentId.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid comment ID.");
        }

        int commentIdParam = Integer.parseInt(commentId);

        try {
            boolean success = commentDAO.softDelete(commentIdParam);
            if (!success) {
                throw new SQLException("Failed to insert comment into database.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating comment", e);
        }
    }
}
