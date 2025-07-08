package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.teamsync.teamsync.dto.messageDTO.MessageCreationDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageResponseDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageUpdateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MessageDTOTests {

    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Nested
    @DisplayName("MessageCreationDTO Tests")
    class MessageCreationDTOTest {

        @Test
        @DisplayName("Should create valid MessageCreationDTO")
        void shouldCreateValidMessageCreationDTO() {
            // Given
            String content = "Hello, World!";
            Long channelId = 1L;
            Long recipientId = 2L;
            Long threadParentId = 3L;

            // When
            MessageCreationDTO dto = new MessageCreationDTO(content, channelId, recipientId, threadParentId);

            // Then
            assertNotNull(dto);
            assertEquals(content, dto.content());
            assertEquals(channelId, dto.channelId());
            assertEquals(recipientId, dto.recipientId());
            assertEquals(threadParentId, dto.threadParentId());
        }

        @Test
        @DisplayName("Should create valid MessageCreationDTO with null threadParentId")
        void shouldCreateValidMessageCreationDTOWithNullThreadParentId() {
            // Given
            String content = "Hello, World!";
            Long channelId = 1L;
            Long recipientId = 2L;

            // When
            MessageCreationDTO dto = new MessageCreationDTO(content, channelId, recipientId, null);

            // Then
            assertNotNull(dto);
            assertEquals(content, dto.content());
            assertEquals(channelId, dto.channelId());
            assertEquals(recipientId, dto.recipientId());
            assertNull(dto.threadParentId());
        }

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidationWithValidData() {
            // Given
            MessageCreationDTO dto = new MessageCreationDTO("Valid content", 1L, 2L, 3L);

            // When
            Set<ConstraintViolation<MessageCreationDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should fail validation with blank content")
        void shouldFailValidationWithBlankContent(String content) {
            // Given
            MessageCreationDTO dto = new MessageCreationDTO(content, 1L, 2L, 3L);

            // When
            Set<ConstraintViolation<MessageCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Content cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation with null channelId")
        void shouldFailValidationWithNullChannelId() {
            // Given
            MessageCreationDTO dto = new MessageCreationDTO("Valid content", null, 2L, 3L);

            // When
            Set<ConstraintViolation<MessageCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Channel id cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation with null recipientId")
        void shouldFailValidationWithNullRecipientId() {
            // Given
            MessageCreationDTO dto = new MessageCreationDTO("Valid content", 1L, null, 3L);

            // When
            Set<ConstraintViolation<MessageCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Recipient id cannot be null")));
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            MessageCreationDTO dto = new MessageCreationDTO("Test message", 1L, 2L, 3L);

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"content\":\"Test message\""));
            assertTrue(json.contains("\"channel_id\":1"));
            assertTrue(json.contains("\"recipient_id\":2"));
            assertTrue(json.contains("\"thread_parent_id\":3"));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            // Given
            String json = "{\"content\":\"Test message\",\"channel_id\":1,\"recipient_id\":2,\"thread_parent_id\":3}";

            // When
            MessageCreationDTO dto = objectMapper.readValue(json, MessageCreationDTO.class);

            // Then
            assertEquals("Test message", dto.content());
            assertEquals(1L, dto.channelId());
            assertEquals(2L, dto.recipientId());
            assertEquals(3L, dto.threadParentId());
        }

        @Test
        @DisplayName("Should test equality and hashCode")
        void shouldTestEqualityAndHashCode() {
            // Given
            MessageCreationDTO dto1 = new MessageCreationDTO("Test", 1L, 2L, 3L);
            MessageCreationDTO dto2 = new MessageCreationDTO("Test", 1L, 2L, 3L);
            MessageCreationDTO dto3 = new MessageCreationDTO("Different", 1L, 2L, 3L);

            // Then
            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }
    }

    @Nested
    @DisplayName("MessageResponseDTO Tests")
    class MessageResponseDTOTest {

        @Test
        @DisplayName("Should create valid MessageResponseDTO")
        void shouldCreateValidMessageResponseDTO() {
            // Given
            Long id = 1L;
            Long senderId = 2L;
            Long channelId = 3L;
            Long recipientId = 4L;
            String content = "Response message";
            ZonedDateTime timestamp = ZonedDateTime.now();
            Long threadParentId = 5L;

            // When
            MessageResponseDTO dto = new MessageResponseDTO(id, senderId, channelId, recipientId, content, timestamp, threadParentId);

            // Then
            assertNotNull(dto);
            assertEquals(id, dto.id());
            assertEquals(senderId, dto.senderId());
            assertEquals(channelId, dto.channelId());
            assertEquals(recipientId, dto.recipientId());
            assertEquals(content, dto.content());
            assertEquals(timestamp, dto.timestamp());
            assertEquals(threadParentId, dto.threadParentId());
        }

        @Test
        @DisplayName("Should create valid MessageResponseDTO with null values")
        void shouldCreateValidMessageResponseDTOWithNullValues() {
            // Given & When
            MessageResponseDTO dto = new MessageResponseDTO(null, null, null, null, null, null, null);

            // Then
            assertNotNull(dto);
            assertNull(dto.id());
            assertNull(dto.senderId());
            assertNull(dto.channelId());
            assertNull(dto.recipientId());
            assertNull(dto.content());
            assertNull(dto.timestamp());
            assertNull(dto.threadParentId());
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            ZonedDateTime timestamp = ZonedDateTime.parse("2024-01-01T10:00:00Z");
            MessageResponseDTO dto = new MessageResponseDTO(1L, 2L, 3L, 4L, "Test message", timestamp, 5L);

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"id\":1"));
            assertTrue(json.contains("\"sender_id\":2"));
            assertTrue(json.contains("\"channel_id\":3"));
            assertTrue(json.contains("\"recipient_id\":4"));
            assertTrue(json.contains("\"content\":\"Test message\""));
            assertTrue(json.contains("\"timestamp\":"));
            assertTrue(json.contains("\"thread_parent_id\":5"));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            // Given
            String json = "{\"id\":1,\"sender_id\":2,\"channel_id\":3,\"recipient_id\":4,\"content\":\"Test message\",\"timestamp\":\"2024-01-01T10:00:00Z\",\"thread_parent_id\":5}";

            // When
            MessageResponseDTO dto = objectMapper.readValue(json, MessageResponseDTO.class);

            // Then
            assertEquals(1L, dto.id());
            assertEquals(2L, dto.senderId());
            assertEquals(3L, dto.channelId());
            assertEquals(4L, dto.recipientId());
            assertEquals("Test message", dto.content());
            assertEquals(ZonedDateTime.parse("2024-01-01T10:00:00Z"), dto.timestamp());
            assertEquals(5L, dto.threadParentId());
        }

        @Test
        @DisplayName("Should test equality and hashCode")
        void shouldTestEqualityAndHashCode() {
            // Given
            ZonedDateTime timestamp = ZonedDateTime.now();
            MessageResponseDTO dto1 = new MessageResponseDTO(1L, 2L, 3L, 4L, "Test", timestamp, 5L);
            MessageResponseDTO dto2 = new MessageResponseDTO(1L, 2L, 3L, 4L, "Test", timestamp, 5L);
            MessageResponseDTO dto3 = new MessageResponseDTO(2L, 2L, 3L, 4L, "Test", timestamp, 5L);

            // Then
            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }
    }

    @Nested
    @DisplayName("MessageUpdateDTO Tests")
    class MessageUpdateDTOTest {

        @Test
        @DisplayName("Should create valid MessageUpdateDTO")
        void shouldCreateValidMessageUpdateDTO() {
            // Given
            Long channelId = 1L;
            Long recipientId = 2L;
            String content = "Updated message";

            // When
            MessageUpdateDTO dto = new MessageUpdateDTO(channelId, recipientId, content);

            // Then
            assertNotNull(dto);
            assertEquals(channelId, dto.channelId());
            assertEquals(recipientId, dto.recipientId());
            assertEquals(content, dto.content());
        }

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidationWithValidData() {
            // Given
            MessageUpdateDTO dto = new MessageUpdateDTO(1L, 2L, "Valid content");

            // When
            Set<ConstraintViolation<MessageUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation with null channelId")
        void shouldFailValidationWithNullChannelId() {
            // Given
            MessageUpdateDTO dto = new MessageUpdateDTO(null, 2L, "Valid content");

            // When
            Set<ConstraintViolation<MessageUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Channel id cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation with null recipientId")
        void shouldFailValidationWithNullRecipientId() {
            // Given
            MessageUpdateDTO dto = new MessageUpdateDTO(1L, null, "Valid content");

            // When
            Set<ConstraintViolation<MessageUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Recipient id cannot be null")));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should fail validation with blank content")
        void shouldFailValidationWithBlankContent(String content) {
            // Given
            MessageUpdateDTO dto = new MessageUpdateDTO(1L, 2L, content);

            // When
            Set<ConstraintViolation<MessageUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Content cannot be blank")));
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            MessageUpdateDTO dto = new MessageUpdateDTO(1L, 2L, "Updated message");

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"channel_id\":1"));
            assertTrue(json.contains("\"recipient_id\":2"));
            assertTrue(json.contains("\"content\":\"Updated message\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            // Given
            String json = "{\"channel_id\":1,\"recipient_id\":2,\"content\":\"Updated message\"}";

            // When
            MessageUpdateDTO dto = objectMapper.readValue(json, MessageUpdateDTO.class);

            // Then
            assertEquals(1L, dto.channelId());
            assertEquals(2L, dto.recipientId());
            assertEquals("Updated message", dto.content());
        }

        @Test
        @DisplayName("Should test equality and hashCode")
        void shouldTestEqualityAndHashCode() {
            // Given
            MessageUpdateDTO dto1 = new MessageUpdateDTO(1L, 2L, "Test");
            MessageUpdateDTO dto2 = new MessageUpdateDTO(1L, 2L, "Test");
            MessageUpdateDTO dto3 = new MessageUpdateDTO(1L, 2L, "Different");

            // Then
            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }
    }

    @Nested
    @DisplayName("Cross-DTO Integration Tests")
    class CrossDTOIntegrationTest {

        @Test
        @DisplayName("Should convert MessageCreationDTO to MessageResponseDTO simulation")
        void shouldConvertMessageCreationDTOToMessageResponseDTO() {
            // Given
            MessageCreationDTO creationDTO = new MessageCreationDTO("Test message", 1L, 2L, 3L);
            Long messageId = 100L;
            Long senderId = 999L;
            ZonedDateTime timestamp = ZonedDateTime.now();

            // When - Simulate service layer conversion
            MessageResponseDTO responseDTO = new MessageResponseDTO(
                    messageId,
                    senderId,
                    creationDTO.channelId(),
                    creationDTO.recipientId(),
                    creationDTO.content(),
                    timestamp,
                    creationDTO.threadParentId()
            );

            // Then
            assertEquals(messageId, responseDTO.id());
            assertEquals(senderId, responseDTO.senderId());
            assertEquals(creationDTO.channelId(), responseDTO.channelId());
            assertEquals(creationDTO.recipientId(), responseDTO.recipientId());
            assertEquals(creationDTO.content(), responseDTO.content());
            assertEquals(timestamp, responseDTO.timestamp());
            assertEquals(creationDTO.threadParentId(), responseDTO.threadParentId());
        }

        @Test
        @DisplayName("Should handle MessageUpdateDTO with same validation rules as MessageCreationDTO")
        void shouldHandleMessageUpdateDTOWithSameValidationRules() {
            // Given
            MessageCreationDTO creationDTO = new MessageCreationDTO("Original message", 1L, 2L, 3L);
            MessageUpdateDTO updateDTO = new MessageUpdateDTO(1L, 2L, "Updated message");

            // When
            Set<ConstraintViolation<MessageCreationDTO>> creationViolations = validator.validate(creationDTO);
            Set<ConstraintViolation<MessageUpdateDTO>> updateViolations = validator.validate(updateDTO);

            // Then
            assertTrue(creationViolations.isEmpty());
            assertTrue(updateViolations.isEmpty());
            assertEquals(creationDTO.channelId(), updateDTO.channelId());
            assertEquals(creationDTO.recipientId(), updateDTO.recipientId());
            assertNotEquals(creationDTO.content(), updateDTO.content());
        }

        @Test
        @DisplayName("Should handle edge cases with very long content")
        void shouldHandleEdgeCasesWithVeryLongContent() {
            // Given
            String longContent = "A".repeat(10000);
            MessageCreationDTO creationDTO = new MessageCreationDTO(longContent, 1L, 2L, 3L);
            MessageUpdateDTO updateDTO = new MessageUpdateDTO(1L, 2L, longContent);

            // When
            Set<ConstraintViolation<MessageCreationDTO>> creationViolations = validator.validate(creationDTO);
            Set<ConstraintViolation<MessageUpdateDTO>> updateViolations = validator.validate(updateDTO);

            // Then
            assertTrue(creationViolations.isEmpty());
            assertTrue(updateViolations.isEmpty());
            assertEquals(longContent, creationDTO.content());
            assertEquals(longContent, updateDTO.content());
        }

        @Test
        @DisplayName("Should handle special characters in content")
        void shouldHandleSpecialCharactersInContent() {
            // Given
            String specialContent = "Hello! 🌟 This is a test with émojis and spëcial chars: @#$%^&*()";
            MessageCreationDTO creationDTO = new MessageCreationDTO(specialContent, 1L, 2L, null);

            // When
            Set<ConstraintViolation<MessageCreationDTO>> violations = validator.validate(creationDTO);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(specialContent, creationDTO.content());
        }
    }
}