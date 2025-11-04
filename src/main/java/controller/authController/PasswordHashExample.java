package controller.authController;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Example class to demonstrate password hashing using BCrypt.
 * This class hashes a sample password and prints the hashed value.
 */
public class PasswordHashExample {
    public static void main(String[] args) {
        String password = "123"; // mật khẩu gốc
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Hashed password: " + hashed);
    }
}
