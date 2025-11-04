package utils;

import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Staff;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationUtils {
    public static void storeLoginedUser(HttpSession session, Account loginedUser) {
        // Trên JSP có thể truy cập thông qua ${loginedUser}
        session.setAttribute("loginedUser", loginedUser);
    }

    public static Account getLoginedUser(HttpSession session) {
        Account loginedUser = (Account) session.getAttribute("loginedUser");
        if (loginedUser != null) {
            if (loginedUser.getRole().equals("admin") ||  loginedUser.getRole().equals("staff")) {
                return (Staff) session.getAttribute("loginedUser");
            } else  {
                return (User) session.getAttribute("loginedUser");
            }
        }
        return null;
    }

    public static String hashPwd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean checkPwd(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
