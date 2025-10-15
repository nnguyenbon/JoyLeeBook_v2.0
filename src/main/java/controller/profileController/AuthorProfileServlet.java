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
import services.account.UserServices;
import services.general.BadgesServices;
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
        int authorId = ValidationInput.isPositiveInteger(request.getParameter("authorId")) ? Integer.parseInt(request.getParameter("authorId")) : 0;
        try {
            SeriesServices seriesServices = new SeriesServices();
            List<SeriesInfoDTO> seriesInfoDTOList = seriesServices.seriesFromAuthor(authorId);

            AuthorServices authorServices = new AuthorServices();
            authorServices.extractDataFromAuthorId(seriesInfoDTOList,request);

            BadgesServices badgesServices = new BadgesServices();
            UserServices userServices = new UserServices();

            request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
            request.setAttribute("totalSeriesCount", seriesInfoDTOList.size());
            request.setAttribute("user", userServices.getUser(authorId));
            request.setAttribute("badgeList", badgesServices.badgeListFromUser(authorId));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("WEB-INF/views/profile/AuthorProfile.jsp").forward(request, response);
    }
}
