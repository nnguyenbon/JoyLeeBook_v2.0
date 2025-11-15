package utils;

import model.PaginationRequest;
import jakarta.servlet.http.HttpServletRequest;

public class PaginationUtils {

    public static PaginationRequest fromRequest(HttpServletRequest req) {
        int page = parseIntOrDefault(req.getParameter("currentPage"), 1);
        int size = parseIntOrDefault(req.getParameter("sizePage"), 10);
        String orderBy = req.getParameter("orderBy");
        String sortDir = req.getParameter("sortDir");
        return new PaginationRequest(page, size, orderBy, sortDir);
    }

    private static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void sendParameter(HttpServletRequest req, PaginationRequest paginationRequest) {
        int size = req.getAttribute("size") == null ? 0 : Integer.parseInt(req.getAttribute("size").toString());
        req.setAttribute("totalPage", (int) Math.ceil((double) size / paginationRequest.getPageSize()));
        req.setAttribute("currentPage", paginationRequest.getPage());
        req.setAttribute("sizePage", paginationRequest.getPageSize());
        req.setAttribute("orderBy", paginationRequest.getOrderBy());
        req.setAttribute("sortDir", paginationRequest.getSortDir());
    }
}
