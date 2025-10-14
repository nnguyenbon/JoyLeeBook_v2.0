package controller.profileController;

import dao.*;
import db.DBConnection;
import services.account.AuthorServices;
import dto.series.SeriesInfoDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import services.series.SeriesServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/author-profile")
public class AuthorProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorIdParam = request.getParameter("authorId");

        int authorId = ValidationInput.isPositiveInteger(authorIdParam) ? Integer.parseInt(authorIdParam) : 0;

        try {
            Connection connection = DBConnection.getConnection();
            UserDAO userDAO = new UserDAO(connection);
            SeriesDAO seriesDAO = new SeriesDAO(connection);
            BadgesUserDAO badgesUserDAO = new BadgesUserDAO(connection);

            SeriesServices seriesServices = new SeriesServices(connection);
            List<SeriesInfoDTO> seriesInfoDTOList = seriesServices.buildSeriesInfoDTOList(seriesDAO.getSeriesByAuthorId(authorId));

            AuthorServices authorServices = new AuthorServices(connection);
            authorServices.extractDataFromAuthorId(seriesInfoDTOList,request);

            User user = userDAO.findById(authorId);
            List<Badge> badgeList = badgesUserDAO.getBadgesByUserId(authorId);

            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.setAttribute("totalSeriesCount", seriesInfoDTOList.size());
            request.setAttribute("user", user);
            request.setAttribute("badgeList", badgeList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/profile/AuthorProfile.jsp").forward(request, response);
    }
}
