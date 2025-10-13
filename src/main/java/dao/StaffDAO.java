package dao;

import model.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * StaffDAO provides data access methods for the Staff entity.
 * It includes methods to perform CRUD operations and to find staff members by username.
 *
 * @author KToan, HaiDD-dev
 */
public class StaffDAO {
    private final Connection conn;

    public StaffDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieves all non-deleted staff members from the database.
     *
     * @return a list of Staff objects
     * @throws SQLException if a database access error occurs
     */
    public List<Staff> getAll() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staffs WHERE is_deleted = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStaff(rs));
            }
        }
        return list;
    }

    /**
     * Finds a staff member by their ID.
     *
     * @param id the ID of the staff member
     * @return the Staff object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Staff findById(int id) throws SQLException {
        String sql = "SELECT * FROM staffs WHERE staff_id = ? AND is_deleted = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserts a new staff member into the database.
     *
     * @param staff the Staff object to insert
     * @return true if the insertion was successful, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(Staff staff) throws SQLException {
        String sql = "INSERT INTO staffs (username, password_hash, full_name, role, is_deleted, created_at)VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPasswordHash());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.setBoolean(5, false);
            stmt.setTimestamp(6, Timestamp.valueOf(staff.getCreatedAt()));

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing staff member in the database.
     *
     * @param staff the Staff object with updated information
     * @return true if the update was successful, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public boolean update(Staff staff) throws SQLException {
        String sql = "UPDATE staffs SET username = ?, password_hash = ?, full_name = ?, role = ?, is_deleted = ? WHERE staff_id = ? AND is_deleted = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPasswordHash());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.setBoolean(5, staff.isDeleted());
            stmt.setInt(6, staff.getStaffId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Soft deletes a staff member by setting the is_deleted flag to TRUE.
     *
     * @param id the ID of the staff member to delete
     * @return true if the deletion was successful, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE staffs SET is_deleted = TRUE WHERE staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Finds staff members by their username.
     * This method performs a case-insensitive search and allows partial matches.
     *
     * @param username the username to search for
     * @return a list of Staff objects matching the username
     * @throws SQLException if a database access error occurs
     */
    public List<Staff> findByUsername(String username) throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staffs WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + username + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    staffList.add(mapResultSetToStaff(rs));
                }
            }
            return staffList;
        }
    }

    /**
     * Maps a ResultSet row to a Staff object.
     *
     * @param rs the ResultSet to map
     * @return the mapped Staff object
     * @throws SQLException if a database access error occurs
     */
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setUsername(rs.getString("username"));
        staff.setPasswordHash(rs.getString("password_hash"));
        staff.setFullName(rs.getString("full_name"));
        staff.setRole(rs.getString("role"));
        staff.setDeleted(rs.getBoolean("is_deleted"));

        Timestamp created = rs.getTimestamp("created_at");
        staff.setCreatedAt(created != null ? created.toLocalDateTime() : LocalDateTime.now());

        return staff;
    }

    /**
     * Find a staff member by username for login purposes.
     * This method checks that the staff member is not marked as deleted.
     *
     * @param username the username of the staff member
     * @return the Staff object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Staff findByUsernameForLogin(String username) throws SQLException {
        String sql = "SELECT * FROM staffs WHERE username = ? AND is_deleted = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }
        return null;
    }
}
