package dao;

import dao.helper.PaginationDAOHelper;
import db.DBConnection;
import dto.PaginationRequest;
import dto.AccountDTO;
import model.BanReason; // Assuming enum is in model
import services.general.FormatServices;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection conn;

    public AccountDAO() {
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh sách account tùy theo role người đăng nhập
     */
    public List<AccountDTO> getAccounts(String search, String filterByRole, String currentRole, PaginationRequest paginationRequest) throws SQLException {
        List<AccountDTO> list = new ArrayList<>();
        PaginationDAOHelper paginationDAOHelper = new PaginationDAOHelper(paginationRequest);
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        // --- Tạo truy vấn gốc theo quyền ---
        if ("reader".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT user_id AS id, username, full_name, email, role, status, 'user' AS type, created_at ")
                    .append("FROM users WHERE is_deleted = 0 AND role = 'author' ");
        }
        else if ("staff".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT user_id AS id, username, full_name, email, role, status, 'user' AS type, created_at ")
                    .append("FROM users WHERE is_deleted = 0 AND role IN ('reader','author') ");
        }
        else if ("admin".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT * FROM (")
                    .append("SELECT user_id AS id, username, full_name, email, role, status, 'user' AS type, created_at FROM users WHERE is_deleted = 0 ")
                    .append("UNION ALL ")
                    .append("SELECT staff_id AS id, username, full_name, NULL AS email, role, 'active' AS status, 'staff' AS type, created_at FROM staffs WHERE is_deleted = 0 ")
                    .append(") AS combined WHERE 1=1 ");
        }

        // --- Thêm điều kiện search ---
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR full_name LIKE ?) ");
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }

        // --- 3Thêm điều kiện lọc role ---
        if (filterByRole != null && !filterByRole.trim().isEmpty()) {
            sql.append("AND role = ? ");
            params.add(filterByRole.trim());
        }

        // ---  Thêm phân trang ---
        sql.append(paginationDAOHelper.buildPaginationClause());

        PreparedStatement stmt = conn.prepareStatement(sql.toString());

        // --- Gán tham số động ---
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        // --- 6️⃣ Lấy dữ liệu ---
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            AccountDTO acc = new AccountDTO();
            acc.setId(rs.getInt("id"));
            acc.setUsername(rs.getString("username"));
            acc.setFullName(rs.getString("full_name"));
            acc.setEmail(rs.getString("email"));
            acc.setRole(rs.getString("role"));
            acc.setStatus(rs.getString("status"));
            acc.setType(rs.getString("type"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null)
                acc.setCreatedAt(FormatServices.formatDate(createdAt.toLocalDateTime()));
            list.add(acc);
        }

        rs.close();
        stmt.close();
        return list;
    }


    /**
     * Đếm tổng số tài khoản theo quyền người xem
     */
    public int getTotalAccounts(String search, String filterByRole, String currentRole) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if ("reader".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT COUNT(*) FROM users WHERE is_deleted = 0 AND role = 'author' ");
        }
        else if ("staff".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT COUNT(*) FROM users WHERE is_deleted = 0 AND role IN ('reader', 'author') ");
        }
        else if ("admin".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT COUNT(*) FROM users WHERE is_deleted = 0 ");
        }

        // ----- Lọc theo search -----
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR full_name LIKE ?) ");
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }

        // ----- Lọc theo vai trò cụ thể -----
        if (filterByRole != null && !filterByRole.trim().isEmpty()) {
            sql.append("AND role = ? ");
            params.add(filterByRole.trim());
        }

        PreparedStatement stmt = conn.prepareStatement(sql.toString());

        // ----- Gán tham số động -----
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        int total = 0;
        if (rs.next()) total = rs.getInt(1);

        rs.close();
        stmt.close();
        return total;
    }


    public AccountDTO getAccountById(int accountId) throws SQLException {
        AccountDTO acc = null;

        String sql = """
            SELECT user_id AS id, username, full_name, email, role, status,
            'user' AS type, created_at, updated_at
            FROM users
            WHERE is_deleted = 0 AND user_id = ?
            UNION ALL
            SELECT staff_id AS id, username, full_name, NULL AS email, role,
            'active' AS status, 'staff' AS type, created_at, updated_at
            FROM staffs
            WHERE is_deleted = 0 AND staff_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                acc = new AccountDTO();
                acc.setId(rs.getInt("id"));
                acc.setUsername(rs.getString("username"));
                acc.setFullName(rs.getString("full_name"));
                acc.setEmail(rs.getString("email"));
                acc.setRole(rs.getString("role"));
                acc.setStatus(rs.getString("status"));
                acc.setType(rs.getString("type"));
                acc.setCreatedAt(FormatServices.formatDate(rs.getTimestamp("created_at").toLocalDateTime()));
                acc.setUpdatedAt(FormatServices.formatDate(rs.getTimestamp("updated_at").toLocalDateTime()));
            }
            rs.close();
        }

        return acc;
    }

    public List<AccountDTO> search(String query, String currentRole) throws SQLException {
        List<AccountDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // Similar to getAccounts but without pagination and with search always applied
        if ("reader".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT user_id AS id, username, full_name, email, role, status, 'user' AS type ")
                    .append("FROM users WHERE is_deleted = 0 AND role = 'author' AND (username LIKE ? OR full_name LIKE ?) ");
        } else if ("staff".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT user_id AS id, username, full_name, email, role, status, 'user' AS type ")
                    .append("FROM users WHERE is_deleted = 0 AND role IN ('reader','author') AND (username LIKE ? OR full_name LIKE ?) ");
        } else if ("admin".equalsIgnoreCase(currentRole)) {
            sql.append("SELECT user_id AS id, username, full_name, email, role, status, 'user' AS type FROM users WHERE is_deleted = 0 AND (username LIKE ? OR full_name LIKE ?) ")
                    .append("UNION ALL ")
                    .append("SELECT staff_id AS id, username, full_name, NULL AS email, role, 'active' AS status, 'staff' AS type FROM staffs WHERE is_deleted = 0 AND (username LIKE ? OR full_name LIKE ?) ");
        }

        sql.append(" ORDER BY id");

        PreparedStatement stmt;
        if ("admin".equalsIgnoreCase(currentRole)) {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, "%" + query + "%");
            stmt.setString(4, "%" + query + "%");
        } else {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            AccountDTO acc = new AccountDTO();
            acc.setId(rs.getInt("id"));
            acc.setUsername(rs.getString("username"));
            acc.setFullName(rs.getString("full_name"));
            acc.setEmail(rs.getString("email"));
            acc.setRole(rs.getString("role"));
            acc.setStatus(rs.getString("status"));
            acc.setType(rs.getString("type"));
            list.add(acc);
        }

        rs.close();
        stmt.close();
        return list;
    }

    public void insertAccount(AccountDTO account, String password) throws SQLException {
        // Assume hashing password; insert into users table for 'user' type
        String sql = """
            INSERT INTO users (username, full_name, email, role, status, password_hash, created_at, updated_at)
            VALUES (?, ?, ?, ?, 'active', ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getFullName());
            stmt.setString(3, account.getEmail());
            stmt.setString(4, account.getRole());
            stmt.setString(5, hashPassword(password)); // Implement hashPassword
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                account.setId(rs.getInt(1));
            }
            rs.close();
        }
    }

    public void updateAccount(AccountDTO account) throws SQLException {
        // Update users or staffs based on type
        if ("user".equals(account.getType())) {
            String sql = """
                UPDATE users SET full_name = ?, email = ?, role = ?, status = ?, updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ? AND is_deleted = 0
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, account.getFullName());
                stmt.setString(2, account.getEmail());
                stmt.setString(3, account.getRole());
                stmt.setString(4, account.getStatus());
                stmt.setInt(5, account.getId());
                stmt.executeUpdate();
            }
        } else if ("staff".equals(account.getType())) {
            String sql = """
                UPDATE staffs SET full_name = ?, role = ?, updated_at = CURRENT_TIMESTAMP
                WHERE staff_id = ? AND is_deleted = 0
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, account.getFullName());
                stmt.setString(2, account.getRole());
                stmt.setInt(3, account.getId());
                stmt.executeUpdate();
            }
        }
    }

    public void deleteAccount(int accountId) throws SQLException {
        // Soft delete: set is_deleted = 1
        // For both users and staffs
        String sqlUsers = "UPDATE users SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND is_deleted = 0";
        String sqlStaffs = "UPDATE staffs SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE staff_id = ? AND is_deleted = 0";

        try (PreparedStatement stmtUsers = conn.prepareStatement(sqlUsers);
             PreparedStatement stmtStaffs = conn.prepareStatement(sqlStaffs)) {
            stmtUsers.setInt(1, accountId);
            stmtUsers.executeUpdate();

            stmtStaffs.setInt(1, accountId);
            stmtStaffs.executeUpdate();
        }
    }

    public void banAccount(int accountId, BanReason reason) throws SQLException {
        // Set status to 'banned' and add reason (assume a ban_reason column)
        String sql = """
            UPDATE users SET status = 'banned', ban_reason = ?, updated_at = CURRENT_TIMESTAMP
            WHERE user_id = ? AND is_deleted = 0
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reason.name());
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
        // Similar for staffs if needed
    }

    // Helper method (implement actual hashing, e.g., BCrypt)
    private String hashPassword(String password) {
        return password; // Placeholder
    }
}