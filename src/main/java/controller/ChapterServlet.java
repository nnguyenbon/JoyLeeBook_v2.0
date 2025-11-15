package controller;

import dao.*;
import db.DBConnection;
import model.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;
import utils.AuthenticationUtils;
import utils.LockManager;
import utils.PaginationUtils;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/chapter/*")
public class ChapterServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ChapterServlet.class.getName());
    private static final Set<String> ALLOWED_STATUS = new HashSet<>(Arrays.asList("draft", "published"));


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getPathInfo();
            if (action == null) action = "/";
            switch (action) {
                case "/add" -> showAddChapter(request, response);
                case "/edit" -> showEditChapter(request, response);
                case "/delete" -> deleteChapter(request, response);
                case "/detail" -> viewChapterContent(request, response);
                case "/navigate" -> navigateChapter(request, response);
                case "/list" -> viewChapterList(request, response);
                default -> throw new ServletException("Invalid path or function does not exist.");
            }
        } catch (ServletException e) {
            e.printStackTrace();
            throw e;
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getPathInfo();
            if (action == null) action = "/";
            switch (action) {
                case "/insert" -> insertChapter(request, response);
                case "/update" -> updateChapter(request, response);
                case "/approve" -> approveChapter(request, response);
                case "/delete" -> deleteChapter(request, response);
                case "/upload" -> uploadChapter(request, response);
                default -> throw new ServletException("Invalid path or function does not exist.");

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void viewChapterList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
            String approvalStatus = request.getParameter("filterByStatus");
            if (loggedInAccount instanceof Staff) {
                String search = request.getParameter("search");
                ChapterDAO chapterDAO = new ChapterDAO(conn);
                PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
                paginationRequest.setOrderBy("created_at");
                List<Chapter> chapterList = chapterDAO.getAll(search, approvalStatus, "published", paginationRequest);
                for (Chapter chapter : chapterList) {
                    buildChapter(chapter, conn);
                }
                int totalRecords = chapterDAO.getTotalChaptersCount(search, approvalStatus, "published");
                request.setAttribute("chapterList", chapterList);
                request.setAttribute("size", totalRecords);
                request.setAttribute("filterByStatus", approvalStatus);
                PaginationUtils.sendParameter(request, paginationRequest);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_chapterListForStaff.jsp");
                request.setAttribute("activePage", "chapters");
                request.setAttribute("pageTitle", "Manage Chapters");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
                int seriesId = Integer.parseInt(request.getParameter("seriesId"));

                List<Chapter> chapterList;
                if (loggedInAccount instanceof User && loggedInAccount.getRole().equals("author")) {
                    chapterList = buildChapterList(seriesId, "", conn);
                    SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
                    SeriesAuthor authorCurrent = seriesAuthorDAO.findById(seriesId, ((User) loggedInAccount).getUserId());
                    request.setAttribute("authorCurrent", authorCurrent);
                } else {
                    chapterList = buildChapterList(seriesId, "approved", conn);
                }
                request.setAttribute("chapterList", chapterList);
                request.setAttribute("seriesId", seriesId);
                request.getRequestDispatcher("/WEB-INF/views/chapter/_chapterList.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error loading Chapter List", e);
            request.setAttribute("error", "Unable to load your chapters.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildChapter(Chapter chapter, Connection conn) throws SQLException {
        SeriesDAO seriesDAO = new SeriesDAO(conn);
        UserDAO userDAO = new UserDAO(conn);
        LikeDAO likeDAO = new LikeDAO(conn);
        chapter.setSeriesTitle(seriesDAO.findById(chapter.getSeriesId()).getTitle());
        chapter.setAuthorName(userDAO.findById(chapter.getAuthorId()).getUsername());
        chapter.setTotalLike(likeDAO.countByChapter(chapter.getChapterId()));

    }

    private List<Chapter> buildChapterList(int seriesId, String approvalStatus, Connection connection) throws SQLException {
        ChapterDAO chapterDAO = new ChapterDAO(connection);
        List<Chapter> chapterList = chapterDAO.findChapterBySeriesId(seriesId, approvalStatus);
        for (Chapter chapter : chapterList) {
            buildChapter(chapter, connection);
        }
        return chapterList;
    }

    /**
     * Creates a notification object for series approval/rejection.
     *
     * @param conn          the database connection to use for queries
     * @param comment       the staff's feedback comment
     * @param approveStatus the approval decision ("approved" or "rejected")
     * @return a Notification object ready to be inserted into the database
     * @throws SQLException if a database access error occurs while fetching owner ID
     */
    private static Notification createApprovalNotification(Connection conn, Chapter chapter,
                                                           String comment, String approveStatus) throws SQLException {
        UserDAO userDAO = new UserDAO(conn);
        Notification notification = new Notification();
        notification.setUserId(userDAO.findById(chapter.getAuthorId()).getUserId());
        notification.setTitle("Chapter " + chapter.getTitle() + " "+ approveStatus);
        notification.setType("submission_status");
        notification.setMessage(comment);
        notification.setUrlRedirect("/series/detail?seriesId=" + chapter.getSeriesId() + "&chapterId=" + chapter.getChapterId());
        return notification;
    }

    private void viewChapterContent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        String role = "guest";
        int userId = 0;
        if (loggedInAccount instanceof User user) {
            role = user.getRole();
            userId = user.getUserId();
        } else if (loggedInAccount instanceof Staff staff) {
            role = staff.getRole();
            userId =  staff.getStaffId();
        }
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : -1;
        try (Connection conn = DBConnection.getConnection()) {
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            LikeDAO likeDAO = new LikeDAO(conn);
            int chapterId = ValidationInput.isPositiveInteger(request.getParameter("chapterId")) ? Integer.parseInt(request.getParameter("chapterId")) : chapterDAO.getFirstChapterNumber(seriesId);
            if (role.equals("admin") || role.equals("staff")) {
                boolean lock = LockManager.acquire(chapterId, userId, true);
                Chapter chapter = chapterDAO.findById(chapterId);
                buildChapter(chapter, conn);
                request.setAttribute("chapter", chapter);
                request.setAttribute("userId", userId);
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_chapterContentForStaff.jsp");
                request.setAttribute("activePage", "chapters");
                request.setAttribute("pageTitle", "Manage Chapters");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
                String approvalStatus = ("reader".equals(role) || "guest".equals(role)) ? "approved" : null;
                if (chapterId == 0) {
                    String referer = request.getHeader("referer");
                    if (referer != null && !referer.isEmpty()) {
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/series/detail?seriesId=" + seriesId);
                    }
                    return;
                }
                Chapter chapter = chapterDAO.findById(chapterId, approvalStatus);
                buildChapter(chapter, conn);
                boolean liked = likeDAO.isLikedByUser(userId, chapter.getChapterId());
                List<Chapter> chapterList = chapterDAO.findChapterBySeriesId(seriesId, approvalStatus);

                request.setAttribute("chapter", chapter);

                request.setAttribute("chapterId", chapterId);
                ReadingHistoryDAO rhDAO = new ReadingHistoryDAO(conn);
                rhDAO.updateReadingHistory(userId, chapterId);
                request.setAttribute("chapter", chapter);
                request.setAttribute("liked", liked);
                request.setAttribute("firstChapterId", chapterList.get(0).getChapterId());
                request.setAttribute("lastChapterId", chapterList.get(chapterList.size() - 1).getChapterId());
                request.setAttribute("chapter", chapter);
                request.setAttribute("userId", userId);
                request.setAttribute("chapterList", chapterList);
                request.setAttribute("pageTitle", "Chapter Content");
                request.setAttribute("contentPage", "/WEB-INF/views/chapter/_chapterContent.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void showEditChapter(HttpServletRequest request, HttpServletResponse response) {
        String chapterId = request.getParameter("chapterId");
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = 0;
        if (loggedInAccount instanceof User user) {
            userId = user.getUserId();
        }
        try (Connection conn = DBConnection.getConnection()) {
            boolean accquire = LockManager.acquire(Integer.parseInt(chapterId),userId, false);
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            Chapter chapter = chapterDAO.findById(Integer.parseInt(chapterId));
            if (accquire) {
                request.setAttribute("action", "update");
                request.setAttribute("chapter", chapter);
                showAddChapter(request, response);
            } else {
                request.getSession().setAttribute("message", "Chapter is locked now.");
                response.sendRedirect(request.getContextPath() + "/series/detail?seriesId=" + chapter.getSeriesId());
            }
        } catch (ClassNotFoundException | SQLException | ServletException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void approveChapter(HttpServletRequest request, HttpServletResponse response) {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int staffId = ((Staff) loggedInAccount).getStaffId();

        try (Connection conn = DBConnection.getConnection()) {
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            String approveStatus = request.getParameter("approveStatus");
            String comment = request.getParameter("reason") == null ? "" : request.getParameter("reason");
            if (comment.isEmpty()) {
                if (approveStatus.equals("approved")) {
                    comment = "This chapter is in line with our policy.";
                } else {
                    comment = "This chapter isn't in line with our policy.";
                }
            }
            // Initialize DAOs
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            ReviewChapterDAO reviewChapterDAO = new ReviewChapterDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            // Verify series exists
            Chapter chapter = chapterDAO.findById(chapterId, "");
            if (chapter == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Chapter not found.");
                return;
            }

            // Create review record
            ReviewChapter reviewChapter = reviewChapterDAO.findById(chapterId);
            if (reviewChapter == null) {
                reviewChapter = new ReviewChapter();
                reviewChapter.setChapterId(chapterId);
                reviewChapter.setStaffId(staffId);
                reviewChapter.setStatus(approveStatus);
                reviewChapter.setComment(comment);

                if (reviewChapterDAO.insert(reviewChapter)) {
                    // Create and send notification to series owner
                    Notification notification = createApprovalNotification(
                            conn, chapter, comment, approveStatus
                    );
                    notificationsDAO.insertNotification(notification);
                }
            } else {
                reviewChapter.setStatus(approveStatus);
                if (reviewChapterDAO.update(reviewChapter)) {
                    // Create and send notification to series owner
                    Notification notification = createApprovalNotification(
                            conn, chapter, comment, approveStatus
                    );
                    notificationsDAO.insertNotification(notification);
                }
            }

            LockManager.release(chapterId, staffId);
            request.getSession().setAttribute("message", "Chapter handled successfully.");
            response.sendRedirect(request.getContextPath() + "/chapter/list?filterByStatus=pending");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error approving series", e);
        } catch (IOException e) {
            throw new RuntimeException("Error redirecting after approval", e);
        }
    }


    private void insertChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : 0;

        if (userId == 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String numberStr = request.getParameter("chapterNumber");
            String status = request.getParameter("status") == null ? "draft" : request.getParameter("status");
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
                Series series = new SeriesDAO(conn).findById(seriesId);
                if (series == null) {
                    throw new IllegalArgumentException("Series not found.");
                }

                SeriesAuthorDAO saDAO = new SeriesAuthorDAO(conn);
                if (saDAO.isUserAuthorOfSeries(userId, seriesId)) {
                    throw new IllegalAccessException("You do not have permission to add chapter to this series.");
                }
                ChapterDAO chapterDAO = new ChapterDAO(conn);

                int nextNo;
                try {
                    int lastNo = chapterDAO.getLatestChapterNumber(seriesId);
                    if (numberStr == null || numberStr.isBlank() || chapterDAO.checkExistChapterNumber(Integer.parseInt(numberStr), seriesId)) {
                        nextNo = Math.max(1, lastNo + 1);
                    } else {
                        nextNo = Integer.parseInt(numberStr);
                    }
                } catch (SQLException e) {
                    throw new IllegalAccessException("error getting latest chapter number. " + e.getMessage());
                }

                Chapter ch = new Chapter();
                ch.setSeriesId(seriesId);
                ch.setChapterNumber(nextNo);
                ch.setTitle(title);
                ch.setContent(content);
                ch.setStatus(status);
                ch.setApprovalStatus("pending");
                boolean ok = chapterDAO.insert(ch, seriesId, userId);
                if (!ok) {
                    throw new RuntimeException("Database insert failed.");
                }

                request.getSession().setAttribute("message", "Chapter successfully added.");
                response.sendRedirect(request.getContextPath() + "/series/detail?seriesId=" + seriesId);

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("error", "An error occurred: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error/_addChapter.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Series ID.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }


    // Cần xem xét lại hàm này
    // chapter/add?seriesId=
    private void showAddChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int userId = ((User) loggedInAccount).getUserId();

        try {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            try (Connection conn = DBConnection.getConnection()) {
                SeriesDAO seriesDAO = new SeriesDAO(conn);

                if (new SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                    request.setAttribute("error", "You do not have permission to access this page.");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                Series series = seriesDAO.findById(seriesId);
                request.setAttribute("series", series);
                SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
                SeriesAuthor owner = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
                if (owner.getAuthorId() == userId) {
                    request.setAttribute("owner", "true");
                }

                if (request.getAttribute("action") == null) {
                    request.setAttribute("action", "insert");
                }

                request.setAttribute("contentPage", "/WEB-INF/views/chapter/_addChapter.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);

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
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
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

            if (status != null) {
                chapter.setStatus(status);
            }
            chapter.setApprovalStatus("pending");


            // Update DB
            boolean ok = chapterDAO.update(chapter);
            if (!ok) {
                throw new RuntimeException("Database update failed.");
            } else {
                ReviewChapterDAO reviewChapterDAO = new ReviewChapterDAO(conn);
                ReviewChapter reviewChapter = reviewChapterDAO.findById(chapter.getChapterId());
                if (reviewChapter.getStatus().equals("approved")) {
                    reviewChapter.setStatus("pending");
                    reviewChapterDAO.update(reviewChapter);
                }
            }
            request.getSession().setAttribute("message", "Chapter successfully updated.");
            response.sendRedirect("/series/detail?seriesId=" + seriesId);

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

            // check permission
            int seriesId = chapter.getSeriesId();
            if (new SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
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
        int seriesId = Integer.parseInt(request.getParameter("seriesId"));
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
            boolean accquire = LockManager.acquire(chapterId, userId, false);
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            Chapter chapter = chapterDAO.findByIdIfNotDeleted(chapterId);
            if (accquire) {
                if (chapter == null) {
                    throw new IllegalArgumentException("Chapter not found or already deleted.");
                }
                boolean ok = chapterDAO.delete(chapterId);
                if (!ok) {
                    throw new RuntimeException("Database delete failed.");
                }

                request.getSession().setAttribute("message", "Chapter successfully deleted.");
                response.sendRedirect(request.getContextPath() + "/series/detail?seriesId=" + seriesId);

            } else {
                request.getSession().setAttribute("message", "Chapter is locked now.");
                response.sendRedirect(request.getContextPath() + "/series/detail?seriesId=" + chapter.getSeriesId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to delete chapter.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void uploadChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = Integer.parseInt(request.getParameter("seriesId"));
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
            String jsonResponse = "";
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (chapter.getApprovalStatus().equals("approved")) {
                jsonResponse = String.format(
                        "{\"success\": false, \"message\": \"This chapter already been approved.\"}"
                );
                response.getWriter().write(jsonResponse);
                return;
            } else if (chapter.getApprovalStatus().equals("pending")) {
                if (chapter.getStatus().equals("draft")) {
                    chapterDAO.updateStatus(chapterId, "published");
                    jsonResponse = String.format(
                            "{\"success\": true, \"message\": \"Uploaded chapter has been successfully!\"}"
                    );
                } else {
                    jsonResponse = String.format(
                            "{\"success\": false, \"message\": \"Chapter has already been uploaded and is pending review!\"}"
                    );
                }
                response.getWriter().write(jsonResponse);
                return;
            } else {
                chapterDAO.updateApprovalStatus(chapterId, "pending");
                jsonResponse = String.format(
                        "{\"success\": true, \"message\": \"Uploaded chapter has been successfully!\"}"
                );
                response.getWriter().write(jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to upload chapter.");
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


    private void navigateChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 1;
        int chapterNumber = ValidationInput.isPositiveInteger(request.getParameter("chapterNumber")) ? Integer.parseInt(request.getParameter("chapterNumber")) : 1;
        String type = request.getParameter("type");

        try (Connection conn = DBConnection.getConnection()) {
            Chapter chapter = new Chapter();
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            if (type.equals("next")) {
                chapter = chapterDAO.getNextChapter(seriesId, chapterNumber);
            } else if (type.equals("previous")) {
                chapter = chapterDAO.getPreviousChapter(seriesId, chapterNumber);
            }
            response.sendRedirect(request.getContextPath() + "/chapter/detail?seriesId=" + seriesId + "&chapterId=" + chapter.getChapterId());
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