package controller.generalController;

import dao.CategoryDAO;
import dao.ChapterDAO;
import dao.SeriesDAO;
import dao.StaffDAO;
import db.DBConnection;
import dto.category.CategoryInfoDTO;
import dto.chapter.ChapterDetailDTO;
import dto.chapter.ChapterInfoDTO;
import dto.chapter.ChapterItemDTO;
import dto.chapter.ChapterViewDTO;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Series;
import model.Staff;
import services.account.StaffServices;
import services.category.CategoryServices;
import services.chapter.ChapterServices;
import services.general.PaginationServices;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String staffIdParam = request.getParameter("staffId");
//        Chưa có session bắt bằng cách truyền param
        String staffIdParam = "1";
        int staffId = ValidationInput.isPositiveInteger(staffIdParam) ? Integer.parseInt(staffIdParam) : 1;
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");

        try {
            Connection connection = DBConnection.getConnection();
            StaffDAO staffDAO = new StaffDAO(connection);
            Staff staff = staffDAO.findById(staffId);
            StaffServices staffServices = new StaffServices();
            if (staffServices.handleRedirect(type, connection, request, response)) {
                return;
            }

            request.setAttribute("type", type != null ? type : "series");
            request.setAttribute("staffId", staff.getStaffId());
            request.setAttribute("staffName", staff.getFullName());
            request.getRequestDispatcher("/WEB-INF/views/general/StaffDashboard.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
