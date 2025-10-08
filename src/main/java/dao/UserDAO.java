package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }


    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }


    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }


    public boolean insert(User user) throws SQLException {
        String sql = "INSERT INTO users (email, username, password_hash, full_name, role, email_otp,is_verified, status, google_id, created_at)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getEmailOtp());
            stmt.setBoolean(7, user.isVerified());
            stmt.setString(8, "inactive");
            stmt.setString(9, user.getGoogleAccountId());
            stmt.setTimestamp(10, Timestamp.valueOf(user.getCreatedAt()));
            return stmt.executeUpdate() > 0;
        }
    }


    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, username = ?, password_hash = ?, full_name = ?, role = ?, email_otp = ?, is_verified = ?, status = ?, google_id = ? WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getEmailOtp());
            stmt.setBoolean(7, user.isVerified());
            stmt.setString(8, user.getStatus());
            stmt.setString(9, user.getGoogleAccountId());
            stmt.setInt(10, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }


    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE users SET status = '?' WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "inactive");
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setEmailOtp(rs.getString("email_otp"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setStatus(rs.getString("status"));
        user.setGoogleAccountId(rs.getString("google_id"));
        user.setGoogleAccount(user.getGoogleAccountId() != null);

        Timestamp created = rs.getTimestamp("created_at");
        user.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return user;
    }
}