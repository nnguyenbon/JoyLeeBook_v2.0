package model;

public class ReportComment extends Report {
    private String commentContent;
    private String commenterUsername;
    private String chapterTitle;
    private String reporterUsername;
    private String staffUsername;

    // Additional Getters and Setters
    public String getCommentContent() { return commentContent; }
    public void setCommentContent(String commentContent) { this.commentContent = commentContent; }

    public String getCommenterUsername() { return commenterUsername; }
    public void setCommenterUsername(String commenterUsername) { this.commenterUsername = commenterUsername; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }

    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }

    public String getStaffUsername() { return staffUsername; }
    public void setStaffUsername(String staffUsername) { this.staffUsername = staffUsername; }
}
