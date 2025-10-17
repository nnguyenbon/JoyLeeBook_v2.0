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
        ReportChapterDTO reportChapterDTO = new ReportChapterDTO();
        reportChapterDTO.setId(report.getReportId());
        reportChapterDTO.setChapterId(report.getTargetId());
        Chapter chapter = chapterDAO.findById(report.getTargetId());
        reportChapterDTO.setChapterNumber(chapter.getChapterNumber());
        reportChapterDTO.setTitle(chapter.getTitle());
        reportChapterDTO.setSeriesTitle(seriesDAO.findById(chapter.getSeriesId()).getTitle());
        reportChapterDTO.setReporterUsername(userDAO.findById(report.getReporterId()).getUsername());
        reportChapterDTO.setStatus(FormatServices.formatString(report.getStatus()));
        reportChapterDTO.setCreatedAt(FormatServices.formatDate(report.getCreatedAt()));
        reportChapterDTO.setUpdatedAt(FormatServices.formatDate(report.getUpdatedAt()));
        return reportChapterDTO;
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
        ReportCommentDTO reportCommentDTO = new ReportCommentDTO();
        reportCommentDTO.setId(report.getReportId());
        reportCommentDTO.setCommentId(report.getTargetId());
        Comment comment = commentDAO.findById(report.getTargetId());
        reportCommentDTO.setReporterUsername(userDAO.findById(comment.getUserId()).getUsername());
        reportCommentDTO.setContent(comment.getContent());
        reportCommentDTO.setReporterUsername(userDAO.findById(report.getReporterId()).getUsername());
        reportCommentDTO.setStatus(FormatServices.formatString(report.getStatus()));
        reportCommentDTO.setCreatedAt(FormatServices.formatDate(report.getCreatedAt()));
        reportCommentDTO.setUpdatedAt(FormatServices.formatDate(report.getUpdatedAt()));
        return reportCommentDTO;
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
    public boolean handleRedirect (String type, HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
        PaginationServices paginationServices = new PaginationServices();
        ReportServices reportServices = new ReportServices();
        if ("chapter".equals(type)) {
            List<ReportChapterDTO> reportList = reportServices.buildReportChapterDTOList(reportDAO.getAll());
            List<ReportChapterDTO> reportChapterDTOList = paginationServices.handleParameterPage(reportList, request);
            request.setAttribute("size", reportList.size());
            request.setAttribute("reportChapterDTOList", reportChapterDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/reportview/ReportChapterView.jsp").forward(request, response);
            return true;
        } else if ("comment".equals(type)) {
            List<ReportCommentDTO> reportList = reportServices.buildReportCommentDTOList(reportDAO.getAll());
            List<ReportCommentDTO> reportCommentDTOList = paginationServices.handleParameterPage(reportList, request);
            request.setAttribute("size", reportList.size());
            request.setAttribute("reportCommentDTOList", reportCommentDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/reportview/ReportCommentView.jsp").forward(request, response);
            return true;
        }
        return false;
    }
}
