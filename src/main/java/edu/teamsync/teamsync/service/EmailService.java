package edu.teamsync.teamsync.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class  EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String resetToken, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request - TeamSync");

            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String emailBody = String.format(
                    "Hello %s,\n\n" +
                            "You have requested to reset your password for your TeamSync account.\n\n" +
                            "Please click the following link to reset your password:\n%s\n\n" +
                            "This link will expire in 1 hour.\n\n" +
                            "If you did not request this password reset, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "TeamSync Team",
                    userName, resetUrl
            );

            message.setText(emailBody);
            mailSender.send(message);

            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}