package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.config.JwtProvider;
import edu.teamsync.teamsync.dto.authDTO.TokenRefreshResponseDTO;
import edu.teamsync.teamsync.entity.RefreshToken;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.repository.RefreshTokenRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private final Long userId = 1L;
    private final String userEmail = "test@example.com";
    private final String tokenValue = "test-refresh-token-123";
    private final String newAccessToken = "new-access-token-456";
    private final long refreshTokenExpiration = 604800L; // 7 days

    private Users user;
    private RefreshToken refreshToken;
    private RefreshToken expiredToken;
    private RefreshToken revokedToken;

    @BeforeEach
    void setUp() {
        // Set refresh token expiration via reflection
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", refreshTokenExpiration);

        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();

        expiredToken = RefreshToken.builder()
                .id(2L)
                .token("expired-token")
                .user(user)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(8))
                .revoked(false)
                .build();

        revokedToken = RefreshToken.builder()
                .id(3L)
                .token("revoked-token")
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .revoked(true)
                .build();
    }

    @Test
    void createRefreshToken_Success() {
        // Arrange
        when(refreshTokenRepository.countValidTokensByUser(eq(user), any(LocalDateTime.class))).thenReturn(0);
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        String result = refreshTokenService.createRefreshToken(user, httpServletRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
        verify(refreshTokenRepository).countValidTokensByUser(eq(user), any(LocalDateTime.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken savedToken = tokenCaptor.getValue();
        assertEquals(user, savedToken.getUser());
        assertFalse(savedToken.getRevoked());
        assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void createRefreshToken_EnforcesTokenLimit() {
        // Arrange
        RefreshToken oldToken1 = RefreshToken.builder().id(4L).token("old-token-1").user(user).build();
        RefreshToken oldToken2 = RefreshToken.builder().id(5L).token("old-token-2").user(user).build();
        List<RefreshToken> oldTokens = Arrays.asList(oldToken1, oldToken2);

        when(refreshTokenRepository.countValidTokensByUser(eq(user), any(LocalDateTime.class))).thenReturn(5);
        when(refreshTokenRepository.findOldestValidTokensByUser(eq(user), any(LocalDateTime.class))).thenReturn(oldTokens);
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        String result = refreshTokenService.createRefreshToken(user, httpServletRequest);

        // Assert
        assertNotNull(result);
        verify(refreshTokenRepository).findOldestValidTokensByUser(eq(user), any(LocalDateTime.class));
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class)); // 2 saves: 1 for new token, 1 for revoked tokens
        assertTrue(oldToken1.getRevoked());
    }

    @Test
    void refreshToken_Success() {
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn(newAccessToken);
        // Mock for new token uniqueness check during rotation
        when(refreshTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(refreshToken))  // Initial token lookup
                .thenReturn(Optional.empty());          // New token uniqueness check
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        TokenRefreshResponseDTO result = refreshTokenService.refreshToken(tokenValue);

        // Assert
        assertNotNull(result);
        assertEquals(newAccessToken, result.getToken());
        assertNotNull(result.getRefreshToken());
        assertNotEquals(tokenValue, result.getRefreshToken()); // Token should be rotated
        verify(refreshTokenRepository, atLeastOnce()).findByToken(anyString());
        verify(jwtProvider).generateToken(any(Authentication.class));
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void refreshToken_TokenNotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.refreshToken(tokenValue)
        );
        assertEquals("Invalid refresh token", exception.getMessage());
        verify(refreshTokenRepository).findByToken(tokenValue);
    }

    @Test
    void refreshToken_TokenExpired() {
        // Arrange
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.refreshToken("expired-token")
        );
        assertEquals("Refresh token has expired", exception.getMessage());
        verify(refreshTokenRepository).findByToken("expired-token");
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void refreshToken_TokenRevoked() {
        // Arrange
        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                refreshTokenService.refreshToken("revoked-token")
        );
        assertEquals("Refresh token has been revoked", exception.getMessage());
        verify(refreshTokenRepository).findByToken("revoked-token");
    }

    @Test
    void revokeToken_Success() {
        // Arrange
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        refreshTokenService.revokeToken(tokenValue);

        // Assert
        verify(refreshTokenRepository).findByToken(tokenValue);
        verify(refreshTokenRepository).save(refreshToken);
        assertTrue(refreshToken.getRevoked());
    }

    @Test
    void revokeToken_TokenNotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                refreshTokenService.revokeToken(tokenValue)
        );
        assertEquals("Refresh token not found", exception.getMessage());
        verify(refreshTokenRepository).findByToken(tokenValue);
    }

    @Test
    void revokeAllUserTokens_ByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(user);

        // Act
        refreshTokenService.revokeAllUserTokens(userEmail);

        // Assert
        verify(userRepository).findByEmail(userEmail);
        verify(refreshTokenRepository).revokeAllByUser(user);
    }

    @Test
    void revokeAllUserTokens_ByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        // Act
        refreshTokenService.revokeAllUserTokens(userEmail);

        // Assert
        verify(userRepository).findByEmail(userEmail);
        verify(refreshTokenRepository, never()).revokeAllByUser(any(Users.class));
    }

    @Test
    void revokeAllUserTokens_ByUser_Success() {
        // Act
        refreshTokenService.revokeAllUserTokens(user);

        // Assert
        verify(refreshTokenRepository).revokeAllByUser(user);
    }

    @Test
    void validateToken_ValidToken() {
        // Arrange
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        // Act
        boolean result = refreshTokenService.validateToken(tokenValue);

        // Assert
        assertTrue(result);
        verify(refreshTokenRepository).findByToken(tokenValue);
    }

    @Test
    void validateToken_InvalidToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));

        // Act
        boolean result = refreshTokenService.validateToken("revoked-token");

        // Assert
        assertFalse(result);
        verify(refreshTokenRepository).findByToken("revoked-token");
    }

    @Test
    void validateToken_TokenNotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        // Act
        boolean result = refreshTokenService.validateToken(tokenValue);

        // Assert
        assertFalse(result);
        verify(refreshTokenRepository).findByToken(tokenValue);
    }

    @Test
    void getUserActiveTokens_Success() {
        // Arrange
        List<RefreshToken> activeTokens = Arrays.asList(refreshToken);
        when(refreshTokenRepository.findByUserAndRevokedFalse(user)).thenReturn(activeTokens);

        // Act
        List<RefreshToken> result = refreshTokenService.getUserActiveTokens(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(refreshToken, result.get(0));
        verify(refreshTokenRepository).findByUserAndRevokedFalse(user);
    }

    @Test
    void getUserActiveTokens_NoActiveTokens() {
        // Arrange
        when(refreshTokenRepository.findByUserAndRevokedFalse(user)).thenReturn(Collections.emptyList());

        // Act
        List<RefreshToken> result = refreshTokenService.getUserActiveTokens(user);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(refreshTokenRepository).findByUserAndRevokedFalse(user);
    }

    @Test
    void cleanupExpiredTokens_Success() {
        // Act
        refreshTokenService.cleanupExpiredTokens();

        // Assert
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void generateUniqueToken_EnsuresUniqueness() {
        // Arrange
        when(refreshTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(refreshToken)) // First call returns existing token
                .thenReturn(Optional.empty()); // Second call returns empty (unique token)
        when(refreshTokenRepository.countValidTokensByUser(eq(user), any(LocalDateTime.class))).thenReturn(0);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        String result = refreshTokenService.createRefreshToken(user, httpServletRequest);

        // Assert
        assertNotNull(result);
        verify(refreshTokenRepository, times(2)).findByToken(anyString()); // Called twice due to uniqueness check
    }

    @Test
    void createRefreshToken_TokenGenerationWithProperLength() {
        // Arrange
        when(refreshTokenRepository.countValidTokensByUser(eq(user), any(LocalDateTime.class))).thenReturn(0);
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        String result = refreshTokenService.createRefreshToken(user, httpServletRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
        // Base64 URL-encoded 32 bytes should be around 43 characters without padding
        assertTrue(result.length() >= 40);
    }

    @Test
    void refreshToken_GeneratesNewAuthenticationCorrectly() {
        // Arrange
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn(newAccessToken);

        // Single, more flexible stubbing that handles both cases:
        when(refreshTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(refreshToken))  // Initial token lookup
                .thenReturn(Optional.empty());          // New token uniqueness check

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        TokenRefreshResponseDTO result = refreshTokenService.refreshToken(tokenValue);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<Authentication> authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(jwtProvider).generateToken(authCaptor.capture());
        Authentication capturedAuth = authCaptor.getValue();
        assertEquals(userEmail, capturedAuth.getName());
        assertNull(capturedAuth.getCredentials());
    }

    @Test
    void refreshToken_UpdatesTokenPropertiesCorrectly() {
        // Arrange
        String originalToken = refreshToken.getToken();
        LocalDateTime originalExpiresAt = refreshToken.getExpiresAt();

        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn(newAccessToken);
        // Single stubbing that handles both cases:
        when(refreshTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(refreshToken))  // Initial token lookup
                .thenReturn(Optional.empty());          // New token uniqueness check
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        TokenRefreshResponseDTO result = refreshTokenService.refreshToken(tokenValue);

        // Assert
        assertNotNull(result);
        assertNotEquals(originalToken, refreshToken.getToken());
        assertTrue(refreshToken.getExpiresAt().isAfter(originalExpiresAt));
        verify(refreshTokenRepository).save(refreshToken);
    }
}