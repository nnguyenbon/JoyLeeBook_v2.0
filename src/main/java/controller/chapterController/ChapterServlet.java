package controller.chapterController;

import db.DBConnection;
import dto.chapter.ChapterDetailDTO;
import dto.chapter.ChapterItemDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Chapter;
import model.Series;
import model.User;
import services.chapter.ChapterManagementService;
import services.chapter.ChapterServices;
import services.chapter.MyChapterService;
import services.general.CommentServices;
import services.chapter.LikeServices;
import utils.ValidationInput;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/chapter")
public class ChapterServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ChapterServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "add" -> addChapter(request, response);
            case "edit" -> updateChapter(request, response);
            case "delete" -> deleteChapter(request, response);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "add" -> showAddChapter(request, response);
            case "edit" -> showUpdateChapter(request, response);
            case "detail" -> viewChapterContent(request, response);
            case "navigate" -> navigateChapter(request, response);
            default -> viewChapterList(request, response);
        }
    }

    private void addChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = ValidationInput.isPositiveInteger(request.getSession().getAttribute("userId").toString()) ? Integer.parseInt(request.getSession().getAttribute("userId").toString()) : -1;
        // testing
        // userId = 3;

        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            try (Connection conn = DBConnection.getConnection()) {
                ChapterManagementService service = new ChapterManagementService(conn);
                Chapter newChapter = service.createChapter(userId, seriesId, title, content);

                if (newChapter != null) {
                    // forward to manage series page with success message
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

    private void showAddChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        // testing
        // userId = 3;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            try (Connection conn = DBConnection.getConnection()) {
                ChapterManagementService service = new ChapterManagementService(conn);

                // check if user is author of the series
                if (new dao.SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                    request.setAttribute("error", "You do not have permission to access this page.");
                    request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                    return;
                }

                // get series details
                Series series = service.getSeriesById(seriesId);
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

    private void updateChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        // testing
        // userId = 3;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);

            int chapterId = Integer.parseInt(request.getParameter("id"));
            String title = trim(request.getParameter("title"));
            String content = request.getParameter("content");
            String numberStr = request.getParameter("chapterNumber");
            Integer chapterNumber = (numberStr == null || numberStr.isBlank()) ? null : Integer.parseInt(numberStr);
            String status = trim(request.getParameter("status"));

            Chapter updated = svc.updateChapter(userId, chapterId, title, content, chapterNumber, status);

            // not done yet
            // redirect to view page
            response.sendRedirect(request.getContextPath() + "/chapter?id=" + updated.getChapterId() + "&updated=true");

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
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        // testing
        // userId = 3;

        if (userId == null) {
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
            ChapterManagementService svc = new ChapterManagementService(conn);

            int chapterId = Integer.parseInt(idStr);
            Chapter chapter = svc.getChapterById(chapterId);
            if (chapter == null) {
                request.setAttribute("error", "Chapter not found.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            // check permission
            int seriesId = chapter.getSeriesId();
            if (new dao.SeriesAuthorDAO(conn).isUserAuthorOfSeries(userId, seriesId)) {
                request.setAttribute("error", "You do not have permission to edit this chapter.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            Series series = svc.getSeriesById(seriesId);

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
        Integer chapterId = parseIntOrNull(request.getParameter("id"));
        if (chapterId == null) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        Integer userId = getUserId(request.getSession());

        // testing
        // userId = 3;

        String role = getUserRole(request.getSession());

        // testing
        // role = "author";

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);
            svc.deleteChapter(userId, role, chapterId);

            // redirect to author's chapter list with success message
            response.sendRedirect(request.getContextPath() + "/my-chapters?deleted=1");
        } catch (IllegalAccessException ex) {
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to delete chapter.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void showDeleteChapter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer chapterId = parseIntOrNull(request.getParameter("id"));
        if (chapterId == null) {
            request.setAttribute("error", "Missing chapter id.");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            return;
        }

        Integer userId = getUserId(request.getSession());

        // testing
        // userId = 3;

        String role = getUserRole(request.getSession());

        // testing
        // role = "author";

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            ChapterManagementService svc = new ChapterManagementService(conn);

            var chapter = svc.getChapterById(chapterId);
            if (chapter == null) {
                request.setAttribute("error", "Chapter not found or already deleted.");
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
                return;
            }

            // check permission
            if (!svc.canDeleteChapter(userId, role, chapter.getSeriesId())) {
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
        User loginedUser = (User) request.getSession().getAttribute("loginedUser");
        String role = (loginedUser != null) ? loginedUser.getRole() : "reader";
        if (role.equals("admin") ||  role.equals("staff")) {
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
            try {
                int seriesId = ValidationInput.isPositiveInteger(request.getParameter("seriesId")) ? Integer.parseInt(request.getParameter("seriesId")) : 0;
                User user = (User) request.getSession().getAttribute("loginedUser");
                int userId = user != null ? user.getUserId() : 0;
                ChapterServices chapterServices = new ChapterServices();
                CommentServices commentServices = new CommentServices();
                LikeServices likeService = new LikeServices();
                String chapterIdParam = request.getParameter("chapterId");
                int chapterId = ValidationInput.isPositiveInteger(chapterIdParam) ? Integer.parseInt(chapterIdParam) : chapterServices.getFirstChapterNumber(seriesId);
                chapterServices.updateReadingHistory(userId, chapterId);
                List<ChapterDetailDTO> chapterDetailDTOList = chapterServices.chaptersFromSeries(seriesId);
                request.setAttribute("firstChapterId", chapterDetailDTOList.get(0).getChapterId());
                request.setAttribute("lastChapterId", chapterDetailDTOList.get(chapterDetailDTOList.size()-1).getChapterId());
                request.setAttribute("userId", userId);
                request.setAttribute("chapterDetailDTO", chapterServices.buildChapterDetailDTO(chapterId));
                request.setAttribute("chapterInfoDTOList",chapterDetailDTOList );
                request.setAttribute("commentDetailDTOList", commentServices.commentsFromChapter(chapterId));
                request.setAttribute("liked", likeService.hasUserLiked(userId, chapterId));
                request.setAttribute("pageTitle","Chapter Content");
                request.setAttribute("contentPage", "/WEB-INF/views/chapter/ChapterContent.jsp");
                request.setAttribute("seriesId", seriesId);
                request.setAttribute("chapterId", chapterId);
                request.getRequestDispatcher("WEB-INF/views/chapter/ChapterContent.jsp").forward(request, response);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void viewChapterList (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginedUser = (User) request.getSession().getAttribute("loginedUser");
        String role = (loginedUser != null) ? loginedUser.getRole() : "reader";
        if (role.equals("admin") ||  role.equals("staff")) {

        } else if (role.equals("author")) {

            // get userId from session
            Integer userId = (Integer) request.getSession().getAttribute("userId");

            // testing
            // userId = 3;

            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String mode = "author";

            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            String keyword = trimToNull(request.getParameter("q"));
            String status = trimToNull(request.getParameter("status")); // only for author mode

            try (Connection conn = DBConnection.getConnection()) {
               MyChapterService service = new MyChapterService(conn);

                MyChapterService.PagedResult<ChapterItemDTO> result;
                result = service.getAuthoredChapters(userId, page, size, status, keyword);

                request.setAttribute("mode", mode);
                request.setAttribute("result", result);
                request.setAttribute("items", result.getItems());
                request.setAttribute("page", result.getPage());
                request.setAttribute("totalPages", result.getTotalPages());
                request.setAttribute("total", result.getTotal());
                request.setAttribute("q", keyword);
                request.setAttribute("status", status);

                // forward to JSP
                request.getRequestDispatcher("/WEB-INF/views/chapter/my-chapters.jsp").forward(request, response);

            } catch (SQLException e) {
                log.log(Level.SEVERE, "Error loading My Chapter List", e);
                request.setAttribute("error", "Unable to load your chapters.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {

        }
    }

    private void navigateChapter (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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