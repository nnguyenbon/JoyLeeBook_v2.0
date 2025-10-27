package controller.authController;

import db.DBConnection;
import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utils.LoginUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Servlet to handle Google OAuth2 callback.
 *
 * @author HaiDD-dev
 */
@WebServlet(name = "GoogleCallbackServlet", urlPatterns = "/auth/google/callback")
public class GoogleCallbackServlet extends HttpServlet {
    private String clientId, clientSecret, redirectUri;

    /**
     * Initializes the servlet by loading Google OAuth2 client ID, client secret, and redirect URI from properties file.
     *
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    public void init() throws ServletException {
        try {
            Properties p = new Properties();
            p.load(getClass().getClassLoader().getResourceAsStream("auth.properties"));
            clientId = p.getProperty("GOOGLE_CLIENT_ID");
            clientSecret = p.getProperty("GOOGLE_CLIENT_SECRET");
            redirectUri = p.getProperty("REDIRECT_URI");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Handles GET requests for Google OAuth2 callback.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String error = req.getParameter("error");
        if (error != null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String state = req.getParameter("state");
        String expected = (String) req.getSession().getAttribute("OAUTH_STATE");
        if (expected == null || !expected.equals(state)) {
            resp.sendError(400, "Invalid state");
            return;
        }
        String code = req.getParameter("code");
        try {
            // 1) Exchange code for token
            HttpClient client = HttpClient.newHttpClient();
            String body = "code=" + enc(code) + "&client_id=" + enc(clientId) + "&client_secret=" + enc(clientSecret) + "&redirect_uri=" + enc(redirectUri) + "&grant_type=authorization_code";
            HttpRequest tokenReq = HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token")).header("Content-Type", "application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> tokenRes = client.send(tokenReq, HttpResponse.BodyHandlers.ofString());
            JSONObject tok = new JSONObject(tokenRes.body());
            String accessToken = tok.getString("access_token");

            // 2) Get userinfo
            HttpRequest uiReq = HttpRequest.newBuilder(URI.create("https://www.googleapis.com/oauth2/v3/userinfo")).header("Authorization", "Bearer " + accessToken).GET().build();
            HttpResponse<String> uiRes = client.send(uiReq, HttpResponse.BodyHandlers.ofString());
            JSONObject userinfo = new JSONObject(uiRes.body());

            String googleId = userinfo.getString("sub");
            String email = userinfo.optString("email", null);
            String fullName = userinfo.optString("name", "");
            // Assuming the username can be derived from the email or name
            String username = email != null ? email.split("@")[0] : fullName.replaceAll("\\s+", "");


            UserDAO dao = new UserDAO(DBConnection.getConnection());
            // You will need to modify your UserDAO to handle this method
            int userId = dao.upsertGoogleUser(username, fullName, email, googleId);

            User u = dao.findById(userId);
            LoginUtils.storeLoginedUser(req.getSession(), u);
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (SQLException | InterruptedException e) {
            throw new ServletException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility method to URL-encode a string using UTF-8 encoding.
     */
    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}