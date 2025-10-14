package services.general;

import jakarta.servlet.http.HttpServletRequest;
import utils.ValidationInput;

import java.util.List;

public class PaginationServices {
        public <T> List<T> paginationPage (List<T> list, int totalPage, int currentPage, int sizePage) {
            if (currentPage == totalPage) {
                return list.subList((currentPage-1)*sizePage, list.size());
            } else {
                return list.subList((currentPage-1)*sizePage, currentPage*sizePage);
            }
        }

        public <T> List<T> handleParameterPage (List<T> list, HttpServletRequest request) {
            int totalPage = ValidationInput.isPositiveInteger(request.getParameter("totalPage")) ? Integer.parseInt(request.getParameter("totalPage")) : Math.round((float) list.size() /8);
            int currentPage = ValidationInput.isPositiveInteger(request.getParameter("currentPage")) ? Integer.parseInt(request.getParameter("currentPage")) : 1;
            int sizePage = ValidationInput.isPositiveInteger(request.getParameter("sizePage")) ? Integer.parseInt(request.getParameter("sizePage")) : 8;
            request.setAttribute("totalPage", totalPage);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("sizePage", sizePage);
            return paginationPage(list, totalPage, currentPage, sizePage);
        }
}
