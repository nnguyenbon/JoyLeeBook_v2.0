package services.account;

import dao.*;
import db.DBConnection;
import dto.author.AuthorItemDTO;
import dto.category.CategoryInfoDTO;
import dto.chapter.ChapterDetailDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chapter;
import model.Staff;
import model.User;
import services.category.CategoryServices;
import services.chapter.ChapterServices;
import services.general.PaginationServices;
import services.series.SeriesServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utils.HashPwd.hashPwd;

public class StaffServices {
    private final StaffDAO staffDAO;
    private final SeriesDAO seriesDAO;
    private final ChapterDAO chapterDAO;
    private final CategoryDAO categoryDAO;
    public StaffServices() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getConnection();
        this.staffDAO = new StaffDAO(connection);
        this.seriesDAO = new SeriesDAO(connection);
        this.chapterDAO = new ChapterDAO(connection);
        this.categoryDAO = new CategoryDAO(connection);
    }

    public boolean handleRedirect (String type, HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ServletException, IOException {
        PaginationServices paginationServices = new PaginationServices();
        if ("series".equals(type)) {
            SeriesServices seriesServices = new SeriesServices();
            List<SeriesInfoDTO> seriesList = seriesServices.buildSeriesInfoDTOList(seriesDAO.getAll());
            List<SeriesInfoDTO> seriesInfoDTOList = paginationServices.handleParameterPage(seriesList, request);
            request.setAttribute("size", seriesList.size());
            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/SeriesListView.jsp").forward(request, response);
            return true;
        } else if ("chapter".equals(type)) {
            ChapterServices chapterServices = new ChapterServices();
            List<ChapterDetailDTO> chapterList = chapterServices.buildChapterDetailDTOList(chapterDAO.getAll());
            List<ChapterDetailDTO> chapterDetailDTOList = paginationServices.handleParameterPage(chapterList, request);
            request.setAttribute("size", chapterList.size());
            request.setAttribute("chapterDetailDTOList", chapterDetailDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/ChaptersListView.jsp").forward(request, response);
            return true;
        } else if ("genres".equals(type)) {
            CategoryServices categoryServices = new CategoryServices();
            List<CategoryInfoDTO> categoryList = categoryServices.buildCategoryInfoDTOList(categoryDAO.getAll());
            List<CategoryInfoDTO> categoryInfoDTOList = paginationServices.handleParameterPage(categoryList, request);
            request.setAttribute("size", categoryList.size());
            request.setAttribute("categoryInfoDTOList", categoryInfoDTOList);
            request.getRequestDispatcher("/WEB-INF/views/general/staffview/GenresListView.jsp").forward(request, response);
            return true;
        }
        return false;
    }
    public Staff getStaffAccount(int staffId) throws SQLException, ClassNotFoundException {
        return staffDAO.findById(staffId);
    }

    public AuthorItemDTO buildAuthorItemDTO(User author) throws SQLException {
        AuthorItemDTO authorItemDTO = new AuthorItemDTO();
        authorItemDTO.setAuthorId(author.getUserId());
        authorItemDTO.setUserName(author.getUsername());
        authorItemDTO.setSeriesList(seriesDAO.getSeriesByAuthorId(author.getUserId()));
        authorItemDTO.setTotalChapters(seriesDAO.getSeriesByAuthorId(author.getUserId()).size());
        return authorItemDTO;
    }

//    public List<Staff> buildStaffItemDTOList(List<User> authors) throws SQLException {
//        List<AuthorItemDTO> authorItemDTOList = new ArrayList<>();
//        for (User author : authors) {
//            authorItemDTOList.add(buildAuthorItemDTO(author));
//        }
//        return authorItemDTOList;
//    }

    public Staff createStaff(String username, String fullname, String password) throws SQLException {
        if(username.isEmpty() || fullname.isEmpty() || password.isEmpty()){
            throw new SQLException("Username and/or password cannot be empty");
        }

        String passwordHash = hashPwd(password);

        Staff newStaff = new Staff();
        newStaff.setUsername(username);
        newStaff.setFullName(fullname);
        newStaff.setPasswordHash(passwordHash);

        try {
            boolean success = staffDAO.insert(newStaff);
            if (!success) {
                throw new SQLException("Failed to create staff account into database.");
            }
            return newStaff;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating staff account", e);
        }
    }

    public Staff editStaff(String username, String fullname, String password) throws SQLException {
        if (username.isEmpty() || fullname.isEmpty()) {
            throw new SQLException("Username and/or fullname cannot be empty");
        }

        if (password.isEmpty()) {
            throw new SQLException("Password cannot be empty");
        }

        Staff staff = new Staff();
        staff.setUsername(username);
        staff.setFullName(fullname);
        staff.setPasswordHash(password);

        try {
            boolean success = staffDAO.update(staff);
            if (!success) {
                throw new SQLException("Failed to edit staff account into database.");
            }
            return staff;
        } catch (SQLException e) {
            throw new RuntimeException("Database error staff while edit account", e);
        }
    }

    public void deleteStaff(String userIdParam) throws SQLException {
        if (userIdParam.isEmpty()) throw new SQLException("Username cannot be empty");

        int userId;
        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            throw new SQLException("Username must be an integer");
        }

        try {
            boolean success = staffDAO.delete(userId);
            if (!success) {
                throw new SQLException("Failed to delete staff account from database.");
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Database error while deleting staff account", e);
        }
    }
}
