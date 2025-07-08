package edu.teamsync.teamsync.dtos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskStatusHistoryDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
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

@DisplayName("Task DTO Tests")
class TaskDTOTests {

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
    @DisplayName("TaskCreationDTO Tests")
    class TaskCreationDTOTests {

        @Test
        @DisplayName("Should create TaskCreationDTO with valid data")
        void shouldCreateTaskCreationDTOWithValidData() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .status("OPEN")
                    .assignedTo(1L)
                    .deadline(ZonedDateTime.now().plusDays(1))
                    .priority("HIGH")
                    .parentTaskId(2L)
                    .projectId(3L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("Test Task", dto.getTitle());
            assertEquals("Test Description", dto.getDescription());
            assertEquals("OPEN", dto.getStatus());
            assertEquals(1L, dto.getAssignedTo());
            assertEquals("HIGH", dto.getPriority());
            assertEquals(2L, dto.getParentTaskId());
            assertEquals(3L, dto.getProjectId());
        }

        @Test
        @DisplayName("Should fail validation when title is blank")
        void shouldFailValidationWhenTitleIsBlank() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("")
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
        }

        @Test
        @DisplayName("Should fail validation when title is null")
        void shouldFailValidationWhenTitleIsNull() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title(null)
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
        }

        @Test
        @DisplayName("Should fail validation when title exceeds 100 characters")
        void shouldFailValidationWhenTitleExceeds100Characters() {
            // Given
            String longTitle = "a".repeat(101);
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title(longTitle)
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title must be at most 100 characters")));
        }

        @Test
        @DisplayName("Should fail validation when description exceeds 1000 characters")
        void shouldFailValidationWhenDescriptionExceeds1000Characters() {
            // Given
            String longDescription = "a".repeat(1001);
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Valid Title")
                    .description(longDescription)
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description must be at most 1000 characters")));
        }

        @Test
        @DisplayName("Should fail validation when deadline is in the past")
        void shouldFailValidationWhenDeadlineIsInPast() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Valid Title")
                    .deadline(ZonedDateTime.now().minusDays(1))
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Deadline must be in the future")));
        }

        @Test
        @DisplayName("Should fail validation when project ID is null")
        void shouldFailValidationWhenProjectIdIsNull() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Valid Title")
                    .projectId(null)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Project ID is required")));
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .status("OPEN")
                    .assignedTo(1L)
                    .deadline(ZonedDateTime.now().plusDays(1))
                    .priority("HIGH")
                    .parentTaskId(2L)
                    .projectId(3L)
                    .build();

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"assigned_to\""));
            assertTrue(json.contains("\"parent_task_id\""));
            assertTrue(json.contains("\"project_id\""));
            assertFalse(json.contains("\"assignedTo\""));
            assertFalse(json.contains("\"parentTaskId\""));
            assertFalse(json.contains("\"projectId\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            // Given
            String json = """
                {
                    "title": "Test Task",
                    "description": "Test Description",
                    "status": "OPEN",
                    "assigned_to": 1,
                    "priority": "HIGH",
                    "parent_task_id": 2,
                    "project_id": 3
                }
                """;

            // When
            TaskCreationDTO dto = objectMapper.readValue(json, TaskCreationDTO.class);

            // Then
            assertEquals("Test Task", dto.getTitle());
            assertEquals("Test Description", dto.getDescription());
            assertEquals("OPEN", dto.getStatus());
            assertEquals(1L, dto.getAssignedTo());
            assertEquals("HIGH", dto.getPriority());
            assertEquals(2L, dto.getParentTaskId());
            assertEquals(3L, dto.getProjectId());
        }
    }

    @Nested
    @DisplayName("TaskResponseDTO Tests")
    class TaskResponseDTOTests {

        @Test
        @DisplayName("Should create TaskResponseDTO with all fields")
        void shouldCreateTaskResponseDTOWithAllFields() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            LocalDate today = LocalDate.now();
            List<Long> subtasks = Arrays.asList(1L, 2L, 3L);
            List<String> attachments = Arrays.asList("file1.pdf", "file2.doc");
            List<TaskStatusHistoryDTO> statusHistory = Arrays.asList(
                    TaskStatusHistoryDTO.builder()
                            .status("OPEN")
                            .changedBy(1L)
                            .changedAt(now)
                            .comment("Task created")
                            .build()
            );

            // When
            TaskResponseDTO dto = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .description("Test Description")
                    .status("IN_PROGRESS")
                    .deadline(now.plusDays(7))
                    .priority("HIGH")
                    .timeEstimate("2 hours")
                    .aiTimeEstimate("1.5 hours")
                    .aiPriority("MEDIUM")
                    .smartDeadline(now.plusDays(5))
                    .projectId(10L)
                    .assignedTo(2L)
                    .assignedBy(3L)
                    .assignedAt(now.minusDays(1))
                    .parentTaskId(4L)
                    .tentativeStartingDate(today)
                    .subtasks(subtasks)
                    .attachments(attachments)
                    .statusHistory(statusHistory)
                    .build();

            // Then
            assertEquals(1L, dto.getId());
            assertEquals("Test Task", dto.getTitle());
            assertEquals("Test Description", dto.getDescription());
            assertEquals("IN_PROGRESS", dto.getStatus());
            assertEquals(now.plusDays(7), dto.getDeadline());
            assertEquals("HIGH", dto.getPriority());
            assertEquals("2 hours", dto.getTimeEstimate());
            assertEquals("1.5 hours", dto.getAiTimeEstimate());
            assertEquals("MEDIUM", dto.getAiPriority());
            assertEquals(now.plusDays(5), dto.getSmartDeadline());
            assertEquals(10L, dto.getProjectId());
            assertEquals(2L, dto.getAssignedTo());
            assertEquals(3L, dto.getAssignedBy());
            assertEquals(now.minusDays(1), dto.getAssignedAt());
            assertEquals(4L, dto.getParentTaskId());
            assertEquals(today, dto.getTentativeStartingDate());
            assertEquals(subtasks, dto.getSubtasks());
            assertEquals(attachments, dto.getAttachments());
            assertEquals(statusHistory, dto.getStatusHistory());
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            TaskResponseDTO dto = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .assignedTo(2L)
                    .assignedBy(3L)
                    .assignedAt(ZonedDateTime.now())
                    .parentTaskId(4L)
                    .projectId(5L)
                    .timeEstimate("2 hours")
                    .aiTimeEstimate("1.5 hours")
                    .aiPriority("MEDIUM")
                    .smartDeadline(ZonedDateTime.now().plusDays(5))
                    .tentativeStartingDate(LocalDate.now())
                    .statusHistory(Arrays.asList())
                    .build();

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"assigned_to\""));
            assertTrue(json.contains("\"assigned_by\""));
            assertTrue(json.contains("\"assigned_at\""));
            assertTrue(json.contains("\"parent_task_id\""));
            assertTrue(json.contains("\"project_id\""));
            assertTrue(json.contains("\"time_estimate\""));
            assertTrue(json.contains("\"ai_time_estimate\""));
            assertTrue(json.contains("\"ai_priority\""));
            assertTrue(json.contains("\"smart_deadline\""));
            assertTrue(json.contains("\"tentative_starting_date\""));
            assertTrue(json.contains("\"status_history\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            // Given
            String json = """
                {
                    "id": 1,
                    "title": "Test Task",
                    "description": "Test Description",
                    "status": "IN_PROGRESS",
                    "priority": "HIGH",
                    "time_estimate": "2 hours",
                    "ai_time_estimate": "1.5 hours",
                    "ai_priority": "MEDIUM",
                    "project_id": 5,
                    "assigned_to": 2,
                    "assigned_by": 3,
                    "parent_task_id": 4,
                    "subtasks": [1, 2, 3],
                    "attachments": ["file1.pdf", "file2.doc"],
                    "status_history": []
                }
                """;

            // When
            TaskResponseDTO dto = objectMapper.readValue(json, TaskResponseDTO.class);

            // Then
            assertEquals(1L, dto.getId());
            assertEquals("Test Task", dto.getTitle());
            assertEquals("Test Description", dto.getDescription());
            assertEquals("IN_PROGRESS", dto.getStatus());
            assertEquals("HIGH", dto.getPriority());
            assertEquals("2 hours", dto.getTimeEstimate());
            assertEquals("1.5 hours", dto.getAiTimeEstimate());
            assertEquals("MEDIUM", dto.getAiPriority());
            assertEquals(5L, dto.getProjectId());
            assertEquals(2L, dto.getAssignedTo());
            assertEquals(3L, dto.getAssignedBy());
            assertEquals(4L, dto.getParentTaskId());
            assertEquals(Arrays.asList(1L, 2L, 3L), dto.getSubtasks());
            assertEquals(Arrays.asList("file1.pdf", "file2.doc"), dto.getAttachments());
            assertNotNull(dto.getStatusHistory());
        }
    }

    @Nested
    @DisplayName("TaskUpdateDTO Tests")
    class TaskUpdateDTOTests {

        @Test
        @DisplayName("Should create TaskUpdateDTO with valid data")
        void shouldCreateTaskUpdateDTOWithValidData() {
            // Given
            ZonedDateTime futureDate = ZonedDateTime.now().plusDays(1);
            LocalDate today = LocalDate.now();
            List<Long> subtasks = Arrays.asList(1L, 2L);
            List<String> attachments = Arrays.asList("file1.pdf");
            List<TaskStatusHistoryDTO> statusHistory = Arrays.asList(
                    TaskStatusHistoryDTO.builder()
                            .status("UPDATED")
                            .changedBy(1L)
                            .changedAt(ZonedDateTime.now())
                            .comment("Task updated")
                            .build()
            );

            // When
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .title("Updated Task")
                    .description("Updated Description")
                    .status("IN_PROGRESS")
                    .deadline(futureDate)
                    .priority("MEDIUM")
                    .timeEstimate("3 hours")
                    .aiTimeEstimate("2.5 hours")
                    .aiPriority("LOW")
                    .smartDeadline(futureDate.plusDays(1))
                    .projectId(1L)
                    .assignedTo(2L)
                    .assignedBy(3L)
                    .assignedAt(ZonedDateTime.now())
                    .parentTaskId(4L)
                    .tentativeStartingDate(today)
                    .subtasks(subtasks)
                    .attachments(attachments)
                    .statusHistory(statusHistory)
                    .build();

            // Then
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
            assertEquals("Updated Task", dto.getTitle());
            assertEquals("Updated Description", dto.getDescription());
            assertEquals("IN_PROGRESS", dto.getStatus());
            assertEquals(futureDate, dto.getDeadline());
            assertEquals("MEDIUM", dto.getPriority());
            assertEquals("3 hours", dto.getTimeEstimate());
            assertEquals("2.5 hours", dto.getAiTimeEstimate());
            assertEquals("LOW", dto.getAiPriority());
            assertEquals(futureDate.plusDays(1), dto.getSmartDeadline());
            assertEquals(1L, dto.getProjectId());
            assertEquals(2L, dto.getAssignedTo());
            assertEquals(3L, dto.getAssignedBy());
            assertEquals(4L, dto.getParentTaskId());
            assertEquals(today, dto.getTentativeStartingDate());
            assertEquals(subtasks, dto.getSubtasks());
            assertEquals(attachments, dto.getAttachments());
            assertEquals(statusHistory, dto.getStatusHistory());
        }

        @Test
        @DisplayName("Should fail validation when title exceeds 100 characters")
        void shouldFailValidationWhenTitleExceeds100Characters() {
            // Given
            String longTitle = "a".repeat(101);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .title(longTitle)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title must be at most 100 characters")));
        }

        @Test
        @DisplayName("Should fail validation when description exceeds 1000 characters")
        void shouldFailValidationWhenDescriptionExceeds1000Characters() {
            // Given
            String longDescription = "a".repeat(1001);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .description(longDescription)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description must be at most 1000 characters")));
        }

        @Test
        @DisplayName("Should fail validation when deadline is in the past")
        void shouldFailValidationWhenDeadlineIsInPast() {
            // Given
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .deadline(ZonedDateTime.now().minusDays(1))
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Deadline must be in the present or future")));
        }

        @Test
        @DisplayName("Should fail validation when smart deadline is in the past")
        void shouldFailValidationWhenSmartDeadlineIsInPast() {
            // Given
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .smartDeadline(ZonedDateTime.now().minusDays(1))
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Smart deadline must be in the present or future")));
        }

        @Test
        @DisplayName("Should fail validation when time estimate exceeds 50 characters")
        void shouldFailValidationWhenTimeEstimateExceeds50Characters() {
            // Given
            String longTimeEstimate = "a".repeat(51);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .timeEstimate(longTimeEstimate)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Time estimate must be at most 50 characters")));
        }

        @Test
        @DisplayName("Should fail validation when AI time estimate exceeds 50 characters")
        void shouldFailValidationWhenAiTimeEstimateExceeds50Characters() {
            // Given
            String longAiTimeEstimate = "a".repeat(51);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .aiTimeEstimate(longAiTimeEstimate)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("AI time estimate must be at most 50 characters")));
        }


        @Test
        @DisplayName("Should accept present date for deadline")
        void shouldAcceptPresentDateForDeadline() {
            // Given - Add a small buffer to ensure the time is truly present/future
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .deadline(ZonedDateTime.now().plusSeconds(1))
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.stream().noneMatch(v -> v.getMessage().contains("Deadline must be in the present or future")));
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .title("Updated Task")
                    .timeEstimate("3 hours")
                    .aiTimeEstimate("2.5 hours")
                    .aiPriority("LOW")
                    .smartDeadline(ZonedDateTime.now().plusDays(1))
                    .projectId(1L)
                    .assignedTo(2L)
                    .assignedBy(3L)
                    .assignedAt(ZonedDateTime.now())
                    .parentTaskId(4L)
                    .tentativeStartingDate(LocalDate.now())
                    .statusHistory(Arrays.asList())
                    .build();

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"time_estimate\""));
            assertTrue(json.contains("\"ai_time_estimate\""));
            assertTrue(json.contains("\"ai_priority\""));
            assertTrue(json.contains("\"smart_deadline\""));
            assertTrue(json.contains("\"project_id\""));
            assertTrue(json.contains("\"assigned_to\""));
            assertTrue(json.contains("\"assigned_by\""));
            assertTrue(json.contains("\"assigned_at\""));
            assertTrue(json.contains("\"parent_task_id\""));
            assertTrue(json.contains("\"tentative_starting_date\""));
            assertTrue(json.contains("\"status_history\""));
        }
    }

    @Nested
    @DisplayName("TaskStatusHistoryDTO Tests")
    class TaskStatusHistoryDTOTests {

        @Test
        @DisplayName("Should create TaskStatusHistoryDTO with all fields")
        void shouldCreateTaskStatusHistoryDTOWithAllFields() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();

            // When
            TaskStatusHistoryDTO dto = TaskStatusHistoryDTO.builder()
                    .status("COMPLETED")
                    .changedBy(1L)
                    .changedAt(now)
                    .comment("Task completed successfully")
                    .build();

            // Then
            assertEquals("COMPLETED", dto.getStatus());
            assertEquals(1L, dto.getChangedBy());
            assertEquals(now, dto.getChangedAt());
            assertEquals("Task completed successfully", dto.getComment());
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            // Given
            TaskStatusHistoryDTO dto = TaskStatusHistoryDTO.builder()
                    .status("COMPLETED")
                    .changedBy(1L)
                    .changedAt(ZonedDateTime.now())
                    .comment("Task completed successfully")
                    .build();

            // When
            String json = objectMapper.writeValueAsString(dto);

            // Then
            assertTrue(json.contains("\"changed_by\""));
            assertTrue(json.contains("\"changed_at\""));
            assertFalse(json.contains("\"changedBy\""));
            assertFalse(json.contains("\"changedAt\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            // Given
            String json = """
                {
                    "status": "COMPLETED",
                    "changed_by": 1,
                    "changed_at": "2023-12-01T10:00:00Z",
                    "comment": "Task completed successfully"
                }
                """;

            // When
            TaskStatusHistoryDTO dto = objectMapper.readValue(json, TaskStatusHistoryDTO.class);

            // Then
            assertEquals("COMPLETED", dto.getStatus());
            assertEquals(1L, dto.getChangedBy());
            assertEquals("Task completed successfully", dto.getComment());
            assertNotNull(dto.getChangedAt());
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // Given & When
            TaskStatusHistoryDTO dto = TaskStatusHistoryDTO.builder()
                    .status(null)
                    .changedBy(null)
                    .changedAt(null)
                    .comment(null)
                    .build();

            // Then
            assertNull(dto.getStatus());
            assertNull(dto.getChangedBy());
            assertNull(dto.getChangedAt());
            assertNull(dto.getComment());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTests {

        @Test
        @DisplayName("Should support builder pattern for all DTOs")
        void shouldSupportBuilderPatternForAllDTOs() {
            // Given & When & Then
            assertDoesNotThrow(() -> {
                TaskCreationDTO.builder().build();
                TaskResponseDTO.builder().build();
                TaskUpdateDTO.builder().build();
                TaskStatusHistoryDTO.builder().build();
            });
        }

        @Test
        @DisplayName("Should support no-args constructor for all DTOs")
        void shouldSupportNoArgsConstructorForAllDTOs() {
            // Given & When & Then
            assertDoesNotThrow(() -> {
                new TaskCreationDTO();
                new TaskResponseDTO();
                new TaskUpdateDTO();
                new TaskStatusHistoryDTO();
            });
        }

        @Test
        @DisplayName("Should support all-args constructor for all DTOs")
        void shouldSupportAllArgsConstructorForAllDTOs() {
            // Given & When & Then
            assertDoesNotThrow(() -> {
                new TaskCreationDTO("title", "desc", "status", 1L,
                        ZonedDateTime.now().plusDays(1), "priority", 2L, 3L);

                new TaskResponseDTO(1L, "title", "desc", "status",
                        ZonedDateTime.now(), "priority", "timeEst", "aiTimeEst",
                        "aiPriority", ZonedDateTime.now(), 1L, 2L, 3L,
                        ZonedDateTime.now(), 4L, LocalDate.now(),
                        Arrays.asList(), Arrays.asList(), Arrays.asList());

                new TaskUpdateDTO("title", "desc", "status",
                        ZonedDateTime.now().plusDays(1), "priority", "timeEst",
                        "aiTimeEst", "aiPriority", ZonedDateTime.now().plusDays(1),
                        1L, 2L, 3L, ZonedDateTime.now(), 4L, LocalDate.now(),
                        Arrays.asList(), Arrays.asList(), Arrays.asList());

                new TaskStatusHistoryDTO("status", 1L, ZonedDateTime.now(), "comment");
            });
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should implement equals and hashCode correctly for TaskCreationDTO")
        void shouldImplementEqualsAndHashCodeCorrectlyForTaskCreationDTO() {
            // Given
            TaskCreationDTO dto1 = TaskCreationDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .projectId(1L)
                    .build();

            TaskCreationDTO dto2 = TaskCreationDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .projectId(1L)
                    .build();

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly for TaskResponseDTO")
        void shouldImplementEqualsAndHashCodeCorrectlyForTaskResponseDTO() {
            // Given
            TaskResponseDTO dto1 = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .description("Test Description")
                    .build();

            TaskResponseDTO dto2 = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .description("Test Description")
                    .build();

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly for TaskUpdateDTO")
        void shouldImplementEqualsAndHashCodeCorrectlyForTaskUpdateDTO() {
            // Given
            TaskUpdateDTO dto1 = TaskUpdateDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .status("OPEN")
                    .build();

            TaskUpdateDTO dto2 = TaskUpdateDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .status("OPEN")
                    .build();

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly for TaskStatusHistoryDTO")
        void shouldImplementEqualsAndHashCodeCorrectlyForTaskStatusHistoryDTO() {
            // Given
            ZonedDateTime now = ZonedDateTime.now();
            TaskStatusHistoryDTO dto1 = TaskStatusHistoryDTO.builder()
                    .status("COMPLETED")
                    .changedBy(1L)
                    .changedAt(now)
                    .comment("Task completed")
                    .build();

            TaskStatusHistoryDTO dto2 = TaskStatusHistoryDTO.builder()
                    .status("COMPLETED")
                    .changedBy(1L)
                    .changedAt(now)
                    .comment("Task completed")
                    .build();

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate meaningful toString for TaskCreationDTO")
        void shouldGenerateMeaningfulToStringForTaskCreationDTO() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Test Task")
                    .description("Test Description")
                    .projectId(1L)
                    .build();

            // When
            String toString = dto.toString();

            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("Test Task"));
            assertTrue(toString.contains("Test Description"));
            assertTrue(toString.contains("1"));
        }

        @Test
        @DisplayName("Should generate meaningful toString for TaskResponseDTO")
        void shouldGenerateMeaningfulToStringForTaskResponseDTO() {
            // Given
            TaskResponseDTO dto = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .status("OPEN")
                    .build();

            // When
            String toString = dto.toString();

            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Test Task"));
            assertTrue(toString.contains("OPEN"));
        }

        @Test
        @DisplayName("Should generate meaningful toString for TaskUpdateDTO")
        void shouldGenerateMeaningfulToStringForTaskUpdateDTO() {
            // Given
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .title("Updated Task")
                    .status("IN_PROGRESS")
                    .priority("HIGH")
                    .build();

            // When
            String toString = dto.toString();

            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("Updated Task"));
            assertTrue(toString.contains("IN_PROGRESS"));
            assertTrue(toString.contains("HIGH"));
        }

        @Test
        @DisplayName("Should generate meaningful toString for TaskStatusHistoryDTO")
        void shouldGenerateMeaningfulToStringForTaskStatusHistoryDTO() {
            // Given
            TaskStatusHistoryDTO dto = TaskStatusHistoryDTO.builder()
                    .status("COMPLETED")
                    .changedBy(1L)
                    .comment("Task completed")
                    .build();

            // When
            String toString = dto.toString();

            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("COMPLETED"));
            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Task completed"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesAndBoundaryTests {

        @Test
        @DisplayName("Should handle maximum valid title length for TaskCreationDTO")
        void shouldHandleMaximumValidTitleLengthForTaskCreationDTO() {
            // Given
            String maxTitle = "a".repeat(100);
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title(maxTitle)
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(100, dto.getTitle().length());
        }

        @Test
        @DisplayName("Should handle maximum valid description length for TaskCreationDTO")
        void shouldHandleMaximumValidDescriptionLengthForTaskCreationDTO() {
            // Given
            String maxDescription = "a".repeat(1000);
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Valid Title")
                    .description(maxDescription)
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(1000, dto.getDescription().length());
        }

        @Test
        @DisplayName("Should handle maximum valid time estimate length for TaskUpdateDTO")
        void shouldHandleMaximumValidTimeEstimateLengthForTaskUpdateDTO() {
            // Given
            String maxTimeEstimate = "a".repeat(50);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .timeEstimate(maxTimeEstimate)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(50, dto.getTimeEstimate().length());
        }

        @Test
        @DisplayName("Should handle maximum valid AI time estimate length for TaskUpdateDTO")
        void shouldHandleMaximumValidAiTimeEstimateLengthForTaskUpdateDTO() {
            // Given
            String maxAiTimeEstimate = "a".repeat(50);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .aiTimeEstimate(maxAiTimeEstimate)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals(50, dto.getAiTimeEstimate().length());
        }

        @Test
        @DisplayName("Should handle empty lists in TaskResponseDTO")
        void shouldHandleEmptyListsInTaskResponseDTO() {
            // Given
            TaskResponseDTO dto = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .subtasks(Arrays.asList())
                    .attachments(Arrays.asList())
                    .statusHistory(Arrays.asList())
                    .build();

            // When & Then
            assertNotNull(dto.getSubtasks());
            assertNotNull(dto.getAttachments());
            assertNotNull(dto.getStatusHistory());
            assertTrue(dto.getSubtasks().isEmpty());
            assertTrue(dto.getAttachments().isEmpty());
            assertTrue(dto.getStatusHistory().isEmpty());
        }

        @Test
        @DisplayName("Should handle null optional fields in TaskCreationDTO")
        void shouldHandleNullOptionalFieldsInTaskCreationDTO() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("Required Title")
                    .projectId(1L)
                    .description(null)
                    .status(null)
                    .assignedTo(null)
                    .deadline(null)
                    .priority(null)
                    .parentTaskId(null)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
            assertNull(dto.getDescription());
            assertNull(dto.getStatus());
            assertNull(dto.getAssignedTo());
            assertNull(dto.getDeadline());
            assertNull(dto.getPriority());
            assertNull(dto.getParentTaskId());
        }

        @Test
        @DisplayName("Should handle whitespace-only title validation")
        void shouldHandleWhitespaceOnlyTitleValidation() {
            // Given
            TaskCreationDTO dto = TaskCreationDTO.builder()
                    .title("   ")
                    .projectId(1L)
                    .build();

            // When
            Set<ConstraintViolation<TaskCreationDTO>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
        }
        @Test
        @DisplayName("Should handle exactly present time for deadline validation")
        void shouldHandleExactlyPresentTimeForDeadlineValidation() {
            // Given - Add a small buffer to ensure the time is truly present/future
            ZonedDateTime presentOrFuture = ZonedDateTime.now().plusSeconds(1);
            TaskUpdateDTO dto = TaskUpdateDTO.builder()
                    .deadline(presentOrFuture)
                    .smartDeadline(presentOrFuture)
                    .build();

            // When
            Set<ConstraintViolation<TaskUpdateDTO>> violations = validator.validate(dto);

            // Then
            // Should pass validation as @FutureOrPresent allows present time
            assertTrue(violations.stream().noneMatch(v ->
                    v.getMessage().contains("Deadline must be in the present or future") ||
                            v.getMessage().contains("Smart deadline must be in the present or future")));
        }

    }


    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete task lifecycle DTOs")
        void shouldHandleCompleteTaskLifecycleDTOs() throws Exception {
            // Given - Create task
            TaskCreationDTO createDTO = TaskCreationDTO.builder()
                    .title("New Task")
                    .description("Task Description")
                    .status("OPEN")
                    .assignedTo(1L)
                    .deadline(ZonedDateTime.now().plusDays(7))
                    .priority("HIGH")
                    .projectId(1L)
                    .build();

            // When & Then - Validate creation
            Set<ConstraintViolation<TaskCreationDTO>> createViolations = validator.validate(createDTO);
            assertTrue(createViolations.isEmpty());

            // Given - Update task
            TaskUpdateDTO updateDTO = TaskUpdateDTO.builder()
                    .title("Updated Task")
                    .description("Updated Description")
                    .status("IN_PROGRESS")
                    .priority("MEDIUM")
                    .timeEstimate("4 hours")
                    .build();

            // When & Then - Validate update
            Set<ConstraintViolation<TaskUpdateDTO>> updateViolations = validator.validate(updateDTO);
            assertTrue(updateViolations.isEmpty());

            // Given - Response with status history
            TaskStatusHistoryDTO statusHistory = TaskStatusHistoryDTO.builder()
                    .status("IN_PROGRESS")
                    .changedBy(1L)
                    .changedAt(ZonedDateTime.now())
                    .comment("Task updated to in progress")
                    .build();

            TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Updated Task")
                    .description("Updated Description")
                    .status("IN_PROGRESS")
                    .priority("MEDIUM")
                    .timeEstimate("4 hours")
                    .projectId(1L)
                    .assignedTo(1L)
                    .statusHistory(Arrays.asList(statusHistory))
                    .build();

            // When & Then - Serialize and deserialize
            String json = objectMapper.writeValueAsString(responseDTO);
            TaskResponseDTO deserializedDTO = objectMapper.readValue(json, TaskResponseDTO.class);

            assertEquals(responseDTO.getId(), deserializedDTO.getId());
            assertEquals(responseDTO.getTitle(), deserializedDTO.getTitle());
            assertEquals(responseDTO.getStatus(), deserializedDTO.getStatus());
            assertEquals(responseDTO.getStatusHistory().size(), deserializedDTO.getStatusHistory().size());
        }

        @Test
        @DisplayName("Should handle complex nested structures")
        void shouldHandleComplexNestedStructures() throws Exception {
            // Given
            List<TaskStatusHistoryDTO> statusHistory = Arrays.asList(
                    TaskStatusHistoryDTO.builder()
                            .status("OPEN")
                            .changedBy(1L)
                            .changedAt(ZonedDateTime.now().minusDays(2))
                            .comment("Task created")
                            .build(),
                    TaskStatusHistoryDTO.builder()
                            .status("IN_PROGRESS")
                            .changedBy(2L)
                            .changedAt(ZonedDateTime.now().minusDays(1))
                            .comment("Started working on task")
                            .build(),
                    TaskStatusHistoryDTO.builder()
                            .status("COMPLETED")
                            .changedBy(2L)
                            .changedAt(ZonedDateTime.now())
                            .comment("Task completed successfully")
                            .build()
            );

            TaskResponseDTO dto = TaskResponseDTO.builder()
                    .id(1L)
                    .title("Complex Task")
                    .subtasks(Arrays.asList(2L, 3L, 4L))
                    .attachments(Arrays.asList("doc1.pdf", "image1.jpg", "spreadsheet1.xlsx"))
                    .statusHistory(statusHistory)
                    .build();

            // When
            String json = objectMapper.writeValueAsString(dto);
            TaskResponseDTO deserializedDTO = objectMapper.readValue(json, TaskResponseDTO.class);

            // Then
            assertEquals(dto.getId(), deserializedDTO.getId());
            assertEquals(dto.getTitle(), deserializedDTO.getTitle());
            assertEquals(dto.getSubtasks().size(), deserializedDTO.getSubtasks().size());
            assertEquals(dto.getAttachments().size(), deserializedDTO.getAttachments().size());
            assertEquals(dto.getStatusHistory().size(), deserializedDTO.getStatusHistory().size());

            // Verify status history details
            for (int i = 0; i < statusHistory.size(); i++) {
                TaskStatusHistoryDTO original = dto.getStatusHistory().get(i);
                TaskStatusHistoryDTO deserialized = deserializedDTO.getStatusHistory().get(i);
                assertEquals(original.getStatus(), deserialized.getStatus());
                assertEquals(original.getChangedBy(), deserialized.getChangedBy());
                assertEquals(original.getComment(), deserialized.getComment());
            }
        }
    }
}