package dao;

import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    // Lấy toàn bộ user chưa bị xóa
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_deleted = 0";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    // Tìm user theo ID
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ? AND is_deleted = 0";
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

    // Thêm user mới
    public boolean insert(User user) throws SQLException {
        String sql = """
                INSERT INTO users (username, full_name, email, password_hash, 
                                   is_deleted, points, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setBoolean(5, false);
            stmt.setInt(6, 0);
            stmt.setTimestamp(7, Timestamp.valueOf(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(8, Timestamp.valueOf(user.getUpdatedAt() != null ? user.getUpdatedAt() : LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    // Cập nhật thông tin user
//    public boolean update(User user) throws SQLException {
//        String sql = """
//                UPDATE users
//                SET username = ?, full_name = ?, bio = ?, email = ?, password_hash = ?,
//                    role = ?, is_verified = ?, is_deleted = ?, status = ?, points = ?, updated_at = ?
//                WHERE user_id = ? AND is_deleted = 0
//                """;
//
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, user.getUsername());
//            stmt.setString(2, user.getFullName());
//            stmt.setString(3, user.getBio());
//            stmt.setString(4, user.getEmail());
//            stmt.setString(5, user.getPasswordHash());
//            stmt.setString(6, user.getRole());
//            stmt.setBoolean(7, user.isVerified());
//            stmt.setBoolean(8, user.isDeleted());
//            stmt.setString(9, user.getStatus());
//            stmt.setInt(10, user.getPoints());
//            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now())); // cập nhật thời gian sửa đổi
//            stmt.setInt(12, user.getUserId());
//            return stmt.executeUpdate() > 0;
//        }
//    }

    public boolean updatePassword(int userId, String password) throws SQLException {
        String sql = """
                UPDATE users
                SET password_hash = ?, updated_at = ?
                WHERE user_id = ? AND is_deleted = 0
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // cập nhật thời gian sửa đổi
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateProfile(User user) throws SQLException {
        String sql = """
                UPDATE users
                SET username = ?, full_name = ?, bio = ?, updated_at = ?
                WHERE user_id = ? AND is_deleted = 0
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getBio());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now())); // cập nhật thời gian sửa đổi
            stmt.setInt(5, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }


    // Xóa mềm user (chuyển cờ is_deleted = 1)
    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE users SET is_deleted = 1, status = 'inactive', updated_at = GETDATE() WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Tìm theo tên người dùng
    public List<User> findByName(String name) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? AND is_deleted = 0 AND role = 'author'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    // Lấy top người dùng theo điểm
    public List<User> selectTopUserPoints(int limit) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM users WHERE is_deleted = 0 AND role = 'reader' ORDER BY points DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    /**
     * Update user role to 'author' by user ID.
     *
     * @param userId The ID of the user to update.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException           If a database access error occurs.
     * @throws ClassNotFoundException If the JDBC driver class is not found.
     */
    public boolean updateUserRoleToAuthor(int userId) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Users SET role = 'author' WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Find user by username for authentication
     *
     * @return User object or null if not found
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    public User findByUserLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPasswordFromDB = rs.getString("password_hash");
                    if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                        return mapResultSetToUser(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public boolean findByUsernameOrEmail(String username, String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    // Hàm map dữ liệu từ ResultSet sang đối tượng User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setBio(rs.getString("bio"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setDeleted(rs.getBoolean("is_deleted"));
        user.setStatus(rs.getString("status"));
        user.setPoints(rs.getInt("points"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        user.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());
        user.setUpdatedAt(updated != null ? updated.toLocalDateTime() : LocalDateTime.now());

        return user;
    }
}
