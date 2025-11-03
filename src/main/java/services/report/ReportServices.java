//package services.report;
//
//import dao.*;
//import db.DBConnection;
//import dto.PaginationRequest;
//import dto.report.ReportBaseDTO;
//import dto.report.ReportChapterDTO;
//import dto.report.ReportCommentDTO;
//import jakarta.servlet.ServletException;
//import model.Chapter;
//import model.Comment;
//import model.Report;
//import services.general.FormatServices;
//
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ReportServices {
//    private final Connection connection;
//    private final ReportDAO reportDAO;
//    private final UserDAO userDAO;
//    private final ChapterDAO chapterDAO;
//    private final SeriesDAO seriesDAO;
//    private final CommentDAO commentDAO;
//
//    public ReportServices() throws SQLException, ClassNotFoundException {
//        this.connection = DBConnection.getConnection();
//        this.reportDAO = new ReportDAO(connection);
//        this.userDAO = new UserDAO(connection);
//        this.chapterDAO = new ChapterDAO(connection);
//        this.seriesDAO = new SeriesDAO(connection);
//        this.commentDAO = new CommentDAO(connection);
//    }
//
////    public ReportChapterDTO buildReportChapterDTO(Report report) throws SQLException {
////        if (report == null) {
////            throw new IllegalArgumentException("Report cannot be null");
////        }
////
////        ReportChapterDTO reportChapterDTO = new ReportChapterDTO();
////        reportChapterDTO.setId(report.getReportId());
////
//////        Integer chapterId = report.getChapterId();
////        if (chapterId == null) {
////            throw new IllegalStateException("Report type mismatch: expected chapter report but got null chapterId");
////        }
////        reportChapterDTO.setChapterId(chapterId);
////        Chapter chapter = chapterDAO.findById(chapterId);
////        if (chapter == null) {
////            throw new SQLException("Chapter not found for id: " + chapterId);
////        }
////
////        reportChapterDTO.setChapterNumber(chapter.getChapterNumber());
////        reportChapterDTO.setTitle(chapter.getTitle());
////        reportChapterDTO.setSeriesTitle(seriesDAO.findById(chapter.getSeriesId()).getTitle());
////        reportChapterDTO.setReporterUsername(userDAO.findById(report.getReporterId()).getUsername());
////        reportChapterDTO.setStatus(FormatServices.formatString(report.getStatus()));
////        reportChapterDTO.setCreatedAt(FormatServices.formatDate(report.getCreatedAt()));
////        reportChapterDTO.setUpdatedAt(FormatServices.formatDate(report.getUpdatedAt()));
////
////        return reportChapterDTO;
////    }
//
//
////    public List<ReportBaseDTO> buildReportChapterDTOList(List<Report> reportList) throws SQLException {
////        List<ReportBaseDTO> reportChapterDTOList = new ArrayList<>();
////        for (Report report : reportList) {
////            if(report.getTargetType().equals("chapter")) {
////                reportChapterDTOList.add(buildReportChapterDTO(report));
////            }
////        }
////        return reportChapterDTOList;
////    }
//
//
////    public ReportCommentDTO buildReportCommentDTO(Report report) throws SQLException {
////        if (report == null) {
////            throw new IllegalArgumentException("Report cannot be null");
////        }
////
////        Integer commentId = report.getCommentId();
////        if (commentId == null) {
////            throw new IllegalStateException("Report type mismatch: expected comment report but got null commentId");
////        }
////
////        ReportCommentDTO reportCommentDTO = new ReportCommentDTO();
////        reportCommentDTO.setId(report.getReportId());
////        reportCommentDTO.setCommentId(commentId);
////
////        Comment comment = commentDAO.findById(commentId);
////        if (comment == null) {
////            throw new SQLException("Comment not found for id: " + commentId);
////        }
////
////        reportCommentDTO.setUsernameComment(userDAO.findById(comment.getUserId()).getUsername());
////        reportCommentDTO.setContent(comment.getContent());
////        reportCommentDTO.setReporterUsername(userDAO.findById(report.getReporterId()).getUsername());
////        reportCommentDTO.setStatus(FormatServices.formatString(report.getStatus()));
////        reportCommentDTO.setCreatedAt(FormatServices.formatDate(report.getCreatedAt()));
////        reportCommentDTO.setUpdatedAt(FormatServices.formatDate(report.getUpdatedAt()));
////
////        return reportCommentDTO;
////    }
//
////    public List<ReportBaseDTO> buildReportCommentDTOList(List<Report> reportList) throws SQLException {
////        List<ReportBaseDTO> reportChapterDTOList = new ArrayList<>();
////        for (Report report : reportList) {
////            if(report.getTargetType().equals("comment")) {
////                reportChapterDTOList.add(buildReportCommentDTO(report));
////            }
////        }
////        return reportChapterDTOList;
////    }
//
//    public Report createReport(int userId, String type, String targetIdParam, String reason, String description) throws SQLException, ClassNotFoundException {
//        int targetId;
//        try {
//            targetId = Integer.parseInt(targetIdParam.trim());
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Invalid target ID");
//        }
//
//        if (reason == null || reason.trim().isEmpty()) {
//            throw new IllegalArgumentException("No reason for this report");
//        }
//
//        String fullReason = reason.trim();
//        if (description != null && !description.trim().isEmpty()) {
//            fullReason += " — Details: " + description.trim();
//        }
//
//        if (fullReason.length() > 500) {
//            fullReason = fullReason.substring(0, 499);
//        }
//
//        Report report = new Report();
//        report.setReporterId(userId);
//        report.setTargetType(type);
//        report.setReason(fullReason);
//        report.setStatus("pending");
//        report.setStaffId(null);
//        report.setCreatedAt(LocalDateTime.now());
//        report.setUpdatedAt(LocalDateTime.now());
//
//        if ("comment".equalsIgnoreCase(type)) {
//            report.setCommentId(targetId);
//            report.setChapterId(null);
//        } else if ("chapter".equalsIgnoreCase(type)) {
//            report.setCommentId(null);
//            report.setChapterId(targetId);
//        } else {
//            throw new IllegalArgumentException("Invalid report type: must be 'comment' or 'chapter'");
//        }
//
//        boolean inserted = reportDAO.insert(report);
//        return inserted ? report : null;
//    }
//
//    public List<ReportBaseDTO> buildReportList(String type, PaginationRequest paginationRequest) throws SQLException, ClassNotFoundException, ServletException, IOException {
//        if ("chapter".equals(type)) {
//            return buildReportChapterDTOList(reportDAO.getAllWithType(type, paginationRequest));
//        } else if ("comment".equals(type)) {
//            return buildReportCommentDTOList(reportDAO.getAllWithType(type, paginationRequest));
//        }
//        return null;
//    }
//
//    public int countReports (String type) throws SQLException {
//        return reportDAO.countReports(type);
//    }
//    public ReportBaseDTO getReportById(int reportId, String type) throws SQLException, ClassNotFoundException, ServletException, IOException {
//        if (type.equalsIgnoreCase("comment")) {
//            return buildReportCommentDTO(reportDAO.getById(reportId));
//        } else if (type.equalsIgnoreCase("chapter")) {
//            return buildReportChapterDTO(reportDAO.getById(reportId));
//        } else return null;
//    }
//}
