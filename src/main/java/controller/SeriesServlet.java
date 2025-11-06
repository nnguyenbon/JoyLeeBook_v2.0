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
@WebServlet("/series/*")
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
        String action = request.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/add" -> showAddSeriesForm(request, response);
            case "/edit" -> showEditSeriesForm(request, response);
            case "/detail" -> viewSeriesDetail(request, response);
            case "/delete" -> deleteSeries(request, response);
            default -> viewSeriesList(request, response);
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
        String action = request.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/insert" -> insertSeries(request, response);
            case "/update" -> updateSeries(request, response);
            case "/approve" -> approveSeries(request, response);
            default -> doGet(request, response);
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
                request.setAttribute("pageTitle", "Manage Series");
                request.getRequestDispatcher("/WEB-INF/views/layout/layoutStaff.jsp").forward(request, response);
            } else if ("author".equals(role)) {
                request.getRequestDispatcher("/WEB-INF/views/series/_seriesListOfAuthor.jsp").forward(request, response);
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
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
            ChapterDAO chapterDAO = new ChapterDAO(conn);
            Series series = buildSeries(conn, seriesDAO.findById(seriesId, approvalStatus));

            if (series == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Series not found.");
                return;
            }
            List<Chapter> chapterList = chapterDAO.findChapterBySeriesId(seriesId, approvalStatus);

            request.setAttribute("series", series);
            request.setAttribute("chapterList", chapterList);
            request.setAttribute("pageTitle", "Series Detail");
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
            if(nameFilePart.isEmpty() || nameFilePart.equals(oldImg)) {
                System.out.println(oldImg);
                series.setCoverImgUrl(oldImg);
            } else {
                if (!filePart.getSubmittedFileName().trim().isEmpty()) {
                    System.out.println(filePart.getSubmittedFileName());
                    series.setCoverImgUrl(WebpConverter.convertToWebp(filePart, getServletContext()));
                } else {
                    System.out.println("ki cuc qua");
                }
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

            response.sendRedirect(request.getContextPath() + "/series/list?filterByStatus=pending");

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
        List<SeriesAuthor> seriesAuthorList = seriesAuthorDAO.findBySeriesId(series.getSeriesId());
        series.setAuthorNameList(userDAO.getAuthorNameList(seriesAuthorList));

        return series;
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