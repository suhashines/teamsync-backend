package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.AuthController;
import edu.teamsync.teamsync.dto.authDTO.*;
import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreationDTO userCreationDTO;
    private UserResponseDTO userResponseDTO;
    private LoginRequestDTO loginRequestDTO;
    private AuthResponseDTO authResponseDTO;
    private RefreshTokenRequestDTO refreshTokenRequestDTO;
    private TokenRefreshResponseDTO tokenRefreshResponseDTO;
    private UserUpdateRequestDTO userUpdateRequestDTO;
    private PasswordChangeRequestDTO passwordChangeRequestDTO;
    private PasswordResetRequestDTO passwordResetRequestDTO;
    private PasswordResetDTO passwordResetDTO;

    @BeforeEach
    void setup() {
        // Setup UserCreationDTO
        userCreationDTO = new UserCreationDTO();
        userCreationDTO.setName("John Doe");
        userCreationDTO.setEmail("john.doe@example.com");
        userCreationDTO.setPassword("password123");

        // Setup UserResponseDTO
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John Doe");
        userResponseDTO.setEmail("john.doe@example.com");
        userResponseDTO.setDesignation("Software Engineer");

        // Setup LoginRequestDTO
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("john.doe@example.com");
        loginRequestDTO.setPassword("password123");

        // Setup AuthResponseDTO
        authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setUser(userResponseDTO);
        authResponseDTO.setToken("access-token");
        authResponseDTO.setRefreshToken("refresh-token");

        // Setup RefreshTokenRequestDTO
        refreshTokenRequestDTO = new RefreshTokenRequestDTO();
        refreshTokenRequestDTO.setRefreshToken("refresh-token");

        // Setup TokenRefreshResponseDTO
        tokenRefreshResponseDTO = new TokenRefreshResponseDTO();
        tokenRefreshResponseDTO.setToken("new-access-token");
        tokenRefreshResponseDTO.setRefreshToken("new-refresh-token");

        // Setup UserUpdateRequestDTO
        userUpdateRequestDTO = new UserUpdateRequestDTO();
        userUpdateRequestDTO.setName("John Updated");
        userUpdateRequestDTO.setDesignation("Senior Software Engineer");
        userUpdateRequestDTO.setProfile_picture("profile.jpg");

        // Setup PasswordChangeRequestDTO
        passwordChangeRequestDTO = new PasswordChangeRequestDTO();
        passwordChangeRequestDTO.setCurrentPassword("oldPassword");
        passwordChangeRequestDTO.setNewPassword("newPassword123");

        // Setup PasswordResetRequestDTO
        passwordResetRequestDTO = new PasswordResetRequestDTO();
        passwordResetRequestDTO.setEmail("john.doe@example.com");

        // Setup PasswordResetDTO
        passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setToken("reset-token");
        passwordResetDTO.setNewPassword("newPassword123");
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_ValidData_ReturnsCreatedResponse() throws Exception {
        when(authService.registerUser(any(UserCreationDTO.class), any())).thenReturn(userResponseDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.designation").value("Software Engineer"));

        verify(authService, times(1)).registerUser(any(UserCreationDTO.class), any());
    }

    @Test
    @DisplayName("Should return bad request when registering user with invalid data")
    void registerUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserCreationDTO invalidDTO = new UserCreationDTO();
        // Missing required fields

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(any(UserCreationDTO.class), any());
    }

    @Test
    @DisplayName("Should return bad request when registering user with invalid email")
    void registerUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserCreationDTO invalidDTO = new UserCreationDTO();
        invalidDTO.setName("John Doe");
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(any(UserCreationDTO.class), any());
    }

    @Test
    @DisplayName("Should login user successfully")
    void loginUser_ValidCredentials_ReturnsSuccessResponse() throws Exception {
        when(authService.loginUser(any(LoginRequestDTO.class), any())).thenReturn(authResponseDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User logged in successfully"))
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.name").value("John Doe"))
                .andExpect(jsonPath("$.data.token").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));

        verify(authService, times(1)).loginUser(any(LoginRequestDTO.class), any());
    }

    @Test
    @DisplayName("Should return bad request when login with invalid data")
    void loginUser_InvalidData_ReturnsBadRequest() throws Exception {
        LoginRequestDTO invalidDTO = new LoginRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).loginUser(any(LoginRequestDTO.class), any());
    }

    @Test
    @DisplayName("Should return bad request when login with invalid email format")
    void loginUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        LoginRequestDTO invalidDTO = new LoginRequestDTO();
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).loginUser(any(LoginRequestDTO.class), any());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refreshToken_ValidToken_ReturnsSuccessResponse() throws Exception {
        when(authService.refreshToken(anyString())).thenReturn(tokenRefreshResponseDTO);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.token").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));

        verify(authService, times(1)).refreshToken(eq("refresh-token"));
    }

    @Test
    @DisplayName("Should return bad request when refresh token with invalid data")
    void refreshToken_InvalidData_ReturnsBadRequest() throws Exception {
        RefreshTokenRequestDTO invalidDTO = new RefreshTokenRequestDTO();
        // Missing refresh token

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).refreshToken(anyString());
    }

    @Test
    @DisplayName("Should logout user successfully")
    void logout_ValidTokens_ReturnsSuccessResponse() throws Exception {
        doNothing().when(authService).logout(anyString(), anyString());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer jwt-token")
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User logged out successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).logout(eq("refresh-token"), eq("jwt-token"));
    }

    @Test
    @DisplayName("Should logout user successfully without Authorization header")
    void logout_NoAuthHeader_ReturnsSuccessResponse() throws Exception {
        doNothing().when(authService).logout(anyString(), anyString());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User logged out successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).logout(eq("refresh-token"), eq(null));
    }

    @Test
    @DisplayName("Should get current user successfully")
    void getCurrentUser_ValidRequest_ReturnsSuccessResponse() throws Exception {
        when(authService.getCurrentUser()).thenReturn(userResponseDTO);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Current user retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.designation").value("Software Engineer"));

        verify(authService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("Should update current user successfully")
    void updateCurrentUser_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(authService).updateCurrentUser(any(UserUpdateRequestDTO.class));

        mockMvc.perform(post("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Current User updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).updateCurrentUser(any(UserUpdateRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating current user with invalid data")
    void updateCurrentUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserUpdateRequestDTO invalidDTO = new UserUpdateRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).updateCurrentUser(any(UserUpdateRequestDTO.class));
    }

    @Test
    @DisplayName("Should change password successfully")
    void changePassword_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(authService).changePassword(any(PasswordChangeRequestDTO.class));

        mockMvc.perform(post("/auth/password-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when changing password with invalid data")
    void changePassword_InvalidData_ReturnsBadRequest() throws Exception {
        PasswordChangeRequestDTO invalidDTO = new PasswordChangeRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/auth/password-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).changePassword(any(PasswordChangeRequestDTO.class));
    }

    @Test
    @DisplayName("Should request password reset successfully")
    void requestPasswordReset_ValidEmail_ReturnsSuccessResponse() throws Exception {
        doNothing().when(authService).requestPasswordReset(any(PasswordResetRequestDTO.class));

        mockMvc.perform(post("/auth/password-reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("If the email exists, a password reset link has been sent"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).requestPasswordReset(any(PasswordResetRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when requesting password reset with invalid email")
    void requestPasswordReset_InvalidEmail_ReturnsBadRequest() throws Exception {
        PasswordResetRequestDTO invalidDTO = new PasswordResetRequestDTO();
        invalidDTO.setEmail("invalid-email");

        mockMvc.perform(post("/auth/password-reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).requestPasswordReset(any(PasswordResetRequestDTO.class));
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(authService).resetPassword(any(PasswordResetDTO.class));

        mockMvc.perform(post("/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Password reset successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authService, times(1)).resetPassword(any(PasswordResetDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when resetting password with invalid data")
    void resetPassword_InvalidData_ReturnsBadRequest() throws Exception {
        PasswordResetDTO invalidDTO = new PasswordResetDTO();
        // Missing required fields

        mockMvc.perform(post("/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).resetPassword(any(PasswordResetDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when resetting password with short password")
    void resetPassword_ShortPassword_ReturnsBadRequest() throws Exception {
        PasswordResetDTO invalidDTO = new PasswordResetDTO();
        invalidDTO.setToken("reset-token");
        invalidDTO.setNewPassword("short");

        mockMvc.perform(post("/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).resetPassword(any(PasswordResetDTO.class));
    }

    @Test
    @DisplayName("Should handle missing request body for register")
    void registerUser_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for login")
    void loginUser_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for refresh token")
    void refreshToken_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for update current user")
    void updateCurrentUser_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for register")
    void registerUser_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for login")
    void loginUser_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void registerUser_ServiceException_ReturnsErrorResponse() throws Exception {
        when(authService.registerUser(any(UserCreationDTO.class), any())).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exceptions for login")
    void loginUser_ServiceException_ReturnsErrorResponse() throws Exception {
        when(authService.loginUser(any(LoginRequestDTO.class), any())).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exceptions for getCurrentUser")
    void getCurrentUser_ServiceException_ReturnsErrorResponse() throws Exception {
        when(authService.getCurrentUser()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isInternalServerError());
    }
}