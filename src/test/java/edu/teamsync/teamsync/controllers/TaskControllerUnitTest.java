//package edu.teamsync.teamsync.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import edu.teamsync.teamsync.controller.TaskController;
//import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
//import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
//import edu.teamsync.teamsync.dto.taskDTO.TaskStatusHistoryDTO;
//import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
//import edu.teamsync.teamsync.service.TaskService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.time.ZonedDateTime;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(TaskController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class TaskControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private TaskService taskService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private TaskResponseDTO task1;
//    private TaskResponseDTO task2;
//    private TaskCreationDTO createDTO;
//    private TaskUpdateDTO updateDTO;
//    private TaskStatusHistoryDTO statusHistoryDTO;
//
//    @BeforeEach
//    void setup() {
//        statusHistoryDTO = TaskStatusHistoryDTO.builder()
//                .status("OPEN")
//                .changedBy(1L)
//                .changedAt(ZonedDateTime.now())
//                .comment("Task created")
//                .build();
//
//        task1 = TaskResponseDTO.builder()
//                .id(1L)
//                .title("Task 1")
//                .description("Description for task 1")
//                .status("OPEN")
//                .deadline(ZonedDateTime.now().plusDays(7))
//                .priority("HIGH")
//                .timeEstimate("4 hours")
//                .aiTimeEstimate("3.5 hours")
//                .aiPriority("MEDIUM")
//                .smartDeadline(ZonedDateTime.now().plusDays(5))
//                .projectId(100L)
//                .assignedTo(10L)
//                .assignedBy(20L)
//                .assignedAt(ZonedDateTime.now())
//                .parentTaskId(null)
//                .tentativeStartingDate(LocalDate.now().plusDays(1))
//                .subtasks(List.of(2L, 3L))
//                .attachments(List.of("file1.pdf", "file2.docx"))
//                .statusHistory(List.of(statusHistoryDTO))
//                .build();
//
//        task2 = TaskResponseDTO.builder()
//                .id(2L)
//                .title("Task 2")
//                .description("Description for task 2")
//                .status("IN_PROGRESS")
//                .deadline(ZonedDateTime.now().plusDays(14))
//                .priority("MEDIUM")
//                .timeEstimate("8 hours")
//                .aiTimeEstimate("7 hours")
//                .aiPriority("LOW")
//                .smartDeadline(ZonedDateTime.now().plusDays(10))
//                .projectId(100L)
//                .assignedTo(11L)
//                .assignedBy(21L)
//                .assignedAt(ZonedDateTime.now())
//                .parentTaskId(1L)
//                .tentativeStartingDate(LocalDate.now().plusDays(2))
//                .subtasks(List.of())
//                .attachments(List.of("file3.txt"))
//                .statusHistory(List.of(statusHistoryDTO))
//                .build();
//
//        createDTO = TaskCreationDTO.builder()
//                .title("New Task")
//                .description("Description for new task")
//                .status("OPEN")
//                .assignedTo(10L)
//                .deadline(ZonedDateTime.now().plusDays(7))
//                .priority("HIGH")
//                .parentTaskId(null)
//                .projectId(100L)
//                .build();
//
//        updateDTO = TaskUpdateDTO.builder()
//                .title("Updated Task")
//                .description("Updated description")
//                .status("IN_PROGRESS")
//                .deadline(ZonedDateTime.now().plusDays(10))
//                .priority("MEDIUM")
//                .timeEstimate("6 hours")
//                .aiTimeEstimate("5.5 hours")
//                .aiPriority("MEDIUM")
//                .smartDeadline(ZonedDateTime.now().plusDays(8))
//                .projectId(100L)
//                .assignedTo(11L)
//                .assignedBy(21L)
//                .assignedAt(ZonedDateTime.now())
//                .parentTaskId(1L)
//                .tentativeStartingDate(LocalDate.now().plusDays(3))
//                .subtasks(List.of(3L, 4L))
//                .attachments(List.of("updated_file.pdf"))
//                .statusHistory(List.of(statusHistoryDTO))
//                .build();
//    }
//
//    @Test
//    @DisplayName("Should return task by ID with success response")
//    void getTaskById_ValidId_ReturnsSuccessResponse() throws Exception {
//        when(taskService.getTaskById(1L)).thenReturn(task1);
//
//        mockMvc.perform(get("/tasks/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Task retrieved successfully"))
//                .andExpect(jsonPath("$.data.id").value(1))
//                .andExpect(jsonPath("$.data.title").value("Task 1"))
//                .andExpect(jsonPath("$.data.description").value("Description for task 1"))
//                .andExpect(jsonPath("$.data.status").value("OPEN"))
//                .andExpect(jsonPath("$.data.priority").value("HIGH"))
//                .andExpect(jsonPath("$.data.project_id").value(100))
//                .andExpect(jsonPath("$.data.assigned_to").value(10))
//                .andExpect(jsonPath("$.data.assigned_by").value(20))
//                .andExpect(jsonPath("$.data.time_estimate").value("4 hours"))
//                .andExpect(jsonPath("$.data.ai_time_estimate").value("3.5 hours"))
//                .andExpect(jsonPath("$.data.ai_priority").value("MEDIUM"))
//                .andExpect(jsonPath("$.data.subtasks", hasSize(2)))
//                .andExpect(jsonPath("$.data.attachments", hasSize(2)))
//                .andExpect(jsonPath("$.data.status_history", hasSize(1)));
//
//        verify(taskService, times(1)).getTaskById(1L);
//    }
//
//    @Test
//    @DisplayName("Should return all tasks with success response")
//    void getAllTasks_ReturnsSuccessResponse() throws Exception {
//        List<TaskResponseDTO> taskList = List.of(task1, task2);
//
//        when(taskService.getAllTasks()).thenReturn(taskList);
//
//        mockMvc.perform(get("/tasks"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("All tasks retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].id").value(1))
//                .andExpect(jsonPath("$.data[0].title").value("Task 1"))
//                .andExpect(jsonPath("$.data[1].id").value(2))
//                .andExpect(jsonPath("$.data[1].title").value("Task 2"))
//                .andExpect(jsonPath("$.data[1].parent_task_id").value(1));
//
//        verify(taskService, times(1)).getAllTasks();
//    }
//
//    @Test
//    @DisplayName("Should return empty list when no tasks exist")
//    void getAllTasks_EmptyList_ReturnsSuccessResponse() throws Exception {
//        List<TaskResponseDTO> emptyList = List.of();
//
//        when(taskService.getAllTasks()).thenReturn(emptyList);
//
//        mockMvc.perform(get("/tasks"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("All tasks retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(0)));
//
//        verify(taskService, times(1)).getAllTasks();
//    }
//
//    @Test
//    @DisplayName("Should return tasks by project ID with success response")
//    void getTasksByProjectId_ValidProjectId_ReturnsSuccessResponse() throws Exception {
//        List<TaskResponseDTO> projectTasks = List.of(task1, task2);
//
//        when(taskService.getTasksByProjectId(100L)).thenReturn(projectTasks);
//
//        mockMvc.perform(get("/tasks/project/100"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].project_id").value(100))
//                .andExpect(jsonPath("$.data[1].project_id").value(100));
//
//        verify(taskService, times(1)).getTasksByProjectId(100L);
//    }
//
//    @Test
//    @DisplayName("Should return empty list when no tasks exist for project")
//    void getTasksByProjectId_EmptyList_ReturnsSuccessResponse() throws Exception {
//        List<TaskResponseDTO> emptyList = List.of();
//
//        when(taskService.getTasksByProjectId(999L)).thenReturn(emptyList);
//
//        mockMvc.perform(get("/tasks/project/999"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(0)));
//
//        verify(taskService, times(1)).getTasksByProjectId(999L);
//    }
//
//    @Test
//    @DisplayName("Should create task successfully")
//    void createTask_ValidData_ReturnsCreatedResponse() throws Exception {
//        doNothing().when(taskService).createTask(any(TaskCreationDTO.class));
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
//                .andExpect(jsonPath("$.status").value("CREATED"))
//                .andExpect(jsonPath("$.message").value("Task created successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(taskService, times(1)).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with invalid data")
//    void createTask_InvalidData_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = new TaskCreationDTO();
//        // Missing required fields like title and projectId
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with null title")
//    void createTask_NullTitle_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
//                .title(null)
//                .description("Description")
//                .projectId(100L)
//                .build();
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with blank title")
//    void createTask_BlankTitle_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
//                .title("")
//                .description("Description")
//                .projectId(100L)
//                .build();
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with null project ID")
//    void createTask_NullProjectId_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
//                .title("Valid Title")
//                .description("Description")
//                .projectId(null)
//                .build();
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with title exceeding max length")
//    void createTask_TitleTooLong_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
//                .title("a".repeat(101)) // Exceeds 100 character limit
//                .description("Description")
//                .projectId(100L)
//                .build();
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with description exceeding max length")
//    void createTask_DescriptionTooLong_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
//                .title("Valid Title")
//                .description("a".repeat(1001)) // Exceeds 1000 character limit
//                .projectId(100L)
//                .build();
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating task with past deadline")
//    void createTask_PastDeadline_ReturnsBadRequest() throws Exception {
//        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
//                .title("Valid Title")
//                .description("Description")
//                .deadline(ZonedDateTime.now().minusDays(1)) // Past deadline
//                .projectId(100L)
//                .build();
//
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should update task successfully")
//    void updateTask_ValidData_ReturnsSuccessResponse() throws Exception {
//        doNothing().when(taskService).updateTask(anyLong(), any(TaskUpdateDTO.class));
//
//        mockMvc.perform(put("/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Task updated successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(taskService, times(1)).updateTask(eq(1L), any(TaskUpdateDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when updating task with invalid data")
//    void updateTask_InvalidData_ReturnsBadRequest() throws Exception {
//        TaskUpdateDTO invalidDTO = TaskUpdateDTO.builder()
//                .title("a".repeat(101)) // Exceeds 100 character limit
//                .build();
//
//        mockMvc.perform(put("/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).updateTask(anyLong(), any(TaskUpdateDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when updating task with past deadline")
//    void updateTask_PastDeadline_ReturnsBadRequest() throws Exception {
//        TaskUpdateDTO invalidDTO = TaskUpdateDTO.builder()
//                .title("Valid Title")
//                .deadline(ZonedDateTime.now().minusDays(1)) // Past deadline
//                .build();
//
//        mockMvc.perform(put("/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(taskService, never()).updateTask(anyLong(), any(TaskUpdateDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should delete task successfully")
//    void deleteTask_ValidId_ReturnsSuccessResponse() throws Exception {
//        doNothing().when(taskService).deleteTask(anyLong());
//
//        mockMvc.perform(delete("/tasks/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Task deleted successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(taskService, times(1)).deleteTask(eq(1L));
//    }
//
//    @Test
//    @DisplayName("Should handle service exceptions gracefully")
//    void getAllTasks_ServiceException_ReturnsErrorResponse() throws Exception {
//        when(taskService.getAllTasks()).thenThrow(new RuntimeException("Service error"));
//
//        mockMvc.perform(get("/tasks"))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for getTaskById")
//    void getTaskById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(get("/tasks/invalid"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for getTasksByProjectId")
//    void getTasksByProjectId_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(get("/tasks/project/invalid"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for updateTask")
//    void updateTask_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/tasks/invalid")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for deleteTask")
//    void deleteTask_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(delete("/tasks/invalid"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle missing request body for createTask")
//    void createTask_MissingRequestBody_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle missing request body for updateTask")
//    void updateTask_MissingRequestBody_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle malformed JSON for createTask")
//    void createTask_MalformedJSON_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(post("/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle malformed JSON for updateTask")
//    void updateTask_MalformedJSON_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }"))
//                .andExpect(status().isBadRequest());
//    }
//}

