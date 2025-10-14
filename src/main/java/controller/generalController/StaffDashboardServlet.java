package controller.generalController;

import dao.SeriesDAO;
import dao.StaffDAO;
import db.DBConnection;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Series;
import model.Staff;
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


        try {
            Connection connection = DBConnection.getConnection();
            StaffDAO staffDAO = new StaffDAO(connection);
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            Staff staff = staffDAO.findById(staffId);

            PaginationServices paginationServices = new PaginationServices();
            SeriesServices seriesServices = new SeriesServices(connection);
            List<SeriesInfoDTO> seriesList = seriesServices.buildSeriesInfoDTOList(seriesDAO.getAll());
            List<SeriesInfoDTO> seriesInfoDTOList = paginationServices.handleParameterPage(seriesList, request);

            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.setAttribute("staffId", staff.getStaffId());
            request.setAttribute("staffName", staff.getFullName());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/general/StaffDashboard.jsp").forward(request, response);
    }
}
