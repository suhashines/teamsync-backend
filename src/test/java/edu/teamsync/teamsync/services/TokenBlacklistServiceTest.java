package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.entity.BlacklistedToken;
import edu.teamsync.teamsync.repository.BlacklistedTokenRepository;
import edu.teamsync.teamsync.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenBlacklistServiceTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private final String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String emptyToken = "";
    private final String whitespaceToken = "   ";
    private final String nullToken = null;

    private BlacklistedToken blacklistedToken;

    @BeforeEach
    void setUp() {
        blacklistedToken = BlacklistedToken.builder()
                .id(1L)
                .token(validToken)
                .blacklistedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void blacklistToken_Success() {
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class))).thenReturn(blacklistedToken);

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
        verify(blacklistedTokenRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_WithNullToken() {
        tokenBlacklistService.blacklistToken(nullToken);

        verify(blacklistedTokenRepository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_WithEmptyToken() {
        tokenBlacklistService.blacklistToken(emptyToken);

        verify(blacklistedTokenRepository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_WithWhitespaceToken() {
        tokenBlacklistService.blacklistToken(whitespaceToken);

        verify(blacklistedTokenRepository, never()).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_VerifyTokenContent() {
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class))).thenAnswer(invocation -> {
            BlacklistedToken savedToken = invocation.getArgument(0);
            assertEquals(validToken, savedToken.getToken());
            assertNotNull(savedToken.getBlacklistedAt());
            return savedToken;
        });

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
    }

    @Test
    void isTokenBlacklisted_TokenExists_ReturnsTrue() {
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertTrue(result);
        verify(blacklistedTokenRepository).existsByToken(validToken);
        verify(blacklistedTokenRepository, times(1)).existsByToken(validToken);
    }

    @Test
    void isTokenBlacklisted_TokenDoesNotExist_ReturnsFalse() {
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertFalse(result);
        verify(blacklistedTokenRepository).existsByToken(validToken);
        verify(blacklistedTokenRepository, times(1)).existsByToken(validToken);
    }

    @Test
    void isTokenBlacklisted_WithNullToken_ReturnsFalse() {
        when(blacklistedTokenRepository.existsByToken(nullToken)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(nullToken);

        assertFalse(result);
        verify(blacklistedTokenRepository).existsByToken(nullToken);
    }

    @Test
    void isTokenBlacklisted_WithEmptyToken_ReturnsFalse() {
        when(blacklistedTokenRepository.existsByToken(emptyToken)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(emptyToken);

        assertFalse(result);
        verify(blacklistedTokenRepository).existsByToken(emptyToken);
    }

    @Test
    void isTokenBlacklisted_WithWhitespaceToken_ReturnsFalse() {
        when(blacklistedTokenRepository.existsByToken(whitespaceToken)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(whitespaceToken);

        assertFalse(result);
        verify(blacklistedTokenRepository).existsByToken(whitespaceToken);
    }

    @Test
    void blacklistToken_MultipleTokens_Success() {
        String token1 = "token1";
        String token2 = "token2";

        when(blacklistedTokenRepository.save(any(BlacklistedToken.class))).thenReturn(blacklistedToken);

        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);

        verify(blacklistedTokenRepository, times(2)).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_RepositoryThrowsException_ExceptionPropagated() {
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> tokenBlacklistService.blacklistToken(validToken));
        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
    }

    @Test
    void isTokenBlacklisted_RepositoryThrowsException_ExceptionPropagated() {
        when(blacklistedTokenRepository.existsByToken(validToken))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> tokenBlacklistService.isTokenBlacklisted(validToken));
        verify(blacklistedTokenRepository).existsByToken(validToken);
    }
}