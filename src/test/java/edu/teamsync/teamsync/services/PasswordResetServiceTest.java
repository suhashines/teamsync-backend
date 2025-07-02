package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.authDTO.PasswordResetDTO;
import edu.teamsync.teamsync.dto.authDTO.PasswordResetRequestDTO;
import edu.teamsync.teamsync.entity.PasswordResetToken;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.repository.PasswordResetTokenRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.EmailService;
import edu.teamsync.teamsync.service.PasswordResetService;
import edu.teamsync.teamsync.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private final String userEmail = "test@example.com";
    private final String userName = "Test User";
    private final Long userId = 1L;
    private final String validToken = "validToken123";
    private final String newPassword = "newPassword123";
    private final String encodedPassword = "encodedPassword123";

    private Users user;
    private PasswordResetRequestDTO passwordResetRequestDTO;
    private PasswordResetDTO passwordResetDTO;
    private PasswordResetToken passwordResetToken;
    private PasswordResetToken expiredToken;

    @BeforeEach
    void setUp() {
        // Set the password reset expiration value
        ReflectionTestUtils.setField(passwordResetService, "passwordResetExpiration", 3600L);

        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .name(userName)
                .password("oldPassword")
                .build();

        passwordResetRequestDTO = new PasswordResetRequestDTO();
        passwordResetRequestDTO.setEmail(userEmail);

        passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setToken(validToken);
        passwordResetDTO.setNewPassword(newPassword);

        passwordResetToken = PasswordResetToken.builder()
                .id(1L)
                .token(validToken)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .used(false)
                .build();

        expiredToken = PasswordResetToken.builder()
                .id(2L)
                .token("expiredToken")
                .user(user)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .createdAt(LocalDateTime.now().minusHours(2))
                .used(false)
                .build();
    }

    @Test
    void requestPasswordReset_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordResetTokenRepository.countValidTokensByUser(any(Users.class), any(LocalDateTime.class))).thenReturn(0);
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        doNothing().when(passwordResetTokenRepository).markAllTokensAsUsedByUser(user);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);

        passwordResetService.requestPasswordReset(passwordResetRequestDTO);

        verify(userRepository).findByEmail(userEmail);
        verify(passwordResetTokenRepository).countValidTokensByUser(any(Users.class), any(LocalDateTime.class));
        verify(passwordResetTokenRepository).markAllTokensAsUsedByUser(user);
        verify(emailService).sendPasswordResetEmail(eq(userEmail), anyString(), eq(userName));
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void requestPasswordReset_UserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        passwordResetService.requestPasswordReset(passwordResetRequestDTO);

        verify(userRepository).findByEmail(userEmail);
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestPasswordReset_RateLimited() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordResetTokenRepository.countValidTokensByUser(any(Users.class), any(LocalDateTime.class))).thenReturn(3);

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.requestPasswordReset(passwordResetRequestDTO));
        verify(userRepository).findByEmail(userEmail);
        verify(passwordResetTokenRepository).countValidTokensByUser(any(Users.class), any(LocalDateTime.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestPasswordReset_EmailServiceFails() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordResetTokenRepository.countValidTokensByUser(any(Users.class), any(LocalDateTime.class))).thenReturn(0);
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        doNothing().when(passwordResetTokenRepository).markAllTokensAsUsedByUser(user);
        doThrow(new RuntimeException("Email service error")).when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.requestPasswordReset(passwordResetRequestDTO));
        verify(userRepository).findByEmail(userEmail);
        verify(emailService).sendPasswordResetEmail(eq(userEmail), anyString(), eq(userName));
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_Success() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(validToken)).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(refreshTokenService).revokeAllUserTokens(user);

        passwordResetService.resetPassword(passwordResetDTO);

        verify(passwordResetTokenRepository).findByTokenAndUsedFalse(validToken);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(passwordResetToken);
        verify(refreshTokenService).revokeAllUserTokens(user);
        assertEquals(encodedPassword, user.getPassword());
        assertTrue(passwordResetToken.getUsed());
    }

    @Test
    void resetPassword_TokenNotFound() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(validToken)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.resetPassword(passwordResetDTO));
        verify(passwordResetTokenRepository).findByTokenAndUsedFalse(validToken);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void resetPassword_ExpiredToken() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(expiredToken.getToken())).thenReturn(Optional.of(expiredToken));

        PasswordResetDTO expiredTokenDTO = new PasswordResetDTO();
        expiredTokenDTO.setToken(expiredToken.getToken());
        expiredTokenDTO.setNewPassword(newPassword);

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.resetPassword(expiredTokenDTO));
        verify(passwordResetTokenRepository).findByTokenAndUsedFalse(expiredToken.getToken());
        verify(passwordResetTokenRepository).delete(expiredToken);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void resetPassword_UsedToken() {
        PasswordResetToken usedToken = PasswordResetToken.builder()
                .id(3L)
                .token("usedToken")
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .used(true)
                .build();

        when(passwordResetTokenRepository.findByTokenAndUsedFalse(usedToken.getToken())).thenReturn(Optional.of(usedToken));

        PasswordResetDTO usedTokenDTO = new PasswordResetDTO();
        usedTokenDTO.setToken(usedToken.getToken());
        usedTokenDTO.setNewPassword(newPassword);

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.resetPassword(usedTokenDTO));
        verify(passwordResetTokenRepository).findByTokenAndUsedFalse(usedToken.getToken());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void cleanupExpiredTokens_Success() {
        doNothing().when(passwordResetTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));

        passwordResetService.cleanupExpiredTokens();

        verify(passwordResetTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void requestPasswordReset_UniqueTokenGeneration() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordResetTokenRepository.countValidTokensByUser(any(Users.class), any(LocalDateTime.class))).thenReturn(0);
        when(passwordResetTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(passwordResetToken)) // First call returns existing token
                .thenReturn(Optional.empty()); // Second call returns empty (unique token)
        doNothing().when(passwordResetTokenRepository).markAllTokensAsUsedByUser(user);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);

        passwordResetService.requestPasswordReset(passwordResetRequestDTO);

        verify(passwordResetTokenRepository, times(2)).findByToken(anyString());
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }


