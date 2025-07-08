package edu.teamsync.teamsync.dtos;

import edu.teamsync.teamsync.dto.authDTO.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("LoginRequestDTO Tests")
    class LoginRequestDTOTests {

        @Test
        @DisplayName("Should create LoginRequestDTO with valid data")
        void createLoginRequestDTO_ValidData_Success() {
            LoginRequestDTO dto = LoginRequestDTO.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            assertEquals("test@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when email is blank")
        void createLoginRequestDTO_BlankEmail_ValidationFails() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("");
            dto.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email is null")
        void createLoginRequestDTO_NullEmail_ValidationFails() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail(null);
            dto.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email format is invalid")
        void createLoginRequestDTO_InvalidEmailFormat_ValidationFails() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("invalid-email");
            dto.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")));
        }

        @Test
        @DisplayName("Should fail validation when password is blank")
        void createLoginRequestDTO_BlankPassword_ValidationFails() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("test@example.com");
            dto.setPassword("");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
        }

        @Test
        @DisplayName("Should fail validation when password is null")
        void createLoginRequestDTO_NullPassword_ValidationFails() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("test@example.com");
            dto.setPassword(null);

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
        }

        @Test
        @DisplayName("Should create LoginRequestDTO using no-args constructor")
        void createLoginRequestDTO_NoArgsConstructor_Success() {
            LoginRequestDTO dto = new LoginRequestDTO();
            assertNotNull(dto);
            assertNull(dto.getEmail());
            assertNull(dto.getPassword());
        }

        @Test
        @DisplayName("Should create LoginRequestDTO using all-args constructor")
        void createLoginRequestDTO_AllArgsConstructor_Success() {
            LoginRequestDTO dto = new LoginRequestDTO("test@example.com", "password123");
            assertEquals("test@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());
        }

        @Test
        @DisplayName("Should test equals and hashCode")
        void testEqualsAndHashCode() {
            LoginRequestDTO dto1 = new LoginRequestDTO("test@example.com", "password123");
            LoginRequestDTO dto2 = new LoginRequestDTO("test@example.com", "password123");
            LoginRequestDTO dto3 = new LoginRequestDTO("different@example.com", "password123");

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void testToString() {
            LoginRequestDTO dto = new LoginRequestDTO("test@example.com", "password123");
            String toString = dto.toString();
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("password123"));
        }
    }

    @Nested
    @DisplayName("RefreshTokenRequestDTO Tests")
    class RefreshTokenRequestDTOTests {

        @Test
        @DisplayName("Should create RefreshTokenRequestDTO with valid data")
        void createRefreshTokenRequestDTO_ValidData_Success() {
            RefreshTokenRequestDTO dto = RefreshTokenRequestDTO.builder()
                    .refreshToken("valid-refresh-token")
                    .build();

            assertEquals("valid-refresh-token", dto.getRefreshToken());

            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when refresh token is blank")
        void createRefreshTokenRequestDTO_BlankToken_ValidationFails() {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken("");

            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Refresh token is required")));
        }

        @Test
        @DisplayName("Should fail validation when refresh token is null")
        void createRefreshTokenRequestDTO_NullToken_ValidationFails() {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken(null);

            Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Refresh token is required")));
        }

        @Test
        @DisplayName("Should create RefreshTokenRequestDTO using constructors")
        void createRefreshTokenRequestDTO_Constructors_Success() {
            RefreshTokenRequestDTO dto1 = new RefreshTokenRequestDTO();
            RefreshTokenRequestDTO dto2 = new RefreshTokenRequestDTO("token");

            assertNotNull(dto1);
            assertNull(dto1.getRefreshToken());
            assertEquals("token", dto2.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("PasswordChangeRequestDTO Tests")
    class PasswordChangeRequestDTOTests {

        @Test
        @DisplayName("Should create PasswordChangeRequestDTO with valid data")
        void createPasswordChangeRequestDTO_ValidData_Success() {
            PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO();
            dto.setCurrentPassword("oldPassword");
            dto.setNewPassword("newPassword123");

            assertEquals("oldPassword", dto.getCurrentPassword());
            assertEquals("newPassword123", dto.getNewPassword());

            Set<ConstraintViolation<PasswordChangeRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when current password is blank")
        void createPasswordChangeRequestDTO_BlankCurrentPassword_ValidationFails() {
            PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO();
            dto.setCurrentPassword("");
            dto.setNewPassword("newPassword123");

            Set<ConstraintViolation<PasswordChangeRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
        }

        @Test
        @DisplayName("Should fail validation when new password is blank")
        void createPasswordChangeRequestDTO_BlankNewPassword_ValidationFails() {
            PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO();
            dto.setCurrentPassword("oldPassword");
            dto.setNewPassword("");

            Set<ConstraintViolation<PasswordChangeRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New Password is required")));
        }

        @Test
        @DisplayName("Should fail validation when both passwords are null")
        void createPasswordChangeRequestDTO_NullPasswords_ValidationFails() {
            PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO();
            dto.setCurrentPassword(null);
            dto.setNewPassword(null);

            Set<ConstraintViolation<PasswordChangeRequestDTO>> violations = validator.validate(dto);
            assertEquals(2, violations.size());
        }
    }

    @Nested
    @DisplayName("PasswordResetRequestDTO Tests")
    class PasswordResetRequestDTOTests {

        @Test
        @DisplayName("Should create PasswordResetRequestDTO with valid data")
        void createPasswordResetRequestDTO_ValidData_Success() {
            PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
            dto.setEmail("test@example.com");

            assertEquals("test@example.com", dto.getEmail());

            Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when email is blank")
        void createPasswordResetRequestDTO_BlankEmail_ValidationFails() {
            PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
            dto.setEmail("");

            Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email format is invalid")
        void createPasswordResetRequestDTO_InvalidEmailFormat_ValidationFails() {
            PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
            dto.setEmail("invalid-email");

            Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Please provide a valid email address")));
        }

        @Test
        @DisplayName("Should fail validation when email is null")
        void createPasswordResetRequestDTO_NullEmail_ValidationFails() {
            PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
            dto.setEmail(null);

            Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
        }
    }

    @Nested
    @DisplayName("PasswordResetDTO Tests")
    class PasswordResetDTOTests {

        @Test
        @DisplayName("Should create PasswordResetDTO with valid data")
        void createPasswordResetDTO_ValidData_Success() {
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setToken("valid-token");
            dto.setNewPassword("newPassword123");

            assertEquals("valid-token", dto.getToken());
            assertEquals("newPassword123", dto.getNewPassword());

            Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when token is blank")
        void createPasswordResetDTO_BlankToken_ValidationFails() {
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setToken("");
            dto.setNewPassword("newPassword123");

            Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Token is required")));
        }

        @Test
        @DisplayName("Should fail validation when new password is blank")
        void createPasswordResetDTO_BlankPassword_ValidationFails() {
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setToken("valid-token");
            dto.setNewPassword("");

            Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
            assertTrue(violations.size() >= 1); // At least one violation
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("New password is required")));
        }

        @Test
        @DisplayName("Should fail validation when password is too short")
        void createPasswordResetDTO_ShortPassword_ValidationFails() {
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setToken("valid-token");
            dto.setNewPassword("short");

            Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 8 characters long")));
        }

        @Test
        @DisplayName("Should pass validation when password is exactly 8 characters")
        void createPasswordResetDTO_EightCharPassword_ValidationPasses() {
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setToken("valid-token");
            dto.setNewPassword("12345678");

            Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when both fields are null")
        void createPasswordResetDTO_NullFields_ValidationFails() {
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setToken(null);
            dto.setNewPassword(null);

            Set<ConstraintViolation<PasswordResetDTO>> violations = validator.validate(dto);
            assertEquals(2, violations.size());
        }
    }

    @Nested
    @DisplayName("UserUpdateRequestDTO Tests")
    class UserUpdateRequestDTOTests {

        @Test
        @DisplayName("Should create UserUpdateRequestDTO with valid data")
        void createUserUpdateRequestDTO_ValidData_Success() {
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
            dto.setName("John Doe");
            dto.setDesignation("Software Engineer");
            dto.setProfile_picture("profile.jpg");

            assertEquals("John Doe", dto.getName());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals("profile.jpg", dto.getProfile_picture());

            Set<ConstraintViolation<UserUpdateRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void createUserUpdateRequestDTO_BlankName_ValidationFails() {
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
            dto.setName("");
            dto.setDesignation("Software Engineer");

            Set<ConstraintViolation<UserUpdateRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
        }

        @Test
        @DisplayName("Should fail validation when designation is blank")
        void createUserUpdateRequestDTO_BlankDesignation_ValidationFails() {
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
            dto.setName("John Doe");
            dto.setDesignation("");

            Set<ConstraintViolation<UserUpdateRequestDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Designation is required")));
        }

        @Test
        @DisplayName("Should pass validation when profile picture is null")
        void createUserUpdateRequestDTO_NullProfilePicture_ValidationPasses() {
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
            dto.setName("John Doe");
            dto.setDesignation("Software Engineer");
            dto.setProfile_picture(null);

            Set<ConstraintViolation<UserUpdateRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when both required fields are null")
        void createUserUpdateRequestDTO_NullRequiredFields_ValidationFails() {
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
            dto.setName(null);
            dto.setDesignation(null);

            Set<ConstraintViolation<UserUpdateRequestDTO>> violations = validator.validate(dto);
            assertEquals(2, violations.size());
        }
    }

    @Nested
    @DisplayName("AuthResponseDTO Tests")
    class AuthResponseDTOTests {

        @Test
        @DisplayName("Should create AuthResponseDTO with valid data")
        void createAuthResponseDTO_ValidData_Success() {
            // Create a mock UserResponseDTO
            edu.teamsync.teamsync.dto.userDTO.UserResponseDTO user = new edu.teamsync.teamsync.dto.userDTO.UserResponseDTO();
            user.setId(1L);
            user.setName("John Doe");
            user.setEmail("john@example.com");

            AuthResponseDTO dto = AuthResponseDTO.builder()
                    .user(user)
                    .token("access-token")
                    .refreshToken("refresh-token")
                    .build();

            assertEquals(user, dto.getUser());
            assertEquals("access-token", dto.getToken());
            assertEquals("refresh-token", dto.getRefreshToken());
        }

        @Test
        @DisplayName("Should create AuthResponseDTO using no-args constructor")
        void createAuthResponseDTO_NoArgsConstructor_Success() {
            AuthResponseDTO dto = new AuthResponseDTO();
            assertNotNull(dto);
            assertNull(dto.getUser());
            assertNull(dto.getToken());
            assertNull(dto.getRefreshToken());
        }

        @Test
        @DisplayName("Should create AuthResponseDTO using all-args constructor")
        void createAuthResponseDTO_AllArgsConstructor_Success() {
            edu.teamsync.teamsync.dto.userDTO.UserResponseDTO user = new edu.teamsync.teamsync.dto.userDTO.UserResponseDTO();
            AuthResponseDTO dto = new AuthResponseDTO(user, "token", "refresh");

            assertEquals(user, dto.getUser());
            assertEquals("token", dto.getToken());
            assertEquals("refresh", dto.getRefreshToken());
        }

        @Test
        @DisplayName("Should test equals and hashCode")
        void testEqualsAndHashCode() {
            edu.teamsync.teamsync.dto.userDTO.UserResponseDTO user = new edu.teamsync.teamsync.dto.userDTO.UserResponseDTO();
            AuthResponseDTO dto1 = new AuthResponseDTO(user, "token", "refresh");
            AuthResponseDTO dto2 = new AuthResponseDTO(user, "token", "refresh");
            AuthResponseDTO dto3 = new AuthResponseDTO(user, "different", "refresh");

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("TokenRefreshResponseDTO Tests")
    class TokenRefreshResponseDTOTests {

        @Test
        @DisplayName("Should create TokenRefreshResponseDTO with valid data")
        void createTokenRefreshResponseDTO_ValidData_Success() {
            TokenRefreshResponseDTO dto = TokenRefreshResponseDTO.builder()
                    .token("new-access-token")
                    .refreshToken("new-refresh-token")
                    .build();

            assertEquals("new-access-token", dto.getToken());
            assertEquals("new-refresh-token", dto.getRefreshToken());
        }

        @Test
        @DisplayName("Should create TokenRefreshResponseDTO using constructors")
        void createTokenRefreshResponseDTO_Constructors_Success() {
            TokenRefreshResponseDTO dto1 = new TokenRefreshResponseDTO();
            TokenRefreshResponseDTO dto2 = new TokenRefreshResponseDTO("token", "refresh");

            assertNotNull(dto1);
            assertNull(dto1.getToken());
            assertNull(dto1.getRefreshToken());

            assertEquals("token", dto2.getToken());
            assertEquals("refresh", dto2.getRefreshToken());
        }

        @Test
        @DisplayName("Should test setters and getters")
        void testSettersAndGetters() {
            TokenRefreshResponseDTO dto = new TokenRefreshResponseDTO();
            dto.setToken("access-token");
            dto.setRefreshToken("refresh-token");

            assertEquals("access-token", dto.getToken());
            assertEquals("refresh-token", dto.getRefreshToken());
        }

        @Test
        @DisplayName("Should test equals and hashCode")
        void testEqualsAndHashCode() {
            TokenRefreshResponseDTO dto1 = new TokenRefreshResponseDTO("token", "refresh");
            TokenRefreshResponseDTO dto2 = new TokenRefreshResponseDTO("token", "refresh");
            TokenRefreshResponseDTO dto3 = new TokenRefreshResponseDTO("different", "refresh");

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("Cross-DTO Integration Tests")
    class CrossDTOIntegrationTests {

        @Test
        @DisplayName("Should validate multiple DTOs with combined constraints")
        void validateMultipleDTOs_CombinedConstraints_Success() {
            // Test a typical authentication flow
            LoginRequestDTO loginDTO = new LoginRequestDTO("user@example.com", "password123");
            RefreshTokenRequestDTO refreshDTO = new RefreshTokenRequestDTO("refresh-token");
            PasswordChangeRequestDTO passwordChangeDTO = new PasswordChangeRequestDTO();
            passwordChangeDTO.setCurrentPassword("oldPassword");
            passwordChangeDTO.setNewPassword("newPassword123");

            Set<ConstraintViolation<LoginRequestDTO>> loginViolations = validator.validate(loginDTO);
            Set<ConstraintViolation<RefreshTokenRequestDTO>> refreshViolations = validator.validate(refreshDTO);
            Set<ConstraintViolation<PasswordChangeRequestDTO>> passwordViolations = validator.validate(passwordChangeDTO);

            assertTrue(loginViolations.isEmpty());
            assertTrue(refreshViolations.isEmpty());
            assertTrue(passwordViolations.isEmpty());
        }

        @Test
        @DisplayName("Should handle null values across all DTOs")
        void handleNullValues_AllDTOs_ValidationFails() {
            LoginRequestDTO loginDTO = new LoginRequestDTO(null, null);
            RefreshTokenRequestDTO refreshDTO = new RefreshTokenRequestDTO(null);
            PasswordChangeRequestDTO passwordChangeDTO = new PasswordChangeRequestDTO();
            passwordChangeDTO.setCurrentPassword(null);
            passwordChangeDTO.setNewPassword(null);

            Set<ConstraintViolation<LoginRequestDTO>> loginViolations = validator.validate(loginDTO);
            Set<ConstraintViolation<RefreshTokenRequestDTO>> refreshViolations = validator.validate(refreshDTO);
            Set<ConstraintViolation<PasswordChangeRequestDTO>> passwordViolations = validator.validate(passwordChangeDTO);

            assertFalse(loginViolations.isEmpty());
            assertFalse(refreshViolations.isEmpty());
            assertFalse(passwordViolations.isEmpty());
        }
    }
}