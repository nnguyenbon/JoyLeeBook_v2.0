package utils;

import dao.StaffDAO;
import dao.UserDAO;
import db.DBConnection;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Staff;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

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
    public static User checkLoginUser(String username, String password) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            return userDAO.findByUserLogin(username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Staff checkLoginStaff(String username, String password) throws SQLException {
        try (Connection conn = DBConnection.getConnection()){
            StaffDAO staffDAO = new StaffDAO(conn);
            return staffDAO.findByUserLogin(username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkOTP(HttpSession session, String otp) {
        String hashedOTP = (String) session.getAttribute("otp");
        return AuthenticationUtils.checkPwd(otp, hashedOTP);
    }

    public static User createUser(HttpSession session) throws SQLException {
        try (Connection conn = DBConnection.getConnection()){
            User register = (User) session.getAttribute("register");


            String password = register.getPasswordHash();
            String hashedPassword = AuthenticationUtils.hashPwd(password);
            register.setPasswordHash(hashedPassword);
            UserDAO userDAO = new UserDAO(conn);
            boolean isSuccess = userDAO.insert(register);
            if(isSuccess) {
                return register;
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkExistUsername(String username) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection()){
            UserDAO userDAO = new UserDAO(conn);
            return userDAO.checkByUsername(username);
        }
    }

    public static boolean checkExistEmail(String email) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            return userDAO.checkByEmail(email);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean sendOTP(HttpSession session, User user) throws SQLException {
        // Tạo OTP ngẫu nhiên 6 chữ số
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);

        // Lưu OTP vào session để xác thực sau
        String hashedOTP = AuthenticationUtils.hashPwd(String.valueOf(otp));
        session.setAttribute("otp", hashedOTP);
        System.out.println(otp);
        // Gửi email
        String subject = "OTP code confirms your registration";
        String message = "Your OTP code is: " + otp;

        try {
            EmailUtility.sendEmail(user.getEmail(), subject, message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

    }
}
