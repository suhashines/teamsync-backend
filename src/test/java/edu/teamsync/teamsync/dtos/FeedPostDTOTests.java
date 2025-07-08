package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostCreateRequest;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostResponseDTO;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostUpdateRequest;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostWithReactionDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FeedPost DTO Tests")
class FeedPostDTOTests {

    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("FeedPostCreateRequest Tests")
    class FeedPostCreateRequestTests {

        @Test
        @DisplayName("Should create valid FeedPostCreateRequest")
        void shouldCreateValidFeedPostCreateRequest() {
            // Given
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent("Test content");
            request.setMediaUrls(new String[]{"url1", "url2"});
            request.setEventDate(LocalDate.now());
            request.setPollOptions(new String[]{"Option 1", "Option 2"});

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(FeedPosts.FeedPostType.text, request.getType());
            assertEquals("Test content", request.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, request.getMediaUrls());
            assertEquals(LocalDate.now(), request.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, request.getPollOptions());
        }

        @Test
        @DisplayName("Should fail validation when type is null")
        void shouldFailValidationWhenTypeIsNull() {
            // Given
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(null);
            request.setContent("Test content");

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Type is required")));
        }

        @Test
        @DisplayName("Should fail validation when content is blank")
        void shouldFailValidationWhenContentIsBlank() {
            // Given
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent("");

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Content is required")));
        }

