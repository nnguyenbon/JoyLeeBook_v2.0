package controller.authorController;

import dao.BadgeDAO;
import dao.SeriesAuthorDAO;
import dao.SeriesDAO;
import dao.UserDAO;
import db.DBConnection;
import model.Account;
import model.Series;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "CoAuthorManagementServlet", value = "/manage-coauthors/*")
public class CoAuthorManagementServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Account currentUser = AuthenticationUtils.getLoginedUser(request.getSession());

            // test
//            currentUser = new User();
//            currentUser.setUserId(4);
//            currentUser.setRole("author");
            // end test

            if (currentUser == null || !"author".equals(currentUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            User user = (User) currentUser;
            int userId = user.getUserId();

            Connection conn = DBConnection.getConnection();

            SeriesDAO seriesDAO = new SeriesDAO(conn);
            List<Series> series = seriesDAO.getSeriesByUserId(userId);

            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            // <SeriesId, List<User>>
            HashMap<Integer, List<User>> seriesAuthorMap = new HashMap<>();
            for( Series s : series) {
                List<User> authors = seriesAuthorDAO.findUsersBySeriesId(s.getSeriesId());
                seriesAuthorMap.put(s.getSeriesId(), authors);
            }

            request.setAttribute("series", series);
            request.setAttribute("seriesAuthorMap", seriesAuthorMap);

            request.setAttribute("pageTitle", "AuthorDashboard");
            request.setAttribute("contentPage", "/WEB-INF/views/author/manage-coauthors.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error/error.jsp");
        } catch (ClassNotFoundException e) {
            System.out.println("Error in CoAuthorManagementServlet: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // add
    // remove
    // accept
    // reject
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();

        if (action == null) action = "";
        switch (action) {
            case "/add" -> addCoAuthor(request, response);
            case "remove" -> removeCoAuthor(request, response);
            default -> doGet(request, response);
        }
    }

    private void addCoAuthor(HttpServletRequest request, HttpServletResponse response) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        UserDAO userDAO = new UserDAO(conn);
        User userToAdd = userDAO.findByUsername(username);

        if (userToAdd == null || !"author".equals(userToAdd.getRole())) {
            response.sendRedirect(redirectUrl + "&error=userNotFoundOrNotAuthor");
            return;
        }

        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
        List<User> currentAuthors = seriesAuthorDAO.findUsersBySeriesId(seriesId);
        boolean isAlreadyAuthor = currentAuthors.stream().anyMatch(u -> u.getUserId() == userToAdd.getUserId());

        if (isAlreadyAuthor) {
            response.sendRedirect(redirectUrl + "&error=userIsAlreadyAuthor");
            return;
        }

        seriesAuthorDAO.addAuthorToSeries(seriesId, userToAdd.getUserId());
        response.sendRedirect(redirectUrl + "&message=authorAddedSuccess");
    }

    private void removeCoAuthor(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ClassNotFoundException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        // test
//        currentUser = new User();
//        currentUser.setUserId(4);
//        currentUser.setRole("author");
        // end test

        Connection conn = DBConnection.getConnection();

        // Prevent users from removing themselves, or the main author.
        if (currentUser.getUserId() == userIdToRemove) {
            response.sendRedirect(redirectUrl + "&error=cannotRemoveSelf");
            return;
        }

        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
        seriesAuthorDAO.removeAuthorFromSeries(seriesId, userIdToRemove);
        response.sendRedirect(redirectUrl + "&message=authorRemovedSuccess");
    }
}