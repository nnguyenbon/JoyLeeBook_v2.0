package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import utils.LoginUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter này kiểm soát quyền truy cập (Authorization) dựa trên vai trò (role).
 * Nó giả định rằng vai trò của người dùng được lưu trong Session với key là "USER_ROLE".
 * <p>
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
            "/search",
            "/auth/google/callback",
            "/auth/google",
            "/chapter-content",
            "/navigate-chapter"
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "READER" (và các role cao hơn)
    private static final List<String> READER_PATHS = Arrays.asList(
            "/profile",
            "/register-author",
            "report-chapter",
            "like-chapter",
            "/create-comment",
            "/delete-comment",
            "/edit-comment",
            "/library",
            "/profile"

    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "AUTHOR" (và các role cao hơn)
    private static final List<String> AUTHOR_PATHS = Arrays.asList(
            "/my-series",
            "/manage-coauthors",
            "/manage-chapters",
            "/my-chapters",
            "/add-chapter",
            "/delete-chapter",
            "/update-chapter",
            "/author-profile"
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "STAFF" (và các role cao hơn)
    private static final List<String> STAFF_PATHS = Arrays.asList(
            "/staff",
            "/create-genre",
            "/delete-genre",
            "/edit-genre"
    );

    // Các đường dẫn CHỈ DÀNH CHO ROLE "ADMIN"
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin",
            "/user/manage"
    );

    private static final List<String> SHARED_PATHS = Arrays.asList(
            "/logout",
            "/change-password"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        READER_PATHS.addAll(SHARED_PATHS);
        AUTHOR_PATHS.addAll(SHARED_PATHS);
        STAFF_PATHS.addAll(SHARED_PATHS);
        ADMIN_PATHS.addAll(STAFF_PATHS);
    }

    // =================================================================================
    // KẾT THÚC PHẦN TEMPLATE
    // =================================================================================


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(); // false: không tạo session mới nếu chưa có

        if (this.needJDBC(req)) {
            String path = req.getServletPath();
            String role = "guest";
            User user = LoginUtils.getLoginedUser(session);
            if (user != null) {
                role = user.getRole();
            }
            System.out.println(req.getServletPath() + " " + role);

            if (isPathAllowed(path, PUBLIC_PATHS)) {
                System.out.println(isPathAllowed(path, PUBLIC_PATHS));
                chain.doFilter(request, response);
            } else if (isPathAllowed(path, READER_PATHS) && role.equals("reader")) {
                chain.doFilter(request, response);
            } else if (isPathAllowed(path, AUTHOR_PATHS) && role.equals("author")) {
                chain.doFilter(request, response);
            } else if (isPathAllowed(path, STAFF_PATHS) && role.equals("staff")) {
                chain.doFilter(request, response);
            } else if (isPathAllowed(path, ADMIN_PATHS) && role.equals("admin")) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect("/login");
            }
        } else {
            chain.doFilter(request, response);
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
            if (path.equals(allowedPath)) {
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