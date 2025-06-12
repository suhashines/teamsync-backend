//package edu.teamsync.teamsync.service;
//
//import edu.teamsync.teamsync.dto.authDTO.PasswordResetDTO;
//import edu.teamsync.teamsync.dto.authDTO.PasswordResetRequestDTO;
//import edu.teamsync.teamsync.entity.PasswordResetToken;
//import edu.teamsync.teamsync.entity.Users;
//import edu.teamsync.teamsync.exception.http.NotFoundException;
//import edu.teamsync.teamsync.repository.PasswordResetTokenRepository;
//import edu.teamsync.teamsync.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.Base64;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PasswordResetService {
//
//    private static final int TOKEN_LENGTH = 32;
//    private static final int MAX_RESET_ATTEMPTS_PER_HOUR = 3;
//
//    private final PasswordResetTokenRepository passwordResetTokenRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final EmailService emailService;
//    private final RefreshTokenService refreshTokenService;
//    private final SecureRandom secureRandom = new SecureRandom();
//
//    @Value("${app.password-reset.expiration:3600}") // 1 hour in seconds
//    private long passwordResetExpiration;
//
//    @Transactional
//    public void requestPasswordReset(PasswordResetRequestDTO request) {
//        Users user = userRepository.findByEmail(request.getEmail());
//
//        if (user == null) {
//            // Don't reveal if email exists or not for security
//            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
//            return;
//        }
//
//        // Check rate limiting
//        if (isRateLimited(user)) {
//            throw new IllegalArgumentException("Too many password reset requests. Please try again later.");
//        }
//
//        // Cleanup expired tokens
//        cleanupExpiredTokens();
//
//        // Invalidate existing tokens for this user
//        passwordResetTokenRepository.markAllTokensAsUsedByUser(user);
//
//        // Generate unique token
//        String tokenValue = generateUniqueToken();
//
//        // Create and save password reset token
//        PasswordResetToken resetToken = PasswordResetToken.builder()
//                .token(tokenValue)
//                .user(user)
//                .expiresAt(LocalDateTime.now().plusSeconds(passwordResetExpiration))
//                .build();
//
//        passwordResetTokenRepository.save(resetToken);
//
//        // Send email
//        try {
//            emailService.sendPasswordResetEmail(
//                    user.getEmail(),
//                    tokenValue,
//                    user.getName()
//            );
//            log.info("Password reset email sent to: {}", user.getEmail());
//        } catch (Exception e) {
//            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
//            throw new RuntimeException("Failed to send password reset email");
//        }
//    }
//
//    @Transactional
//    public void resetPassword(PasswordResetDTO resetRequest) {
//        PasswordResetToken resetToken = passwordResetTokenRepository
//                .findByTokenAndUsedFalse(resetRequest.getToken())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));
//
//        // Validate token
//        if (!resetToken.isValid()) {
//            if (resetToken.isExpired()) {
//                passwordResetTokenRepository.delete(resetToken);
//                throw new IllegalArgumentException("Reset token has expired");
//            } else {
//                throw new IllegalArgumentException("Reset token has already been used");
//            }
//        }
//
//        Users user = resetToken.getUser();
//
//        // Update password
//        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
//        userRepository.save(user);
//
//        // Mark token as used
//        resetToken.setUsed(true);
//        passwordResetTokenRepository.save(resetToken);
//
//        // Revoke all existing refresh tokens for security
//        refreshTokenService.revokeAllUserTokens(user);
//
//        log.info("Password reset successfully for user: {}", user.getEmail());
//    }
//
//    @Transactional
//    public void cleanupExpiredTokens() {
//        passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
//    }
//
//    private String generateUniqueToken() {
//        String token;
//        do {
//            byte[] randomBytes = new byte[TOKEN_LENGTH];
//            secureRandom.nextBytes(randomBytes);
//            token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
//        } while (passwordResetTokenRepository.findByToken(token).isPresent());
//
//        return token;
//    }
//
//    private boolean isRateLimited(Users user) {
//        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
//        int recentAttempts = passwordResetTokenRepository.countValidTokensByUser(user, oneHourAgo);
//        return recentAttempts >= MAX_RESET_ATTEMPTS_PER_HOUR;
//    }
//}

package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.authDTO.PasswordResetDTO;
import edu.teamsync.teamsync.dto.authDTO.PasswordResetRequestDTO;
import edu.teamsync.teamsync.entity.PasswordResetToken;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.repository.PasswordResetTokenRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_LENGTH = 32;
    private static final int MAX_RESET_ATTEMPTS_PER_HOUR = 3;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.password-reset.expiration:3600}") // 1 hour in seconds
    private long passwordResetExpiration;

    @Transactional
    public void requestPasswordReset(PasswordResetRequestDTO request) {
        Users user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            // Don't reveal if email exists or not for security
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            return;
        }

        // Check rate limiting
        if (isRateLimited(user)) {
            throw new IllegalArgumentException("Too many password reset requests. Please try again later.");
        }

        // Cleanup expired tokens
        cleanupExpiredTokens();

        // Invalidate existing tokens for this user
        passwordResetTokenRepository.markAllTokensAsUsedByUser(user);

        // Generate unique token
        String tokenValue = generateUniqueToken();

        // Create password reset token (don't save yet)
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(passwordResetExpiration))
                .build();

        // Try to send email first before saving token
        try {
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    tokenValue,
                    user.getName()
            );
            log.info("Password reset email sent successfully to: {}", user.getEmail());

            // Only save token if email was sent successfully
            passwordResetTokenRepository.save(resetToken);

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);

            // Throw a more specific exception that can be handled by your exception handler
            throw new IllegalArgumentException("Unable to send password reset email. Please try again later or contact support.");
        }
    }

    @Transactional
    public void resetPassword(PasswordResetDTO resetRequest) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenAndUsedFalse(resetRequest.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        // Validate token
        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                passwordResetTokenRepository.delete(resetToken);
                throw new IllegalArgumentException("Reset token has expired");
            } else {
                throw new IllegalArgumentException("Reset token has already been used");
            }
        }

        Users user = resetToken.getUser();

        // Update password
        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Revoke all existing refresh tokens for security
        refreshTokenService.revokeAllUserTokens(user);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private String generateUniqueToken() {
        String token;
        do {
            byte[] randomBytes = new byte[TOKEN_LENGTH];
            secureRandom.nextBytes(randomBytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } while (passwordResetTokenRepository.findByToken(token).isPresent());

        return token;
    }

    private boolean isRateLimited(Users user) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int recentAttempts = passwordResetTokenRepository.countValidTokensByUser(user, oneHourAgo);
        return recentAttempts >= MAX_RESET_ATTEMPTS_PER_HOUR;
    }
}