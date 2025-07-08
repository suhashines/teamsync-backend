package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reaction DTO Tests")
class ReactionDTOTests {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("ReactionCreateRequestDTO Tests")
    class ReactionCreateRequestDTOTests {

        @Test
        @DisplayName("Should create valid ReactionCreateRequestDTO")
        void shouldCreateValidReactionCreateRequestDTO() {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(1L);
            dto.setReactionType("LIKE");

            // When
            Set<ConstraintViolation<ReactionCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getReactionType()).isEqualTo("LIKE");
        }

        @Test
        @DisplayName("Should fail validation when userId is null")
        void shouldFailValidationWhenUserIdIsNull() {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(null);
            dto.setReactionType("LIKE");

            // When
            Set<ConstraintViolation<ReactionCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("User ID is required");
        }

        @Test
        @DisplayName("Should fail validation when reactionType is null")
        void shouldFailValidationWhenReactionTypeIsNull() {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(1L);
            dto.setReactionType(null);

            // When
            Set<ConstraintViolation<ReactionCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Reaction type is required");
        }

        @Test
        @DisplayName("Should fail validation when reactionType is blank")
        void shouldFailValidationWhenReactionTypeIsBlank() {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(1L);
            dto.setReactionType("   ");

            // When
            Set<ConstraintViolation<ReactionCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Reaction type is required");
        }

        @Test
        @DisplayName("Should fail validation when reactionType is empty")
        void shouldFailValidationWhenReactionTypeIsEmpty() {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(1L);
            dto.setReactionType("");

            // When
            Set<ConstraintViolation<ReactionCreateRequestDTO>> violations = validator.validate(dto);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Reaction type is required");
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(123L);
            dto.setReactionType("HEART");

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertThat(json).contains("\"user_id\":123");
            assertThat(json).contains("\"reaction_type\":\"HEART\"");
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            String json = "{\"user_id\":456,\"reaction_type\":\"LAUGH\"}";

            // When
            ReactionCreateRequestDTO dto = objectMapper.readValue(json, ReactionCreateRequestDTO.class);

            // Then
            assertThat(dto.getUserId()).isEqualTo(456L);
            assertThat(dto.getReactionType()).isEqualTo("LAUGH");
        }

        @Test
        @DisplayName("Should handle equals and hashCode correctly")
        void shouldHandleEqualsAndHashCodeCorrectly() {
            // Given
            ReactionCreateRequestDTO dto1 = new ReactionCreateRequestDTO();
            dto1.setUserId(1L);
            dto1.setReactionType("LIKE");

            ReactionCreateRequestDTO dto2 = new ReactionCreateRequestDTO();
            dto2.setUserId(1L);
            dto2.setReactionType("LIKE");

            ReactionCreateRequestDTO dto3 = new ReactionCreateRequestDTO();
            dto3.setUserId(2L);
            dto3.setReactionType("LIKE");

            // Then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).isNotEqualTo(dto3);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
            assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
        }

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Given
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(1L);
            dto.setReactionType("LIKE");

            // When
            String toString = dto.toString();

