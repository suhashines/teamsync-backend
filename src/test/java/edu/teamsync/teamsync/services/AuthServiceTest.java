package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.config.JwtProvider;
import edu.teamsync.teamsync.dto.authDTO.*;
import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.AuthMapper;
import edu.teamsync.teamsync.mapper.UserMapper;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.AuthService;
import edu.teamsync.teamsync.service.PasswordResetService;
import edu.teamsync.teamsync.service.RefreshTokenService;
import edu.teamsync.teamsync.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private final String userEmail = "test@example.com";
    private final String password = "password123";
    private final String encodedPassword = "encodedPassword123";
    private final String jwtToken = "jwt.token.string";
    private final String refreshToken = "refresh.token.string";
    private final Long userId = 1L;

    private Users user;
    private UserCreationDTO userCreationDTO;
    private LoginRequestDTO loginRequestDTO;
    private UserResponseDTO userResponseDTO;
    private AuthResponseDTO authResponseDTO;
    private TokenRefreshResponseDTO tokenRefreshResponseDTO;
    private UserUpdateRequestDTO userUpdateRequestDTO;
    private PasswordChangeRequestDTO passwordChangeRequestDTO;
    private PasswordResetRequestDTO passwordResetRequestDTO;
    private PasswordResetDTO passwordResetDTO;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .password(encodedPassword)
                .name("Test User")
                .designation("Developer")
                .profilePicture("profile.jpg")
                .joinDate(LocalDate.now())
                .build();

        userCreationDTO = new UserCreationDTO();
        userCreationDTO.setEmail(userEmail);
        userCreationDTO.setPassword(password);
        userCreationDTO.setName("Test User");

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(userEmail);
        loginRequestDTO.setPassword(password);

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userId);
        userResponseDTO.setEmail(userEmail);
        userResponseDTO.setName("Test User");
        userResponseDTO.setDesignation("Developer");

        authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setUser(userResponseDTO);
        authResponseDTO.setToken(jwtToken);
        authResponseDTO.setRefreshToken(refreshToken);

        tokenRefreshResponseDTO = new TokenRefreshResponseDTO();
        tokenRefreshResponseDTO.setToken(jwtToken);
        tokenRefreshResponseDTO.setRefreshToken(refreshToken);

        userUpdateRequestDTO = new UserUpdateRequestDTO();
        userUpdateRequestDTO.setName("Updated Name");
        userUpdateRequestDTO.setDesignation("Senior Developer");
        userUpdateRequestDTO.setProfile_picture("updated_profile.jpg");

        passwordChangeRequestDTO = new PasswordChangeRequestDTO();
        passwordChangeRequestDTO.setCurrentPassword(password);
        passwordChangeRequestDTO.setNewPassword("newPassword123");

        passwordResetRequestDTO = new PasswordResetRequestDTO();
        passwordResetRequestDTO.setEmail(userEmail);

        passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setToken("reset-token");
        passwordResetDTO.setNewPassword("newPassword123");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(null);
        when(userMapper.toEntity(userCreationDTO)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UserResponseDTO result = authService.registerUser(userCreationDTO, httpServletRequest);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals(userEmail, result.getEmail());
            verify(userRepository).findByEmail(userEmail);
            verify(userMapper).toEntity(userCreationDTO);
            verify(passwordEncoder).encode(password);
            verify(userRepository).save(any(Users.class));
            verify(userMapper).toResponseDTO(user);
            verify(securityContext).setAuthentication(any(Authentication.class));
        }
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser(userCreationDTO, httpServletRequest));
        verify(userRepository).findByEmail(userEmail);
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(refreshTokenService.createRefreshToken(user, httpServletRequest)).thenReturn(refreshToken);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);
        when(authMapper.toAuthResponseDTO(userResponseDTO, jwtToken, refreshToken)).thenReturn(authResponseDTO);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            AuthResponseDTO result = authService.loginUser(loginRequestDTO, httpServletRequest);

            assertNotNull(result);
            assertEquals(jwtToken, result.getToken());
            assertEquals(refreshToken, result.getRefreshToken());
            verify(userRepository, times(2)).findByEmail(userEmail);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(jwtProvider).generateToken(any(Authentication.class));
            verify(refreshTokenService).createRefreshToken(user, httpServletRequest);
            verify(userMapper).toResponseDTO(user);
            verify(authMapper).toAuthResponseDTO(userResponseDTO, jwtToken, refreshToken);
            verify(securityContext).setAuthentication(any(Authentication.class));
        }
    }

    @Test
    void loginUser_UserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(BadCredentialsException.class,
                () -> authService.loginUser(loginRequestDTO, httpServletRequest));
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void loginUser_InvalidPassword() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.loginUser(loginRequestDTO, httpServletRequest));
        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void loginUser_UserNotFoundAfterAuthentication() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user).thenReturn(null);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(NotFoundException.class,
                    () -> authService.loginUser(loginRequestDTO, httpServletRequest));
            verify(userRepository, times(2)).findByEmail(userEmail);
            verify(passwordEncoder).matches(password, encodedPassword);
        }
    }

    @Test
    void refreshToken_Success() {
        when(refreshTokenService.refreshToken(refreshToken)).thenReturn(tokenRefreshResponseDTO);

        TokenRefreshResponseDTO result = authService.refreshToken(refreshToken);

        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());
        assertEquals(refreshToken, result.getRefreshToken());
        verify(refreshTokenService).refreshToken(refreshToken);
    }

    @Test
    void logout_Success() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            authService.logout(refreshToken, jwtToken);

            verify(tokenBlacklistService).blacklistToken(jwtToken);
            verify(refreshTokenService).revokeToken(refreshToken);
            mockedSecurityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    void logout_WithNullTokens() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            authService.logout(null, null);

            verify(tokenBlacklistService, never()).blacklistToken(anyString());
            verify(refreshTokenService, never()).revokeToken(anyString());
            mockedSecurityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    void logout_WithEmptyTokens() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            authService.logout("", "");

            verify(tokenBlacklistService, never()).blacklistToken(anyString());
            verify(refreshTokenService, never()).revokeToken(anyString());
            mockedSecurityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    void isTokenBlacklisted_Success() {
        when(tokenBlacklistService.isTokenBlacklisted(jwtToken)).thenReturn(true);

        boolean result = authService.isTokenBlacklisted(jwtToken);

        assertTrue(result);
        verify(tokenBlacklistService).isTokenBlacklisted(jwtToken);
    }

    @Test
    void getCurrentUser_Success() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(authMapper.toCurrentUserResponseDTO(user)).thenReturn(userResponseDTO);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            UserResponseDTO result = authService.getCurrentUser();

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals(userEmail, result.getEmail());
            verify(userRepository).findByEmail(userEmail);
            verify(authMapper).toCurrentUserResponseDTO(user);
        }
    }

    @Test
    void getCurrentUser_NotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(SecurityException.class, () -> authService.getCurrentUser());
        }
    }

    @Test
    void getCurrentUser_AnonymousUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(SecurityException.class, () -> authService.getCurrentUser());
        }
    }

    @Test
    void getCurrentUser_UserNotFound() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(NotFoundException.class, () -> authService.getCurrentUser());
            verify(userRepository).findByEmail(userEmail);
        }
    }

    @Test
    void updateCurrentUser_Success() {
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            authService.updateCurrentUser(userUpdateRequestDTO);

            verify(userRepository).findByEmail(userEmail);
            verify(userRepository).save(user);
            assertEquals(userUpdateRequestDTO.getName(), user.getName());
            assertEquals(userUpdateRequestDTO.getDesignation(), user.getDesignation());
            assertEquals(userUpdateRequestDTO.getProfile_picture(), user.getProfilePicture());
        }
    }

    @Test
    void updateCurrentUser_UserNotFound() {
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(NotFoundException.class, () -> authService.updateCurrentUser(userUpdateRequestDTO));
            verify(userRepository).findByEmail(userEmail);
        }
    }

    @Test
    void changePassword_Success() {
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(passwordEncoder.encode(passwordChangeRequestDTO.getNewPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            authService.changePassword(passwordChangeRequestDTO);

            verify(userRepository).findByEmail(userEmail);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(passwordEncoder).encode(passwordChangeRequestDTO.getNewPassword());
            verify(userRepository).save(user);
        }
    }

    @Test
    void changePassword_UserNotFound() {
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(NotFoundException.class, () -> authService.changePassword(passwordChangeRequestDTO));
            verify(userRepository).findByEmail(userEmail);
        }
    }

    @Test
    void changePassword_IncorrectCurrentPassword() {
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(RuntimeException.class, () -> authService.changePassword(passwordChangeRequestDTO));
            verify(userRepository).findByEmail(userEmail);
            verify(passwordEncoder).matches(password, encodedPassword);
        }
    }

    @Test
    void requestPasswordReset_Success() {
        doNothing().when(passwordResetService).requestPasswordReset(passwordResetRequestDTO);

        authService.requestPasswordReset(passwordResetRequestDTO);

        verify(passwordResetService).requestPasswordReset(passwordResetRequestDTO);
    }

    @Test
    void resetPassword_Success() {
        doNothing().when(passwordResetService).resetPassword(passwordResetDTO);

        authService.resetPassword(passwordResetDTO);

        verify(passwordResetService).resetPassword(passwordResetDTO);
    }
}
