package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.teamsync.teamsync.dto.projectDTO.*;
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

class ProjectDTOTests {

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
    @DisplayName("AddMemberDTO Tests")
    class AddMemberDTOTests {

        @Test
        @DisplayName("Valid AddMemberDTO should pass validation")
        void validAddMemberDTO_shouldPassValidation() {
            AddMemberDTO dto = AddMemberDTO.builder()
                    .userId(1L)
                    .role("MEMBER")
                    .build();

            Set<ConstraintViolation<AddMemberDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("AddMemberDTO with null userId should fail validation")
        void addMemberDTO_withNullUserId_shouldFailValidation() {
            AddMemberDTO dto = AddMemberDTO.builder()
                    .userId(null)
                    .role("MEMBER")
                    .build();

            Set<ConstraintViolation<AddMemberDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Id is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AddMemberDTO with null role should fail validation")
        void addMemberDTO_withNullRole_shouldFailValidation() {
            AddMemberDTO dto = AddMemberDTO.builder()
                    .userId(1L)
                    .role(null)
                    .build();

            Set<ConstraintViolation<AddMemberDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Role is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("AddMemberDTO JSON serialization should use snake_case")
        void addMemberDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            AddMemberDTO dto = AddMemberDTO.builder()
                    .userId(1L)
                    .role("ADMIN")
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("user_id"));
            assertTrue(json.contains("role"));
        }

        @Test
        @DisplayName("AddMemberDTO JSON deserialization should work with snake_case")
        void addMemberDTO_jsonDeserialization_shouldWorkWithSnakeCase() throws Exception {
            String json = "{\"user_id\":1,\"role\":\"ADMIN\"}";

            AddMemberDTO dto = objectMapper.readValue(json, AddMemberDTO.class);
            assertEquals(1L, dto.getUserId());
            assertEquals("ADMIN", dto.getRole());
        }

        @Test
        @DisplayName("AddMemberDTO builder should work correctly")
        void addMemberDTO_builder_shouldWorkCorrectly() {
            AddMemberDTO dto = AddMemberDTO.builder()
                    .userId(5L)
                    .role("MODERATOR")
                    .build();

            assertEquals(5L, dto.getUserId());
            assertEquals("MODERATOR", dto.getRole());
        }

        @Test
        @DisplayName("AddMemberDTO equals and hashCode should work")
        void addMemberDTO_equalsAndHashCode_shouldWork() {
            AddMemberDTO dto1 = AddMemberDTO.builder()
                    .userId(1L)
                    .role("MEMBER")
                    .build();

            AddMemberDTO dto2 = AddMemberDTO.builder()
                    .userId(1L)
                    .role("MEMBER")
                    .build();

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("InitialMemberDTO Tests")
    class InitialMemberDTOTests {

        @Test
        @DisplayName("Valid InitialMemberDTO should pass validation")
        void validInitialMemberDTO_shouldPassValidation() {
            InitialMemberDTO dto = InitialMemberDTO.builder()
                    .userId(1L)
                    .role("OWNER")
                    .build();

            Set<ConstraintViolation<InitialMemberDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("InitialMemberDTO with null userId should fail validation")
        void initialMemberDTO_withNullUserId_shouldFailValidation() {
            InitialMemberDTO dto = InitialMemberDTO.builder()
                    .userId(null)
                    .role("OWNER")
                    .build();

            Set<ConstraintViolation<InitialMemberDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Id is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("InitialMemberDTO with null role should fail validation")
        void initialMemberDTO_withNullRole_shouldFailValidation() {
            InitialMemberDTO dto = InitialMemberDTO.builder()
                    .userId(1L)
                    .role(null)
                    .build();

            Set<ConstraintViolation<InitialMemberDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Role is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("InitialMemberDTO JSON serialization should use snake_case")
        void initialMemberDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            InitialMemberDTO dto = InitialMemberDTO.builder()
                    .userId(2L)
                    .role("OWNER")
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("user_id"));
            assertTrue(json.contains("role"));
        }
    }

    @Nested
    @DisplayName("ProjectCreationDTO Tests")
    class ProjectCreationDTOTests {

        @Test
        @DisplayName("Valid ProjectCreationDTO should pass validation")
        void validProjectCreationDTO_shouldPassValidation() {
            List<InitialMemberDTO> members = Arrays.asList(
                    InitialMemberDTO.builder().userId(1L).role("OWNER").build(),
                    InitialMemberDTO.builder().userId(2L).role("MEMBER").build()
            );

            ProjectCreationDTO dto = ProjectCreationDTO.builder()
                    .title("Test Project")
                    .description("A test project description")
                    .initialMembers(members)
                    .build();

            Set<ConstraintViolation<ProjectCreationDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("ProjectCreationDTO with null title should fail validation")
        void projectCreationDTO_withNullTitle_shouldFailValidation() {
            ProjectCreationDTO dto = ProjectCreationDTO.builder()
                    .title(null)
                    .description("A test project description")
                    .build();

            Set<ConstraintViolation<ProjectCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Title is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectCreationDTO with null description should fail validation")
        void projectCreationDTO_withNullDescription_shouldFailValidation() {
            ProjectCreationDTO dto = ProjectCreationDTO.builder()
                    .title("Test Project")
                    .description(null)
                    .build();

            Set<ConstraintViolation<ProjectCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Description is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectCreationDTO with title exceeding 100 characters should fail validation")
        void projectCreationDTO_withLongTitle_shouldFailValidation() {
            String longTitle = "a".repeat(101);
            ProjectCreationDTO dto = ProjectCreationDTO.builder()
                    .title(longTitle)
                    .description("A test project description")
                    .build();

            Set<ConstraintViolation<ProjectCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Title must be at most 100 characters", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectCreationDTO with description exceeding 1000 characters should fail validation")
        void projectCreationDTO_withLongDescription_shouldFailValidation() {
            String longDescription = "a".repeat(1001);
            ProjectCreationDTO dto = ProjectCreationDTO.builder()
                    .title("Test Project")
                    .description(longDescription)
                    .build();

            Set<ConstraintViolation<ProjectCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Description must be at most 1000 characters", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectCreationDTO JSON serialization should use snake_case")
        void projectCreationDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            List<InitialMemberDTO> members = Arrays.asList(
                    InitialMemberDTO.builder().userId(1L).role("OWNER").build()
            );

            ProjectCreationDTO dto = ProjectCreationDTO.builder()
                    .title("Test Project")
                    .description("A test project description")
                    .initialMembers(members)
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("initial_members"));
            assertTrue(json.contains("title"));
            assertTrue(json.contains("description"));
        }
    }

    @Nested
    @DisplayName("ProjectDTO Tests")
    class ProjectDTOUnitTests {

        @Test
        @DisplayName("ProjectDTO should be created successfully")
        void projectDTO_shouldBeCreatedSuccessfully() {
            ZonedDateTime now = ZonedDateTime.now();
            List<ProjectMemberDTO> members = Arrays.asList(
                    ProjectMemberDTO.builder().userId(1L).role("OWNER").joinedAt(now).build()
            );

            ProjectDTO dto = ProjectDTO.builder()
                    .id(1L)
                    .title("Test Project")
                    .description("A test project")
                    .createdBy(1L)
                    .createdAt(now)
                    .members(members)
                    .build();

            assertEquals(1L, dto.getId());
            assertEquals("Test Project", dto.getTitle());
            assertEquals("A test project", dto.getDescription());
            assertEquals(1L, dto.getCreatedBy());
            assertEquals(now, dto.getCreatedAt());
            assertEquals(1, dto.getMembers().size());
        }

        @Test
        @DisplayName("ProjectDTO JSON serialization should use snake_case")
        void projectDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            ZonedDateTime now = ZonedDateTime.now();
            List<ProjectMemberDTO> members = Arrays.asList(
                    ProjectMemberDTO.builder().userId(1L).role("OWNER").joinedAt(now).build()
            );

            ProjectDTO dto = ProjectDTO.builder()
                    .id(1L)
                    .title("Test Project")
                    .description("A test project")
                    .createdBy(1L)
                    .createdAt(now)
                    .members(members)
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("created_by"));
            assertTrue(json.contains("created_at"));
            assertTrue(json.contains("members"));
        }
    }

    @Nested
    @DisplayName("ProjectMemberDTO Tests")
    class ProjectMemberDTOTests {

        @Test
        @DisplayName("ProjectMemberDTO should be created successfully")
        void projectMemberDTO_shouldBeCreatedSuccessfully() {
            ZonedDateTime joinedAt = ZonedDateTime.now();

            ProjectMemberDTO dto = ProjectMemberDTO.builder()
                    .userId(1L)
                    .role("MEMBER")
                    .joinedAt(joinedAt)
                    .build();

            assertEquals(1L, dto.getUserId());
            assertEquals("MEMBER", dto.getRole());
            assertEquals(joinedAt, dto.getJoinedAt());
        }

        @Test
        @DisplayName("ProjectMemberDTO JSON serialization should use snake_case")
        void projectMemberDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            ZonedDateTime joinedAt = ZonedDateTime.now();

            ProjectMemberDTO dto = ProjectMemberDTO.builder()
                    .userId(1L)
                    .role("MEMBER")
                    .joinedAt(joinedAt)
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("user_id"));
            assertTrue(json.contains("joined_at"));
        }

        @Test
        @DisplayName("ProjectMemberDTO JSON deserialization should work with snake_case")
        void projectMemberDTO_jsonDeserialization_shouldWorkWithSnakeCase() throws Exception {
            String json = "{\"user_id\":1,\"role\":\"MEMBER\",\"joined_at\":\"2023-01-01T00:00:00Z\"}";

            ProjectMemberDTO dto = objectMapper.readValue(json, ProjectMemberDTO.class);
            assertEquals(1L, dto.getUserId());
            assertEquals("MEMBER", dto.getRole());
            assertNotNull(dto.getJoinedAt());
        }
    }

