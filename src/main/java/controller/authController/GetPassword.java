package controller.authController;

import utils.AuthenticationUtils;

public class GetPassword {

    public static void main(String[] args) {
        String password = "123";
        System.out.println(AuthenticationUtils.hashPwd(password));
    }
}
