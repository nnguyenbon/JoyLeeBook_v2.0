package services;

import dao.ChapterDAO;
import dto.ChapterItemDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * MyChapterService class provides services related to chapters,
 * including fetching authored chapters and reading history with pagination.
 *
 * @author HaiDD-dev
 */
public class MyChapterService {

    public static class PagedResult<T> {
        private final List<T> items;
        private final int page;
        private final int pageSize;
        private final int total;
        private final int totalPages;

        public PagedResult(List<T> items, int page, int pageSize, int total) {
            this.items = items;
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.totalPages = (int) Math.ceil(total / (double) pageSize);
        }

        public List<T> getItems() {
            return items;
        }

        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotal() {
            return total;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    private final ChapterDAO dao;

    /**
     * Constructor to initialize MyChapterService with a database connection.
     *
     * @param conn the database connection
     */
    public MyChapterService(Connection conn) {
        this.dao = new ChapterDAO(conn);
    }

    /**
     * Fetches a paginated list of chapters authored by a specific user.
     *
     * @param userId       the ID of the user
     * @param page         the page number (1-based)
     * @param pageSize     the number of items per page
     * @param statusFilter optional status filter (e.g., "published", "draft")
     * @param keyword      optional keyword to search in chapter titles
     * @return a PagedResult containing the list of ChapterListItem and pagination info
     * @throws SQLException if a database access error occurs
     */
    public PagedResult<ChapterItemDTO> getAuthoredChapters(int userId, int page, int pageSize, String statusFilter, String keyword) throws SQLException {
        int offset = (Math.max(page, 1) - 1) * pageSize;
        var items = dao.getAuthoredChapters(userId, offset, pageSize, statusFilter, keyword);
        int total = dao.countAuthoredChapters(userId, statusFilter, keyword);
        return new PagedResult<>(items, page, pageSize, total);
    }

    /**
     * Fetches a paginated list of chapters from the user's reading history.
     *
     * @param userId   the ID of the user
     * @param page     the page number (1-based)
     * @param pageSize the number of items per page
     * @param keyword  optional keyword to search in chapter titles
     * @return a PagedResult containing the list of ChapterListItem and pagination info
     * @throws SQLException if a database access error occurs
     */
    public PagedResult<ChapterItemDTO> getReadingHistoryChapters(int userId, int page, int pageSize, String keyword) throws SQLException {
        int offset = (Math.max(page, 1) - 1) * pageSize;
        var items = dao.getReadingHistoryChapters(userId, offset, pageSize, keyword);
        int total = dao.countReadingHistoryChapters(userId, keyword);
        return new PagedResult<>(items, page, pageSize, total);
    }
}
