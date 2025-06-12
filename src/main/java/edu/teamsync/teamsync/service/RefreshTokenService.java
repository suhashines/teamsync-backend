package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.config.JwtProvider;
import edu.teamsync.teamsync.dto.authDTO.TokenRefreshResponseDTO;
import edu.teamsync.teamsync.entity.RefreshToken;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.repository.RefreshTokenRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int REFRESH_TOKEN_LENGTH = 32;
    private static final int MAX_TOKENS_PER_USER = 5; // Maximum active tokens per user

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.refresh-token.expiration:604800}") // 7 days in seconds
    private long refreshTokenExpiration;

    @Transactional
    public String createRefreshToken(Users user, HttpServletRequest request) {
        // Clean up expired tokens for this user
        cleanupExpiredTokens();

        // Enforce maximum tokens per user
        enforceTokenLimit(user);

        // Generate unique token
        String tokenValue = generateUniqueToken();

        // Create and save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .build();

        refreshTokenRepository.save(refreshToken);

//        log.info("Created refresh token for user: {} from IP: {}", user.getEmail(), ipAddress);
        return tokenValue;
    }

    @Transactional
    public TokenRefreshResponseDTO refreshToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // Validate token
        if (!refreshToken.isValid()) {
            if (refreshToken.isExpired()) {
                refreshTokenRepository.delete(refreshToken);
                throw new IllegalArgumentException("Refresh token has expired");
            } else {
                throw new IllegalArgumentException("Refresh token has been revoked");
            }
        }

        Users user = refreshToken.getUser();

        // Generate new access token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, null);
        String newAccessToken = jwtProvider.generateToken(authentication);

        // Generate new refresh token (rotate refresh tokens for security)
        String newRefreshToken = generateUniqueToken();

        // Update existing refresh token
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration));
        refreshTokenRepository.save(refreshToken);
        return TokenRefreshResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void revokeToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(String email) {
        Users user = userRepository.findByEmail(email);
        if (user != null) {
            refreshTokenRepository.revokeAllByUser(user);
            log.info("Revoked all refresh tokens for user: {}", email);
        }
    }

    @Transactional
    public void revokeAllUserTokens(Users user) {
        refreshTokenRepository.revokeAllByUser(user);
    }

    public boolean validateToken(String tokenValue) {
        return refreshTokenRepository.findByToken(tokenValue)
                .map(RefreshToken::isValid)
                .orElse(false);
    }

    public List<RefreshToken> getUserActiveTokens(Users user) {
        return refreshTokenRepository.findByUserAndRevokedFalse(user);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private String generateUniqueToken() {
        String token;
        do {
            byte[] randomBytes = new byte[REFRESH_TOKEN_LENGTH];
            secureRandom.nextBytes(randomBytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } while (refreshTokenRepository.findByToken(token).isPresent()); // Ensure uniqueness

        return token;
    }

    private void enforceTokenLimit(Users user) {
        int activeTokenCount = refreshTokenRepository.countValidTokensByUser(user, LocalDateTime.now());

        if (activeTokenCount >= MAX_TOKENS_PER_USER) {
            // Revoke oldest tokens to make room for new one
            List<RefreshToken> oldestTokens = refreshTokenRepository
                    .findOldestValidTokensByUser(user, LocalDateTime.now());

            int tokensToRevoke = activeTokenCount - MAX_TOKENS_PER_USER + 1;
            for (int i = 0; i < tokensToRevoke && i < oldestTokens.size(); i++) {
                RefreshToken tokenToRevoke = oldestTokens.get(i);
                tokenToRevoke.setRevoked(true);
                refreshTokenRepository.save(tokenToRevoke);
            }
        }
    }
}