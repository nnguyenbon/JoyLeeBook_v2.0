package services.auth;

import dao.StaffDAO;
import dao.UserDAO;
import db.DBConnection;
import model.Staff;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginServices {

    private final Connection conn;

    public LoginServices() throws SQLException, ClassNotFoundException {
        this.conn = DBConnection.getConnection();
    }

    public User checkLoginUser(String username, String password) throws SQLException {
        UserDAO userDAO = new UserDAO(conn);
        return userDAO.findByUserLogin(username, password);
    }

    public Staff checkLoginStaff(String username, String password) throws SQLException {
        StaffDAO staffDAO = new StaffDAO(conn);
        return staffDAO.findByUserLogin(username, password);
    }
}
