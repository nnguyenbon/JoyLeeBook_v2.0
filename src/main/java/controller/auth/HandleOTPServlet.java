package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import services.auth.HandleOTPServices;


import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/handleOTP")
public class HandleOTPServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User register;
        if(session.getAttribute("register") == null){
            String userName = request.getParameter("username");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");

            register = new User();
            register.setUsername(userName);
            register.setPasswordHash(password);
            register.setFullName(fullName);
            register.setEmail(email);
        } else {
            register = (User) session.getAttribute("register");
        }

        try {


            HandleOTPServices handleOTPServices = new HandleOTPServices();
            boolean isExist = handleOTPServices.checkUserExist(register);
            if(isExist) {
                System.out.println("Username or email is already exist.");
                request.setAttribute("message", "Username or email is already exist.");
                return;
            }

            request.setAttribute("pageTitle", "Verify Email");
            request.setAttribute("contentPage", "/WEB-INF/views/auth/VerifyOTP.jsp");
            request.getRequestDispatcher("/WEB-INF/views/components/_layoutAuth.jsp").forward(request, response);
            System.out.println("redirect to verify email");
            boolean sendSuccess = handleOTPServices.sendOTP( session, register);
            if(sendSuccess) {
                System.out.println("OTP has been sent to your email.");
                session.setAttribute("register", register);
                request.setAttribute("message", "OTP has been sent to your email.");

            } else {
                System.out.println("Something went wrong.");
                request.setAttribute("message", "Something went wrong. Please try again.");
            }


        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error in HandleOTPServlet: " + e.getMessage());

        }


    }
}
