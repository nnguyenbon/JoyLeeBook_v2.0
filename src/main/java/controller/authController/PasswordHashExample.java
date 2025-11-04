package controller.authController;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashExample {
    public static void main(String[] args) {
        String password = "hash1"; // mật khẩu gốc
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Hashed password: " + hashed);
    }
}
