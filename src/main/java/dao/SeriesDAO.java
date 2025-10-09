package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.ws.rs.core.Link;
import model.Series;
import model.SeriesAuthor;
import org.eclipse.tags.shaded.org.apache.regexp.RE;

public class SeriesDAO {
    private final Connection conn;

    public SeriesDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Series> getAll() throws SQLException {
        List<Series> list = new ArrayList<>();
        String sql = "SELECT * FROM series WHERE is_deleted = false";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractSeriesFromResultSet(rs));
            }
        }
        return list;
    }

    public Series findById(int seriesId) throws SQLException {
        String sql = "SELECT * FROM series WHERE series_id = ? AND is_deleted = false";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractSeriesFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(Series series, int authorId) throws SQLException {
        String sql = "INSERT INTO series (title, description, cover_image_url, status, created_at, updated_at, is_deleted, points)VALUES (?, ?, ?, ?, ?, ?, ?, false, 0)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, series.getTitle());
            ps.setInt(2, authorId);
            ps.setString(3, series.getDescription());
            ps.setString(4, series.getCoverImgUrl());
            ps.setInt(5, series.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        series.setSeriesId(rs.getInt(1));
                    }
                    SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
                    SeriesAuthor seriesAuthor = new SeriesAuthor(series.getSeriesId(), authorId, LocalDateTime.now());
                    seriesAuthorDAO.add(seriesAuthor);
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Series series) throws SQLException {
        String sql = "UPDATE series SET title = ?, description = ?, cover_image_url = ?, status = ?, updated_at = ?, points = ? WHERE series_id = ? AND is_deleted = false";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, series.getTitle());
            ps.setString(2, series.getDescription());
            ps.setString(3, series.getCoverImgUrl());
            ps.setInt(4, series.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, series.getSeriesId());
            ps.setInt(7, series.getPoints());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int seriesId) throws SQLException {
        String sql = "UPDATE series SET is_deleted = true, updated_at = ? WHERE series_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, seriesId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Series> getTopRatedSeries(int limit) throws SQLException {
        List<Series> topSerieslist = new ArrayList<>();
        String sql = "SELECT TOP (" + limit +") s.series_id, title, points" +
                "FROM series s ORDER BY points DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Series series = new Series();
                series.setSeriesId(rs.getInt("series_id"));
                series.setTitle(rs.getString("title"));
                series.setPoints(rs.getInt("points"));
                topSerieslist.add(series);
            }
            return topSerieslist;
        }
    }

    public int getTotalSeriesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM series";
        try (PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countSeriesByCategory(int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM series s " +
                "JOIN series_categories sc ON s.series_id = sc.series_id " +
                "WHERE sc.category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Series> getSeriesByAuthorId(int authorId) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series s " +
                "JOIN dbo.series_author sa ON s.series_id = sa.series_id " +
                "WHERE sa.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, authorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    public List<Series> getSeriesByCategoryId(int categoryId) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series s JOIN series_categories sc ON s.series_id = sc.series_id WHERE sc.category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    public List<Series> getSeriesByStatus (int limit, String status) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit + ") FROM series WHERE status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }



    public List<Series> findByName(String name) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT * FROM series WHERE title LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    public List<Series> getRecentlyUpdated (int limit) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP ("+ limit +") * FROM series ORDER BY updated_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    public List<Series> getNewReleasedSeries(int limit) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit +") * FROM series ORDER BY created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    public List<Series> getWeeklySeries(int limit) throws SQLException {
        List<Series> seriesList = new ArrayList<>();
        String sql = "SELECT TOP (" + limit +")" +
                "    s.series_id," +
                "    s.title," +
                "    SUM(r.rating_value) AS total_rating" +
                "FROM series s" +
                "JOIN Rating r ON s.series_id = r.series_id" +
                "WHERE DATEPART(WEEK, r.rating_date) = DATEPART(WEEK, GETDATE())" +
                "  AND DATEPART(YEAR, r.rating_date) = DATEPART(YEAR, GETDATE())" +
                "GROUP BY s.series_id, s.title" +
                "ORDER BY total_rating DESC;";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seriesList.add(extractSeriesFromResultSet(rs));
            }
            return seriesList;
        }
    }

    private Series extractSeriesFromResultSet(ResultSet rs) throws SQLException {
        Series s = new Series();
        s.setSeriesId(rs.getInt("series_id"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setCoverImgUrl(rs.getString("cover_image_url"));
        s.setStatus(rs.getInt("status"));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        s.setDeleted(rs.getBoolean("is_deleted"));
        s.setPoints(rs.getInt("points"));
        return s;
    }


}
