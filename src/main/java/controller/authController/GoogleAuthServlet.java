package controller.authController;

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
 * Servlet to handle Google OAuth2 authentication.
 * It initiates the OAuth2 flow by redirecting users to Google's authorization endpoint.
 * It reads the client ID and redirect URI from a properties file.
 */
@WebServlet(name = "GoogleAuthServlet", urlPatterns = "/auth/google")
public class GoogleAuthServlet extends HttpServlet {
    private String clientId;
    private String redirectUri;

    /**
     * Initializes the servlet by loading the client ID and redirect URI from the properties file.
     *
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    public void init() throws ServletException {
        try {
            //Load properties from auth.properties file
            Properties p = new Properties();
            p.load(getClass().getClassLoader().getResourceAsStream("auth.properties")); //Load properties file
            clientId = p.getProperty("GOOGLE_CLIENT_ID"); //Get client ID
            redirectUri = p.getProperty("REDIRECT_URI"); //Get redirect URI
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
        req.getSession().setAttribute("OAUTH_STATE", state); //Store state in session for CSRF protection
        //Construct the Google OAuth2 authorization URL
        String url = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code" + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) + "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8) + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8) + "&access_type=online";
        resp.sendRedirect(url); //Redirect to Google's OAuth2 authorization endpoint
    }
}