@Test
void requestPasswordReset_MultipleUsers_Success() {
    Users user2 = Users.builder()
            .id(2L)
            .email("user2@example.com")
            .name("User Two")
            .build();
    PasswordResetRequestDTO request2 = new PasswordResetRequestDTO();
    request2.setEmail("user2@example.com");

    // First user request
    when(userRepository.findByEmail(userEmail)).thenReturn(user);
    when(passwordResetTokenRepository.countValidTokensByUser(eq(user), any(LocalDateTime.class))).thenReturn(0);
    when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
    doNothing().when(passwordResetTokenRepository).markAllTokensAsUsedByUser(user);
    doNothing().when(emailService).sendPasswordResetEmail(eq(userEmail), anyString(), eq(userName));
    when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);

    // Second user request
    when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
    when(passwordResetTokenRepository.countValidTokensByUser(eq(user2), any(LocalDateTime.class))).thenReturn(0);
    doNothing().when(passwordResetTokenRepository).markAllTokensAsUsedByUser(user2);
    doNothing().when(emailService).sendPasswordResetEmail(eq("user2@example.com"), anyString(), eq("User Two"));

    passwordResetService.requestPasswordReset(passwordResetRequestDTO);
    passwordResetService.requestPasswordReset(request2);

    verify(passwordResetTokenRepository, times(2)).save(any(PasswordResetToken.class));
    verify(emailService).sendPasswordResetEmail(eq(userEmail), anyString(), eq(userName));
    verify(emailService).sendPasswordResetEmail(eq("user2@example.com"), anyString(), eq("User Two"));
}

    @Test
    void resetPassword_VerifyPasswordEncoding() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(validToken)).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            assertEquals(encodedPassword, savedUser.getPassword());
            return savedUser;
        });
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(refreshTokenService).revokeAllUserTokens(user);

        passwordResetService.resetPassword(passwordResetDTO);

        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
        assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    void resetPassword_VerifyTokenMarkedAsUsed() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(validToken)).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> {
            PasswordResetToken savedToken = invocation.getArgument(0);
            assertTrue(savedToken.getUsed());
            return savedToken;
        });
        doNothing().when(refreshTokenService).revokeAllUserTokens(user);

        passwordResetService.resetPassword(passwordResetDTO);

        verify(passwordResetTokenRepository).save(passwordResetToken);
        assertTrue(passwordResetToken.getUsed());
    }

    @Test
    void resetPassword_VerifyRefreshTokensRevoked() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(validToken)).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(refreshTokenService).revokeAllUserTokens(user);

        passwordResetService.resetPassword(passwordResetDTO);

        verify(refreshTokenService).revokeAllUserTokens(user);
    }
}