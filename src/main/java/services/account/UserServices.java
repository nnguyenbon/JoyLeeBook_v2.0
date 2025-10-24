package services.account;

import dao.UserDAO;
import db.DBConnection;
import dto.author.AuthorItemDTO;
import dto.series.SeriesInfoDTO;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static utils.HashPwd.hashPwd;

public class UserServices {
    public UserDAO userDAO;

    public UserServices() throws SQLException, ClassNotFoundException {
        Connection conn = DBConnection.getConnection();
        this.userDAO = new UserDAO(conn);
    }

    public List<User> topUsersPoints(int limit) throws SQLException {
        return userDAO.selectTopUserPoints(limit);
    }

    public User getUser(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    public boolean editProfile(User user) throws SQLException {
        return userDAO.updateProfile(user);
    }

    public String editPassword(int userId, String password, String newPassword, String confirmPassword) throws SQLException {
        if (!newPassword.equals(confirmPassword)) return "Your password and confirm password do not match";

        String oldPassword = userDAO.findById(userId).getPasswordHash();
        if (oldPassword.equals(password)) return "Your old password is incorrect";

        if (userDAO.updatePassword(userId, newPassword)) {
            return "Update password successfully!";
        } else {
            return "Something went wrong. Please try again.";
        }
    }

    public User createUser(String username, String fullname, String password) throws SQLException {
        if (username.isEmpty() || fullname.isEmpty() || password.isEmpty()) {
            throw new SQLException("Username and/or password cannot be empty");
        }

        String passwordHash = hashPwd(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setFullName(fullname);
        newUser.setPasswordHash(passwordHash);

        try {
            boolean success = userDAO.insert(newUser);
            if (!success) {
                throw new SQLException("Failed to create reader account into database.");
            }
            return newUser;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating reader account", e);
        }
    }

    public User editUser(String username, String fullname) throws SQLException {
        if (username.isEmpty() || fullname.isEmpty()) {
            throw new SQLException("Username and/or fullname cannot be empty");
        }

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullname);

        try {
            boolean success = userDAO.updateProfile(user);
            if (!success) {
                throw new SQLException("Failed to edit account into database.");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while edit account", e);
        }
    }

    public void deleteUser(String userIdParam) throws SQLException {
        if (userIdParam.isEmpty()) throw new SQLException("Username cannot be empty");

        int userId;
        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            throw new SQLException("Username must be an integer");
        }

        try {
            boolean success = userDAO.delete(userId);
            if (!success) {
                throw new SQLException("Failed to delete account from database.");
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Database error while deleting account", e);
        }
    }
}
