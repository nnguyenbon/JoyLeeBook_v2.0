package controller.seriesController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/add-series")
public class AddSeriesServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int userId = ValidationInput.isPositiveInteger(request.getParameter("userId")) ? Integer.parseInt(request.getParameter("userId")) : 1;

        try {
            String coverImgUrl = request.getParameter("coverImgUrl");
            String title =  request.getParameter("title");
            String[] genre = request.getParameterValues("genres");
            String status = request.getParameter("status");
            String description = request.getParameter("description");

            SeriesServices seriesServices = new SeriesServices();
            seriesServices.createSeries(userId, coverImgUrl, title, status, description);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}