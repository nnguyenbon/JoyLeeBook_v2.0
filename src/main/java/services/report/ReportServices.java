package services.report;

import dao.*;
import db.DBConnection;
import dto.category.CategoryInfoDTO;
import dto.chapter.ChapterDetailDTO;
import dto.report.ReportChapterDTO;
import dto.report.ReportCommentDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;
import model.Comment;
import model.Report;
import services.chapter.ChapterServices;
import services.general.FormatServices;
import services.general.PaginationServices;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportServices {
    private final Connection connection;
    private final ReportDAO reportDAO;
    private final UserDAO userDAO;
    private final ChapterDAO chapterDAO;
    private final SeriesDAO seriesDAO;
    private final CommentDAO commentDAO;
    public ReportServices() throws SQLException, ClassNotFoundException {
        this.connection = DBConnection.getConnection();
        this.reportDAO = new ReportDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.seriesDAO = new SeriesDAO(connection);
        this.commentDAO = new CommentDAO(connection);
    }

    public ReportChapterDTO buildReportChapterDTO(Report report) throws SQLException {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        ReportChapterDTO dto = new ReportChapterDTO();
        dto.setId(report.getReportId());

        Integer chapterId = report.getChapterId();
        if (chapterId == null) {
            throw new IllegalStateException("Report type mismatch: expected chapter report but got null chapterId");
        }
        dto.setChapterId(chapterId);
        Chapter chapter = chapterDAO.findById(chapterId);
        if (chapter == null) {
            throw new SQLException("Chapter not found for id: " + chapterId);
        }

        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setTitle(chapter.getTitle());
        dto.setSeriesTitle(seriesDAO.findById(chapter.getSeriesId()).getTitle());
        dto.setReporterUsername(userDAO.findById(report.getReporterId()).getUsername());
        dto.setStatus(FormatServices.formatString(report.getStatus()));
        dto.setCreatedAt(FormatServices.formatDate(report.getCreatedAt()));
        dto.setUpdatedAt(FormatServices.formatDate(report.getUpdatedAt()));

        return dto;
    }


    public List<ReportChapterDTO> buildReportChapterDTOList(List<Report> reportList) throws SQLException {
        List<ReportChapterDTO> reportChapterDTOList = new ArrayList<>();
        for (Report report : reportList) {
            if(report.getTargetType().equals("chapter")) {
                reportChapterDTOList.add(buildReportChapterDTO(report));
            }
        }
        return reportChapterDTOList;
    }


    public ReportCommentDTO buildReportCommentDTO(Report report) throws SQLException {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        Integer commentId = report.getCommentId();
        if (commentId == null) {
            throw new IllegalStateException("Report type mismatch: expected comment report but got null commentId");
        }

        ReportCommentDTO dto = new ReportCommentDTO();
        dto.setId(report.getReportId());
        dto.setCommentId(commentId);

        Comment comment = commentDAO.findById(commentId);
        if (comment == null) {
            throw new SQLException("Comment not found for id: " + commentId);
        }

        dto.setUsernameComment(userDAO.findById(comment.getUserId()).getUsername());
        dto.setContent(comment.getContent());
        dto.setReporterUsername(userDAO.findById(report.getReporterId()).getUsername());
        dto.setStatus(FormatServices.formatString(report.getStatus()));
        dto.setCreatedAt(FormatServices.formatDate(report.getCreatedAt()));
        dto.setUpdatedAt(FormatServices.formatDate(report.getUpdatedAt()));

        return dto;
    }

    public List<ReportCommentDTO> buildReportCommentDTOList(List<Report> reportList) throws SQLException {
        List<ReportCommentDTO> reportChapterDTOList = new ArrayList<>();
        for (Report report : reportList) {
            if(report.getTargetType().equals("comment")) {
                reportChapterDTOList.add(buildReportCommentDTO(report));
            }
        }
        return reportChapterDTOList;
    }

    public Report createReport(int userId, String type, String targetIdParam, String reason, String description) throws SQLException, ClassNotFoundException {
        int targetId;
        try {
            targetId = Integer.parseInt(targetIdParam.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid target ID");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("No reason for this report");
        }

        String fullReason = reason.trim();
        if (description != null && !description.trim().isEmpty()) {
            fullReason += " — Details: " + description.trim();
        }

        if (fullReason.length() > 500) {
            fullReason = fullReason.substring(0, 499);
        }

        Report report = new Report();
        report.setReporterId(userId);
        report.setTargetType(type);
        report.setReason(fullReason);
        report.setStatus("pending");
        report.setStaffId(null);
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        if ("comment".equalsIgnoreCase(type)) {
            report.setCommentId(targetId);
            report.setChapterId(null);
        } else if ("chapter".equalsIgnoreCase(type)) {
            report.setCommentId(null);
            report.setChapterId(targetId);
        } else {
            throw new IllegalArgumentException("Invalid report type: must be 'comment' or 'chapter'");
        }

        boolean inserted = reportDAO.insert(report);
        return inserted ? report : null;
    }

    public boolean handleRedirect (String type, HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
        PaginationServices paginationServices = new PaginationServices();
        if ("chapter".equals(type)) {
            List<ReportChapterDTO> reportList = this.buildReportChapterDTOList(reportDAO.getAll());
            List<ReportChapterDTO> reportChapterDTOList = paginationServices.handleParameterPage(reportList, request);
            request.setAttribute("size", reportList.size());
            request.setAttribute("reportChapterDTOList", reportChapterDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/reportview/ReportChapterView.jsp").forward(request, response);
            return true;
        } else if ("comment".equals(type)) {
            List<ReportCommentDTO> reportList = this.buildReportCommentDTOList(reportDAO.getAll());
            List<ReportCommentDTO> reportCommentDTOList = paginationServices.handleParameterPage(reportList, request);
            request.setAttribute("size", reportList.size());
            request.setAttribute("reportCommentDTOList", reportCommentDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/reportview/ReportCommentView.jsp").forward(request, response);
            return true;
        }
        return false;
    }
}
