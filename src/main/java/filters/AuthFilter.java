package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthFilter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    // Danh sách các đường dẫn CÔNG KHAI (không cần đăng nhập)
    // Ví dụ: trang login, register, trang chủ, và các tài nguyên tĩnh (css, js)
    private final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login",
            "/register",
            "/home",
            "/static/" // Giả sử tất cả CSS, JS, Images nằm trong /static/
    );

    // Danh sách các đường dẫn chỉ dành cho ADMIN
    private final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin" // Bất kỳ URL nào bắt đầu bằng /admin
    );

    // Danh sách các đường dẫn chỉ dành cho STAFF (và ADMIN)
    private final List<String> STAFF_PATHS = Arrays.asList(
            "/staff" // Bất kỳ URL nào bắt đầu bằng /staff
    );

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Lấy session, không tạo mới

        // 1. Lấy thông tin đường dẫn (path) và ContextPath
        String contextPath = httpRequest.getContextPath();
        String path = httpRequest.getRequestURI().substring(contextPath.length());

        // 2. Kiểm tra xem người dùng đã đăng nhập chưa
        User user = null;
        if (session != null) {
            // "USER_SESSION" là tên attribute bạn đặt khi lưu user vào session lúc đăng nhập
            user = (User) session.getAttribute("USER_SESSION");
        }

        // 3. Xử lý các đường dẫn CÔNG KHAI (Public Paths)
        // Kiểm tra xem path có bắt đầu bằng bất kỳ chuỗi nào trong PUBLIC_PATHS không
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublicPath) {
            chain.doFilter(request, response); // Cho phép đi tiếp
            return;
        }

        // 4. Xử lý các đường dẫn CẦN ĐĂNG NHẬP (Authentication)
        // Nếu không phải public path mà user là null (chưa đăng nhập) -> Đá về trang login
        if (user == null) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?message=Vui lòng đăng nhập!");
            return;
        }

        // 5. Xử lý các đường dẫn CẦN QUYỀN (Authorization)
        // Tại thời điểm này, user CHẮC CHẮN đã đăng nhập.
        String userRole = user.getRole(); // Ví dụ: "ADMIN"

        // Kiểm tra quyền ADMIN
        boolean isAdminPath = ADMIN_PATHS.stream().anyMatch(path::startsWith);
        if (isAdminPath) {
            if ("ADMIN".equals(userRole)) {
                chain.doFilter(request, response); // Có quyền ADMIN, cho đi tiếp
            } else {
                // Đã đăng nhập nhưng không có quyền
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp"); // Chuyển đến trang báo lỗi 403
            }
            return;
        }

        // Kiểm tra quyền STAFF
        boolean isStaffPath = STAFF_PATHS.stream().anyMatch(path::startsWith);
        if (isStaffPath) {
            if ("ADMIN".equals(userRole) || "STAFF".equals(userRole)) {
                chain.doFilter(request, response); // Có quyền STAFF hoặc ADMIN, cho đi tiếp
            } else {
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp");
            }
            return;
        }

        // 6. Nếu không phải public, không phải admin, không phải staff
        // -> Đây là các trang cho người dùng đã đăng nhập (ví dụ: /profile, /checkout)
        // Mà user đã vượt qua kiểm tra (user != null) ở bước 4, nên ta cho đi tiếp.
        chain.doFilter(request, response);
    }

}
