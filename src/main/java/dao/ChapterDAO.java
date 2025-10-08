package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Chapter;

public class ChapterDAO {
    private final Connection conn;

    public ChapterDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Chapter> getAll() throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapters WHERE is_deleted = false";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                chapters.add(extractChapterFromResultSet(rs));
            }
        }
        return chapters;
    }

    public Chapter findById(int chapterId) throws SQLException {
        String sql = "SELECT * FROM chapters WHERE chapter_id = ? AND is_deleted = false";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractChapterFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(Chapter chapter, int seriesId, int authorId) throws SQLException {
        String sql = "INSERT INTO chapters (series_id, author_id, chapter_number, title, content, status, is_deleted, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, false, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, seriesId);
            ps.setInt(2, authorId);
            ps.setInt(3, chapter.getChapterNumber());
            ps.setString(4, chapter.getTitle());
            ps.setString(5, chapter.getContent());
            ps.setString(6, chapter.getStatus());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        chapter.setChapterId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Chapter chapter) throws SQLException {
        String sql = "UPDATE chapters SET chapter_number = ?, title = ?, content = ?, status = ?, updated_at = ? WHERE chapter_id = ? AND is_deleted = false";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chapter.getChapterNumber());
            ps.setString(2, chapter.getTitle());
            ps.setString(3, chapter.getContent());
            ps.setString(4, chapter.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, chapter.getChapterId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int chapterId) throws SQLException {
        String sql = "UPDATE chapters SET is_deleted = true, updated_at = ? WHERE chapter_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, chapterId);
            return ps.executeUpdate() > 0;
        }
    }

    private Chapter extractChapterFromResultSet(ResultSet rs) throws SQLException {
        Chapter chapter = new Chapter();
        chapter.setChapterId(rs.getInt("chapter_id"));
        chapter.setChapterNumber(rs.getInt("chapter_number"));
        chapter.setTitle(rs.getString("title"));
        chapter.setContent(rs.getString("content"));
        chapter.setStatus(rs.getString("status"));
        chapter.setDeleted(rs.getBoolean("is_deleted"));
        chapter.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        chapter.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return chapter;
    }
}
