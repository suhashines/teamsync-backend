package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.dto.commentDTO.CommentCreateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentResponseDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentUpdateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.ReplyCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comment DTO Unit Tests")
class CommentDTOTests {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("CommentCreateRequestDTO Tests")
    class CommentCreateRequestDTOTests {

        @Test
        @DisplayName("Should create valid CommentCreateRequestDTO")
        void shouldCreateValidCommentCreateRequestDTO() {
            // Given
            CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
            dto.setPostId(1L);
            dto.setContent("Test comment content");
            dto.setParentCommentId(null);

            // When
            Set<ConstraintViolation<CommentCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(1L, dto.getPostId());
            assertEquals("Test comment content", dto.getContent());
            assertNull(dto.getParentCommentId());
        }

        @Test
        @DisplayName("Should create valid CommentCreateRequestDTO with parent comment")
        void shouldCreateValidCommentCreateRequestDTOWithParent() {
            // Given
            CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
            dto.setPostId(1L);
            dto.setContent("Test reply content");
            dto.setParentCommentId(2L);

            // When
            Set<ConstraintViolation<CommentCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(2L, dto.getParentCommentId());
        }

        @Test
        @DisplayName("Should fail validation when postId is null")
        void shouldFailValidationWhenPostIdIsNull() {
            // Given
            CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
            dto.setPostId(null);
            dto.setContent("Test content");

            // When
            Set<ConstraintViolation<CommentCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Post ID is required")));
        }

        @Test
        @DisplayName("Should fail validation when content is null")
        void shouldFailValidationWhenContentIsNull() {
            // Given
            CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
            dto.setPostId(1L);
            dto.setContent(null);

            // When
            Set<ConstraintViolation<CommentCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Content is required")));
        }

        @Test
        @DisplayName("Should fail validation when content is blank")
        void shouldFailValidationWhenContentIsBlank() {
            // Given
            CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
            dto.setPostId(1L);
            dto.setContent("   ");

            // When
            Set<ConstraintViolation<CommentCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Content is required")));
        }

        @Test
        @DisplayName("Should serialize to snake_case JSON")
        void shouldSerializeToSnakeCaseJSON() throws JsonProcessingException {
            // Given
            CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
            dto.setPostId(1L);
            dto.setContent("Test content");
            dto.setParentCommentId(2L);

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("post_id"));
            assertTrue(json.contains("parent_comment_id"));
            assertFalse(json.contains("postId"));
            assertFalse(json.contains("parentCommentId"));
        }