package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.authorization.ProjectAuthorizationService;
import edu.teamsync.teamsync.controller.TaskController;
import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskStatusHistoryDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
import edu.teamsync.teamsync.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private ProjectAuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponseDTO task1;
    private TaskResponseDTO task2;
    private TaskCreationDTO createDTO;
    private TaskUpdateDTO updateDTO;
    private TaskStatusHistoryDTO statusHistoryDTO;

    @BeforeEach
    void setup() {
        statusHistoryDTO = TaskStatusHistoryDTO.builder()
                .status("OPEN")
                .changedBy(1L)
                .changedAt(ZonedDateTime.now())
                .comment("Task created")
                .build();

        task1 = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .description("Description for task 1")
                .status("OPEN")
                .deadline(ZonedDateTime.now().plusDays(7))
                .priority("HIGH")
                .timeEstimate("4 hours")
                .aiTimeEstimate("3.5 hours")
                .aiPriority("MEDIUM")
                .smartDeadline(ZonedDateTime.now().plusDays(5))
                .projectId(100L)
                .assignedTo(10L)
                .assignedBy(20L)
                .assignedAt(ZonedDateTime.now())
                .parentTaskId(null)
                .tentativeStartingDate(LocalDate.now().plusDays(1))
                .subtasks(List.of(2L, 3L))
                .attachments(List.of("file1.pdf", "file2.docx"))
                .statusHistory(List.of(statusHistoryDTO))
                .build();

        task2 = TaskResponseDTO.builder()
                .id(2L)
                .title("Task 2")
                .description("Description for task 2")
                .status("IN_PROGRESS")
                .deadline(ZonedDateTime.now().plusDays(14))
                .priority("MEDIUM")
                .timeEstimate("8 hours")
                .aiTimeEstimate("7 hours")
                .aiPriority("LOW")
                .smartDeadline(ZonedDateTime.now().plusDays(10))
                .projectId(100L)
                .assignedTo(11L)
                .assignedBy(21L)
                .assignedAt(ZonedDateTime.now())
                .parentTaskId(1L)
                .tentativeStartingDate(LocalDate.now().plusDays(2))
                .subtasks(List.of())
                .attachments(List.of("file3.txt"))
                .statusHistory(List.of(statusHistoryDTO))
                .build();

        createDTO = TaskCreationDTO.builder()
                .title("New Task")
                .description("Description for new task")
                .status("OPEN")
                .assignedTo(10L)
                .deadline(ZonedDateTime.now().plusDays(7))
                .priority("HIGH")
                .parentTaskId(null)
                .projectId(100L)
                .build();

        updateDTO = TaskUpdateDTO.builder()
                .title("Updated Task")
                .description("Updated description")
                .status("IN_PROGRESS")
                .deadline(ZonedDateTime.now().plusDays(10))
                .priority("MEDIUM")
                .timeEstimate("6 hours")
                .aiTimeEstimate("5.5 hours")
                .aiPriority("MEDIUM")
                .smartDeadline(ZonedDateTime.now().plusDays(8))
                .projectId(100L)
                .assignedTo(11L)
                .assignedBy(21L)
                .assignedAt(ZonedDateTime.now())
                .parentTaskId(1L)
                .tentativeStartingDate(LocalDate.now().plusDays(3))
                .subtasks(List.of(3L, 4L))
                .attachments(List.of("updated_file.pdf"))
                .statusHistory(List.of(statusHistoryDTO))
                .build();

        // Setup default authorization behavior
        doNothing().when(authorizationService).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return task by ID with success response")
    void getTaskById_ValidId_ReturnsSuccessResponse() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task1);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Task retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Task 1"))
                .andExpect(jsonPath("$.data.description").value("Description for task 1"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"))
                .andExpect(jsonPath("$.data.project_id").value(100))
                .andExpect(jsonPath("$.data.assigned_to").value(10))
                .andExpect(jsonPath("$.data.assigned_by").value(20))
                .andExpect(jsonPath("$.data.time_estimate").value("4 hours"))
                .andExpect(jsonPath("$.data.ai_time_estimate").value("3.5 hours"))
                .andExpect(jsonPath("$.data.ai_priority").value("MEDIUM"))
                .andExpect(jsonPath("$.data.subtasks", hasSize(2)))
                .andExpect(jsonPath("$.data.attachments", hasSize(2)))
                .andExpect(jsonPath("$.data.status_history", hasSize(1)));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    @DisplayName("Should return all tasks with success response")
    void getAllTasks_ReturnsSuccessResponse() throws Exception {
        List<TaskResponseDTO> taskList = List.of(task1, task2);

        when(taskService.getAllTasks()).thenReturn(taskList);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All tasks retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Task 1"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("Task 2"))
                .andExpect(jsonPath("$.data[1].parent_task_id").value(1));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void getAllTasks_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<TaskResponseDTO> emptyList = List.of();

        when(taskService.getAllTasks()).thenReturn(emptyList);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All tasks retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("Should return tasks by project ID with success response")
    void getTasksByProjectId_ValidProjectId_ReturnsSuccessResponse() throws Exception {
        List<TaskResponseDTO> projectTasks = List.of(task1, task2);

        when(taskService.getTasksByProjectId(100L)).thenReturn(projectTasks);

        mockMvc.perform(get("/tasks/project/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].project_id").value(100))
                .andExpect(jsonPath("$.data[1].project_id").value(100));

        verify(taskService, times(1)).getTasksByProjectId(100L);
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist for project")
    void getTasksByProjectId_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<TaskResponseDTO> emptyList = List.of();

        when(taskService.getTasksByProjectId(999L)).thenReturn(emptyList);

        mockMvc.perform(get("/tasks/project/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(taskService, times(1)).getTasksByProjectId(999L);
    }

    @Test
    @DisplayName("Should create task successfully")
    void createTask_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(taskService).createTask(any(TaskCreationDTO.class));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(taskService, times(1)).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, times(1)).requireProjectAdminOrOwner(100L);
    }

    @Test
    @DisplayName("Should return bad request when creating task with invalid data")
    void createTask_InvalidData_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = new TaskCreationDTO();
        // Missing required fields like title and projectId

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when creating task with null title")
    void createTask_NullTitle_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
                .title(null)
                .description("Description")
                .projectId(100L)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when creating task with blank title")
    void createTask_BlankTitle_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
                .title("")
                .description("Description")
                .projectId(100L)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when creating task with null project ID")
    void createTask_NullProjectId_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
                .title("Valid Title")
                .description("Description")
                .projectId(null)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when creating task with title exceeding max length")
    void createTask_TitleTooLong_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
                .title("a".repeat(101)) // Exceeds 100 character limit
                .description("Description")
                .projectId(100L)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when creating task with description exceeding max length")
    void createTask_DescriptionTooLong_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
                .title("Valid Title")
                .description("a".repeat(1001)) // Exceeds 1000 character limit
                .projectId(100L)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should return bad request when creating task with past deadline")
    void createTask_PastDeadline_ReturnsBadRequest() throws Exception {
        TaskCreationDTO invalidDTO = TaskCreationDTO.builder()
                .title("Valid Title")
                .description("Description")
                .deadline(ZonedDateTime.now().minusDays(1)) // Past deadline
                .projectId(100L)
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskCreationDTO.class));
        verify(authorizationService, never()).requireProjectAdminOrOwner(anyLong());
    }

    @Test
    @DisplayName("Should update task successfully")
    void updateTask_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(taskService).updateTask(anyLong(), any(TaskUpdateDTO.class));

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Task updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating task with invalid data")
    void updateTask_InvalidData_ReturnsBadRequest() throws Exception {
        TaskUpdateDTO invalidDTO = TaskUpdateDTO.builder()
                .title("a".repeat(101)) // Exceeds 100 character limit
                .build();

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(anyLong(), any(TaskUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating task with past deadline")
    void updateTask_PastDeadline_ReturnsBadRequest() throws Exception {
        TaskUpdateDTO invalidDTO = TaskUpdateDTO.builder()
                .title("Valid Title")
                .deadline(ZonedDateTime.now().minusDays(1)) // Past deadline
                .build();

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(anyLong(), any(TaskUpdateDTO.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_ValidId_ReturnsSuccessResponse() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task1);
        doNothing().when(taskService).deleteTask(anyLong());

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).deleteTask(eq(1L));
        verify(authorizationService, times(1)).requireProjectAdminOrOwner(100L);
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getAllTasks_ServiceException_ReturnsErrorResponse() throws Exception {
        when(taskService.getAllTasks()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getTaskById")
    void getTaskById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/tasks/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getTasksByProjectId")
    void getTasksByProjectId_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/tasks/project/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updateTask")
    void updateTask_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/tasks/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deleteTask")
    void deleteTask_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/tasks/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createTask")
    void createTask_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updateTask")
    void updateTask_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createTask")
    void createTask_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updateTask")
    void updateTask_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}