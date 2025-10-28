package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import dao.helper.PaginationDAOHelper;
import dto.PaginationRequest;
import model.Series;
import model.SeriesAuthor;

/**
 * Data Access Object (DAO) for the Series entity.
 * Provides methods to perform CRUD operations on the series table in the database.
 *
 * @author KToan, HaiDD-dev, Trunguyen
 */
public class SeriesDAO {
    private final Connection conn;

    public SeriesDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Builds the base SQL query for selecting or counting series based on the given filters.
     *
     * @param isCount   true if building a count query, false for select query
     * @param genreIds  List of genre IDs to filter by
     * @param userId    The author (user) ID
     * @param status    Series status ("Ongoing", "Completed", etc.)
     * @param search    Keyword for title search
     * @return          A StringBuilder containing the full SQL query
     */

    // ========================== QUERY BUILDER ===============================

    private StringBuilder buildSeriesBaseQuery(boolean isCount, List<Integer> genreIds, int userId, String status, String search) {
        StringBuilder sql = new StringBuilder();

        if (isCount) {
            sql.append("SELECT COUNT(DISTINCT s.series_id) AS total ");
        } else {
            sql.append("""
                SELECT DISTINCT 
                    s.series_id, s.title, s.description, s.cover_image_url,
                    s.status, s.is_deleted, s.rating_points, 
                    s.created_at, s.updated_at
                """);
        }

        sql.append("FROM series s ");

        if (genreIds != null && !genreIds.isEmpty()) {
            sql.append("""
                INNER JOIN series_categories sc ON s.series_id = sc.series_id
                INNER JOIN categories c ON sc.category_id = c.category_id
                """);
        }

        if (userId > 0) {
            sql.append("INNER JOIN series_author sa ON s.series_id = sa.series_id ");
        }

        sql.append("WHERE s.is_deleted = 0 ");

        if (genreIds != null && !genreIds.isEmpty()) {
            sql.append("AND c.category_id IN (");
            for (int i = 0; i < genreIds.size(); i++) {
                sql.append("?");
                if (i < genreIds.size() - 1) sql.append(",");
            }
            sql.append(") ");
        }

        if (userId > 0) {
            sql.append("AND sa.user_id = ? ");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND s.status = ? ");
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND s.title LIKE ? ");
        }

        return sql;
    }

    private int setSeriesQueryParameters(PreparedStatement stmt, List<Integer> genreIds, int userId, String status, String search) throws SQLException {
        int index = 1;

        if (genreIds != null && !genreIds.isEmpty()) {
            for (Integer id : genreIds) {
                stmt.setInt(index++, id);
            }
        }

        if (userId > 0) {
            stmt.setInt(index++, userId);
        }

        if (status != null && !status.trim().isEmpty()) {
            stmt.setString(index++, status);
        }

        if (search != null && !search.trim().isEmpty()) {
            stmt.setString(index++, "%" + search + "%");
        }

        return index;
    }

    // ========================== SELECT LIST + COUNT ===============================

