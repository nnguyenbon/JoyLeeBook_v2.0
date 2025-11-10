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
import org.json.JSONArray;
import org.json.JSONObject;
import utils.AuthenticationUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "CoAuthorManagementServlet", value = "/manage-coauthors/*")
public class CoAuthorManagementServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action.");
            return;
        }
        switch (action) {
            case "/users" -> getUserName(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
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
            case "/remove" -> removeCoAuthor(request, response);
            default -> doGet(request, response);
        }
    }

    private void getUserName(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User author = (User) AuthenticationUtils.getLoginedUser(request.getSession());
//        if (author == null || !"author".equals(author.getRole())) {
//            String json = """
//                {
//                    "success": false,
//                    "message": "You are not logged in or you are not an author."
//                }";
//                """;
//            response.getWriter().write(json);
//        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String username = request.getParameter("username");
            if (username == null) {
                String json = """
                    {
                        "success": false,
                        "message": "You must provide a username."
                    }""";
                response.getWriter().write(json);
            }

            UserDAO userDAO = new UserDAO(conn);
            List<User> users = userDAO.findByName(username);

            JSONArray jsonArray = new JSONArray();
            for (User user : users) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", user.getUserId());
                jsonObject.put("name", user.getUsername());
                jsonArray.put(jsonObject);
            }
            response.getWriter().write(jsonArray.toString());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void addCoAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        User author = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        if (author == null || !"author".equals(author.getRole())) {
            response.sendRedirect("/login");
            return;
        }

        String mail = request.getParameter("username");


    }

    private void removeCoAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}