package services.auth;

import dao.UserDAO;
import db.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginServices {

    private final Connection conn;

    public LoginServices() throws SQLException, ClassNotFoundException {
        this.conn = DBConnection.getConnection();
    }

    public User checkLogin(String username, String password) throws SQLException {
        UserDAO userDAO = new UserDAO(conn);
        return userDAO.findByUserLogin(username, password);
    }

}
