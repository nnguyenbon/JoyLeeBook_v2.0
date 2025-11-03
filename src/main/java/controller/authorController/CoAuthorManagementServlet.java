package controller.authorController;

import dao.SeriesAuthorDAO;
import dao.SeriesDAO;
import dao.UserDAO;
import db.DBConnection;
import model.Series;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CoAuthorManagementServlet", value = "/manage-coauthors")
public class CoAuthorManagementServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User currentUser = (User) request.getSession().getAttribute("user");

            // test
//            currentUser = new User();
//            currentUser.setUserId(4);
//            currentUser.setRole("author");
            // end test

            if (currentUser == null || !"author".equals(currentUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            Connection conn = DBConnection.getConnection();

            SeriesDAO seriesDAO = new SeriesDAO(conn);
            Series series = seriesDAO.findById(seriesId, "ongoing");

            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            List<User> authors = seriesAuthorDAO.findUsersBySeriesId(seriesId);

            request.setAttribute("series", series);
            request.setAttribute("authors", authors);
            request.getRequestDispatcher("/WEB-INF/views/author/manage-coauthors.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error/error.jsp");
        } catch (ClassNotFoundException e) {
            System.out.println("Error in CoAuthorManagementServlet: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int seriesId = Integer.parseInt(request.getParameter("seriesId"));
        String redirectUrl = request.getContextPath() + "/manage-coauthors?seriesId=" + seriesId;

        try {
            if ("add".equals(action)) {
                String username = request.getParameter("username");
                addCoAuthor(username, seriesId, redirectUrl, request, response);
            } else if ("remove".equals(action)) {
                int userIdToRemove = Integer.parseInt(request.getParameter("userId"));
                removeCoAuthor(request, userIdToRemove, seriesId, redirectUrl, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error/error.jsp");
        }
    }

    private void addCoAuthor(String username, int seriesId, String redirectUrl, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ClassNotFoundException {
        Connection conn = DBConnection.getConnection();

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

    private void removeCoAuthor(HttpServletRequest request, int userIdToRemove, int seriesId, String redirectUrl, HttpServletResponse response) throws SQLException, IOException, ClassNotFoundException {
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