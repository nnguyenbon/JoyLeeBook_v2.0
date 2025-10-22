package services.auth;

import dao.UserDAO;
import db.DBConnection;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import model.User;
import utils.EmailUtility;
import utils.HashPwd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class HandleOTPServices {

    private final Connection conn;

    public HandleOTPServices() throws SQLException, ClassNotFoundException {
        this.conn = DBConnection.getConnection();
    }

    public boolean checkUserExist(User user) throws SQLException {
        UserDAO userDAO = new UserDAO(conn);
        return userDAO.findByUsernameOrEmail(user.getUsername(), user.getEmail());
    }

    public boolean sendOTP(HttpSession session, User user) throws SQLException {

        // Tạo OTP ngẫu nhiên 6 chữ số
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);

        // Lưu OTP vào session để xác thực sau
        String hashedOTP = HashPwd.hashPwd(String.valueOf(otp));
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
