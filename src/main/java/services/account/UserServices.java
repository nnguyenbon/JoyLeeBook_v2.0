package services.account;

import dao.UserDAO;
import db.DBConnection;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class UserServices {
    public UserDAO userDAO;
    public UserServices() throws SQLException, ClassNotFoundException {
        this.userDAO = new UserDAO(DBConnection.getConnection());
    }

    public List<User> topUsersPoints(int limit) throws SQLException {
        return userDAO.selectTopUserPoints(limit);
    }

    public User getUser(int userId) throws SQLException {
        return userDAO.findById(userId);
    }
}