        @Test
        @DisplayName("Should fail validation when content is null")
        void shouldFailValidationWhenContentIsNull() {
            // Given
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent(null);

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Content is required")));
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent("Test content");
            request.setMediaUrls(new String[]{"url1", "url2"});
            request.setEventDate(LocalDate.of(2024, 1, 1));
            request.setPollOptions(new String[]{"Option 1", "Option 2"});

            // When
            String json = objectMapper.writeValueAsString(request);

            // Then
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"content\""));
            assertTrue(json.contains("\"media_urls\""));
            assertTrue(json.contains("\"event_date\""));
            assertTrue(json.contains("\"poll_options\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            String json = """
                {
                    "type": "text",
                    "content": "Test content",
                    "media_urls": ["url1", "url2"],
                    "event_date": "2024-01-01",
                    "poll_options": ["Option 1", "Option 2"]
                }
                """;

            // When
            FeedPostCreateRequest request = objectMapper.readValue(json, FeedPostCreateRequest.class);

            // Then
            assertEquals(FeedPosts.FeedPostType.text, request.getType());
            assertEquals("Test content", request.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, request.getMediaUrls());
            assertEquals(LocalDate.of(2024, 1, 1), request.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, request.getPollOptions());
        }
    }

    @Nested
    @DisplayName("FeedPostResponseDTO Tests")
    class FeedPostResponseDTOTests {

        @Test
        @DisplayName("Should create valid FeedPostResponseDTO")
        void shouldCreateValidFeedPostResponseDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            FeedPostResponseDTO response = new FeedPostResponseDTO();
            response.setId(1L);
            response.setType(FeedPosts.FeedPostType.text);
            response.setAuthorId(100L);
            response.setContent("Test content");
            response.setMediaUrls(new String[]{"url1", "url2"});
            response.setCreatedAt(now);
            response.setEventDate(LocalDate.now());
            response.setPollOptions(new String[]{"Option 1", "Option 2"});
            response.setAiGenerated(true);
            response.setAiSummary("AI generated summary");

            // Then
            assertEquals(1L, response.getId());
            assertEquals(FeedPosts.FeedPostType.text, response.getType());
            assertEquals(100L, response.getAuthorId());
            assertEquals("Test content", response.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, response.getMediaUrls());
            assertEquals(now, response.getCreatedAt());
            assertEquals(LocalDate.now(), response.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, response.getPollOptions());
            assertTrue(response.isAiGenerated());
            assertEquals("AI generated summary", response.getAiSummary());
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            FeedPostResponseDTO response = new FeedPostResponseDTO();
            response.setId(1L);
            response.setType(FeedPosts.FeedPostType.text);
            response.setAuthorId(100L);
            response.setContent("Test content");
            response.setAiGenerated(true);
            response.setAiSummary("AI summary");

            // When
            String json = objectMapper.writeValueAsString(response);

            // Then
            assertTrue(json.contains("\"author_id\""));
            assertTrue(json.contains("\"created_at\""));
            assertTrue(json.contains("\"event_date\""));
            assertTrue(json.contains("\"poll_options\""));
            assertTrue(json.contains("\"ai_generated\""));
            assertTrue(json.contains("\"ai_summary\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            String json = """
                {
                    "id": 1,
                    "type": "text",
                    "author_id": 100,
                    "content": "Test content",
                    "media_urls": ["url1", "url2"],
                    "created_at": "2024-01-01T10:00:00Z",
                    "event_date": "2024-01-01",
                    "poll_options": ["Option 1", "Option 2"],
                    "ai_generated": true,
                    "ai_summary": "AI summary"
                }
                """;

            // When
            FeedPostResponseDTO response = objectMapper.readValue(json, FeedPostResponseDTO.class);

            // Then
            assertEquals(1L, response.getId());
            assertEquals(FeedPosts.FeedPostType.text, response.getType());
            assertEquals(100L, response.getAuthorId());
            assertEquals("Test content", response.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, response.getMediaUrls());
            assertEquals(LocalDate.of(2024, 1, 1), response.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, response.getPollOptions());
            assertTrue(response.isAiGenerated());
            assertEquals("AI summary", response.getAiSummary());
        }
    }

    @Nested
    @DisplayName("FeedPostUpdateRequest Tests")
    class FeedPostUpdateRequestTests {

        @Test
        @DisplayName("Should create valid FeedPostUpdateRequest")
        void shouldCreateValidFeedPostUpdateRequest() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            FeedPostUpdateRequest request = new FeedPostUpdateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setAuthorId(100L);
            request.setContent("Updated content");
            request.setMediaUrls(new String[]{"url1", "url2"});
            request.setCreatedAt(now);
            request.setEventDate(LocalDate.now());
            request.setPollOptions(new String[]{"Option 1", "Option 2"});
            request.setIsAiGenerated(true);
            request.setAiSummary("AI summary");
            request.setReactions(Arrays.asList(new ReactionDetailDTO()));

            // When
            Set<ConstraintViolation<FeedPostUpdateRequest>> violations = validator.validate(request);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(FeedPosts.FeedPostType.text, request.getType());
            assertEquals(100L, request.getAuthorId());
            assertEquals("Updated content", request.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, request.getMediaUrls());
            assertEquals(now, request.getCreatedAt());
            assertEquals(LocalDate.now(), request.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, request.getPollOptions());
            assertTrue(request.getIsAiGenerated());
            assertEquals("AI summary", request.getAiSummary());
            assertNotNull(request.getReactions());
        }

        @Test
        @DisplayName("Should fail validation when type is null")
        void shouldFailValidationWhenTypeIsNull() {
            // Given
            FeedPostUpdateRequest request = new FeedPostUpdateRequest();
            request.setType(null);
            request.setAuthorId(100L);

            // When
            Set<ConstraintViolation<FeedPostUpdateRequest>> violations = validator.validate(request);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Type is required")));
        }

        @Test
        @DisplayName("Should fail validation when authorId is null")
        void shouldFailValidationWhenAuthorIdIsNull() {
            // Given
            FeedPostUpdateRequest request = new FeedPostUpdateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setAuthorId(null);

            // When
            Set<ConstraintViolation<FeedPostUpdateRequest>> violations = validator.validate(request);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Author Id is required")));
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            FeedPostUpdateRequest request = new FeedPostUpdateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setAuthorId(100L);
            request.setContent("Updated content");
            request.setIsAiGenerated(true);
            request.setAiSummary("AI summary");

            // When
            String json = objectMapper.writeValueAsString(request);

            // Then
            assertTrue(json.contains("\"author_id\""));
            assertTrue(json.contains("\"created_at\""));
            assertTrue(json.contains("\"event_date\""));
            assertTrue(json.contains("\"poll_options\""));
            assertTrue(json.contains("\"is_ai_generated\""));
            assertTrue(json.contains("\"ai_summary\""));
        }
    }

    @Nested
    @DisplayName("FeedPostWithReactionDTO Tests")
    class FeedPostWithReactionDTOTests {

        @Test
        @DisplayName("Should create valid FeedPostWithReactionDTO")
        void shouldCreateValidFeedPostWithReactionDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            FeedPostWithReactionDTO dto = new FeedPostWithReactionDTO();
            dto.setId(1L);
            dto.setType(FeedPosts.FeedPostType.text);
            dto.setAuthorId(100L);
            dto.setContent("Test content");
            dto.setMediaUrls(new String[]{"url1", "url2"});
            dto.setCreatedAt(now);
            dto.setEventDate(LocalDate.now());
            dto.setPollOptions(new String[]{"Option 1", "Option 2"});
            dto.setAiGenerated(true);
            dto.setAiSummary("AI summary");
            dto.setReactions(Arrays.asList(new ReactionDetailDTO()));

            // Then
            assertEquals(1L, dto.getId());
            assertEquals(FeedPosts.FeedPostType.text, dto.getType());
            assertEquals(100L, dto.getAuthorId());
            assertEquals("Test content", dto.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, dto.getMediaUrls());
            assertEquals(now, dto.getCreatedAt());
            assertEquals(LocalDate.now(), dto.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, dto.getPollOptions());
            assertTrue(dto.isAiGenerated());
            assertEquals("AI summary", dto.getAiSummary());
            assertNotNull(dto.getReactions());
            assertEquals(1, dto.getReactions().size());
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            FeedPostWithReactionDTO dto = new FeedPostWithReactionDTO();
            dto.setId(1L);
            dto.setType(FeedPosts.FeedPostType.text);
            dto.setAuthorId(100L);
            dto.setContent("Test content");
            dto.setAiGenerated(true);
            dto.setAiSummary("AI summary");
            dto.setReactions(Arrays.asList(new ReactionDetailDTO()));

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"author_id\""));
            assertTrue(json.contains("\"created_at\""));
            assertTrue(json.contains("\"event_date\""));
            assertTrue(json.contains("\"poll_options\""));
            assertTrue(json.contains("\"ai_generated\""));
            assertTrue(json.contains("\"ai_summary\""));
            assertTrue(json.contains("\"reactions\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            String json = """
                {
                    "id": 1,
                    "type": "text",
                    "author_id": 100,
                    "content": "Test content",
                    "media_urls": ["url1", "url2"],
                    "created_at": "2024-01-01T10:00:00Z",
                    "event_date": "2024-01-01",
                    "poll_options": ["Option 1", "Option 2"],
                    "ai_generated": true,
                    "ai_summary": "AI summary",
                    "reactions": []
                }
                """;

            // When
            FeedPostWithReactionDTO dto = objectMapper.readValue(json, FeedPostWithReactionDTO.class);

            // Then
            assertEquals(1L, dto.getId());
            assertEquals(FeedPosts.FeedPostType.text, dto.getType());
            assertEquals(100L, dto.getAuthorId());
            assertEquals("Test content", dto.getContent());
            assertArrayEquals(new String[]{"url1", "url2"}, dto.getMediaUrls());
            assertEquals(LocalDate.of(2024, 1, 1), dto.getEventDate());
            assertArrayEquals(new String[]{"Option 1", "Option 2"}, dto.getPollOptions());
            assertTrue(dto.isAiGenerated());
            assertEquals("AI summary", dto.getAiSummary());
            assertNotNull(dto.getReactions());
            assertTrue(dto.getReactions().isEmpty());
        }
    }

    @Nested
    @DisplayName("Cross-DTO Integration Tests")
    class CrossDtoIntegrationTests {

        @Test
        @DisplayName("Should maintain data consistency across DTOs")
        void shouldMaintainDataConsistencyAcrossDTOs() {
            // Given - Create request
            FeedPostCreateRequest createRequest = new FeedPostCreateRequest();
            createRequest.setType(FeedPosts.FeedPostType.poll);
            createRequest.setContent("Which option do you prefer?");
            createRequest.setPollOptions(new String[]{"Option A", "Option B", "Option C"});
            createRequest.setEventDate(LocalDate.of(2024, 12, 25));

            // When - Simulate response creation
            FeedPostResponseDTO response = new FeedPostResponseDTO();
            response.setId(1L);
            response.setType(createRequest.getType());
            response.setAuthorId(100L);
            response.setContent(createRequest.getContent());
            response.setPollOptions(createRequest.getPollOptions());
            response.setEventDate(createRequest.getEventDate());
            response.setCreatedAt(ZonedDateTime.now());
            response.setAiGenerated(false);

            // Then - Verify consistency
            assertEquals(createRequest.getType(), response.getType());
            assertEquals(createRequest.getContent(), response.getContent());
            assertArrayEquals(createRequest.getPollOptions(), response.getPollOptions());
            assertEquals(createRequest.getEventDate(), response.getEventDate());
        }

        @Test
        @DisplayName("Should handle null values consistently")
        void shouldHandleNullValuesConsistently() {
            // Given
            FeedPostCreateRequest createRequest = new FeedPostCreateRequest();
            createRequest.setType(FeedPosts.FeedPostType.text);
            createRequest.setContent("Simple text post");
            // mediaUrls, eventDate, pollOptions are null

            FeedPostResponseDTO response = new FeedPostResponseDTO();
            response.setId(1L);
            response.setType(createRequest.getType());
            response.setContent(createRequest.getContent());
            response.setAuthorId(100L);
            response.setCreatedAt(ZonedDateTime.now());
            // Other fields remain null

            // Then
            assertNull(createRequest.getMediaUrls());
            assertNull(createRequest.getEventDate());
            assertNull(createRequest.getPollOptions());
            assertNull(response.getMediaUrls());
            assertNull(response.getEventDate());
            assertNull(response.getPollOptions());
        }

        @Test
        @DisplayName("Should handle empty arrays consistently")
        void shouldHandleEmptyArraysConsistently() {
            // Given
            FeedPostCreateRequest createRequest = new FeedPostCreateRequest();
            createRequest.setType(FeedPosts.FeedPostType.poll);
            createRequest.setContent("Media post");
            createRequest.setMediaUrls(new String[]{});
            createRequest.setPollOptions(new String[]{});

            // Then
            assertNotNull(createRequest.getMediaUrls());
            assertNotNull(createRequest.getPollOptions());
            assertEquals(0, createRequest.getMediaUrls().length);
            assertEquals(0, createRequest.getPollOptions().length);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle very long content")
        void shouldHandleVeryLongContent() {
            // Given
            String longContent = "A".repeat(10000);
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent(longContent);

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(longContent, request.getContent());
        }

        @Test
        @DisplayName("Should handle special characters in content")
        void shouldHandleSpecialCharactersInContent() {
            // Given
            String specialContent = "Hello! @#$%^&*()_+-=[]{}|;':\",./<>?`~";
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent(specialContent);

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(specialContent, request.getContent());
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            // Given
            String unicodeContent = "Hello 世界 🌍 émoji test";
            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.text);
            request.setContent(unicodeContent);

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(unicodeContent, request.getContent());
        }

        @Test
        @DisplayName("Should handle large arrays")
        void shouldHandleLargeArrays() {
            // Given
            String[] largeMediaUrls = new String[100];
            String[] largePollOptions = new String[50];

            for (int i = 0; i < 100; i++) {
                largeMediaUrls[i] = "https://example.com/media/" + i;
            }

            for (int i = 0; i < 50; i++) {
                largePollOptions[i] = "Option " + i;
            }

            FeedPostCreateRequest request = new FeedPostCreateRequest();
            request.setType(FeedPosts.FeedPostType.poll);
            request.setContent("Large poll");
            request.setMediaUrls(largeMediaUrls);
            request.setPollOptions(largePollOptions);

            // When
            Set<ConstraintViolation<FeedPostCreateRequest>> violations = validator.validate(request);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(100, request.getMediaUrls().length);
            assertEquals(50, request.getPollOptions().length);
        }
    }
}