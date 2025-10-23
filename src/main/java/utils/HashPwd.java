package utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashPwd {


    public static String hashPwd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean checkPwd(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