        @Test
        @DisplayName("Should deserialize from snake_case JSON")
        void shouldDeserializeFromSnakeCaseJSON() throws JsonProcessingException {
            // Given
            String json = "{\"post_id\":1,\"content\":\"Test content\",\"parent_comment_id\":2}";

            // When
            CommentCreateRequestDTO dto = objectMapper.readValue(json, CommentCreateRequestDTO.class);

            // Then
            assertEquals(1L, dto.getPostId());
            assertEquals("Test content", dto.getContent());
            assertEquals(2L, dto.getParentCommentId());
        }
    }

    @Nested
    @DisplayName("CommentResponseDTO Tests")
    class CommentResponseDTOTests {

        @Test
        @DisplayName("Should create valid CommentResponseDTO")
        void shouldCreateValidCommentResponseDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionDetailDTO reaction = new ReactionDetailDTO();
            // Assuming ReactionDetailDTO has basic properties
            List<ReactionDetailDTO> reactions = Arrays.asList(reaction);

            CommentResponseDTO dto = new CommentResponseDTO();
            dto.setId(1L);
            dto.setPostId(2L);
            dto.setAuthorId(3L);
            dto.setContent("Test comment");
            dto.setTimestamp(now);
            dto.setParentCommentId(4L);
            dto.setReactions(reactions);
            dto.setReplyCount(5);

            // Then
            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getPostId());
            assertEquals(3L, dto.getAuthorId());
            assertEquals("Test comment", dto.getContent());
            assertEquals(now, dto.getTimestamp());
            assertEquals(4L, dto.getParentCommentId());
            assertEquals(reactions, dto.getReactions());
            assertEquals(5, dto.getReplyCount());
        }

        @Test
        @DisplayName("Should handle null values properly")
        void shouldHandleNullValuesProperly() {
            // Given
            CommentResponseDTO dto = new CommentResponseDTO();
            dto.setId(1L);
            dto.setPostId(2L);
            dto.setAuthorId(3L);
            dto.setContent("Test comment");
            dto.setTimestamp(ZonedDateTime.now());
            dto.setParentCommentId(null);
            dto.setReactions(null);
            dto.setReplyCount(null);

            // Then
            assertNull(dto.getParentCommentId());
            assertNull(dto.getReactions());
            assertNull(dto.getReplyCount());
        }

        @Test
        @DisplayName("Should serialize to snake_case JSON")
        void shouldSerializeToSnakeCaseJSON() throws JsonProcessingException {
            // Given
            CommentResponseDTO dto = new CommentResponseDTO();
            dto.setId(1L);
            dto.setPostId(2L);
            dto.setAuthorId(3L);
            dto.setContent("Test comment");
            dto.setTimestamp(ZonedDateTime.now());
            dto.setParentCommentId(4L);
            dto.setReplyCount(5);

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("post_id"));
            assertTrue(json.contains("author_id"));
            assertTrue(json.contains("parent_comment_id"));
            assertTrue(json.contains("reply_count"));
            assertFalse(json.contains("postId"));
            assertFalse(json.contains("authorId"));
        }

        @Test
        @DisplayName("Should deserialize from snake_case JSON")
        void shouldDeserializeFromSnakeCaseJSON() throws JsonProcessingException {
            // Given
            String json = "{\"id\":1,\"post_id\":2,\"author_id\":3,\"content\":\"Test comment\",\"timestamp\":\"2023-12-01T10:00:00Z\",\"parent_comment_id\":4,\"reply_count\":5}";

            // When
            CommentResponseDTO dto = objectMapper.readValue(json, CommentResponseDTO.class);

            // Then
            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getPostId());
            assertEquals(3L, dto.getAuthorId());
            assertEquals("Test comment", dto.getContent());
            assertEquals(4L, dto.getParentCommentId());
            assertEquals(5, dto.getReplyCount());
        }
    }

    @Nested
    @DisplayName("CommentUpdateRequestDTO Tests")
    class CommentUpdateRequestDTOTests {

        @Test
        @DisplayName("Should create valid CommentUpdateRequestDTO")
        void shouldCreateValidCommentUpdateRequestDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
            dto.setPostId(1L);
            dto.setAuthorId(2L);
            dto.setContent("Updated content");
            dto.setTimestamp(now);
            dto.setParentCommentId(3L);
            dto.setReplyCount(4);

            // When
            Set<ConstraintViolation<CommentUpdateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(1L, dto.getPostId());
            assertEquals(2L, dto.getAuthorId());
            assertEquals("Updated content", dto.getContent());
            assertEquals(now, dto.getTimestamp());
            assertEquals(3L, dto.getParentCommentId());
            assertEquals(4, dto.getReplyCount());
        }

        @Test
        @DisplayName("Should fail validation when postId is null")
        void shouldFailValidationWhenPostIdIsNull() {
            // Given
            CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
            dto.setPostId(null);
            dto.setAuthorId(1L);
            dto.setContent("Test content");

            // When
            Set<ConstraintViolation<CommentUpdateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Post ID is required")));
        }

        @Test
        @DisplayName("Should fail validation when authorId is null")
        void shouldFailValidationWhenAuthorIdIsNull() {
            // Given
            CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
            dto.setPostId(1L);
            dto.setAuthorId(null);
            dto.setContent("Test content");

            // When
            Set<ConstraintViolation<CommentUpdateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Author ID is required")));
        }

        @Test
        @DisplayName("Should fail validation when content is blank")
        void shouldFailValidationWhenContentIsBlank() {
            // Given
            CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
            dto.setPostId(1L);
            dto.setAuthorId(2L);
            dto.setContent("");

            // When
            Set<ConstraintViolation<CommentUpdateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Content is required")));
        }

        @Test
        @DisplayName("Should serialize to snake_case JSON")
        void shouldSerializeToSnakeCaseJSON() throws JsonProcessingException {
            // Given
            CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
            dto.setPostId(1L);
            dto.setAuthorId(2L);
            dto.setContent("Updated content");
            dto.setParentCommentId(3L);
            dto.setReplyCount(4);

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("post_id"));
            assertTrue(json.contains("author_id"));
            assertTrue(json.contains("parent_comment_id"));
            assertTrue(json.contains("reply_count"));
        }
    }

    @Nested
    @DisplayName("ReplyCreateRequestDTO Tests")
    class ReplyCreateRequestDTOTests {

        @Test
        @DisplayName("Should create valid ReplyCreateRequestDTO")
        void shouldCreateValidReplyCreateRequestDTO() {
            // Given
            ReplyCreateRequestDTO dto = ReplyCreateRequestDTO.builder()
                    .content("Test reply content")
                    .author_id(1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("Test reply content", dto.getContent());
            assertEquals(1L, dto.getAuthor_id());
        }

        @Test
        @DisplayName("Should create using no-args constructor")
        void shouldCreateUsingNoArgsConstructor() {
            // Given
            ReplyCreateRequestDTO dto = new ReplyCreateRequestDTO();
            dto.setContent("Test content");
            dto.setAuthor_id(1L);

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("Test content", dto.getContent());
            assertEquals(1L, dto.getAuthor_id());
        }

        @Test
        @DisplayName("Should create using all-args constructor")
        void shouldCreateUsingAllArgsConstructor() {
            // Given
            ReplyCreateRequestDTO dto = new ReplyCreateRequestDTO("Test content", 1L);

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("Test content", dto.getContent());
            assertEquals(1L, dto.getAuthor_id());
        }

        @Test
        @DisplayName("Should fail validation when content is null")
        void shouldFailValidationWhenContentIsNull() {
            // Given
            ReplyCreateRequestDTO dto = ReplyCreateRequestDTO.builder()
                    .content(null)
                    .author_id(1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Reply content cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when content is blank")
        void shouldFailValidationWhenContentIsBlank() {
            // Given
            ReplyCreateRequestDTO dto = ReplyCreateRequestDTO.builder()
                    .content("   ")
                    .author_id(1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Reply content cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when content is too long")
        void shouldFailValidationWhenContentIsTooLong() {
            // Given
            String longContent = "a".repeat(1001);
            ReplyCreateRequestDTO dto = ReplyCreateRequestDTO.builder()
                    .content(longContent)
                    .author_id(1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Reply content must be between 1 and 1000 characters")));
        }

        @Test
        @DisplayName("Should fail validation when author_id is null")
        void shouldFailValidationWhenAuthorIdIsNull() {
            // Given
            ReplyCreateRequestDTO dto = ReplyCreateRequestDTO.builder()
                    .content("Test content")
                    .author_id(null)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Author ID is required")));
        }

        @Test
        @DisplayName("Should fail validation when author_id is not positive")
        void shouldFailValidationWhenAuthorIdIsNotPositive() {
            // Given
            ReplyCreateRequestDTO dto = ReplyCreateRequestDTO.builder()
                    .content("Test content")
                    .author_id(-1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Author ID must be a positive number")));
        }

        @Test
        @DisplayName("Should accept content at boundary limits")
        void shouldAcceptContentAtBoundaryLimits() {
            // Given - Test minimum length (1 character)
            ReplyCreateRequestDTO dto1 = ReplyCreateRequestDTO.builder()
                    .content("a")
                    .author_id(1L)
                    .build();

            // Given - Test maximum length (1000 characters)
            String maxContent = "a".repeat(1000);
            ReplyCreateRequestDTO dto2 = ReplyCreateRequestDTO.builder()
                    .content(maxContent)
                    .author_id(1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations1 = validator.validate(dto1);
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations2 = validator.validate(dto2);

            // Then
            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
        }

        @Test
        @DisplayName("Should test equals and hashCode")
        void shouldTestEqualsAndHashCode() {
            // Given
            ReplyCreateRequestDTO dto1 = new ReplyCreateRequestDTO("Test content", 1L);
            ReplyCreateRequestDTO dto2 = new ReplyCreateRequestDTO("Test content", 1L);
            ReplyCreateRequestDTO dto3 = new ReplyCreateRequestDTO("Different content", 1L);

            // Then
            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString")
        void shouldTestToString() {
            // Given
            ReplyCreateRequestDTO dto = new ReplyCreateRequestDTO("Test content", 1L);

            // When
            String toString = dto.toString();

            // Then
            assertTrue(toString.contains("Test content"));
            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("ReplyCreateRequestDTO"));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete comment creation flow")
        void shouldHandleCompleteCommentCreationFlow() throws JsonProcessingException {
            // Given - Create request
            CommentCreateRequestDTO createRequest = new CommentCreateRequestDTO();
            createRequest.setPostId(1L);
            createRequest.setContent("Test comment");
            createRequest.setParentCommentId(null);

            // When - Serialize and deserialize
            String json = objectMapper.writeValueAsString(createRequest);
            CommentCreateRequestDTO deserializedRequest = objectMapper.readValue(json, CommentCreateRequestDTO.class);

            // Then - Validate
            Set<ConstraintViolation<CommentCreateRequestDTO>> violations = validator.validate(deserializedRequest);
            assertTrue(violations.isEmpty());
            assertEquals(createRequest.getPostId(), deserializedRequest.getPostId());
            assertEquals(createRequest.getContent(), deserializedRequest.getContent());
        }

        @Test
        @DisplayName("Should handle reply creation with validation")
        void shouldHandleReplyCreationWithValidation() {
            // Given
            ReplyCreateRequestDTO reply = ReplyCreateRequestDTO.builder()
                    .content("This is a reply")
                    .author_id(1L)
                    .build();

            // When
            Set<ConstraintViolation<ReplyCreateRequestDTO>> violations = validator.validate(reply);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("This is a reply", reply.getContent());
            assertEquals(1L, reply.getAuthor_id());
        }
    }
}