    @Nested
    @DisplayName("ProjectUpdateDTO Tests")
    class ProjectUpdateDTOTests {

        @Test
        @DisplayName("Valid ProjectUpdateDTO should pass validation")
        void validProjectUpdateDTO_shouldPassValidation() {
            ZonedDateTime pastDate = ZonedDateTime.now().minusDays(1);
            List<ProjectMemberDTO> members = Arrays.asList(
                    ProjectMemberDTO.builder().userId(1L).role("OWNER").joinedAt(pastDate).build()
            );

            ProjectUpdateDTO dto = ProjectUpdateDTO.builder()
                    .title("Updated Project")
                    .description("Updated description")
                    .createdBy(1L)
                    .createdAt(pastDate)
                    .members(members)
                    .build();

            Set<ConstraintViolation<ProjectUpdateDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("ProjectUpdateDTO with title exceeding 100 characters should fail validation")
        void projectUpdateDTO_withLongTitle_shouldFailValidation() {
            String longTitle = "a".repeat(101);
            ProjectUpdateDTO dto = ProjectUpdateDTO.builder()
                    .title(longTitle)
                    .description("Updated description")
                    .build();

            Set<ConstraintViolation<ProjectUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Title must be at most 100 characters", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectUpdateDTO with description exceeding 1000 characters should fail validation")
        void projectUpdateDTO_withLongDescription_shouldFailValidation() {
            String longDescription = "a".repeat(1001);
            ProjectUpdateDTO dto = ProjectUpdateDTO.builder()
                    .title("Updated Project")
                    .description(longDescription)
                    .build();

            Set<ConstraintViolation<ProjectUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Description must be at most 1000 characters", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectUpdateDTO with future createdAt should fail validation")
        void projectUpdateDTO_withFutureCreatedAt_shouldFailValidation() {
            ZonedDateTime futureDate = ZonedDateTime.now().plusDays(1);
            ProjectUpdateDTO dto = ProjectUpdateDTO.builder()
                    .title("Updated Project")
                    .description("Updated description")
                    .createdAt(futureDate)
                    .build();

            Set<ConstraintViolation<ProjectUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("createdAt must be in the past or present", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("ProjectUpdateDTO JSON serialization should use snake_case")
        void projectUpdateDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            ZonedDateTime pastDate = ZonedDateTime.now().minusDays(1);

            ProjectUpdateDTO dto = ProjectUpdateDTO.builder()
                    .title("Updated Project")
                    .description("Updated description")
                    .createdBy(1L)
                    .createdAt(pastDate)
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("created_by"));
            assertTrue(json.contains("created_at"));
        }
    }

