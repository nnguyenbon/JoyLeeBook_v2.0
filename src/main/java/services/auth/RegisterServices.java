package services.auth;

import dao.UserDAO;
import db.DBConnection;
import jakarta.servlet.http.HttpSession;
import model.User;
import utils.AuthenticationUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class RegisterServices {

    private final Connection conn;

    public RegisterServices() throws SQLException, ClassNotFoundException {
        this.conn = DBConnection.getConnection();
    }

    public boolean checkOTP(HttpSession session, String otp) {
        String hashedOTP = (String) session.getAttribute("otp");
        return AuthenticationUtils.checkPwd(otp, hashedOTP);
    }

    public User createUser(HttpSession session) throws SQLException {

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

    }

}
