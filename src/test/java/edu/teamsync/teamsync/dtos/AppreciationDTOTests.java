package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationCreateDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationResponseDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AppreciationDTOTests {

    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Nested
    @DisplayName("AppreciationCreateDTO Tests")
    class AppreciationCreateDTOTests {

        @Test
        @DisplayName("Valid AppreciationCreateDTO should pass validation")
        void validAppreciationCreateDTO_shouldPassValidation() {
            AppreciationCreateDTO dto = new AppreciationCreateDTO();
            dto.setToUserId(1L);
            dto.setMessage("Great job on the project!");

            Set<ConstraintViolation<AppreciationCreateDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("AppreciationCreateDTO with null toUserId should fail validation")
        void appreciationCreateDTO_withNullToUserId_shouldFailValidation() {
            AppreciationCreateDTO dto = new AppreciationCreateDTO();
            dto.setToUserId(null);
            dto.setMessage("Great job!");

            Set<ConstraintViolation<AppreciationCreateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("To user ID is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationCreateDTO with blank message should fail validation")
        void appreciationCreateDTO_withBlankMessage_shouldFailValidation() {
            AppreciationCreateDTO dto = new AppreciationCreateDTO();
            dto.setToUserId(1L);
            dto.setMessage("");

            Set<ConstraintViolation<AppreciationCreateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Message is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationCreateDTO with null message should fail validation")
        void appreciationCreateDTO_withNullMessage_shouldFailValidation() {
            AppreciationCreateDTO dto = new AppreciationCreateDTO();
            dto.setToUserId(1L);
            dto.setMessage(null);

            Set<ConstraintViolation<AppreciationCreateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Message is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationCreateDTO with whitespace-only message should fail validation")
        void appreciationCreateDTO_withWhitespaceOnlyMessage_shouldFailValidation() {
            AppreciationCreateDTO dto = new AppreciationCreateDTO();
            dto.setToUserId(1L);
            dto.setMessage("   ");

            Set<ConstraintViolation<AppreciationCreateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Message is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationCreateDTO JSON serialization should use snake_case")
        void appreciationCreateDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            AppreciationCreateDTO dto = new AppreciationCreateDTO();
            dto.setToUserId(1L);
            dto.setMessage("Great work!");

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("to_user_id"));
            assertTrue(json.contains("message"));
            assertFalse(json.contains("toUserId"));
        }

        @Test
        @DisplayName("AppreciationCreateDTO JSON deserialization should work with snake_case")
        void appreciationCreateDTO_jsonDeserialization_shouldWorkWithSnakeCase() throws Exception {
            String json = "{\"to_user_id\":1,\"message\":\"Great work!\"}";

            AppreciationCreateDTO dto = objectMapper.readValue(json, AppreciationCreateDTO.class);
            assertEquals(1L, dto.getToUserId());
            assertEquals("Great work!", dto.getMessage());
        }

        @Test
        @DisplayName("AppreciationCreateDTO equals and hashCode should work correctly")
        void appreciationCreateDTO_equalsAndHashCode_shouldWorkCorrectly() {
            AppreciationCreateDTO dto1 = new AppreciationCreateDTO();
            dto1.setToUserId(1L);
            dto1.setMessage("Great work!");

            AppreciationCreateDTO dto2 = new AppreciationCreateDTO();
            dto2.setToUserId(1L);
            dto2.setMessage("Great work!");

            AppreciationCreateDTO dto3 = new AppreciationCreateDTO();
            dto3.setToUserId(2L);
            dto3.setMessage("Great work!");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }
    }

    @Nested
    @DisplayName("AppreciationResponseDTO Tests")
    class AppreciationResponseDTOTests {

        @Test
        @DisplayName("AppreciationResponseDTO should have no validation constraints")
        void appreciationResponseDTO_shouldHaveNoValidationConstraints() {
            AppreciationResponseDTO dto = new AppreciationResponseDTO();
            // All fields can be null for response DTO
            dto.setId(null);
            dto.setParentPostId(null);
            dto.setFromUserId(null);
            dto.setFromUserName(null);
            dto.setToUserId(null);
            dto.setToUserName(null);
            dto.setMessage(null);
            dto.setTimestamp(null);

            Set<ConstraintViolation<AppreciationResponseDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("AppreciationResponseDTO with all fields should work correctly")
        void appreciationResponseDTO_withAllFields_shouldWorkCorrectly() {
            ZonedDateTime now = ZonedDateTime.now();
            AppreciationResponseDTO dto = new AppreciationResponseDTO();
            dto.setId(1L);
            dto.setParentPostId(2L);
            dto.setFromUserId(3L);
            dto.setFromUserName("John Doe");
            dto.setToUserId(4L);
            dto.setToUserName("Jane Smith");
            dto.setMessage("Excellent work!");
            dto.setTimestamp(now);

            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getParentPostId());
            assertEquals(3L, dto.getFromUserId());
            assertEquals("John Doe", dto.getFromUserName());
            assertEquals(4L, dto.getToUserId());
            assertEquals("Jane Smith", dto.getToUserName());
            assertEquals("Excellent work!", dto.getMessage());
            assertEquals(now, dto.getTimestamp());
        }

        @Test
        @DisplayName("AppreciationResponseDTO JSON serialization should use snake_case")
        void appreciationResponseDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            ZonedDateTime now = ZonedDateTime.now();
            AppreciationResponseDTO dto = new AppreciationResponseDTO();
            dto.setId(1L);
            dto.setParentPostId(2L);
            dto.setFromUserId(3L);
            dto.setFromUserName("John Doe");
            dto.setToUserId(4L);
            dto.setToUserName("Jane Smith");
            dto.setMessage("Excellent work!");
            dto.setTimestamp(now);

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("parent_post_id"));
            assertTrue(json.contains("from_user_id"));
            assertTrue(json.contains("from_user_name"));
            assertTrue(json.contains("to_user_id"));
            assertTrue(json.contains("to_user_name"));
            assertFalse(json.contains("parentPostId"));
            assertFalse(json.contains("fromUserId"));
            assertFalse(json.contains("fromUserName"));
            assertFalse(json.contains("toUserId"));
            assertFalse(json.contains("toUserName"));
        }

        @Test
        @DisplayName("AppreciationResponseDTO JSON deserialization should work with snake_case")
        void appreciationResponseDTO_jsonDeserialization_shouldWorkWithSnakeCase() throws Exception {
            String json = """
                {
                    "id": 1,
                    "parent_post_id": 2,
                    "from_user_id": 3,
                    "from_user_name": "John Doe",
                    "to_user_id": 4,
                    "to_user_name": "Jane Smith",
                    "message": "Excellent work!",
                    "timestamp": "2024-01-01T12:00:00Z"
                }
                """;

            AppreciationResponseDTO dto = objectMapper.readValue(json, AppreciationResponseDTO.class);
            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getParentPostId());
            assertEquals(3L, dto.getFromUserId());
            assertEquals("John Doe", dto.getFromUserName());
            assertEquals(4L, dto.getToUserId());
            assertEquals("Jane Smith", dto.getToUserName());
            assertEquals("Excellent work!", dto.getMessage());
            assertNotNull(dto.getTimestamp());
        }

        @Test
        @DisplayName("AppreciationResponseDTO equals and hashCode should work correctly")
        void appreciationResponseDTO_equalsAndHashCode_shouldWorkCorrectly() {
            ZonedDateTime now = ZonedDateTime.now();

            AppreciationResponseDTO dto1 = new AppreciationResponseDTO();
            dto1.setId(1L);
            dto1.setFromUserId(2L);
            dto1.setMessage("Great!");
            dto1.setTimestamp(now);

            AppreciationResponseDTO dto2 = new AppreciationResponseDTO();
            dto2.setId(1L);
            dto2.setFromUserId(2L);
            dto2.setMessage("Great!");
            dto2.setTimestamp(now);

            AppreciationResponseDTO dto3 = new AppreciationResponseDTO();
            dto3.setId(2L);
            dto3.setFromUserId(2L);
            dto3.setMessage("Great!");
            dto3.setTimestamp(now);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }
    }

    @Nested
    @DisplayName("AppreciationUpdateDTO Tests")
    class AppreciationUpdateDTOTests {

        @Test
        @DisplayName("Valid AppreciationUpdateDTO should pass validation")
        void validAppreciationUpdateDTO_shouldPassValidation() {
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(1L);
            dto.setToUserId(2L);
            dto.setMessage("Updated message");
            dto.setTimestamp(ZonedDateTime.now());

            Set<ConstraintViolation<AppreciationUpdateDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO with null fromUserId should fail validation")
        void appreciationUpdateDTO_withNullFromUserId_shouldFailValidation() {
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(null);
            dto.setToUserId(2L);
            dto.setMessage("Updated message");
            dto.setTimestamp(ZonedDateTime.now());

            Set<ConstraintViolation<AppreciationUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("From user ID is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO with null toUserId should fail validation")
        void appreciationUpdateDTO_withNullToUserId_shouldFailValidation() {
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(1L);
            dto.setToUserId(null);
            dto.setMessage("Updated message");
            dto.setTimestamp(ZonedDateTime.now());

            Set<ConstraintViolation<AppreciationUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("To user ID is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO with blank message should fail validation")
        void appreciationUpdateDTO_withBlankMessage_shouldFailValidation() {
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(1L);
            dto.setToUserId(2L);
            dto.setMessage("");
            dto.setTimestamp(ZonedDateTime.now());

            Set<ConstraintViolation<AppreciationUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Message is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO with null timestamp should fail validation")
        void appreciationUpdateDTO_withNullTimestamp_shouldFailValidation() {
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(1L);
            dto.setToUserId(2L);
            dto.setMessage("Updated message");
            dto.setTimestamp(null);

            Set<ConstraintViolation<AppreciationUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Timestamp is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO with multiple validation errors should fail validation")
        void appreciationUpdateDTO_withMultipleValidationErrors_shouldFailValidation() {
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(null);
            dto.setToUserId(null);
            dto.setMessage("");
            dto.setTimestamp(null);

            Set<ConstraintViolation<AppreciationUpdateDTO>> violations = validator.validate(dto);
            assertEquals(4, violations.size());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO JSON serialization should use snake_case")
        void appreciationUpdateDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            ZonedDateTime now = ZonedDateTime.now();
            AppreciationUpdateDTO dto = new AppreciationUpdateDTO();
            dto.setFromUserId(1L);
            dto.setToUserId(2L);
            dto.setMessage("Updated message");
            dto.setTimestamp(now);

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("from_user_id"));
            assertTrue(json.contains("to_user_id"));
            assertFalse(json.contains("fromUserId"));
            assertFalse(json.contains("toUserId"));
        }

        @Test
        @DisplayName("AppreciationUpdateDTO JSON deserialization should work with snake_case")
        void appreciationUpdateDTO_jsonDeserialization_shouldWorkWithSnakeCase() throws Exception {
            String json = """
                {
                    "from_user_id": 1,
                    "to_user_id": 2,
                    "message": "Updated message",
                    "timestamp": "2024-01-01T12:00:00Z"
                }
                """;

            AppreciationUpdateDTO dto = objectMapper.readValue(json, AppreciationUpdateDTO.class);
            assertEquals(1L, dto.getFromUserId());
            assertEquals(2L, dto.getToUserId());
            assertEquals("Updated message", dto.getMessage());
            assertNotNull(dto.getTimestamp());
        }

        @Test
        @DisplayName("AppreciationUpdateDTO equals and hashCode should work correctly")
        void appreciationUpdateDTO_equalsAndHashCode_shouldWorkCorrectly() {
            ZonedDateTime now = ZonedDateTime.now();

            AppreciationUpdateDTO dto1 = new AppreciationUpdateDTO();
            dto1.setFromUserId(1L);
            dto1.setToUserId(2L);
            dto1.setMessage("Updated message");
            dto1.setTimestamp(now);

            AppreciationUpdateDTO dto2 = new AppreciationUpdateDTO();
            dto2.setFromUserId(1L);
            dto2.setToUserId(2L);
            dto2.setMessage("Updated message");
            dto2.setTimestamp(now);

            AppreciationUpdateDTO dto3 = new AppreciationUpdateDTO();
            dto3.setFromUserId(2L);
            dto3.setToUserId(2L);
            dto3.setMessage("Updated message");
            dto3.setTimestamp(now);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1, dto3);
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }
    }

    @Nested
    @DisplayName("Cross-DTO Integration Tests")
    class CrossDTOIntegrationTests {

        @Test
        @DisplayName("All DTOs should serialize/deserialize consistently")
        void allDTOs_shouldSerializeDeserializeConsistently() throws Exception {
            // Test that all DTOs handle JSON consistently
//            ZonedDateTime now = ZonedDateTime.now();

            // Create DTO
            AppreciationCreateDTO createDTO = new AppreciationCreateDTO();
            createDTO.setToUserId(1L);
            createDTO.setMessage("Test message");

            // Update DTO
            AppreciationUpdateDTO updateDTO = new AppreciationUpdateDTO();
            updateDTO.setFromUserId(1L);
            updateDTO.setToUserId(2L);
            updateDTO.setMessage("Updated message");
//            updateDTO.setTimestamp(now);

            // Response DTO
            AppreciationResponseDTO responseDTO = new AppreciationResponseDTO();
            responseDTO.setId(1L);
            responseDTO.setFromUserId(1L);
            responseDTO.setToUserId(2L);
            responseDTO.setMessage("Response message");
//            responseDTO.setTimestamp(now);

            // Serialize all
            String createJson = objectMapper.writeValueAsString(createDTO);
            String updateJson = objectMapper.writeValueAsString(updateDTO);
            String responseJson = objectMapper.writeValueAsString(responseDTO);

            // Deserialize all
            AppreciationCreateDTO deserializedCreate = objectMapper.readValue(createJson, AppreciationCreateDTO.class);
            AppreciationUpdateDTO deserializedUpdate = objectMapper.readValue(updateJson, AppreciationUpdateDTO.class);
            AppreciationResponseDTO deserializedResponse = objectMapper.readValue(responseJson, AppreciationResponseDTO.class);

            // Verify
            assertEquals(createDTO, deserializedCreate);
            assertEquals(updateDTO, deserializedUpdate);
            assertEquals(responseDTO, deserializedResponse);
        }
    }
}