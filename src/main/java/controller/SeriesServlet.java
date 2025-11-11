package controller;

import dao.*;
import db.DBConnection;
import dto.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.*;
import utils.AuthenticationUtils;
import utils.PaginationUtils;
import utils.ValidationInput;
import utils.WebpConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


/**
 * Servlet implementation class SeriesServlet
 * Handles CRUD operations for series, including viewing, adding, editing,
 * approving, and deleting series.
 * Supports file uploads for series cover images.
 * Routes requests based on user roles (reader, author, staff, admin).
 * Implements pagination and filtering for series listings.
 * Utilizes DAOs for database interactions and utility classes for common tasks.
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // 10MB
@WebServlet(urlPatterns = {"/series/*", "/homepage"})
public class SeriesServlet extends HttpServlet {

    // =========================================================================
    // HTTP REQUEST HANDLERS
    // =========================================================================

    /**
     * Handles HTTP GET requests for viewing series data.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String servletPath = request.getServletPath();
            if (servletPath.equals("/homepage")) {
                viewHomepage(request, response);
            } else {
                String action = request.getPathInfo();
                if (action == null) action = "/";

                switch (action) {
                    case "/add" -> showAddSeriesForm(request, response);
                    case "/edit" -> showEditSeriesForm(request, response);
                    case "/detail" -> viewSeriesDetail(request, response);
                    case "/list" -> viewSeriesList(request, response);
                    default -> throw new ServletException("Invalid path or function does not exist.");
                }
            }
        } catch (ServletException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Handles HTTP POST requests for modifying series data.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getPathInfo();
            if (action == null) action = "";

            switch (action) {
                case "/insert" -> insertSeries(request, response);
                case "/update" -> updateSeries(request, response);
                case "/approve" -> approveSeries(request, response);
                case "/delete" -> deleteSeries(request, response);
                case "/upload" -> uploadSeries(request, response);
                default -> throw new ServletException("Invalid path or function does not exist.");
            }
        } catch (ServletException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void viewHomepage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            List<Series> listSeries = seriesDAO.getAll("approved");
            for (Series series : listSeries) {
                buildSeries(conn, series);
            }

            request.setAttribute("hotSeriesList", getTopRatedSeries(3, listSeries));
            request.setAttribute("weeklySeriesList", getWeeklySeries(8, listSeries));
            request.setAttribute("newReleaseSeriesList", getNewReleasedSeries(4, listSeries));
            request.setAttribute("recentlyUpdatedSeriesList", getRecentlyUpdated(6, listSeries));
            request.setAttribute("completedSeriesList", getSeriesByStatus(6, "completed", listSeries));
            request.setAttribute("categoryList", categoryDAO.getCategoryTop(6));
            request.setAttribute("categories", categoryDAO.getAll());
            request.setAttribute("pageTitle", "JoyLeeBook");
            request.setAttribute("contentPage", "/WEB-INF/views/general/Homepage.jsp");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    // =========================================================================
    // CREATE OPERATIONS
    // =========================================================================

    /**
     * Displays the form for adding a new series.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void showAddSeriesForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            List<Category> categories = categoryDAO.getAll();

            request.setAttribute("categories", categories);
            request.setAttribute("action", "insert");
            request.setAttribute("contentPage", "/WEB-INF/views/series/_showAddSeries.jsp");
            request.setAttribute("activePage", "Add Series");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading add series form", e);
        }
    }

    /**
     * Inserts a new series into the database.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response for redirection
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during file upload or redirection
     */
    private void insertSeries(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        int authorId = ((User) loggedInAccount).getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            // Extract request parameters
            String title = request.getParameter("title");
            String[] genreParams = request.getParameterValues("selectedGenres");
            int[] genreIds = genreParams != null
                    ? Arrays.stream(genreParams).mapToInt(Integer::parseInt).toArray()
                    : new int[0];
            String status = request.getParameter("status");
            String description = request.getParameter("description");

            // Process cover image upload
            Series series = new Series();


            Part filePart = request.getPart("coverImgUrl");
            if (filePart != null && !filePart.getSubmittedFileName().trim().isEmpty()) {
                System.out.println("Uploaded file name: " + filePart);
                System.out.println("Content type: " + filePart.getContentType());
                System.out.println("Size: " + filePart.getSize());

                series.setCoverImgUrl(WebpConverter.convertToWebp(filePart, getServletContext()));
            }

            series.setTitle(title);
            series.setStatus(status);
            series.setApprovalStatus("pending");
            series.setDescription(description);

            // Initialize DAOs
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            SeriesCategoriesDAO seriesCategoriesDAO = new SeriesCategoriesDAO(conn);

            // Insert series and related data
            if (seriesDAO.insertSeries(series)) {
                // Associate genres with series
                for (int genreId : genreIds) {
                    SeriesCategories seriesCategories = new SeriesCategories();
                    seriesCategories.setCategoryId(genreId);
                    seriesCategories.setSeriesId(series.getSeriesId());
                    seriesCategoriesDAO.insertSeriesCategory(seriesCategories);
                }

                // Set author ownership
                SeriesAuthor seriesAuthor = new SeriesAuthor();
                seriesAuthor.setSeriesId(series.getSeriesId());
                seriesAuthor.setAuthorId(authorId);
                seriesAuthor.setOwner(true);
                seriesAuthorDAO.insertSeriesAuthor(seriesAuthor);
            }

            response.sendRedirect(request.getContextPath() + "/author");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error inserting new series", e);
        }
    }

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================

    /**
     * Displays a paginated list of series with filtering and search capabilities.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void viewSeriesList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        String role = "reader";
        int userId = 0;

        if (loggedInAccount instanceof User user) {
            role = user.getRole();
            userId = ((User) loggedInAccount).getUserId();
        } else if (loggedInAccount instanceof Staff staff) {
            role = staff.getRole();
        }


        try (Connection conn = DBConnection.getConnection()) {
            String search = request.getParameter("search");
            String approvalStatus = request.getParameter("filterByStatus");
            String genreParams = request.getParameter("genre");
            List<Integer> genreIds = new ArrayList<>();
            if (genreParams != null) {
                for (String genreParam : genreParams.split(" ")) {
                    genreIds.add(Integer.parseInt(genreParam));
                }
            } else {
                genreIds = null;
            }
            // Readers can only see approved series
            if ("reader".equals(role)) {
                approvalStatus = "approved";
                userId = 0;
            }


            SeriesDAO seriesDAO = new SeriesDAO(conn);
            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("series_id");

            List<Series> seriesList = seriesDAO.getAll(search, genreIds, userId, approvalStatus, paginationRequest);
            for (Series series : seriesList) {
                buildSeries(conn, series);
            }
            int totalRecords = 0;
            if (seriesList.isEmpty()) {
                seriesList = new ArrayList<>();
            } else {
                totalRecords = seriesDAO.getTotalSeriesCount(search, genreIds, userId, approvalStatus);
            }

            request.setAttribute("seriesList", seriesList);
            request.setAttribute("size", totalRecords);
            request.setAttribute("search", search);
            request.setAttribute("filterByStatus", approvalStatus);
            PaginationUtils.sendParameter(request, paginationRequest);

            // Forward to role-specific view
            if ("admin".equals(role) || "staff".equals(role)) {
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_seriesListForStaff.jsp");
                request.setAttribute("pageTitle", "Manage Series");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else if ("author".equals(role)) {
                request.getRequestDispatcher("/WEB-INF/views/series/_seriesListOfAuthor.jsp").forward(request, response);
            } else {
                request.setAttribute("genresParam", genreIds);
                String ajaxHeader = request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(ajaxHeader)) {
                    request.getRequestDispatcher("/WEB-INF/views/series/_seriesList.jsp").forward(request, response);
                }
//                else {
//                    CategoryDAO categoryDAO = new CategoryDAO(conn);
//                    List<Series> listSeries = seriesDAO.getAll("approved");
//                    for (Series series : listSeries) {
//                        buildSeries(conn, series);
//                    }
//
//                    request.setAttribute("hotSeriesList", getTopRatedSeries(3, listSeries));
//                    request.setAttribute("weeklySeriesList", getWeeklySeries(8,  listSeries));
//                    request.setAttribute("newReleaseSeriesList", getNewReleasedSeries(4,  listSeries));
//                    request.setAttribute("recentlyUpdatedSeriesList", getRecentlyUpdated(6, listSeries));
//                    request.setAttribute("completedSeriesList", getSeriesByStatus(6, "completed", listSeries));
//                    request.setAttribute("categoryList", categoryDAO.getCategoryTop(6));
//
//                    request.setAttribute("categories", categoryDAO.getAll());
//                    request.setAttribute("pageTitle", "JoyLeeBook");
//                    request.setAttribute("contentPage", "/WEB-INF/views/general/Homepage.jsp");
//                    request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
//                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error displaying series list", e);
        }
    }

    /**
     * Displays detailed information for a specific series.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void viewSeriesDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account loggedInAccount = AuthenticationUtils.getLoginedUser(request.getSession());
        String role = "reader";
        int userId = -1;
        if (loggedInAccount instanceof User user) {
            userId = user.getUserId();
            role = user.getRole();
        } else if (loggedInAccount instanceof Staff staff) {
            userId = staff.getStaffId();
            role = staff.getRole();
        }

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String approvalStatus = "reader".equals(role) ? "approved" : null;
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            RatingDAO ratingDAO = new RatingDAO(conn);
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            SavedSeriesDAO savedSeriesDAO = new SavedSeriesDAO(conn);
            Series series = buildSeries(conn, seriesDAO.findById(seriesId, approvalStatus));

            if (series == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Series not found.");
                return;
            }

            int ratingByUser = ratingDAO.getRatingValueByUserId(userId, seriesId);
            boolean saved = savedSeriesDAO.isSaved(userId, seriesId);

            request.setAttribute("totalChapter", chapterDAO.getTotalChaptersCount(seriesId));
            request.setAttribute("ratingByUser", ratingByUser);
            request.setAttribute("userId", userId);
            request.setAttribute("saved", saved);
            request.setAttribute("series", series);
            request.setAttribute("pageTitle", "Series Detail");
            // Forward to role-specific layout
            if ("admin".equals(role) || "staff".equals(role)) {
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_seriesDetailForStaff.jsp");
                request.setAttribute("pageTitle", "Manage Series");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
                SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
                int ownerId = seriesAuthorDAO.findOwnerIdBySeriesId(seriesId);
                if (ownerId == userId) {
                    request.setAttribute("owner", "true");
                }
                request.setAttribute("totalChapter", chapterDAO.getTotalChaptersCount(seriesId));
                request.setAttribute("contentPage", "/WEB-INF/views/series/_seriesDetail.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error retrieving series details", e);
        }
    }

    // =========================================================================
    // UPDATE OPERATIONS
    // =========================================================================

    /**
     * Displays the form for editing an existing series.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void showEditSeriesForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            CategoryDAO categoryDAO = new CategoryDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);

            List<Category> categories = categoryDAO.getAll();
            Series series = seriesDAO.findById(seriesId, "");
            series.setCategoryList(categoryDAO.getCategoryBySeriesId(series.getSeriesId()));

            request.setAttribute("categories", categories);
            request.setAttribute("series", series);
            request.setAttribute("action", "update");

            //            request.setAttribute("contentPage", "WEB-INF/views/series/_showEditSeries.jsp");
            request.setAttribute("contentPage", "/WEB-INF/views/series/_showAddSeries.jsp");
            request.setAttribute("activePage", "Edit Series");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading edit series form", e);
        }
    }

    /**
     * Updates an existing series with new data from the form submission.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response for redirection or error messages
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during file upload or redirection
     */
    private void updateSeries(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int authorId = ((User) loggedInAccount).getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            // Fetch existing series
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            Series series = seriesDAO.findById(seriesId, "");

            if (series == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Series not found.");
                return;
            }

            // Verify ownership
            SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
            SeriesAuthor seriesAuthor = seriesAuthorDAO.findById(seriesId, authorId);

            if (seriesAuthor == null || !seriesAuthor.isOwner()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "You do not have permission to edit this series.");
                return;
            }

            // Extract updated data
            String title = request.getParameter("title");
            String status = request.getParameter("status");
            String description = request.getParameter("description");
            String[] genreParams = request.getParameterValues("selectedGenres");
            int[] genreIds = genreParams != null
                    ? Arrays.stream(genreParams).mapToInt(Integer::parseInt).toArray()
                    : new int[0];


            // Update cover image if provided
            String oldImg = request.getParameter("oldCoverImgUrl").replaceFirst("img/", "");
            Part filePart = request.getPart("coverImgUrl");
            String nameFilePart = filePart.getSubmittedFileName().trim();
            System.out.println("name:" + nameFilePart);
            System.out.println("old: " + oldImg);
            if (nameFilePart.isEmpty() || nameFilePart.equals(oldImg)) {
                System.out.println(oldImg);
                series.setCoverImgUrl(oldImg);
            } else {
                if (!filePart.getSubmittedFileName().trim().isEmpty()) {
                    System.out.println(filePart.getSubmittedFileName());
                    series.setCoverImgUrl(WebpConverter.convertToWebp(filePart, getServletContext()));
                }
            }


            // Update series fields
            series.setTitle(title);
            series.setStatus(status);
            if (series.getApprovalStatus().equals("approved")) {
                series.setApprovalStatus("pending"); // Requires re-approval
            }
            series.setDescription(description);

            // Update series in database
            seriesDAO.updateSeries(series);

            // Update categories - remove old and insert new
            SeriesCategoriesDAO seriesCategoriesDAO = new SeriesCategoriesDAO(conn);
            seriesCategoriesDAO.deleteBySeriesId(seriesId);

            for (int genreId : genreIds) {
                SeriesCategories seriesCategory = new SeriesCategories();
                seriesCategory.setSeriesId(seriesId);
                seriesCategory.setCategoryId(genreId);
                seriesCategoriesDAO.insertSeriesCategory(seriesCategory);
            }

            response.sendRedirect(request.getContextPath() + "/author");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error updating series", e);
        }
    }

    // =========================================================================
    // APPROVAL OPERATIONS (STAFF/ADMIN ONLY)
    // =========================================================================

    /**
     * Approves or rejects a series submission.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response for redirection
     * @throws RuntimeException if a database error occurs during the approval process
     */
    private void approveSeries(HttpServletRequest request, HttpServletResponse response) {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int staffId = ((Staff) loggedInAccount).getStaffId();

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String approveStatus = request.getParameter("approveStatus");
            String comment = request.getParameter("comment") == null ? "" : request.getParameter("comment");

            // Initialize DAOs
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            ReviewSeriesDAO reviewSeriesDAO = new ReviewSeriesDAO(conn);
            NotificationsDAO notificationsDAO = new NotificationsDAO(conn);

            // Verify series exists
            Series series = seriesDAO.findById(seriesId, "");
            if (series == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Series not found.");
                return;
            }


            // Create review record
            ReviewSeries reviewSeries = reviewSeriesDAO.findById(seriesId, staffId);
            if  (reviewSeries == null) {
                reviewSeries = new ReviewSeries();
                reviewSeries.setSeriesId(seriesId);
                reviewSeries.setStaffId(staffId);
                reviewSeries.setStatus(approveStatus);
                reviewSeries.setComment(comment);
                // Insert review (database trigger will update series.approval_status)
                if (reviewSeriesDAO.insert(reviewSeries)) {
                    // Create and send notification to series owner
                    Notification notification = createApprovalNotification(
                            conn, seriesId, comment, approveStatus
                    );
                    notificationsDAO.insertNotification(notification);
                }
            } else {
                reviewSeries.setStatus(approveStatus);
                // Insert review (database trigger will update series.approval_status)
                if (reviewSeriesDAO.update(reviewSeries)) {
                    // Create and send notification to series owner
                    Notification notification = createApprovalNotification(
                            conn, seriesId, comment, approveStatus
                    );
                    notificationsDAO.insertNotification(notification);
                }
            }




            response.sendRedirect(request.getContextPath() + "/series/list?filterByStatus=");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error approving series", e);
        } catch (IOException e) {
            throw new RuntimeException("Error redirecting after approval", e);
        }
    }

    // =========================================================================
    // DELETE OPERATIONS
    // =========================================================================

    /**
     * Deletes a series from the database.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response for redirection
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during redirection
     */
    private void deleteSeries(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int authorId = ((User) loggedInAccount).getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            SeriesDAO seriesDAO = new SeriesDAO(conn);
            seriesDAO.deleteSeries(seriesId);

            response.sendRedirect(request.getContextPath() + "/author");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error deleting series", e);
        }
    }

    //=======================================================================
    //UPLOAD METHOD

    private void uploadSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int seriesId = Integer.parseInt(request.getParameter("seriesId"));

        User user = (User) AuthenticationUtils.getLoginedUser(request.getSession());
        int userId = user != null ? user.getUserId() : -1;
        if (userId == -1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            Series series = seriesDAO.findById(seriesId);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (series.getApprovalStatus().equals("approved")) {
                String jsonResponse = String.format(
                        "{\"success\": false, \"message\": \"This series already been approved.\"}"
                );
                response.getWriter().write(jsonResponse);
                return;
            } else if (series.getApprovalStatus().equals("pending")) {
                String jsonResponse = String.format(
                        "{\"success\": false, \"message\": \"Series has already been uploaded and is pending review!\"}"
                );
                response.getWriter().write(jsonResponse);
                return;
            } else {
                seriesDAO.updateApprovalStatus(seriesId, "pending");
                String jsonResponse = String.format(
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
    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Enriches a Series object with additional related data from the database.
     *
     * @param conn   the database connection to use for queries
     * @param series the Series object to enrich with additional data, or null
     * @return the enriched Series object, or null if the input was null
     * @throws SQLException if a database access error occurs
     */
    private static Series buildSeries(Connection conn, Series series) throws SQLException {
        ChapterDAO chapterDAO = new ChapterDAO(conn);
        RatingDAO ratingDAO = new RatingDAO(conn);
        CategoryDAO categoryDAO = new CategoryDAO(conn);
        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
        UserDAO userDAO = new UserDAO(conn);
        series.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
        series.setTotalRating(ratingDAO.getRatingCount(series.getSeriesId()));
        series.setCategoryList(categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
        series.setAuthorNameList(userDAO.getAuthorNameList(seriesAuthorDAO.findBySeriesId(series.getSeriesId())));
        series.setAvgRating(Math.round(ratingDAO.getAverageRating(series.getSeriesId()) * 10.0) / 10.0);
        return series;
    }


    private List<Series> getTopRatedSeries(int limit, List<Series> seriesList) throws SQLException {
        List<Series> copy = new ArrayList<>(seriesList);
        copy.sort((s1, s2) -> Double.compare(s2.getTotalRating(), s1.getTotalRating()));
        return copy.size() > limit ? copy.subList(0, limit) : copy;
    }

    private List<Series> getWeeklySeries(int limit, List<Series> seriesList) throws SQLException {
        List<Series> copy = new ArrayList<>(seriesList);
        copy.sort((s1, s2) -> Double.compare(s2.getAvgRating(), s1.getAvgRating()));
        return copy.size() > limit ? copy.subList(0, limit) : copy;
    }

    private List<Series> getNewReleasedSeries(int limit, List<Series> seriesList) throws SQLException {
        List<Series> copy = new ArrayList<>(seriesList);
        copy.sort((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()));
        return copy.size() > limit ? copy.subList(0, limit) : copy;
    }

    private List<Series> getRecentlyUpdated(int limit, List<Series> seriesList) throws SQLException {
        List<Series> copy = new ArrayList<>(seriesList);
        copy.sort((s1, s2) -> s2.getUpdatedAt().compareTo(s1.getUpdatedAt()));
        return copy.size() > limit ? copy.subList(0, limit) : copy;
    }

    private List<Series> getSeriesByStatus(int limit, String status, List<Series> seriesList) throws SQLException {
        List<Series> copy = new ArrayList<>(seriesList);
        copy.removeIf(series -> !series.getStatus().equals(status));
        return copy.size() > limit ? copy.subList(0, limit) : copy;
    }

    /**
     * Creates a notification object for series approval/rejection.
     *
     * @param conn          the database connection to use for queries
     * @param seriesId      the ID of the series that was reviewed
     * @param comment       the staff's feedback comment
     * @param approveStatus the approval decision ("approved" or "rejected")
     * @return a Notification object ready to be inserted into the database
     * @throws SQLException if a database access error occurs while fetching owner ID
     */
    private static Notification createApprovalNotification(Connection conn, int seriesId,
                                                           String comment, String approveStatus) throws SQLException {
        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);

        Notification notification = new Notification();
        notification.setUserId(seriesAuthorDAO.findOwnerIdBySeriesId(seriesId));
        notification.setTitle("Series " + approveStatus);
        notification.setType("submission_status");
        notification.setMessage(comment);
        notification.setUrlRedirect("/series/detail?seriesId=" + seriesId);

        return notification;
    }
}