    public List<Series> getAll(String search, List<Integer> genreIds, int userId, String status, PaginationRequest paginationRequest) throws SQLException {
        List<Series> list = new ArrayList<>();
        PaginationDAOHelper paginationDAOHelper = new PaginationDAOHelper(paginationRequest);

        StringBuilder sql = buildSeriesBaseQuery(false, genreIds, userId, status, search);
        sql.append(paginationDAOHelper.buildPaginationClause());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setSeriesQueryParameters(stmt, genreIds, userId, status, search);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSeriesFromResultSet(rs));
                }
            }
        }

        return list;
    }

    public int getTotalSeriesCount(String search, List<Integer> genreIds, int userId, String status) throws SQLException {
        StringBuilder sql = buildSeriesBaseQuery(true, genreIds, userId, status, search);
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setSeriesQueryParameters(stmt, genreIds, userId, status, search);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }


    /**
     * Finds a series by its ID.
     *
     * @param seriesId the ID of the series to find
     * @return the Series object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Series findById(int seriesId) throws SQLException {
        String sql = "SELECT * FROM series WHERE series_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Series s = new Series();
                    s.setSeriesId(rs.getInt("series_id"));
                    s.setTitle(rs.getNString("title"));
                    s.setDescription(rs.getNString("description"));
                    s.setCoverImgUrl(rs.getNString("cover_image_url"));
                    s.setStatus(rs.getString("status"));
                    s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Inserts a new series into the database.
     *
     * @param series   the Series object to insert
     * @return true if the insertion was successful, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public boolean insert(Series series) throws SQLException {
        String sql = "INSERT INTO series (title, description, cover_image_url, status, created_at, updated_at, is_deleted, rating_points) VALUES (?, ?, ?, ?, ?, ?, false, 0)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, series.getTitle());
            ps.setInt(2, series.getAuthorId());
            ps.setString(3, series.getDescription());
            ps.setString(4, series.getCoverImgUrl());
            ps.setString(5, series.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        series.setSeriesId(rs.getInt(1));
                    }
                    SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
                    SeriesAuthor seriesAuthor = new SeriesAuthor(series.getSeriesId(), series.getAuthorId(), LocalDateTime.now());
                    seriesAuthorDAO.add(seriesAuthor);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Updates an existing series in the database.
     *
     * @param series the Series object with updated data
     * @return true if the update was successful, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public boolean update(Series series) throws SQLException {
        String sql = "UPDATE series SET title = ?, description = ?, cover_image_url = ?, status = ?, updated_at = ?, rating_points = ? WHERE series_id = ? AND is_deleted = false";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, series.getTitle());
            ps.setString(2, series.getDescription());
            ps.setString(3, series.getCoverImgUrl());
            ps.setString(4, series.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, series.getSeriesId());
            ps.setInt(7, series.getRating_points());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Soft deletes a series by setting its is_deleted flag to true.
     *
     * @param seriesId the ID of the series to delete
     * @return true if the deletion was successful, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public boolean delete(int seriesId) throws SQLException {
        String sql = "UPDATE series SET is_deleted = true, updated_at = ? WHERE series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, seriesId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves the top-rated series from the database.
     *
     * @param limit the maximum number of series to retrieve
     * @return a list of top-rated Series objects
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getTopRatedSeries(int limit) throws SQLException {
        List<Series> topSerieslist = new ArrayList<>();
        String sql = "SELECT TOP (" + limit + ") s.series_id, title, rating_points, description, cover_image_url, status, updated_at" + " FROM series s ORDER BY rating_points DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Series s = new Series();
                s.setSeriesId(rs.getInt("series_id"));
                s.setTitle(rs.getNString("title"));
                s.setDescription(rs.getNString("description"));
                s.setCoverImgUrl(rs.getNString("cover_image_url"));
                s.setStatus(rs.getString("status"));
                s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                topSerieslist.add(s);
            }
            return topSerieslist;
        }
    }

    /**
     * Retrieves the total count of series in the database.
     *
     * @return the total number of series
     * @throws SQLException if a database access error occurs
     */
    public int getTotalSeriesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM series";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Counts the number of series associated with a specific category.
     *
     * @param categoryId the ID of the category
     * @return the count of series in the specified category
     * @throws SQLException if a database access error occurs
     */
    public int countSeriesByCategory(int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM series s " + "JOIN series_categories sc ON s.series_id = sc.series_id " + "WHERE sc.category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Retrieves a list of series associated with a specific author.
     *
     * @param authorId the ID of the author
     * @return a list of Series objects associated with the author
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getSeriesByAuthorId(int authorId) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series s " + "JOIN dbo.series_author sa ON s.series_id = sa.series_id " + "WHERE sa.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    /**
     * Retrieves a list of series associated with a specific category.
     *
     * @param categoryId the ID of the category
     * @return a list of Series objects associated with the category
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getSeriesByCategoryId(int categoryId) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series s JOIN series_categories sc ON s.series_id = sc.series_id WHERE sc.category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    /**
     * Retrieves a list of series based on their status.
     *
     * @param limit  the maximum number of series to retrieve
     * @param status the status of the series to filter by
     * @return a list of Series objects with the specified status
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getSeriesByStatus(int limit, String status) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit + ") * FROM series WHERE status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    /**
     * Searches for series by name using a LIKE query.
     *
     * @param name the name or partial name of the series to search for
     * @return a list of Series objects that match the search criteria
     * @throws SQLException if a database access error occurs
     */
    public List<Series> findByName(String name) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series WHERE title LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    /**
     * Retrieves a list of recently updated series.
     *
     * @param limit the maximum number of series to retrieve
     * @return a list of recently updated Series objects
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getRecentlyUpdated(int limit) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit + ") * FROM series ORDER BY updated_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    /**
     * Retrieves a list of newly released series.
     *
     * @param limit the maximum number of series to retrieve
     * @return a list of newly released Series objects
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getNewReleasedSeries(int limit) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit + ") * FROM series ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    /**
     * Retrieves a list of series that have received the most ratings in the current week.
     *
     * @param limit the maximum number of series to retrieve
     * @return a list of Series objects with the highest ratings this week
     * @throws SQLException if a database access error occurs
     */
    public List<Series> getWeeklySeries(int limit) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit + ")s.series_id, title, rating_points, description, cover_image_url, status, updated_at,  SUM(r.score) AS total_rating " + "FROM series s JOIN ratings r ON s.series_id = r.series_id " + "WHERE DATEPART(WEEK, r.rated_at) = DATEPART(WEEK, GETDATE()) AND DATEPART(YEAR, r.rated_at) = DATEPART(YEAR, GETDATE()) " + "GROUP BY s.series_id, title, rating_points, description, cover_image_url, status, updated_at ORDER BY total_rating DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Series s = new Series();
                s.setSeriesId(rs.getInt("series_id"));
                s.setTitle(rs.getNString("title"));
                s.setDescription(rs.getNString("description"));
                s.setCoverImgUrl(rs.getNString("cover_image_url"));
                s.setStatus(rs.getString("status"));
                s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                s.setRating_points(rs.getInt("total_rating"));
                seriesList.add(s);
            }
            return seriesList;
        }
    }


    public List<Series> getSeriesByUserId(int userId) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series s JOIN saved_series ss ON s.series_id = ss.series_id WHERE ss.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }
    public int countAllNonDeleted() {
        String sql = "SELECT COUNT(*) AS total FROM series WHERE is_deleted = 0";

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
    /**
     * Utility method to extract a Series object from a ResultSet.
     *
     * @param rs the ResultSet to extract data from
     * @return the extracted Series object
     * @throws SQLException if a database access error occurs
     */
    private Series extractSeriesFromResultSet(ResultSet rs) throws SQLException {
        Series s = new Series();
        s.setSeriesId(rs.getInt("series_id"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setCoverImgUrl(rs.getString("cover_image_url"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        s.setDeleted(rs.getBoolean("is_deleted"));
        s.setRating_points(rs.getInt("rating_points"));
        return s;
    }


}
