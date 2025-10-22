package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter này kiểm soát quyền truy cập (Authorization) dựa trên vai trò (role).
 * Nó giả định rằng vai trò của người dùng được lưu trong Session với key là "USER_ROLE".
 *
 * Các vai trò được xử lý theo thứ bậc:
 * - Admin: Có mọi quyền.
 * - Staff: Có quyền của Staff, Author, Reader.
 * - Author: Có quyền của Author, Reader.
 * - Reader: Có quyền của Reader.
 * - Guest (chưa đăng nhập): Chỉ có quyền truy cập các trang Public.
 */
@WebFilter("/*") // Chặn tất cả mọi request
public class AuthorizationFilter implements Filter {

    // Các tài nguyên public (CSS, JS, Images) và các trang (login, register, home)
    // Bất kỳ ai cũng có thể truy cập.
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login",
            "/register",
            "/homepage",
            "/",
            "/handleOTP",
            "/chapter",
            "/search"
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "READER" (và các role cao hơn)
    private static final List<String> READER_PATHS = Arrays.asList(
            "/profile",
            "/article/view"
            // TODO: Thêm các đường dẫn cho Reader
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "AUTHOR" (và các role cao hơn)
    private static final List<String> AUTHOR_PATHS = Arrays.asList(
            "/article/create",
            "/article/edit"
            // TODO: Thêm các đường dẫn cho Author
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "STAFF" (và các role cao hơn)
    private static final List<String> STAFF_PATHS = Arrays.asList(
            "/moderation",
            "/dashboard/reports"
            // TODO: Thêm các đường dẫn cho Staff
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "ADMIN"
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin",
            "/user/manage"
            // TODO: Thêm các đường dẫn cho Admin
    );

    // =================================================================================
    // KẾT THÚC PHẦN TEMPLATE
    // =================================================================================


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // false: không tạo session mới nếu chưa có

        // Lấy đường dẫn tương đối của request (ví dụ: /login, /admin/dashboard)
        String path = req.getRequestURI().substring(req.getContextPath().length());
        System.out.println(req.getServletPath());
        // 1. Kiểm tra các đường dẫn public (ai cũng vào được)
        if (isPathAllowed(path, PUBLIC_PATHS)) {
            chain.doFilter(request, response); // Cho phép đi tiếp
            return;
        }

        // 2. Lấy vai trò (role) từ session
        // Mặc định là "guest" nếu không có session hoặc không có thuộc tính USER_ROLE
        String role = "guest";
        if (session != null && session.getAttribute("USER_ROLE") != null) {
            role = (String) session.getAttribute("USER_ROLE");
        }

        // 3. Kiểm tra quyền hạn (Authorization Check)
        boolean allowed ;

        switch (role) {
            case "admin":
                // Admin được truy cập TẤT CẢ các path (bao gồm admin, staff, author, reader)
                allowed = isPathAllowed(path, ADMIN_PATHS) ||
                        isPathAllowed(path, STAFF_PATHS) ||
                        isPathAllowed(path, AUTHOR_PATHS) ||
                        isPathAllowed(path, READER_PATHS);
                break;
            case "staff":
                // Staff được truy cập (staff, author, reader)
                allowed = isPathAllowed(path, STAFF_PATHS) ||
                        isPathAllowed(path, AUTHOR_PATHS) ||
                        isPathAllowed(path, READER_PATHS);
                break;
            case "author":
                // Author được truy cập (author, reader)
                allowed = isPathAllowed(path, AUTHOR_PATHS) ||
                        isPathAllowed(path, READER_PATHS);
                break;
            case "reader":
                // Reader chỉ được truy cập (reader)
                allowed = isPathAllowed(path, READER_PATHS);
                break;
            case "guest":
            default:
                // Guest đã được xử lý ở (1). Nếu đến được đây nghĩa là Guest
                // đang cố truy cập vào trang cần đăng nhập.
                allowed = false;
                break;
        }

        // 4. Xử lý kết quả
        if (allowed) {
            chain.doFilter(request, response); // Cho phép đi tiếp
        } else {
            // Nếu không được phép
            if (role.equals("guest")) {
                // Nếu là guest (chưa đăng nhập), chuyển hướng đến trang login
                // (Bạn có thể lưu lại trang họ định truy cập vào session để redirect lại sau khi login)
                res.sendRedirect(req.getContextPath() + "/login"); // TODO: Đảm bảo /login có trong PUBLIC_PATHS
            } else {
                // Nếu đã đăng nhập nhưng không có quyền (ví dụ: Reader cố vào /admin)
                // Gửi lỗi 403 (Forbidden - Cấm truy cập)
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
                // Hoặc chuyển hướng về trang lỗi 403 tùy chỉnh
                // res.sendRedirect(req.getContextPath() + "/error-403.jsp");
            }
        }
    }

    /**
     * Hàm tiện ích kiểm tra xem một đường dẫn (path) có bắt đầu
     * bằng bất kỳ tiền tố nào trong danh sách được phép hay không.
     *
     * @param path         Đường dẫn cần kiểm tra (ví dụ: /admin/users)
     * @param allowedPaths Danh sách các tiền tố được phép (ví dụ: ["/admin", "/profile"])
     * @return true nếu đường dẫn được phép, ngược lại false
     */
    private boolean isPathAllowed(String path, List<String> allowedPaths) {
        for (String allowedPath : allowedPaths) {
            if (path.startsWith(allowedPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean needJDBC(HttpServletRequest request) {
        String urlPattern = request.getPathInfo() == null
                ? request.getServletPath()
                : request.getServletPath() + "/*";

        for (ServletRegistration sr : request.getServletContext().getServletRegistrations().values()) {
            if (sr.getMappings().contains(urlPattern)) {
                return true;
            }
        }
        return false;
    }


}