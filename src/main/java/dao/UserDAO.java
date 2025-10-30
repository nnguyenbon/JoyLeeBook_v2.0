package dao;

import model.User;
import utils.AuthenticationUtils;

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

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
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

    public boolean isAuthor(String email) throws SQLException {
        String sql = "SELECT role FROM users WHERE email = ? AND is_deleted = 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String role =  rs.getString("role");
                    if (role.equals("author")) {
                        return true;
                    }
                }
                return false;
            }
        }
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
            stmt.setString(1, AuthenticationUtils.hashPwd(password));
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
        String sql = "UPDATE users SET role = 'author' WHERE user_id = ?";
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
        String sql = "SELECT * FROM users WHERE username = ? OR email = ? ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPasswordFromDB = rs.getString("password_hash");
                    if (AuthenticationUtils.checkPwd(password, hashedPasswordFromDB)) {
                        return mapResultSetToUser(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public boolean checkByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updatePoint (int userId, int point) throws SQLException {
        String sql = "UPDATE users SET points = points + ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, point);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
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

    /**
     * Upsert a user based on Google OAuth information.
     * If a user with the given googleId exists, update their fullName.
     * If not, check if a user with the given email exists:
     * - If yes, update their googleId and fullName.
     * - If no, create a new user record.
     *
     * @param username The username for the new user (if created).
     * @param fullName The full name of the user.
     * @param email    The email of the user (can be null).
     * @param googleId The Google ID of the user.
     * @return The user ID of the upserted user.
     * @throws SQLException If a database access error occurs.
     */
    public int upsertGoogleUser(String username, String fullName, String email, String googleId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            Integer userId = null;

            // First, try to find the user by their Google ID
            try (PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE google_id=?")) {
                ps.setString(1, googleId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            // If not found by Google ID, try to find by email
            if (userId == null && email != null) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE email=?")) {
                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getInt(1);
                            // If found by email, update the google_id for this user
                            try (PreparedStatement updatePs = conn.prepareStatement("UPDATE users SET google_id=?, updated_at=GETDATE() WHERE user_id=?")) {
                                updatePs.setString(1, googleId);
                                updatePs.setInt(2, userId);
                                updatePs.executeUpdate();
                            }
                        }
                    }
                }
            }

            // If the user is still not found, create a new user record
            if (userId == null) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username, full_name, email, google_id, is_verified, role, status, points) " + "VALUES (?, ?, ?, ?, 1, 'reader', 'active', 0); SELECT SCOPE_IDENTITY();")) {
                    ps.setString(1, username);
                    ps.setString(2, fullName);
                    if (email != null) {
                        ps.setString(3, email);
                    } else {
                        ps.setNull(3, java.sql.Types.VARCHAR);
                    }
                    ps.setString(4, googleId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getBigDecimal(1).intValue();
                        }
                    }
                }
            } else {
                // If the user already exists, update their full_name and the updated_at timestamp
                try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET full_name=?, updated_at=GETDATE() WHERE user_id=?")) {
                    ps.setString(1, fullName);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return userId;
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public int countActiveUsers() {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE is_deleted = 0 AND status = 'active'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countActiveAuthors() {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE is_deleted = 0 AND status = 'active' AND role='author'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countBannedUsers() {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE is_deleted = 0 AND status = 'banned'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
