package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteCreationDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteResponseDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteUpdateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PollVoteDTOTests {

    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
    }

    @Nested
    class PollVoteCreationDTOTest {

        @Test
        void constructor_NoArgs_Success() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO();

            assertNotNull(dto);
            assertNull(dto.getPollId());
            assertNull(dto.getSelectedOption());
        }

        @Test
        void constructor_AllArgs_Success() {
            Long pollId = 1L;
            String selectedOption = "Option A";

            PollVoteCreationDTO dto = new PollVoteCreationDTO(pollId, selectedOption);

            assertNotNull(dto);
            assertEquals(pollId, dto.getPollId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void settersAndGetters_Success() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO();
            Long pollId = 1L;
            String selectedOption = "Option A";

            dto.setPollId(pollId);
            dto.setSelectedOption(selectedOption);

            assertEquals(pollId, dto.getPollId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void validation_ValidData_NoViolations() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "Option A");

            Set<ConstraintViolation<PollVoteCreationDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
        }

        @Test
        void validation_NullPollId_Violation() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(null, "Option A");

            Set<ConstraintViolation<PollVoteCreationDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteCreationDTO> violation = violations.iterator().next();
            assertEquals("Poll ID is required", violation.getMessage());
            assertEquals("pollId", violation.getPropertyPath().toString());
        }

        @Test
        void validation_NullSelectedOption_Violation() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, null);

            Set<ConstraintViolation<PollVoteCreationDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteCreationDTO> violation = violations.iterator().next();
            assertEquals("Selected option is required", violation.getMessage());
            assertEquals("selectedOption", violation.getPropertyPath().toString());
        }

        @Test
        void validation_BlankSelectedOption_Violation() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "");

            Set<ConstraintViolation<PollVoteCreationDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteCreationDTO> violation = violations.iterator().next();
            assertEquals("Selected option is required", violation.getMessage());
            assertEquals("selectedOption", violation.getPropertyPath().toString());
        }

        @Test
        void validation_WhitespaceSelectedOption_Violation() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "   ");

            Set<ConstraintViolation<PollVoteCreationDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteCreationDTO> violation = violations.iterator().next();
            assertEquals("Selected option is required", violation.getMessage());
            assertEquals("selectedOption", violation.getPropertyPath().toString());
        }

        @Test
        void validation_MultipleViolations() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(null, null);

            Set<ConstraintViolation<PollVoteCreationDTO>> violations = validator.validate(dto);

            assertEquals(2, violations.size());
        }

        @Test
        void jsonSerialization_Success() throws Exception {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "Option A");

            String json = objectMapper.writeValueAsString(dto);

            assertTrue(json.contains("\"poll_id\":1"));
            assertTrue(json.contains("\"selected_option\":\"Option A\""));
        }

        @Test
        void jsonDeserialization_Success() throws Exception {
            String json = "{\"poll_id\":1,\"selected_option\":\"Option A\"}";

            PollVoteCreationDTO dto = objectMapper.readValue(json, PollVoteCreationDTO.class);

            assertEquals(1L, dto.getPollId());
            assertEquals("Option A", dto.getSelectedOption());
        }

        @Test
        void equals_SameValues_True() {
            PollVoteCreationDTO dto1 = new PollVoteCreationDTO(1L, "Option A");
            PollVoteCreationDTO dto2 = new PollVoteCreationDTO(1L, "Option A");

            assertEquals(dto1, dto2);
        }

        @Test
        void equals_DifferentValues_False() {
            PollVoteCreationDTO dto1 = new PollVoteCreationDTO(1L, "Option A");
            PollVoteCreationDTO dto2 = new PollVoteCreationDTO(2L, "Option B");

            assertNotEquals(dto1, dto2);
        }

        @Test
        void equals_SameReference_True() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "Option A");

            assertEquals(dto, dto);
        }

        @Test
        void equals_NullComparison_False() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "Option A");

            assertNotEquals(dto, null);
        }

        @Test
        void equals_DifferentClass_False() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "Option A");
            String other = "not a dto";

            assertNotEquals(dto, other);
        }

        @Test
        void hashCode_SameValues_Equal() {
            PollVoteCreationDTO dto1 = new PollVoteCreationDTO(1L, "Option A");
            PollVoteCreationDTO dto2 = new PollVoteCreationDTO(1L, "Option A");

            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        void toString_ContainsAllFields() {
            PollVoteCreationDTO dto = new PollVoteCreationDTO(1L, "Option A");

            String toString = dto.toString();

            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Option A"));
            assertTrue(toString.contains("PollVoteCreationDTO"));
        }
    }

    @Nested
    class PollVoteResponseDTOTest {

        @Test
        void constructor_NoArgs_Success() {
            PollVoteResponseDTO dto = new PollVoteResponseDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getPollId());
            assertNull(dto.getUserId());
            assertNull(dto.getSelectedOption());
        }

        @Test
        void constructor_AllArgs_Success() {
            Long id = 1L;
            Long pollId = 2L;
            Long userId = 3L;
            String selectedOption = "Option A";

            PollVoteResponseDTO dto = new PollVoteResponseDTO(id, pollId, userId, selectedOption);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(pollId, dto.getPollId());
            assertEquals(userId, dto.getUserId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void builder_Success() {
            Long id = 1L;
            Long pollId = 2L;
            Long userId = 3L;
            String selectedOption = "Option A";

            PollVoteResponseDTO dto = PollVoteResponseDTO.builder()
                    .id(id)
                    .pollId(pollId)
                    .userId(userId)
                    .selectedOption(selectedOption)
                    .build();

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(pollId, dto.getPollId());
            assertEquals(userId, dto.getUserId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void settersAndGetters_Success() {
            PollVoteResponseDTO dto = new PollVoteResponseDTO();
            Long id = 1L;
            Long pollId = 2L;
            Long userId = 3L;
            String selectedOption = "Option A";

            dto.setId(id);
            dto.setPollId(pollId);
            dto.setUserId(userId);
            dto.setSelectedOption(selectedOption);

            assertEquals(id, dto.getId());
            assertEquals(pollId, dto.getPollId());
            assertEquals(userId, dto.getUserId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void validation_NoConstraints_NoViolations() {
            // PollVoteResponseDTO has no validation constraints
            PollVoteResponseDTO dto = new PollVoteResponseDTO(null, null, null, null);

            Set<ConstraintViolation<PollVoteResponseDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
        }

        @Test
        void jsonSerialization_Success() throws Exception {
            PollVoteResponseDTO dto = PollVoteResponseDTO.builder()
                    .id(1L)
                    .pollId(2L)
                    .userId(3L)
                    .selectedOption("Option A")
                    .build();

            String json = objectMapper.writeValueAsString(dto);

            assertTrue(json.contains("\"id\":1"));
            assertTrue(json.contains("\"poll_id\":2"));
            assertTrue(json.contains("\"user_id\":3"));
            assertTrue(json.contains("\"selected_option\":\"Option A\""));
        }

        @Test
        void jsonDeserialization_Success() throws Exception {
            String json = "{\"id\":1,\"poll_id\":2,\"user_id\":3,\"selected_option\":\"Option A\"}";

            PollVoteResponseDTO dto = objectMapper.readValue(json, PollVoteResponseDTO.class);

            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getPollId());
            assertEquals(3L, dto.getUserId());
            assertEquals("Option A", dto.getSelectedOption());
        }

        @Test
        void equals_SameValues_True() {
            PollVoteResponseDTO dto1 = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");
            PollVoteResponseDTO dto2 = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");

            assertEquals(dto1, dto2);
        }

        @Test
        void equals_DifferentValues_False() {
            PollVoteResponseDTO dto1 = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");
            PollVoteResponseDTO dto2 = new PollVoteResponseDTO(2L, 3L, 4L, "Option B");

            assertNotEquals(dto1, dto2);
        }

        @Test
        void hashCode_SameValues_Equal() {
            PollVoteResponseDTO dto1 = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");
            PollVoteResponseDTO dto2 = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");

            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        void toString_ContainsAllFields() {
            PollVoteResponseDTO dto = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");

            String toString = dto.toString();

            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("2"));
            assertTrue(toString.contains("3"));
            assertTrue(toString.contains("Option A"));
            assertTrue(toString.contains("PollVoteResponseDTO"));
        }
    }

    @Nested
    class PollVoteUpdateDTOTest {

        @Test
        void constructor_NoArgs_Success() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO();

            assertNotNull(dto);
            assertNull(dto.getPollId());
            assertNull(dto.getUserId());
            assertNull(dto.getSelectedOption());
        }

        @Test
        void constructor_AllArgs_Success() {
            Long pollId = 1L;
            Long userId = 2L;
            String selectedOption = "Option A";

            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(pollId, userId, selectedOption);

            assertNotNull(dto);
            assertEquals(pollId, dto.getPollId());
            assertEquals(userId, dto.getUserId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void settersAndGetters_Success() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO();
            Long pollId = 1L;
            Long userId = 2L;
            String selectedOption = "Option A";

            dto.setPollId(pollId);
            dto.setUserId(userId);
            dto.setSelectedOption(selectedOption);

            assertEquals(pollId, dto.getPollId());
            assertEquals(userId, dto.getUserId());
            assertEquals(selectedOption, dto.getSelectedOption());
        }

        @Test
        void validation_ValidData_NoViolations() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, 2L, "Option A");

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
        }

        @Test
        void validation_NullPollId_Violation() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(null, 2L, "Option A");

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteUpdateDTO> violation = violations.iterator().next();
            assertEquals("Poll ID is required", violation.getMessage());
            assertEquals("pollId", violation.getPropertyPath().toString());
        }

        @Test
        void validation_NullUserId_Violation() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, null, "Option A");

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteUpdateDTO> violation = violations.iterator().next();
            assertEquals("User ID is required", violation.getMessage());
            assertEquals("userId", violation.getPropertyPath().toString());
        }

        @Test
        void validation_NullSelectedOption_Violation() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, 2L, null);

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteUpdateDTO> violation = violations.iterator().next();
            assertEquals("Selected option is required", violation.getMessage());
            assertEquals("selectedOption", violation.getPropertyPath().toString());
        }

        @Test
        void validation_BlankSelectedOption_Violation() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, 2L, "");

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteUpdateDTO> violation = violations.iterator().next();
            assertEquals("Selected option is required", violation.getMessage());
            assertEquals("selectedOption", violation.getPropertyPath().toString());
        }

        @Test
        void validation_WhitespaceSelectedOption_Violation() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, 2L, "   ");

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertEquals(1, violations.size());
            ConstraintViolation<PollVoteUpdateDTO> violation = violations.iterator().next();
            assertEquals("Selected option is required", violation.getMessage());
            assertEquals("selectedOption", violation.getPropertyPath().toString());
        }

        @Test
        void validation_AllFieldsNull_MultipleViolations() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(null, null, null);

            Set<ConstraintViolation<PollVoteUpdateDTO>> violations = validator.validate(dto);

            assertEquals(3, violations.size());
        }

        @Test
        void jsonSerialization_Success() throws Exception {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, 2L, "Option A");

            String json = objectMapper.writeValueAsString(dto);

            assertTrue(json.contains("\"poll_id\":1"));
            assertTrue(json.contains("\"user_id\":2"));
            assertTrue(json.contains("\"selected_option\":\"Option A\""));
        }

        @Test
        void jsonDeserialization_Success() throws Exception {
            String json = "{\"poll_id\":1,\"user_id\":2,\"selected_option\":\"Option A\"}";

            PollVoteUpdateDTO dto = objectMapper.readValue(json, PollVoteUpdateDTO.class);

            assertEquals(1L, dto.getPollId());
            assertEquals(2L, dto.getUserId());
            assertEquals("Option A", dto.getSelectedOption());
        }

        @Test
        void equals_SameValues_True() {
            PollVoteUpdateDTO dto1 = new PollVoteUpdateDTO(1L, 2L, "Option A");
            PollVoteUpdateDTO dto2 = new PollVoteUpdateDTO(1L, 2L, "Option A");

            assertEquals(dto1, dto2);
        }

        @Test
        void equals_DifferentValues_False() {
            PollVoteUpdateDTO dto1 = new PollVoteUpdateDTO(1L, 2L, "Option A");
            PollVoteUpdateDTO dto2 = new PollVoteUpdateDTO(2L, 3L, "Option B");

            assertNotEquals(dto1, dto2);
        }

        @Test
        void hashCode_SameValues_Equal() {
            PollVoteUpdateDTO dto1 = new PollVoteUpdateDTO(1L, 2L, "Option A");
            PollVoteUpdateDTO dto2 = new PollVoteUpdateDTO(1L, 2L, "Option A");

            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        void toString_ContainsAllFields() {
            PollVoteUpdateDTO dto = new PollVoteUpdateDTO(1L, 2L, "Option A");

            String toString = dto.toString();

            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("2"));
            assertTrue(toString.contains("Option A"));
            assertTrue(toString.contains("PollVoteUpdateDTO"));
        }
    }

    @Nested
    class CrossDTOTest {

        @Test
        void dtoInteroperability_CreationToUpdate() {
            // Test that data can be transferred between DTOs
            PollVoteCreationDTO creationDTO = new PollVoteCreationDTO(1L, "Option A");

            PollVoteUpdateDTO updateDTO = new PollVoteUpdateDTO();
            updateDTO.setPollId(creationDTO.getPollId());
            updateDTO.setUserId(2L); // Additional field in update
            updateDTO.setSelectedOption(creationDTO.getSelectedOption());

            assertEquals(creationDTO.getPollId(), updateDTO.getPollId());
            assertEquals(creationDTO.getSelectedOption(), updateDTO.getSelectedOption());
            assertEquals(2L, updateDTO.getUserId());
        }

        @Test
        void dtoInteroperability_ResponseToUpdate() {
            // Test that data can be transferred from response to update
            PollVoteResponseDTO responseDTO = PollVoteResponseDTO.builder()
                    .id(1L)
                    .pollId(2L)
                    .userId(3L)
                    .selectedOption("Option A")
                    .build();

            PollVoteUpdateDTO updateDTO = new PollVoteUpdateDTO();
            updateDTO.setPollId(responseDTO.getPollId());
            updateDTO.setUserId(responseDTO.getUserId());
            updateDTO.setSelectedOption("Option B"); // Changed option

            assertEquals(responseDTO.getPollId(), updateDTO.getPollId());
            assertEquals(responseDTO.getUserId(), updateDTO.getUserId());
            assertNotEquals(responseDTO.getSelectedOption(), updateDTO.getSelectedOption());
        }

        @Test
        void jsonNamingStrategy_ConsistentAcrossDTOs() throws Exception {
            // Test that all DTOs use consistent snake_case naming
            PollVoteCreationDTO creationDTO = new PollVoteCreationDTO(1L, "Option A");
            PollVoteResponseDTO responseDTO = new PollVoteResponseDTO(1L, 2L, 3L, "Option A");
            PollVoteUpdateDTO updateDTO = new PollVoteUpdateDTO(1L, 2L, "Option A");

            String creationJson = objectMapper.writeValueAsString(creationDTO);
            String responseJson = objectMapper.writeValueAsString(responseDTO);
            String updateJson = objectMapper.writeValueAsString(updateDTO);

            // All should use snake_case
            assertTrue(creationJson.contains("poll_id"));
            assertTrue(creationJson.contains("selected_option"));
            assertTrue(responseJson.contains("poll_id"));
            assertTrue(responseJson.contains("user_id"));
            assertTrue(responseJson.contains("selected_option"));
            assertTrue(updateJson.contains("poll_id"));
            assertTrue(updateJson.contains("user_id"));
            assertTrue(updateJson.contains("selected_option"));
        }
    }
}