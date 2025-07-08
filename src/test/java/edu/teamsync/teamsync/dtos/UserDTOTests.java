package edu.teamsync.teamsync.dtos;

import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("UserCreationDTO Tests")
    class UserCreationDTOTests {

        @Test
        @DisplayName("Should create valid UserCreationDTO with all required fields")
        void createUserCreationDTO_ValidData_ShouldPass() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void createUserCreationDTO_BlankName_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required")));
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void createUserCreationDTO_NullName_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name(null)
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required")));
        }

        @Test
        @DisplayName("Should fail validation when name is whitespace only")
        void createUserCreationDTO_WhitespaceOnlyName_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("   ")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required")));
        }

        @Test
        @DisplayName("Should fail validation when email is blank")
        void createUserCreationDTO_BlankEmail_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("")
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email is null")
        void createUserCreationDTO_NullEmail_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email(null)
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email format is invalid")
        void createUserCreationDTO_InvalidEmailFormat_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("invalid-email")
                    .password("password123")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email must be valid")));
        }

        @Test
        @DisplayName("Should fail validation when password is blank")
        void createUserCreationDTO_BlankPassword_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Password is required")));
        }

        @Test
        @DisplayName("Should fail validation when password is null")
        void createUserCreationDTO_NullPassword_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password(null)
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Password is required")));
        }

        @Test
        @DisplayName("Should fail validation with multiple errors")
        void createUserCreationDTO_MultipleErrors_ShouldFail() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("")
                    .email("invalid-email")
                    .password("")
                    .build();

            Set<ConstraintViolation<UserCreationDTO>> violations = validator.validate(dto);

            assertEquals(3, violations.size());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email must be valid")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Password is required")));
        }

        @Test
        @DisplayName("Should create UserCreationDTO using no-args constructor")
        void createUserCreationDTO_NoArgsConstructor_ShouldWork() {
            UserCreationDTO dto = new UserCreationDTO();
            dto.setName("John Doe");
            dto.setEmail("john.doe@example.com");
            dto.setPassword("password123");

            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());
        }

        @Test
        @DisplayName("Should create UserCreationDTO using all-args constructor")
        void createUserCreationDTO_AllArgsConstructor_ShouldWork() {
            UserCreationDTO dto = new UserCreationDTO("John Doe", "john.doe@example.com", "password123");

            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());
        }

        @Test
        @DisplayName("Should test equals and hashCode methods")
        void createUserCreationDTO_EqualsAndHashCode_ShouldWork() {
            UserCreationDTO dto1 = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            UserCreationDTO dto2 = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            UserCreationDTO dto3 = UserCreationDTO.builder()
                    .name("Jane Doe")
                    .email("jane.doe@example.com")
                    .password("password456")
                    .build();

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void createUserCreationDTO_ToString_ShouldWork() {
            UserCreationDTO dto = UserCreationDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();

            String toString = dto.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("John Doe"));
            assertTrue(toString.contains("john.doe@example.com"));
            assertTrue(toString.contains("password123"));
        }
    }

    @Nested
    @DisplayName("UserResponseDTO Tests")
    class UserResponseDTOTests {

        @Test
        @DisplayName("Should create valid UserResponseDTO with all fields")
        void createUserResponseDTO_ValidData_ShouldPass() {
            LocalDate birthdate = LocalDate.of(1990, 5, 15);
            LocalDate joinDate = LocalDate.of(2020, 1, 10);

            UserResponseDTO dto = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .predictedBurnoutRisk(false)
                    .build();

            assertEquals(1L, dto.getId());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("profile.jpg", dto.getProfilePicture());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals(birthdate, dto.getBirthdate());
            assertEquals(joinDate, dto.getJoinDate());
            assertEquals(false, dto.getPredictedBurnoutRisk());
        }

        @Test
        @DisplayName("Should create UserResponseDTO with null optional fields")
        void createUserResponseDTO_NullOptionalFields_ShouldPass() {
            UserResponseDTO dto = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture(null)
                    .designation(null)
                    .birthdate(null)
                    .joinDate(null)
                    .predictedBurnoutRisk(null)
                    .build();

            assertEquals(1L, dto.getId());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertNull(dto.getProfilePicture());
            assertNull(dto.getDesignation());
            assertNull(dto.getBirthdate());
            assertNull(dto.getJoinDate());
            assertNull(dto.getPredictedBurnoutRisk());
        }

        @Test
        @DisplayName("Should create UserResponseDTO using no-args constructor")
        void createUserResponseDTO_NoArgsConstructor_ShouldWork() {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(1L);
            dto.setName("John Doe");
            dto.setEmail("john.doe@example.com");
            dto.setProfilePicture("profile.jpg");
            dto.setDesignation("Software Engineer");
            dto.setBirthdate(LocalDate.of(1990, 5, 15));
            dto.setJoinDate(LocalDate.of(2020, 1, 10));
            dto.setPredictedBurnoutRisk(true);

            assertEquals(1L, dto.getId());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("profile.jpg", dto.getProfilePicture());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals(LocalDate.of(1990, 5, 15), dto.getBirthdate());
            assertEquals(LocalDate.of(2020, 1, 10), dto.getJoinDate());
            assertTrue(dto.getPredictedBurnoutRisk());
        }

        @Test
        @DisplayName("Should create UserResponseDTO using all-args constructor")
        void createUserResponseDTO_AllArgsConstructor_ShouldWork() {
            LocalDate birthdate = LocalDate.of(1990, 5, 15);
            LocalDate joinDate = LocalDate.of(2020, 1, 10);

            UserResponseDTO dto = new UserResponseDTO(1L, "John Doe", "john.doe@example.com",
                    "profile.jpg", "Software Engineer", birthdate, joinDate, false);

            assertEquals(1L, dto.getId());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("profile.jpg", dto.getProfilePicture());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals(birthdate, dto.getBirthdate());
            assertEquals(joinDate, dto.getJoinDate());
            assertFalse(dto.getPredictedBurnoutRisk());
        }

        @Test
        @DisplayName("Should test equals and hashCode methods")
        void createUserResponseDTO_EqualsAndHashCode_ShouldWork() {
            LocalDate birthdate = LocalDate.of(1990, 5, 15);
            LocalDate joinDate = LocalDate.of(2020, 1, 10);

            UserResponseDTO dto1 = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .predictedBurnoutRisk(false)
                    .build();

            UserResponseDTO dto2 = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .predictedBurnoutRisk(false)
                    .build();

            UserResponseDTO dto3 = UserResponseDTO.builder()
                    .id(2L)
                    .name("Jane Doe")
                    .email("jane.doe@example.com")
                    .profilePicture("profile2.jpg")
                    .designation("Product Manager")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .predictedBurnoutRisk(true)
                    .build();

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void createUserResponseDTO_ToString_ShouldWork() {
            UserResponseDTO dto = UserResponseDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(LocalDate.of(1990, 5, 15))
                    .joinDate(LocalDate.of(2020, 1, 10))
                    .predictedBurnoutRisk(false)
                    .build();

            String toString = dto.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("John Doe"));
            assertTrue(toString.contains("john.doe@example.com"));
            assertTrue(toString.contains("Software Engineer"));
        }
    }

    @Nested
    @DisplayName("UserUpdateDTO Tests")
    class UserUpdateDTOTests {

        @Test
        @DisplayName("Should create valid UserUpdateDTO with all required fields")
        void createUserUpdateDTO_ValidData_ShouldPass() {
            LocalDate birthdate = LocalDate.of(1990, 5, 15);
            LocalDate joinDate = LocalDate.of(2020, 1, 10);

            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("profile.jpg", dto.getProfilePicture());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals(birthdate, dto.getBirthdate());
            assertEquals(joinDate, dto.getJoinDate());
        }

        @Test
        @DisplayName("Should create valid UserUpdateDTO with only required fields")
        void createUserUpdateDTO_OnlyRequiredFields_ShouldPass() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertNull(dto.getProfilePicture());
            assertNull(dto.getDesignation());
            assertNull(dto.getBirthdate());
            assertNull(dto.getJoinDate());
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void createUserUpdateDTO_BlankName_ShouldFail() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("")
                    .email("john.doe@example.com")
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required")));
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void createUserUpdateDTO_NullName_ShouldFail() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name(null)
                    .email("john.doe@example.com")
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required")));
        }

        @Test
        @DisplayName("Should fail validation when email is blank")
        void createUserUpdateDTO_BlankEmail_ShouldFail() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("")
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email is null")
        void createUserUpdateDTO_NullEmail_ShouldFail() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email(null)
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email is required")));
        }

        @Test
        @DisplayName("Should fail validation when email format is invalid")
        void createUserUpdateDTO_InvalidEmailFormat_ShouldFail() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("invalid-email")
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Email must be valid")));
        }

        @Test
        @DisplayName("Should create UserUpdateDTO using no-args constructor")
        void createUserUpdateDTO_NoArgsConstructor_ShouldWork() {
            UserUpdateDTO dto = new UserUpdateDTO();
            dto.setName("John Doe");
            dto.setEmail("john.doe@example.com");
            dto.setProfilePicture("profile.jpg");
            dto.setDesignation("Software Engineer");
            dto.setBirthdate(LocalDate.of(1990, 5, 15));
            dto.setJoinDate(LocalDate.of(2020, 1, 10));

            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("profile.jpg", dto.getProfilePicture());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals(LocalDate.of(1990, 5, 15), dto.getBirthdate());
            assertEquals(LocalDate.of(2020, 1, 10), dto.getJoinDate());
        }

        @Test
        @DisplayName("Should create UserUpdateDTO using all-args constructor")
        void createUserUpdateDTO_AllArgsConstructor_ShouldWork() {
            LocalDate birthdate = LocalDate.of(1990, 5, 15);
            LocalDate joinDate = LocalDate.of(2020, 1, 10);

            UserUpdateDTO dto = new UserUpdateDTO("John Doe", "john.doe@example.com",
                    "profile.jpg", "Software Engineer", birthdate, joinDate);

            assertEquals("John Doe", dto.getName());
            assertEquals("john.doe@example.com", dto.getEmail());
            assertEquals("profile.jpg", dto.getProfilePicture());
            assertEquals("Software Engineer", dto.getDesignation());
            assertEquals(birthdate, dto.getBirthdate());
            assertEquals(joinDate, dto.getJoinDate());
        }

        @Test
        @DisplayName("Should test equals and hashCode methods")
        void createUserUpdateDTO_EqualsAndHashCode_ShouldWork() {
            LocalDate birthdate = LocalDate.of(1990, 5, 15);
            LocalDate joinDate = LocalDate.of(2020, 1, 10);

            UserUpdateDTO dto1 = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .build();

            UserUpdateDTO dto2 = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .build();

            UserUpdateDTO dto3 = UserUpdateDTO.builder()
                    .name("Jane Doe")
                    .email("jane.doe@example.com")
                    .profilePicture("profile2.jpg")
                    .designation("Product Manager")
                    .birthdate(birthdate)
                    .joinDate(joinDate)
                    .build();

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void createUserUpdateDTO_ToString_ShouldWork() {
            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .profilePicture("profile.jpg")
                    .designation("Software Engineer")
                    .birthdate(LocalDate.of(1990, 5, 15))
                    .joinDate(LocalDate.of(2020, 1, 10))
                    .build();

            String toString = dto.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("John Doe"));
            assertTrue(toString.contains("john.doe@example.com"));
            assertTrue(toString.contains("Software Engineer"));
        }

        @Test
        @DisplayName("Should handle edge case dates")
        void createUserUpdateDTO_EdgeCaseDates_ShouldWork() {
            LocalDate futureDate = LocalDate.of(2030, 12, 31);
            LocalDate pastDate = LocalDate.of(1900, 1, 1);

            UserUpdateDTO dto = UserUpdateDTO.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .birthdate(pastDate)
                    .joinDate(futureDate)
                    .build();

            Set<ConstraintViolation<UserUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(pastDate, dto.getBirthdate());
            assertEquals(futureDate, dto.getJoinDate());
        }
    }
}
