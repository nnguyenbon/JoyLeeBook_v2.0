package dao;

import dao.helper.PaginationDAOHelper;
import dto.PaginationRequest;
import model.Account;
import model.Staff;
import model.User;
import utils.FormatUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lấy tất cả accounts với phân quyền
     */
    public List<Account> getAllAccounts(String search, String roleFilter, String currentUserRole,
                                        PaginationRequest pagination) {
        List<Account> accounts = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // UNION query để lấy từ cả 2 bảng
        sql.append("WITH AllAccounts AS (");

        // Lấy từ bảng users
        sql.append("SELECT user_id as id, username, full_name, email, password_hash, ");
        sql.append("role, status, is_deleted, created_at, updated_at, ");
        sql.append("bio, google_id, is_verified, points, 'user' as account_type ");
        sql.append("FROM users WHERE 1=1 ");

        // Phân quyền: reader chỉ thấy author
        if ("reader".equals(currentUserRole)) {
            sql.append("AND role = 'author' AND status = 'active' ");
        }

        sql.append("UNION ALL ");

        // Lấy từ bảng staffs - chỉ admin mới thấy
        sql.append("SELECT staff_id as id, username, full_name, NULL as email, password_hash, ");
        sql.append("role, 'active' as status, is_deleted, created_at, updated_at, ");
        sql.append("NULL as bio, NULL as google_id, 0 as is_verified, 0 as points, 'staff' as account_type ");
        sql.append("FROM staffs WHERE 1=1 ");

        // Staff không thấy admin/staff khác
        if ("staff".equalsIgnoreCase(currentUserRole) || "reader".equalsIgnoreCase(currentUserRole)) {
            sql.append("AND 1=0 "); // Không lấy gì từ bảng staffs
        }

        sql.append(") ");
        sql.append("SELECT * FROM AllAccounts WHERE is_deleted = 0 ");

        // Filter theo search
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR full_name LIKE ?) ");
        }

        // Filter theo role
        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            sql.append("AND role = ? ");
        }
        PaginationDAOHelper paginationDAOHelper = new PaginationDAOHelper(pagination);
        // Pagination
        sql.append(paginationDAOHelper.buildPaginationClause());
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            if (roleFilter != null && !roleFilter.trim().isEmpty()) {
                ps.setString(paramIndex, roleFilter);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String accountType = rs.getString("account_type");
                Account account;

                if ("user".equals(accountType)) {
                    account = mapResultSetToUser(rs);
                } else {
                    account = mapResultSetToStaff(rs);
                }

                accounts.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Đếm tổng số accounts (cho pagination)
     */
    public int countAccounts(String search, String roleFilter, String currentUserRole) {
        int count = 0;
        StringBuilder sql = new StringBuilder();

        sql.append("WITH AllAccounts AS (");
        sql.append("SELECT username, full_name, role, is_deleted FROM users ");

        if ("reader".equalsIgnoreCase(currentUserRole)) {
            sql.append("WHERE role = 'author' AND status = 'active' ");
        } else {
            sql.append("WHERE 1=1 ");
        }

        sql.append("UNION ALL ");
        sql.append("SELECT username, full_name, role, is_deleted FROM staffs ");

        if ("staff".equalsIgnoreCase(currentUserRole) || "reader".equalsIgnoreCase(currentUserRole)) {
            sql.append("WHERE 1=0 ");
        } else {
            sql.append("WHERE 1=1 ");
        }

        sql.append(") ");
        sql.append("SELECT COUNT(*) FROM AllAccounts WHERE is_deleted = 0 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR full_name LIKE ?) ");
        }

        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            sql.append("AND role = ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            if (roleFilter != null && !roleFilter.trim().isEmpty()) {
                ps.setString(paramIndex++, roleFilter);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Lấy User theo userId
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id as id, username, full_name, email, password_hash, " +
                "role, status, is_deleted, created_at, updated_at, " +
                "bio, google_id, is_verified, points " +
                "FROM users WHERE user_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy Staff theo staffId
     */
    public Staff getStaffById(int staffId) {
        String sql = "SELECT staff_id AS id, username, full_name, password_hash, " +
                "role, is_deleted, created_at, updated_at " +
                "FROM staffs WHERE staff_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy account theo ID và role (tự động phân biệt User/Staff)
     */
    public Account getAccountById(int id, String role) {
        // Determine which table to query based on role
        boolean isUserRole = "reader".equalsIgnoreCase(role) || "author".equalsIgnoreCase(role);

        if (isUserRole) {
            return getUserById(id);
        } else {
            return getStaffById(id);
        }
    }

    /**
     * Lấy account theo username (tự động tìm trong cả 2 bảng)
     */
    public Account getAccountByUsername(String username) {
        // Try users table first
        String sqlUser = "SELECT user_id as id, username, full_name, email, password_hash, " +
                "role, status, is_deleted, created_at, updated_at, " +
                "bio, google_id, is_verified, points " +
                "FROM users WHERE username = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If not found, try staffs table
        String sqlStaff = "SELECT staff_id as id, username, full_name, password_hash, " +
                "role, is_deleted, created_at, updated_at " +
                "FROM staffs WHERE username = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sqlStaff)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean checkByUsername(String username) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ? UNION SELECT username FROM staffs WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean checkByEmail(String email) throws SQLException {
        String sql = "SELECT email FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Thêm User mới
     */
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, full_name, email, password_hash, role) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm Staff mới
     */
    public boolean insertStaff(Staff staff) {
        String sql = "INSERT INTO staffs (username, full_name, password_hash, role) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getUsername());
            ps.setString(2, staff.getFullName());
            ps.setString(3, staff.getPasswordHash());
            ps.setString(4, staff.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật User
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, role = ?, status = ?, " +
                "bio = ?, updated_at = GETDATE() WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getStatus());
            ps.setString(5, user.getBio());
            ps.setInt(6, user.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật Staff
     */
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staffs SET full_name = ?, role = ?, updated_at = GETDATE() " +
                "WHERE staff_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getFullName());
            ps.setString(2, staff.getRole());
            ps.setInt(3, staff.getStaffId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean checkStaffById(int staffId) {
        return true;
    }
    /**
     * Cập nhật account (tự động phân biệt User/Staff)
     */
    public boolean updateAccount(Account account) {
        if (account instanceof User) {
            return updateUser((User) account);
        } else if (account instanceof Staff) {
            return updateStaff((Staff) account);
        }
        return false;
    }

    /**
     * Xóa User (soft delete)
     */
    public boolean deleteUser(int userId) {
        String sql = "UPDATE users SET is_deleted = 1, updated_at = GETDATE() WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa Staff (soft delete)
     */
    public boolean deleteStaff(int staffId) {
        String sql = "UPDATE staffs SET is_deleted = 1, updated_at = GETDATE() WHERE staff_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa account (tự động phân biệt User/Staff bằng accountType)
     */
    public boolean deleteAccount(int id, String accountType) {
        if ("user".equalsIgnoreCase(accountType)) {
            return deleteUser(id);
        } else {
            return deleteStaff(id);
        }
    }

    /**
     * Cập nhật status của User (ban/unban)
     */
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ?, updated_at = GETDATE() WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setAccountId(rs.getInt("id"));
        user.setUserId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setDeleted(rs.getBoolean("is_deleted"));
        user.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        user.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));

        user.setBio(rs.getString("bio"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setPoints(rs.getInt("points"));

        user.setAccountType("user");

        return user;
    }

    /**
     * Map ResultSet to Staff object
     */
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setAccountId(rs.getInt("id"));
        staff.setStaffId(rs.getInt("id"));
        staff.setUsername(rs.getString("username"));
        staff.setFullName(rs.getString("full_name"));
        staff.setPasswordHash(rs.getString("password_hash"));
        staff.setRole(rs.getString("role"));
        staff.setDeleted(rs.getBoolean("is_deleted"));

        staff.setCreatedAt(FormatUtils.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
        staff.setUpdatedAt(FormatUtils.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));

        staff.setAccountType("staff");
        return staff;
    }


}
