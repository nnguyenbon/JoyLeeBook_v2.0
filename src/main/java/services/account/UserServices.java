package services.account;

import dao.UserDAO;
import db.DBConnection;
import model.User;
import services.general.FormatServices;

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
        User user = userDAO.findById(userId);
        user.setRole(FormatServices.formatString(user.getRole()));
        return user;
    }

    public boolean editProfile(User user) throws SQLException {
        return userDAO.updateProfile(user);
    }

    public String editPassword(int userId, String password, String newPassword, String confirmPassword) throws SQLException {
        if (!newPassword.equals(confirmPassword)) return "Your password and confirm password do not match";

        String oldPassword = userDAO.findById(userId).getPasswordHash();
        if (oldPassword.equals(password)) return "Your old password is incorrect";

        if(userDAO.updatePassword(userId, newPassword)) {
            return "Update password successfully!";
        } else {
            return "Something went wrong. Please try again.";
        }
    }
}
