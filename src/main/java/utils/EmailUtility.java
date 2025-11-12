package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtility {

    public static void sendEmail(String toEmail, String subject, String messageContent) throws MessagingException {
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "sasasafyvn@gmail.com"; // thay bằng email thật
        String password = "axen efej lxda jpxd";    // App password, KHÔNG phải mật khẩu thường

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(subject);
        msg.setText(messageContent);

        Transport.send(msg);
    }

}
