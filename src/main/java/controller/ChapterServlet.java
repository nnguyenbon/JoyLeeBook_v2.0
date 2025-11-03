package controller;

import dao.*;
import db.DBConnection;
import dto.chapter.ChapterDetailDTO;
import dto.PaginationRequest;
import dto.chapter.ChapterItemDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Chapter;
import model.Series;
import model.User;
import services.chapter.ChapterServices;
import services.chapter.MyChapterService;
import utils.AuthenticationUtils;
import utils.PaginationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/chapter/*")
public class ChapterServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ChapterServlet.class.getName());
    private static final Set<String> ALLOWED_STATUS = new HashSet<>(Arrays.asList("draft", "pending", "rejected", "approved"));


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        switch (action) {
            case "/add" -> addChapter(request, response);
            case "/edit" -> updateChapter(request, response);
            case "/delete" -> deleteChapter(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        switch (action) {
            case "/add" -> showAddChapter(request, response);
            case "/edit" -> showUpdateChapter(request, response);
            case "/detail" -> viewChapterContent(request, response);
            case "/navigate" -> navigateChapter(request, response);
            default -> viewChapterList(request, response);
        }
    }

    // Cần xem xét lại hàm này
    private void addChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : -1;
        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            try (Connection conn = DBConnection.getConnection()) {
                // Validate
                if (title == null || title.trim().isEmpty()) {
                    throw new IllegalArgumentException("Title is required.");
                }
                title = title.trim();
                if (title.length() > 255) {
                    throw new IllegalArgumentException("Title is too long (max 255 chars).");
                }
                if (content == null || content.trim().isEmpty()) {
                    throw new IllegalArgumentException("Content is required.");
                }

                // check series exists
                Series series = new SeriesDAO(conn).findById(seriesId);
                if (series == null) {
                    throw new IllegalArgumentException("Series not found.");
                }

                SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);
                // returns TRUE if user is NOT author
                if (saDAO.isUserAuthorOfSeries(userId, seriesId)) {
                    throw new IllegalAccessException("You do not have permission to add chapter to this series.");
                }

                ChapterDAO chapterDAO = new ChapterDAO(conn);
                int lastNo = 0;
                try {
                    lastNo = chapterDAO.getLatestChapterNumber(seriesId);
                } catch (SQLException ignore) {
                }
                int nextNo = Math.max(1, lastNo + 1);

                Chapter ch = new Chapter();
                ch.setSeriesId(seriesId);
                ch.setChapterNumber(nextNo);
                ch.setTitle(title);
                ch.setContent(content);
                ch.setStatus("pending");

                boolean ok = chapterDAO.insert(ch, seriesId, userId);
                if (!ok) {
                    throw new RuntimeException("Database insert failed.");
                }

                Chapter saved = chapterDAO.findBySeriesAndNumberIfNotDeleted(seriesId, nextNo);
                if (saved == null) {
                    saved = ch;
                }
                if (ch != null) {
                    response.sendRedirect(request.getContextPath() + "/manage-series?id=" + seriesId + "&success=true");
                } else {
                    request.setAttribute("error", "Failed to create new chapter.");
                    request.getRequestDispatcher("/WEB-INF/views/chapter/add-chapter.jsp").forward(request, response);
                }

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("error", "An error occurred: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/add-chapter.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Series ID.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }


    // Cần xem xét lại hàm này
    private void showAddChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : -1;
        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            try (Connection conn = DBConnection.getConnection()) {
                SeriesDAO seriesDAO = new SeriesDAO(conn);

                if (new dao.SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                    request.setAttribute("error", "You do not have permission to access this page.");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                Series series = seriesDAO.findById(seriesId);
                request.setAttribute("series", series);
                request.getRequestDispatcher("/WEB-INF/views/chapter/add-chapter.jsp").forward(request, response);

            } catch (Exception e) {
                System.out.println("series " + e.getMessage());
                request.setAttribute("error", "Could not load series data. " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            System.out.println("number " + e.getMessage());
            request.setAttribute("error", "Invalid Series ID.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    // Cần sửa lại đặt tên, đường dẫn url
    private void updateChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : -1;
        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection()) {
            int chapterId = Integer.parseInt(request.getParameter("id"));
            String title = trim(request.getParameter("title"));
            String content = request.getParameter("content");
            String numberStr = request.getParameter("chapterNumber");
            Integer chapterNumber = (numberStr == null || numberStr.isBlank()) ? null : Integer.parseInt(numberStr);
            String status = trim(request.getParameter("status"));

            ChapterDAO chapterDAO = new ChapterDAO(conn);
            Chapter chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
            if (chapter == null) {
                throw new IllegalArgumentException("Chapter not found.");
            }

            int seriesId = chapter.getSeriesId();

            SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);
            if (saDAO.isUserAuthorOfSeries(userId, seriesId)) {
                throw new IllegalAccessException("You do not have permission to edit this chapter.");
            }

            // Validate
            if (title != null) {
                title = title.trim();
                if (title.isEmpty()) {
                    throw new IllegalArgumentException("Title cannot be empty.");
                }
                if (title.length() > 255) {
                    throw new IllegalArgumentException("Title is too long (max 255 chars).");
                }
                chapter.setTitle(title);
            }
            if (content != null) {
                if (content.trim().isEmpty()) {
                    throw new IllegalArgumentException("Content cannot be empty.");
                }
                chapter.setContent(content);
            }
            if (chapterNumber != null) {
                if (chapterNumber <= 0) {
                    throw new IllegalArgumentException("Chapter number must be > 0.");
                }
                chapter.setChapterNumber(chapterNumber);
            }

            // Status
            if (status != null) {
                if (!ALLOWED_STATUS.contains(status)) {
                    throw new IllegalArgumentException("Invalid status.");
                }
                if ("approved".equalsIgnoreCase(status)) {
                    chapter.setStatus("pending");
                } else {
                    chapter.setStatus(status);
                }
            } else {
                chapter.setStatus("pending");
            }

            // Update DB
            boolean ok = chapterDAO.update(chapter);
            if (!ok) {
                throw new RuntimeException("Database update failed.");
            }


            response.sendRedirect(request.getContextPath() + "/chapter?id=" + chapter.getChapterId() + "&updated=true");

        } catch (IllegalAccessException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid chapter id or number.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update chapter: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void showUpdateChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : -1;
        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            int chapterId = Integer.parseInt(idStr);
            Chapter chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
            if (chapter == null) {
                request.setAttribute("error", "Chapter not found.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            int seriesId = chapter.getSeriesId();
            if (new dao.SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                request.setAttribute("error", "You do not have permission to edit this chapter.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            Series series = seriesDAO.findById(seriesId);

            request.setAttribute("series", series);
            request.setAttribute("chapter", chapter);
            request.getRequestDispatcher("/WEB-INF/views/chapter/edit-chapter.jsp").forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Unable to load chapter for editing. " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    /**
     * Utility to trim a string and convert empty to null
     */
    private static String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void deleteChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int chapterId = ValidationInput.isPositiveInteger(request.getParameter("chapterId")) ? Integer.parseInt(request.getParameter("chapterId")) : -1;
        if (chapterId == -1) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : -1;
        String role = user != null ? user.getRole() : null;
        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            Chapter chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
            if (chapter == null) {
                throw new IllegalArgumentException("Chapter not found or already deleted.");
            }
            boolean ok = chapterDAO.delete(chapterId);
            if (!ok) {
                throw new RuntimeException("Database delete failed.");
            }

            // Xem lại đường dẫn
            response.sendRedirect(request.getContextPath() + "/my-chapters?deleted=1");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to delete chapter.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    // Sửa lại như hàm trên
    private void showDeleteChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer chapterId = parseIntOrNull(request.getParameter("id"));
        if (chapterId == null) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        Integer userId = getUserId(request.getSession());
        String role = getUserRole(request.getSession());

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            var chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
            if (chapter == null) {
                request.setAttribute("error", "Chapter not found or already deleted.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            // check permission
            if (seriesAuthorDAO.isUserAuthorOfSeries(userId, chapter.getSeriesId())) {
                request.setAttribute("error", "You do not have permission to delete this chapter.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            request.setAttribute("chapter", chapter);
            request.getRequestDispatcher("/WEB-INF/views/chapter/confirm-delete.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to load delete confirmation.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void viewChapterContent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account loginedUser = AuthenticationUtils.getLoginedUser(request.getSession());
        String role = (loginedUser != null) ? loginedUser.getRole() : "reader";
        if (role.equals("admin") || role.equals("staff")) {
            int chapterId = ValidationInput.isPositiveInteger(request.getParameter("chapterId")) ? Integer.parseInt(request.getParameter("chapterId")) : 1;
            try {
                ChapterServices chapterServices = new ChapterServices();
                request.setAttribute("chapter", chapterServices.buildChapterDetailDTO(chapterId));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            request.getRequestDispatcher("/WEB-INF/views/chapter/ChapterContentForStaff.jsp").forward(request, response);
        } else if (role.equals("author")) {

        } else {
            try(Connection conn = DBConnection.getConnection()) {
                int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 0;
                User user = (User) loginedUser;
                int userId = user != null ? user.getUserId() : 0;
                ChapterServices chapterServices = new ChapterServices();
                CommentDAO commnetDAO = new CommentDAO(conn);
                LikeDAO likeDAO = new LikeDAO(conn);
                String chapterIdParam = request.getParameter("chapterId");
                int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : chapterServices.getFirstChapterNumber(seriesId);
                chapterServices.updateReadingHistory(userId, chapterId);
                List<ChapterDetailDTO> chapterDetailDTOList = chapterServices.chaptersFromSeries(seriesId);
                request.setAttribute("firstChapterId", chapterDetailDTOList.get(0).getChapterId());
                request.setAttribute("lastChapterId", chapterDetailDTOList.get(chapterDetailDTOList.size() - 1).getChapterId());
                request.setAttribute("userId", userId);
                request.setAttribute("chapterDetailDTO", chapterServices.buildChapterDetailDTO(chapterId));
                request.setAttribute("chapterInfoDTOList", chapterDetailDTOList);
                request.setAttribute("commentDetailDTOList", commnetDAO.findByChapter(chapterId));
                request.setAttribute("liked", likeDAO.isLikedByUser(userId, chapterId));
                request.setAttribute("pageTitle", "Chapter Content");
                request.setAttribute("contentPage", "/WEB-INF/views/chapter/ChapterContent.jsp");
                request.setAttribute("seriesId", seriesId);
                request.setAttribute("chapterId", chapterId);
                request.getRequestDispatcher("/WEB-INF/views/components/_layoutUser.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void viewChapterList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account loginedUser = AuthenticationUtils.getLoginedUser(request.getSession());
        String role = (loginedUser != null) ? loginedUser.getRole() : "reader";
        if (role.equals("admin") || role.equals("staff")) {
            String search = request.getParameter("search");
            String status = request.getParameter("status");
            try {
                ChapterServices chapterServices = new ChapterServices();
                PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
                paginationRequest.setOrderBy("chapter_id");
                List<ChapterDetailDTO> chapterDetailDTOList = chapterServices.buildChapterList(search, status, paginationRequest);
                int totalRecords = chapterServices.getTotalChaptersCount(search, status);
                request.setAttribute("size", totalRecords);
                request.setAttribute("chapterDetailDTOList", chapterDetailDTOList);
                request.setAttribute("status", status);
                PaginationUtils.sendParameter(request, paginationRequest);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_chapterListFotStaff.jsp");
                request.setAttribute("activePage", "series");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Error loading Chapter List", e);
                request.setAttribute("error", "Unable to load your chapters.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (role.equals("author")) {
            User user = (User) loginedUser;
            int userId = user.getUserId();
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            String keyword = trimToNull(request.getParameter("q"));
            String status = trimToNull(request.getParameter("status"));

            try (Connection conn = DBConnection.getConnection()) {
                MyChapterService service = new MyChapterService(conn);


                MyChapterService.PagedResult<ChapterItemDTO> result;
                result = service.getAuthoredChapters(userId, page, size, status, keyword);

                request.setAttribute("mode", role);
                request.setAttribute("result", result);
                request.setAttribute("items", result.getItems());
                request.setAttribute("page", result.getPage());
                request.setAttribute("totalPages", result.getTotalPages());
                request.setAttribute("total", result.getTotal());
                request.setAttribute("q", keyword);
                request.getRequestDispatcher("/WEB-INF/views/chapter/my-chapters.jsp").forward(request, response);
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Error loading Chapter List", e);
                request.setAttribute("error", "Unable to load your chapters.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void navigateChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;
        int chapterNumber = ValidationInput.isPositiveInteger(request.getParameter("chapterNumber")) ? Integer.parseInt(request.getParameter("chapterNumber")) : 1;
        String action = request.getParameter("type");

        try {
            String redirectUrl = ChapterServices.getRedirectUrl(action, seriesId, chapterNumber);
            response.sendRedirect(redirectUrl);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Try to get userId from session.
     * Supports both "userId" attribute (Integer) and "authUser" attribute (User).
     * Returns null if not found or invalid.
     */
    private static Integer getUserId(HttpSession session) {
        Object u1 = session.getAttribute("userId");
        if (u1 instanceof Integer) return (Integer) u1;
        Object u2 = session.getAttribute("authUser");
        if (u2 instanceof User) return ((User) u2).getUserId();
        return null;
    }

    /**
     * Try to get user role from session.
     * Supports both "authUser" attribute (User) and "role" attribute (String).
     * Defaults to "reader" if not found or invalid.
     */
    private static String getUserRole(HttpSession session) {
        Object u2 = session.getAttribute("authUser");
        if (u2 instanceof User) {
            String role = ((User) u2).getRole();
            return role != null ? role : "reader";
        }
        Object r = session.getAttribute("role");
        return (r instanceof String) ? (String) r : "reader";
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

}