            // Then
            assertThat(toString).contains("ReactionCreateRequestDTO");
            assertThat(toString).contains("userId=1");
            assertThat(toString).contains("reactionType=LIKE");
        }
    }

    @Nested
    @DisplayName("ReactionDetailDTO Tests")
    class ReactionDetailDTOTests {

        @Test
        @DisplayName("Should create valid ReactionDetailDTO")
        void shouldCreateValidReactionDetailDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionDetailDTO dto = new ReactionDetailDTO();
            dto.setUserId(1L);
            dto.setReactionType("LIKE");
            dto.setCreatedAt(now);

            // When & Then
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getReactionType()).isEqualTo("LIKE");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionDetailDTO dto = new ReactionDetailDTO();
            dto.setUserId(123L);
            dto.setReactionType("HEART");
            dto.setCreatedAt(now);

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertThat(json).contains("\"user_id\":123");
            assertThat(json).contains("\"reaction_type\":\"HEART\"");
            assertThat(json).contains("\"created_at\"");
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws JsonProcessingException {
            // Given
            String json = "{\"user_id\":456,\"reaction_type\":\"LAUGH\",\"created_at\":\"2024-01-15T10:30:00Z\"}";

            // When
            ReactionDetailDTO dto = objectMapper.readValue(json, ReactionDetailDTO.class);

            // Then
            assertThat(dto.getUserId()).isEqualTo(456L);
            assertThat(dto.getReactionType()).isEqualTo("LAUGH");
            assertThat(dto.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // Given
            ReactionDetailDTO dto = new ReactionDetailDTO();

            // When & Then
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getReactionType()).isNull();
            assertThat(dto.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("Should handle equals and hashCode correctly")
        void shouldHandleEqualsAndHashCodeCorrectly() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();

            ReactionDetailDTO dto1 = new ReactionDetailDTO();
            dto1.setUserId(1L);
            dto1.setReactionType("LIKE");
            dto1.setCreatedAt(now);

            ReactionDetailDTO dto2 = new ReactionDetailDTO();
            dto2.setUserId(1L);
            dto2.setReactionType("LIKE");
            dto2.setCreatedAt(now);

            ReactionDetailDTO dto3 = new ReactionDetailDTO();
            dto3.setUserId(2L);
            dto3.setReactionType("LIKE");
            dto3.setCreatedAt(now);

            // Then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).isNotEqualTo(dto3);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionDetailDTO dto = new ReactionDetailDTO();
            dto.setUserId(1L);
            dto.setReactionType("LIKE");
            dto.setCreatedAt(now);

            // When
            String toString = dto.toString();

            // Then
            assertThat(toString).contains("ReactionDetailDTO");
            assertThat(toString).contains("userId=1");
            assertThat(toString).contains("reactionType=LIKE");
            assertThat(toString).contains("createdAt=");
        }
    }

    @Nested
    @DisplayName("ReactionResponseDTO Tests")
    class ReactionResponseDTOTests {

        @Test
        @DisplayName("Should create valid ReactionResponseDTO using setter")
        void shouldCreateValidReactionResponseDTOUsingSetter() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionResponseDTO dto = new ReactionResponseDTO();
            dto.setId(1L);
            dto.setUserId(2L);
            dto.setReactionType("LIKE");
            dto.setCreatedAt(now);

            // When & Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getReactionType()).isEqualTo("LIKE");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create valid ReactionResponseDTO using constructor")
        void shouldCreateValidReactionResponseDTOUsingConstructor() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();

            // When
            ReactionResponseDTO dto = new ReactionResponseDTO(1L, 2L, "LIKE", now);

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getReactionType()).isEqualTo("LIKE");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create valid ReactionResponseDTO using builder")
        void shouldCreateValidReactionResponseDTOUsingBuilder() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();

            // When
            ReactionResponseDTO dto = ReactionResponseDTO.builder()
                    .id(1L)
                    .userId(2L)
                    .reactionType("LIKE")
                    .createdAt(now)
                    .build();

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getReactionType()).isEqualTo("LIKE");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create ReactionResponseDTO with no-args constructor")
        void shouldCreateReactionResponseDTOWithNoArgsConstructor() {
            // When
            ReactionResponseDTO dto = new ReactionResponseDTO();

            // Then
            assertThat(dto.getId()).isNull();
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getReactionType()).isNull();
            assertThat(dto.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("Should serialize to JSON correctly")
        void shouldSerializeToJsonCorrectly() throws JsonProcessingException {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionResponseDTO dto = ReactionResponseDTO.builder()
                    .id(123L)
                    .userId(456L)
                    .reactionType("HEART")
                    .createdAt(now)
                    .build();

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertThat(json).contains("\"id\":123");
            assertThat(json).contains("\"userId\":456");
            assertThat(json).contains("\"reactionType\":\"HEART\"");
            assertThat(json).contains("\"createdAt\"");
        }

        @Test
        @DisplayName("Should deserialize from JSON correctly")
        void shouldDeserializeFromJsonCorrectly() throws JsonProcessingException {
            // Given
            String json = "{\"id\":789,\"userId\":12,\"reactionType\":\"LAUGH\",\"createdAt\":\"2024-01-15T10:30:00Z\"}";

            // When
            ReactionResponseDTO dto = objectMapper.readValue(json, ReactionResponseDTO.class);

            // Then
            assertThat(dto.getId()).isEqualTo(789L);
            assertThat(dto.getUserId()).isEqualTo(12L);
            assertThat(dto.getReactionType()).isEqualTo("LAUGH");
            assertThat(dto.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // Given
            ReactionResponseDTO dto = new ReactionResponseDTO();

            // When & Then
            assertThat(dto.getId()).isNull();
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getReactionType()).isNull();
            assertThat(dto.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("Should handle equals and hashCode correctly")
        void shouldHandleEqualsAndHashCodeCorrectly() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();

            ReactionResponseDTO dto1 = ReactionResponseDTO.builder()
                    .id(1L)
                    .userId(2L)
                    .reactionType("LIKE")
                    .createdAt(now)
                    .build();

            ReactionResponseDTO dto2 = ReactionResponseDTO.builder()
                    .id(1L)
                    .userId(2L)
                    .reactionType("LIKE")
                    .createdAt(now)
                    .build();

            ReactionResponseDTO dto3 = ReactionResponseDTO.builder()
                    .id(2L)
                    .userId(2L)
                    .reactionType("LIKE")
                    .createdAt(now)
                    .build();

            // Then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).isNotEqualTo(dto3);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionResponseDTO dto = ReactionResponseDTO.builder()
                    .id(1L)
                    .userId(2L)
                    .reactionType("LIKE")
                    .createdAt(now)
                    .build();

            // When
            String toString = dto.toString();

            // Then
            assertThat(toString).contains("ReactionResponseDTO");
            assertThat(toString).contains("id=1");
            assertThat(toString).contains("userId=2");
            assertThat(toString).contains("reactionType=LIKE");
            assertThat(toString).contains("createdAt=");
        }

        @Test
        @DisplayName("Should test builder pattern thoroughly")
        void shouldTestBuilderPatternThoroughly() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();

            // When
            ReactionResponseDTO dto = ReactionResponseDTO.builder()
                    .id(100L)
                    .userId(200L)
                    .reactionType("LOVE")
                    .createdAt(now)
                    .build();

            // Then
            assertThat(dto.getId()).isEqualTo(100L);
            assertThat(dto.getUserId()).isEqualTo(200L);
            assertThat(dto.getReactionType()).isEqualTo("LOVE");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should test partial builder usage")
        void shouldTestPartialBuilderUsage() {
            // When
            ReactionResponseDTO dto = ReactionResponseDTO.builder()
                    .id(1L)
                    .reactionType("LIKE")
                    .build();

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getReactionType()).isEqualTo("LIKE");
            assertThat(dto.getCreatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Cross-DTO Integration Tests")
    class CrossDTOIntegrationTests {

        @Test
        @DisplayName("Should convert ReactionCreateRequestDTO to ReactionResponseDTO")
        void shouldConvertReactionCreateRequestDTOToReactionResponseDTO() {
            // Given
            ReactionCreateRequestDTO requestDTO = new ReactionCreateRequestDTO();
            requestDTO.setUserId(1L);
            requestDTO.setReactionType("LIKE");

            ZonedDateTime now = ZonedDateTime.now();

            // When
            ReactionResponseDTO responseDTO = ReactionResponseDTO.builder()
                    .id(123L)
                    .userId(requestDTO.getUserId())
                    .reactionType(requestDTO.getReactionType())
                    .createdAt(now)
                    .build();

            // Then
            assertThat(responseDTO.getUserId()).isEqualTo(requestDTO.getUserId());
            assertThat(responseDTO.getReactionType()).isEqualTo(requestDTO.getReactionType());
            assertThat(responseDTO.getId()).isEqualTo(123L);
            assertThat(responseDTO.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should convert ReactionResponseDTO to ReactionDetailDTO")
        void shouldConvertReactionResponseDTOToReactionDetailDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            ReactionResponseDTO responseDTO = ReactionResponseDTO.builder()
                    .id(123L)
                    .userId(1L)
                    .reactionType("LIKE")
                    .createdAt(now)
                    .build();

            // When
            ReactionDetailDTO detailDTO = new ReactionDetailDTO();
            detailDTO.setUserId(responseDTO.getUserId());
            detailDTO.setReactionType(responseDTO.getReactionType());
            detailDTO.setCreatedAt(responseDTO.getCreatedAt());

            // Then
            assertThat(detailDTO.getUserId()).isEqualTo(responseDTO.getUserId());
            assertThat(detailDTO.getReactionType()).isEqualTo(responseDTO.getReactionType());
            assertThat(detailDTO.getCreatedAt()).isEqualTo(responseDTO.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle different reaction types across DTOs")
        void shouldHandleDifferentReactionTypesAcrossDTOs() {
            // Given
            String[] reactionTypes = {"LIKE", "LOVE", "HEART", "LAUGH", "ANGRY", "SAD"};

            for (String reactionType : reactionTypes) {
                // When
                ReactionCreateRequestDTO requestDTO = new ReactionCreateRequestDTO();
                requestDTO.setUserId(1L);
                requestDTO.setReactionType(reactionType);

                ReactionDetailDTO detailDTO = new ReactionDetailDTO();
                detailDTO.setUserId(1L);
                detailDTO.setReactionType(reactionType);
                detailDTO.setCreatedAt(ZonedDateTime.now());

                ReactionResponseDTO responseDTO = ReactionResponseDTO.builder()
                        .id(1L)
                        .userId(1L)
                        .reactionType(reactionType)
                        .createdAt(ZonedDateTime.now())
                        .build();

                // Then
                assertThat(requestDTO.getReactionType()).isEqualTo(reactionType);
                assertThat(detailDTO.getReactionType()).isEqualTo(reactionType);
                assertThat(responseDTO.getReactionType()).isEqualTo(reactionType);
            }
        }
    }
}