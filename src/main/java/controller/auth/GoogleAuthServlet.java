package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

/**
 * Servlet to handle Google OAuth2 authentication initiation.
 *
 * @author HaiDD-dev
 */
@WebServlet(name = "GoogleAuthServlet", urlPatterns = "/auth/google")
public class GoogleAuthServlet extends HttpServlet {
    private String clientId;
    private String redirectUri;

    /**
     * Initializes the servlet by loading Google OAuth2 client ID and redirect URI from properties file.
     *
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    public void init() throws ServletException {
        try {
            Properties p = new Properties();
            p.load(getServletContext().getResourceAsStream("/WEB-INF/classes/auth.properties"));
            clientId = p.getProperty("GOOGLE_CLIENT_ID");
            redirectUri = p.getProperty("REDIRECT_URI");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Handles GET requests to initiate Google OAuth2 authentication.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws IOException if an input or output error is detected
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String state = UUID.randomUUID().toString();
        req.getSession().setAttribute("OAUTH_STATE", state);
        String url = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code" + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) + "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8) + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8) + "&access_type=online";
        resp.sendRedirect(url);
    }
}