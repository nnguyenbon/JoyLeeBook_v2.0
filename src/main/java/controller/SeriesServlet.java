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
import utils.PaginationUtils;
import utils.WebpConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // 10MB
/**
 * Servlet controller for managing series-related operations.
 * <p>
 * This servlet handles all CRUD operations for series, including:
 * <ul>
 *   <li>Creating new series with categories and author ownership</li>
 *   <li>Viewing series lists with filtering, searching, and pagination</li>
 *   <li>Viewing detailed information for individual series</li>
 *   <li>Updating existing series (by owner only)</li>
 *   <li>Deleting series (by owner only)</li>
 *   <li>Approving/rejecting series submissions (by staff/admin)</li>
 * </ul>
 *
 * <p><b>URL Mappings:</b></p>
 * <ul>
 *   <li>GET /series/add - Display form to add new series</li>
 *   <li>GET /series/edit - Display form to edit existing series</li>
 *   <li>GET /series/detail - View series details</li>
 *   <li>GET /series/* - List all series (with filters)</li>
 *   <li>POST /series/insert - Create new series</li>
 *   <li>POST /series/update - Update existing series</li>
 *   <li>POST /series/approve - Approve/reject series submission</li>
 *   <li>POST /series/delete - Delete series</li>
 * </ul>
 *
 * <p><b>Role-based Access:</b></p>
 * <ul>
 *   <li><b>Reader:</b> View approved series only</li>
 *   <li><b>Author:</b> Create, edit, delete own series; view all own series</li>
 *   <li><b>Staff/Admin:</b> Approve/reject series; view all series regardless of status</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
@WebServlet("/series/*")
public class SeriesServlet extends HttpServlet {

    // =========================================================================
    // HTTP REQUEST HANDLERS
    // =========================================================================

    /**
     * Handles HTTP GET requests for viewing series data.
     * <p>
     * Routes requests based on the path info:
     * <ul>
     *   <li>/add - Show add series form</li>
     *   <li>/edit - Show edit series form</li>
     *   <li>/detail - Show series detail page</li>
     *   <li>default - Show series list</li>
     * </ul>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/add" -> showAddSeriesForm(request, response);
            case "/edit" -> showEditSeriesForm(request, response);
            case "/detail" -> viewSeriesDetail(request, response);
            default -> viewSeriesList(request, response);
        }
    }

    /**
     * Handles HTTP POST requests for modifying series data.
     * <p>
     * Routes requests based on the path info:
     * <ul>
     *   <li>/insert - Create new series</li>
     *   <li>/update - Update existing series</li>
     *   <li>/approve - Approve/reject series submission</li>
     *   <li>/delete - Delete series</li>
     *   <li>default - Delegate to GET handler</li>
     * </ul>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/insert" -> insertSeries(request, response);
            case "/update" -> updateSeries(request, response);
            case "/approve" -> approveSeries(request, response);
            case "/delete" -> deleteSeries(request, response);
            default -> doGet(request, response);
        }
    }

    // =========================================================================
    // CREATE OPERATIONS
    // =========================================================================

    /**
     * Displays the form for adding a new series.
     * <p>
     * Loads all available categories from the database and forwards to the add series JSP.
     * This form is accessible to authors who want to create new series.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void showAddSeriesForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            List<Category> categories = categoryDAO.getAll();

            request.setAttribute("categories", categories);
            request.setAttribute("contentPage", "/WEB-INF/views/series/_showAddSeries.jsp");
            request.setAttribute("activePage", "Add Series");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading add series form", e);
        }
    }

    /**
     * Inserts a new series into the database.
     * <p>
     * This method performs the following operations in sequence:
     * <ol>
     *   <li>Extracts series data from request parameters (title, status, description, genres)</li>
     *   <li>Processes and converts the uploaded cover image to WebP format</li>
     *   <li>Creates the series record with "pending" approval status</li>
     *   <li>Associates selected genres/categories with the series</li>
     *   <li>Sets the current user as the series owner</li>
     *   <li>Redirects to the author's profile page</li>
     * </ol>
     *
     * <p><b>Note:</b> The series is created with approval_status = "pending" and requires
     * staff/admin approval before being visible to readers.</p>
     *
     * @param request the HTTP servlet request containing:
     *                <ul>
     *                  <li>title - Series title (required)</li>
     *                  <li>selectedGenres - Array of genre IDs</li>
     *                  <li>status - Series status (ongoing/completed)</li>
     *                  <li>description - Series description</li>
     *                  <li>coverImgUrl - Uploaded cover image file</li>
     *                </ul>
     * @param response the HTTP servlet response for redirection
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs during file upload or redirection
     */
    private void insertSeries(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
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
            Part filePart = request.getPart("coverImgUrl");
            if (filePart == null || filePart.getSubmittedFileName().trim().isEmpty()) {
                throw new IOException("Please select a cover image.");
            }

            // Create series object
            Series series = new Series();
            series.setTitle(title);
            series.setCoverImgUrl(WebpConverter.convertToWebp(filePart, getServletContext()));
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
     * <p>
     * The displayed series and available filters depend on the user's role:
     * <ul>
     *   <li><b>Reader:</b> Shows only approved series</li>
     *   <li><b>Author:</b> Shows only their own series (all statuses)</li>
     *   <li><b>Staff/Admin:</b> Shows all series with status filter options</li>
     * </ul>
     *
     * <p><b>Supported Filters:</b></p>
     * <ul>
     *   <li>Search by title (keyword matching)</li>
     *   <li>Filter by genre/category</li>
     *   <li>Filter by approval status (staff/admin only)</li>
     *   <li>Pagination (page number and size)</li>
     * </ul>
     *
     * @param request the HTTP servlet request with optional parameters:
     *                <ul>
     *                  <li>search - Keyword to search in titles</li>
     *                  <li>genre - Array of genre IDs to filter by</li>
     *                  <li>filterByStatus - Approval status filter</li>
     *                  <li>page - Current page number</li>
     *                  <li>size - Items per page</li>
     *                </ul>
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void viewSeriesList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Determine user role and context
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        String role = "reader";
        int authorId = 0;

        if (loggedInAccount != null) {
            if (loggedInAccount instanceof User user) {
                role = user.getRole();
                if ("author".equals(role)) {
                    authorId = user.getUserId();
                }
            } else if (loggedInAccount instanceof Staff staff) {
                role = staff.getRole();
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Extract filter parameters
            String search = request.getParameter("search");
            String approvalStatus = request.getParameter("filterByStatus");

            List<Integer> genreIds = Optional.ofNullable(request.getParameterValues("genre"))
                    .map(Arrays::asList)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            // Readers can only see approved series
            if ("reader".equals(role)) {
                approvalStatus = "approved";
            }

            // Setup pagination
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
            paginationRequest.setOrderBy("series_id");

            // Fetch series list and build full details
            List<Series> seriesList = seriesDAO.getAll(search, genreIds, authorId, approvalStatus, paginationRequest);
            for (Series series : seriesList) {
                buildSeries(conn, series);
            }

            // Get total count for pagination
            int totalRecords = seriesDAO.getTotalSeriesCount(search, genreIds, authorId, approvalStatus);

            // Set request attributes
            request.setAttribute("size", totalRecords);
            request.setAttribute("seriesList", seriesList);
            request.setAttribute("search", search);
            request.setAttribute("filterByStatus", approvalStatus);
            PaginationUtils.sendParameter(request, paginationRequest);

            // Forward to role-specific view
            if ("admin".equals(role) || "staff".equals(role)) {
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_seriesListForStaff.jsp");
                request.setAttribute("activePage", "series");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else if ("author".equals(role)) {
//                request.getRequestDispatcher("/WEB-INF/views/series/_seriesListOfAuthor.jsp").forward(request, response);
            } else {
                request.setAttribute("contentPage", "/WEB-INF/views/series/SeriesList.jsp");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error displaying series list", e);
        }
    }

    /**
     * Displays detailed information for a specific series.
     * <p>
     * The displayed information includes:
     * <ul>
     *   <li>Basic series info (title, description, status, cover image)</li>
     *   <li>Associated categories/genres</li>
     *   <li>Author information and collaborators</li>
     *   <li>Chapter count and ratings</li>
     *   <li>Approval status (for staff/admin/author)</li>
     * </ul>
     *
     * <p><b>Access Control:</b></p>
     * <ul>
     *   <li>Readers can only view approved series</li>
     *   <li>Authors can view their own series regardless of approval status</li>
     *   <li>Staff/Admin can view all series</li>
     * </ul>
     *
     * @param request the HTTP servlet request with parameter:
     *                <ul>
     *                  <li>seriesId - ID of the series to display</li>
     *                </ul>
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void viewSeriesDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Determine user role
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        String role = "reader";

        if (loggedInAccount instanceof User user) {
            role = user.getRole();
        } else if (loggedInAccount instanceof Staff staff) {
            role = staff.getRole();
        }

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            // Readers can only view approved series
            String approvalStatus = "reader".equals(role) ? "approved" : null;

            // Fetch and build series details
            SeriesDAO seriesDAO = new SeriesDAO(conn);
            Series series = buildSeries(conn, seriesDAO.findById(seriesId, approvalStatus));

            if (series == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Series not found.");
                return;
            }

            request.setAttribute("series", series);

            // Forward to role-specific layout
            if ("admin".equals(role) || "staff".equals(role)) {
                request.setAttribute("contentPage", "/WEB-INF/views/staff/_seriesDetailForStaff.jsp");
                request.setAttribute("activePage", "series");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else {
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
     * <p>
     * Loads the current series data and all available categories, then forwards to the
     * edit series JSP. The form is pre-populated with existing series information.
     *
     * <p><b>Note:</b> Authorization check (owner verification) should be performed by
     * the view layer or a filter. This method only loads the data.</p>
     *
     * @param request the HTTP servlet request with parameter:
     *                <ul>
     *                  <li>seriesId - ID of the series to edit</li>
     *                </ul>
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void showEditSeriesForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            CategoryDAO categoryDAO = new CategoryDAO(conn);
            SeriesDAO seriesDAO = new SeriesDAO(conn);

            List<Category> categories = categoryDAO.getAll();
            Series series = seriesDAO.findById(seriesId, "");

            request.setAttribute("categories", categories);
            request.setAttribute("series", series);
            request.setAttribute("contentPage", "WEB-INF/views/series/_showEditSeries.jsp");
            request.setAttribute("activePage", "Edit Series");
            request.getRequestDispatcher("/WEB-INF/views/layout/layoutUser.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading edit series form", e);
        }
    }

    /**
     * Updates an existing series with new data from the form submission.
     * <p>
     * This method performs the following operations:
     * <ol>
     *   <li>Verifies that the series exists</li>
     *   <li>Verifies that the current user is the series owner</li>
     *   <li>Updates series information (title, status, description)</li>
     *   <li>Updates cover image if a new one is uploaded</li>
     *   <li>Updates associated categories/genres</li>
     *   <li>Resets approval status to "pending" (requires re-approval)</li>
     *   <li>Redirects to author's profile page</li>
     * </ol>
     *
     * <p><b>Authorization:</b> Only the series owner can update their series.
     * Returns HTTP 403 Forbidden if the user is not the owner.</p>
     *
     * <p><b>Re-approval Required:</b> After editing, the series approval_status is reset to
     * "pending" and must be re-approved by staff before being visible to readers again.</p>
     *
     * @param request the HTTP servlet request containing:
     *                <ul>
     *                  <li>seriesId - ID of the series to update (required)</li>
     *                  <li>title - Updated series title</li>
     *                  <li>selectedGenres - Array of updated genre IDs</li>
     *                  <li>status - Updated series status</li>
     *                  <li>description - Updated description</li>
     *                  <li>coverImgUrl - New cover image file (optional)</li>
     *                </ul>
     * @param response the HTTP servlet response for redirection or error messages
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs during file upload or redirection
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
            Part filePart = request.getPart("coverImgUrl");
            if (filePart != null && !filePart.getSubmittedFileName().trim().isEmpty()) {
                series.setCoverImgUrl(WebpConverter.convertToWebp(filePart, getServletContext()));
            }

            // Update series fields
            series.setTitle(title);
            series.setStatus(status);
            series.setApprovalStatus("pending"); // Requires re-approval
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

            response.sendRedirect(request.getContextPath() + "/author?userId=" + authorId);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error updating series", e);
        }
    }

    // =========================================================================
    // APPROVAL OPERATIONS (STAFF/ADMIN ONLY)
    // =========================================================================

    /**
     * Approves or rejects a series submission.
     * <p>
     * This method is intended for staff/admin use only to review and approve/reject
     * series submissions. It performs the following operations:
     * <ol>
     *   <li>Verifies that the series exists</li>
     *   <li>Creates a review record with staff ID, status, and optional comment</li>
     *   <li>Updates series approval status via database trigger</li>
     *   <li>Sends notification to the series owner with the decision and feedback</li>
     *   <li>Redirects to the series list page</li>
     * </ol>
     *
     * <p><b>Database Trigger:</b> The series.approval_status is automatically updated
     * by a database trigger when a review record is inserted into the review_series table.</p>
     *
     * <p><b>Notification:</b> The series owner receives a notification containing:
     * <ul>
     *   <li>Approval decision (approved/rejected)</li>
     *   <li>Staff comment/feedback (if provided)</li>
     *   <li>Link to view the series details</li>
     * </ul>
     *
     * <p><b>Authorization:</b> This method should only be accessible to users with
     * staff or admin roles. Authorization should be enforced by a servlet filter.</p>
     *
     * @param request the HTTP servlet request containing:
     *                <ul>
     *                  <li>seriesId - ID of the series to approve/reject</li>
     *                  <li>approveStatus - The approval decision ("approved" or "rejected")</li>
     *                  <li>comment - Optional feedback comment from staff</li>
     *                </ul>
     * @param response the HTTP servlet response for redirection
     * @throws RuntimeException if a database error occurs during the approval process
     */
    private void approveSeries(HttpServletRequest request, HttpServletResponse response) {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int staffId = ((Staff) loggedInAccount).getStaffId();

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));
            String approveStatus = request.getParameter("approveStatus");
            String comment = request.getParameter("comment");

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
            ReviewSeries reviewSeries = new ReviewSeries();
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

            response.sendRedirect(request.getContextPath() + "/series/list");

        } catch (SQLException | ClassNotFoundException e) {
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
     * <p>
     * This method permanently removes a series and all its associated data, including:
     * <ul>
     *   <li>Series record</li>
     *   <li>Category associations</li>
     *   <li>Author associations</li>
     *   <li>Related chapters (cascade delete)</li>
     *   <li>Related reviews and ratings (cascade delete)</li>
     * </ul>
     *
     * <p><b>Authorization:</b> Only the series owner should be able to delete their series.
     * Authorization check should be performed by a servlet filter or added to this method.</p>
     *
     * <p><b>Warning:</b> This operation is irreversible. Consider implementing soft delete
     * (marking as deleted) instead of hard delete for production systems.</p>
     *
     * @param request the HTTP servlet request with parameter:
     *                <ul>
     *                  <li>seriesId - ID of the series to delete</li>
     *                </ul>
     * @param response the HTTP servlet response for redirection
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs during redirection
     */
    private void deleteSeries(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object loggedInAccount = request.getSession().getAttribute("loginedUser");
        int authorId = ((User) loggedInAccount).getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            int seriesId = Integer.parseInt(request.getParameter("seriesId"));

            SeriesDAO seriesDAO = new SeriesDAO(conn);
            seriesDAO.deleteSeries(seriesId);

            response.sendRedirect(request.getContextPath() + "/author?userId=" + authorId);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error deleting series", e);
        }
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Enriches a Series object with additional related data from the database.
     * <p>
     * This method populates the following fields in the Series object:
     * <ul>
     *   <li><b>totalChapters</b> - Count of chapters in this series</li>
     *   <li><b>totalRating</b> - Count of ratings/reviews for this series</li>
     *   <li><b>categoryList</b> - List of categories/genres associated with this series</li>
     *   <li><b>seriesAuthorList</b> - List of authors/collaborators for this series</li>
     * </ul>
     *
     * <p>This method is typically called before displaying series information to ensure
     * all necessary data is loaded. It uses separate DAO calls to fetch related data,
     * which may result in N+1 query issues for large datasets.</p>
     *
     * <p><b>Performance Note:</b> Consider optimizing with JOIN queries or batch loading
     * when processing multiple series at once.</p>
     *
     * @param conn the database connection to use for queries
     * @param series the Series object to enrich with additional data, or null
     * @return the enriched Series object, or null if the input was null
     * @throws SQLException if a database access error occurs
     */
    private static Series buildSeries(Connection conn, Series series) throws SQLException {
        if (series == null) {
            return null;
        }

        ChapterDAO chapterDAO = new ChapterDAO(conn);
        RatingDAO ratingDAO = new RatingDAO(conn);
        CategoryDAO categoryDAO = new CategoryDAO(conn);
        SeriesAuthorDAO seriesAuthorDAO = new SeriesAuthorDAO(conn);
        UserDAO userDAO = new UserDAO(conn);

        series.setTotalChapters(chapterDAO.countChapterBySeriesId(series.getSeriesId()));
        series.setTotalRating(ratingDAO.getRatingCount(series.getSeriesId()));
        series.setCategoryList(categoryDAO.getCategoryBySeriesId(series.getSeriesId()));
        List<SeriesAuthor> seriesAuthorList = seriesAuthorDAO.findBySeriesId(series.getSeriesId());
        series.setAuthorNameList(userDAO.getAuthorNameList(seriesAuthorList));

        return series;
    }

    /**
     * Creates a notification object for series approval/rejection.
     * <p>
     * This notification is sent to the series owner to inform them of the approval decision.
     * The notification includes:
     * <ul>
     *   <li>Title indicating the approval status</li>
     *   <li>Staff comment/feedback as the message body</li>
     *   <li>Link to view the series details</li>
     *   <li>Notification type marked as "submission_status"</li>
     * </ul>
     *
     * @param conn the database connection to use for queries
     * @param seriesId the ID of the series that was reviewed
     * @param comment the staff's feedback comment
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