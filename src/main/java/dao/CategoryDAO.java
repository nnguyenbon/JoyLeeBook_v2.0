package dao;

import model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for managing 'categories' table.
 * Includes CRUD and find methods.
 */
public class CategoryDAO {
    private final Connection conn;

    public CategoryDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Get all category records.
     */
    public List<Category> getAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        }
        return list;
    }

    /**
     * Find category by ID.
     */
    public Category findById(int id) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }
        return null;
    }

    /**
     * Insert a new category.
     */
    public boolean insert(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Update an existing category.
     */
    public boolean update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getCategoryId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete a category by ID.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Category> getCategoryBySeriesId(int seriesId) throws SQLException {
        List<Category> listCategory = new ArrayList<>();
        String sql = "SELECT * FROM categories c JOIN series_categories sc ON c.category_id = sc.category_id WHERE sc.series_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seriesId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listCategory.add(mapResultSetToCategory(rs));
                }
            }
            return listCategory;
        }
    }

    public boolean matchGenres(int seriesId, List<String> genres) throws SQLException {
        if (genres == null || genres.isEmpty()) return true;

        String placeholders = String.join(",", genres.stream().map(g -> "?").toArray(String[]::new));

        String sql = "SELECT COUNT(*) FROM series_categories sc " +
                "JOIN categories c ON sc.category_id = c.category_id " +
                "WHERE sc.series_id = ? AND c.name IN (" + placeholders + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            for (int i = 0; i < genres.size(); i++) {
                ps.setString(i + 2, genres.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Category> getCategoryTop(int limit) throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + """
                c.category_id,
                        c.name,
                        c.description,
                        COUNT(sc.series_id) AS total_series
                FROM
                categories AS c
                INNER JOIN
                series_categories AS sc ON c.category_id = sc.category_id
                INNER JOIN
                series AS s ON sc.series_id = s.series_id
                GROUP BY
                c.category_id, c.name, c.description
                ORDER BY
                total_series DESC;""";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        }
        return list;
    }
    /**
     * Helper: Map a ResultSet row into a Category object.
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        return category;
    }
}
