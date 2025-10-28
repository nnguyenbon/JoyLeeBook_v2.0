package controller.generalController;

import dto.staff.DashboardStatsDTO;
import dto.staff.QuickStatsDTO;
import dto.staff.RecentActionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.account.StaffServices;

import java.io.IOException;

import java.util.List;


@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Staff loginedStaff = (Staff) request.getSession().getAttribute("loginedStaff");
        try {
            StaffServices staffServices = new StaffServices();
//            Staff currentStaff = staffServices.getCurrentStaff(loginedStaff.getStaffId());

//            int staffId = currentStaff.getStaffId();
            int staffId = 1;
            DashboardStatsDTO stats = staffServices.getDashboardStats(staffId);
            List<RecentActionDTO> recentActions = staffServices.getRecentActions(staffId, 4);
            QuickStatsDTO quickStats = staffServices.getQuickStatsToday(staffId);

            // Set attributes cho JSP
//            request.setAttribute("currentStaff", currentStaff);
            request.setAttribute("dashboardStats", stats);
            request.setAttribute("recentActions", recentActions);
            request.setAttribute("quickStats", quickStats);

            request.setAttribute("pageTitle", "Staff Dashboard");
            request.setAttribute("activePage", "overview");
            request.setAttribute("contentPage", "/WEB-INF/views/general/StaffDashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
        }catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
//    private void viewSeries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int staffId = ValidationInput.isPositiveInteger(request.getParameter("staffId")) ? Integer.parseInt(request.getParameter("staffId")) : 1;
//        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
//
//        try {
//            Staff staff = staffServices.getStaffAccount(staffId);
////            if (staffServices.handleRedirect(type, request, response)) {
////                return;
////            }
//            List<SeriesInfoDTO> seriesInfoDTOList;
//            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
//
//            if ("series".equals(type)) {
//                paginationRequest.setOrderBy("series_id");
//                seriesInfoDTOList = seriesServices.buildSeriesList(PaginationRequest paginationRequest);
//                request.setAttribute("size", seriesInfoDTOList.size());
//                request.setAttribute("seriesInfoDTOList", seriesInfoDTOList);
//                PaginationUtils.sendParameter(request, paginationRequest);
//
//                request.setAttribute("pageTitle", "Staff Dashboard - Series");
//                request.setAttribute("contentPage", "/WEB-INF/views/general/ViewSeriesListForStaff.jsp");
//                request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
//            } else if ("chapter".equals(type)) {
//                ChapterServices chapterServices = new ChapterServices();
//                List<ChapterDetailDTO> chapterList = chapterServices.buildChapterDetailDTOList(chapterDAO.getAll());
//                List<ChapterDetailDTO> chapterDetailDTOList = paginationServices.handleParameterPage(chapterList, request);
//                request.setAttribute("size", chapterList.size());
//                request.setAttribute("chapterDetailDTOList", chapterDetailDTOList);
//                request.getRequestDispatcher("/WEB-INF/views/general/staffview/ChaptersListView.jsp").forward(request, response);
//                return true;
//            } else if ("genres".equals(type)) {
//                CategoryServices categoryServices = new CategoryServices();
//                List<CategoryInfoDTO> categoryList = categoryServices.buildCategoryInfoDTOList(categoryDAO.getAll());
//                List<CategoryInfoDTO> categoryInfoDTOList = paginationServices.handleParameterPage(categoryList, request);
//                request.setAttribute("size", categoryList.size());
//                request.setAttribute("categoryInfoDTOList", categoryInfoDTOList);
//                request.getRequestDispatcher("/WEB-INF/views/general/staffview/GenresListView.jsp").forward(request, response);
//                return true;
//            }
//            return false;
//            request.setAttribute("type", type != null ? type : "series");
//            request.setAttribute("staffId", staff.getStaffId());
//            request.setAttribute("staffName", staff.getFullName());
//            request.setAttribute("pageTitle", "Staff Dashboard - Series");
//            request.setAttribute("contentPage", "/WEB-INF/views/general/StaffDashboard.jsp");
//            request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
//
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private void viewReports(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            String type = request.getParameter("type") == null ? "" : request.getParameter("type");
//
//            ReportServices reportServices = new ReportServices();
//            List<ReportBaseDTO> reportList;
//
//            PaginationRequest paginationRequest = PaginationUtils.fromRequest(request);
//            paginationRequest.setOrderBy("report_id");
//            reportList = reportServices.buildReportList(type, paginationRequest);
//            if (type.equals("chapter")) {
//                request.setAttribute("size", reportServices.countReports("chapter"));
//                request.setAttribute("reportChapterDTOList", reportList);
//                PaginationUtils.sendParameter(request, paginationRequest);
//                request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportChapterView.jsp").forward(request, response);
//                return;
//            } else if (type.equals("comment")) {
//                request.setAttribute("size", reportServices.countReports("comment"));
//                request.setAttribute("reportCommentDTOList", reportList);
//                PaginationUtils.sendParameter(request, paginationRequest);
//                request.getRequestDispatcher("/WEB-INF/views/report/reportview/ReportCommentView.jsp").forward(request, response);
//                return;
//            }
//            request.setAttribute("type", type);
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        request.setAttribute("pageTitle", "Staff Dashboard - Reports");
//        request.setAttribute("contentPage", "/WEB-INF/views/report/ReportList.jsp");
//        request.getRequestDispatcher("/WEB-INF/views/components/_layoutStaff.jsp").forward(request, response);
//    }
//    private void viewUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
//}
