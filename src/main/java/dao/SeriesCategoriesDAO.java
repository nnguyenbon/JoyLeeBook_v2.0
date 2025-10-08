package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.SeriesCategories;

/**
 * DAO for series_categories table.
 * Uses external Connection for better transaction control.
 */
public class SeriesCategoriesDAO {

    /**
     * Get all series-category mappings.
     *
     * @param conn The active database connection.
     * @return List of SeriesCategory objects.
     * @throws SQLException if any SQL error occurs.
     */
    public List<SeriesCategories> getAll(Connection conn) throws SQLException {
        List<SeriesCategories> list = new ArrayList<>();
        String sql = "SELECT series_id, category_id FROM series_categories";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SeriesCategories sc = new SeriesCategories();
                sc.setSeriesId(rs.getInt("series_id"));
                sc.setCategoryId(rs.getInt("category_id"));
                list.add(sc);
            }
        }

        return list;
    }

    /**
     * Find a specific series-category record.
     *
     * @param conn       Active DB connection.
     * @param seriesId   Series ID.
     * @param categoryId Category ID.
     * @return SeriesCategory or null if not found.
     * @throws SQLException if any SQL error occurs.
     */
    public SeriesCategories findById(Connection conn, int seriesId, int categoryId) throws SQLException {
        String sql = "SELECT series_id, category_id FROM series_categories WHERE series_id = ? AND category_id = ?";
        SeriesCategories sc = null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sc = new SeriesCategories();
                    sc.setSeriesId(rs.getInt("series_id"));
                    sc.setCategoryId(rs.getInt("category_id"));
                }
            }
        }

        return sc;
    }

    /**
     * Insert a new series-category record.
     *
     * @param conn Active DB connection.
     * @param sc   The SeriesCategory object to insert.
     * @return true if inserted successfully.
     * @throws SQLException if any SQL error occurs.
     */
    public boolean insert(Connection conn, SeriesCategories sc) throws SQLException {
        String sql = "INSERT INTO series_categories (series_id, category_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sc.getSeriesId());
            ps.setInt(2, sc.getCategoryId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Update an existing record.
     *
     * @param conn          Active DB connection.
     * @param oldSeriesId   Old series ID.
     * @param oldCategoryId Old category ID.
     * @param newData       New data to update.
     * @return true if updated successfully.
     * @throws SQLException if any SQL error occurs.
     */
    public boolean update(Connection conn, int oldSeriesId, int oldCategoryId, SeriesCategories newData) throws SQLException {
        String sql = "UPDATE series_categories SET series_id = ?, category_id = ? WHERE series_id = ? AND category_id = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newData.getSeriesId());
            ps.setInt(2, newData.getCategoryId());
            ps.setInt(3, oldSeriesId);
            ps.setInt(4, oldCategoryId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Delete a record by seriesId and categoryId.
     *
     * @param conn       Active DB connection.
     * @param seriesId   Series ID.
     * @param categoryId Category ID.
     * @return true if deleted successfully.
     * @throws SQLException if any SQL error occurs.
     */
    public boolean delete(Connection conn, int seriesId, int categoryId) throws SQLException {
        String sql = "DELETE FROM series_categories WHERE series_id = ? AND category_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, categoryId);
            return ps.executeUpdate() > 0;
        }
    }
}
