package dto.report;

import model.Report;

public class ReportCommentDTO extends ReportBaseDTO {
    private int commentId;
    private String usernameComment;
    private String content;
    private String reporterUsername;



    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getUsernameComment() {
        return usernameComment;
    }

    public void setUsernameComment(String usernameComment) {
        this.usernameComment = usernameComment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }
}