    @Nested
    @DisplayName("UpdateMemberRoleDTO Tests")
    class UpdateMemberRoleDTOTests {

        @Test
        @DisplayName("Valid UpdateMemberRoleDTO should pass validation")
        void validUpdateMemberRoleDTO_shouldPassValidation() {
            UpdateMemberRoleDTO dto = UpdateMemberRoleDTO.builder()
                    .role("ADMIN")
                    .build();

            Set<ConstraintViolation<UpdateMemberRoleDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("UpdateMemberRoleDTO with null role should fail validation")
        void updateMemberRoleDTO_withNullRole_shouldFailValidation() {
            UpdateMemberRoleDTO dto = UpdateMemberRoleDTO.builder()
                    .role(null)
                    .build();

            Set<ConstraintViolation<UpdateMemberRoleDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Role is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("UpdateMemberRoleDTO JSON serialization should use snake_case")
        void updateMemberRoleDTO_jsonSerialization_shouldUseSnakeCase() throws Exception {
            UpdateMemberRoleDTO dto = UpdateMemberRoleDTO.builder()
                    .role("MODERATOR")
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            assertTrue(json.contains("role"));
        }

        @Test
        @DisplayName("UpdateMemberRoleDTO JSON deserialization should work with snake_case")
        void updateMemberRoleDTO_jsonDeserialization_shouldWorkWithSnakeCase() throws Exception {
            String json = "{\"role\":\"ADMIN\"}";

            UpdateMemberRoleDTO dto = objectMapper.readValue(json, UpdateMemberRoleDTO.class);
            assertEquals("ADMIN", dto.getRole());
        }

        @Test
        @DisplayName("UpdateMemberRoleDTO builder should work correctly")
        void updateMemberRoleDTO_builder_shouldWorkCorrectly() {
            UpdateMemberRoleDTO dto = UpdateMemberRoleDTO.builder()
                    .role("EDITOR")
                    .build();

            assertEquals("EDITOR", dto.getRole());
        }

        @Test
        @DisplayName("UpdateMemberRoleDTO equals and hashCode should work")
        void updateMemberRoleDTO_equalsAndHashCode_shouldWork() {
            UpdateMemberRoleDTO dto1 = UpdateMemberRoleDTO.builder()
                    .role("ADMIN")
                    .build();

            UpdateMemberRoleDTO dto2 = UpdateMemberRoleDTO.builder()
                    .role("ADMIN")
                    .build();

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Complete project creation workflow should work")
        void completeProjectCreationWorkflow_shouldWork() throws Exception {
            // Create initial members
            List<InitialMemberDTO> initialMembers = Arrays.asList(
                    InitialMemberDTO.builder().userId(1L).role("OWNER").build(),
                    InitialMemberDTO.builder().userId(2L).role("MEMBER").build()
            );

            // Create project creation DTO
            ProjectCreationDTO creationDTO = ProjectCreationDTO.builder()
                    .title("Integration Test Project")
                    .description("A project for integration testing")
                    .initialMembers(initialMembers)
                    .build();

            // Validate creation DTO
            Set<ConstraintViolation<ProjectCreationDTO>> violations = validator.validate(creationDTO);
            assertTrue(violations.isEmpty());

            // Serialize to JSON
            String json = objectMapper.writeValueAsString(creationDTO);
            assertNotNull(json);

            // Deserialize from JSON
            ProjectCreationDTO deserializedDTO = objectMapper.readValue(json, ProjectCreationDTO.class);
            assertEquals(creationDTO.getTitle(), deserializedDTO.getTitle());
            assertEquals(creationDTO.getDescription(), deserializedDTO.getDescription());
            assertEquals(creationDTO.getInitialMembers().size(), deserializedDTO.getInitialMembers().size());
        }

        @Test
        @DisplayName("Project member management workflow should work")
        void projectMemberManagementWorkflow_shouldWork() throws Exception {
            // Add member
            AddMemberDTO addMemberDTO = AddMemberDTO.builder()
                    .userId(3L)
                    .role("CONTRIBUTOR")
                    .build();

            Set<ConstraintViolation<AddMemberDTO>> addViolations = validator.validate(addMemberDTO);
            assertTrue(addViolations.isEmpty());

            // Update member role
            UpdateMemberRoleDTO updateRoleDTO = UpdateMemberRoleDTO.builder()
                    .role("ADMIN")
                    .build();

            Set<ConstraintViolation<UpdateMemberRoleDTO>> updateViolations = validator.validate(updateRoleDTO);
            assertTrue(updateViolations.isEmpty());

            // Test JSON serialization/deserialization
            String addJson = objectMapper.writeValueAsString(addMemberDTO);
            String updateJson = objectMapper.writeValueAsString(updateRoleDTO);

            AddMemberDTO deserializedAddDTO = objectMapper.readValue(addJson, AddMemberDTO.class);
            UpdateMemberRoleDTO deserializedUpdateDTO = objectMapper.readValue(updateJson, UpdateMemberRoleDTO.class);

            assertEquals(addMemberDTO.getUserId(), deserializedAddDTO.getUserId());
            assertEquals(addMemberDTO.getRole(), deserializedAddDTO.getRole());
            assertEquals(updateRoleDTO.getRole(), deserializedUpdateDTO.getRole());
        }
    }
}