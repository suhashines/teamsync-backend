//package edu.teamsync.teamsync.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import edu.teamsync.teamsync.controller.ProjectController;
//import edu.teamsync.teamsync.dto.projectDTO.*;
//import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
//import edu.teamsync.teamsync.service.ProjectService;
//import edu.teamsync.teamsync.service.TaskService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ProjectController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class ProjectControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private ProjectService projectService;
//
//    @MockitoBean
//    private TaskService taskService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private ProjectDTO project1;
//    private ProjectDTO project2;
//    private ProjectCreationDTO createDTO;
//    private ProjectUpdateDTO updateDTO;
//    private ProjectMemberDTO member1;
//    private ProjectMemberDTO member2;
//    private AddMemberDTO addMemberDTO;
//    private UpdateMemberRoleDTO updateMemberRoleDTO;
//    private TaskResponseDTO task1;
//    private TaskResponseDTO task2;
//    private Authentication mockAuthentication;
//    private SecurityContext mockSecurityContext;
//
//    @BeforeEach
//    void setup() {
//        // Setup ProjectMemberDTO objects
//        member1 = ProjectMemberDTO.builder()
//                .userId(10L)
//                .role("ADMIN")
//                .joinedAt(ZonedDateTime.now())
//                .build();
//
//        member2 = ProjectMemberDTO.builder()
//                .userId(20L)
//                .role("MEMBER")
//                .joinedAt(ZonedDateTime.now())
//                .build();
//
//        // Setup ProjectDTO objects
//        project1 = ProjectDTO.builder()
//                .id(1L)
//                .title("Test Project 1")
//                .description("Test Description 1")
//                .createdBy(10L)
//                .createdAt(ZonedDateTime.now())
//                .members(List.of(member1, member2))
//                .build();
//
//        project2 = ProjectDTO.builder()
//                .id(2L)
//                .title("Test Project 2")
//                .description("Test Description 2")
//                .createdBy(20L)
//                .createdAt(ZonedDateTime.now())
//                .members(List.of(member2))
//                .build();
//
//        // Setup ProjectCreationDTO
//        createDTO = ProjectCreationDTO.builder()
//                .title("New Project")
//                .description("New Project Description")
//                .initialMembers(List.of(
//                        InitialMemberDTO.builder()
//                                .userId(10L)
//                                .role("ADMIN")
//                                .build()
//                ))
//                .build();
//
//        // Setup ProjectUpdateDTO
//        updateDTO = ProjectUpdateDTO.builder()
//                .title("Updated Project")
//                .description("Updated Description")
//                .createdBy(10L)
//                .createdAt(ZonedDateTime.now())
//                .members(List.of(member1))
//                .build();
//
//        // Setup AddMemberDTO
//        addMemberDTO = AddMemberDTO.builder()
//                .userId(30L)
//                .role("MEMBER")
//                .build();
//
//        // Setup UpdateMemberRoleDTO
//        updateMemberRoleDTO = UpdateMemberRoleDTO.builder()
//                .role("ADMIN")
//                .build();
//
//        // Setup TaskResponseDTO objects
//        task1 = new TaskResponseDTO();
//        task1.setId(1L);
//        task1.setTitle("Task 1");
//        task1.setDescription("Task 1 Description");
//
//        task2 = new TaskResponseDTO();
//        task2.setId(2L);
//        task2.setTitle("Task 2");
//        task2.setDescription("Task 2 Description");
//
//        // Mock authentication
//        mockAuthentication = mock(Authentication.class);
//        when(mockAuthentication.getName()).thenReturn("admin@example.com");
//
//        mockSecurityContext = mock(SecurityContext.class);
//        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
//    }
//
//    @Test
//    @DisplayName("Should return all projects with success response")
//    void getAllProjects_ReturnsSuccessResponse() throws Exception {
//        List<ProjectDTO> projectList = List.of(project1, project2);
//
//        when(projectService.getAllProjects()).thenReturn(projectList);
//
//        mockMvc.perform(get("/projects"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Projects retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].id").value(1))
//                .andExpect(jsonPath("$.data[0].title").value("Test Project 1"))
//                .andExpect(jsonPath("$.data[1].id").value(2))
//                .andExpect(jsonPath("$.data[1].title").value("Test Project 2"));
//    }
//
//    @Test
//    @DisplayName("Should return empty list when no projects exist")
//    void getAllProjects_EmptyList_ReturnsSuccessResponse() throws Exception {
//        List<ProjectDTO> emptyList = List.of();
//
//        when(projectService.getAllProjects()).thenReturn(emptyList);
//
//        mockMvc.perform(get("/projects"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Projects retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(0)));
//    }
//
//    @Test
//    @DisplayName("Should return project by ID with success response")
//    void getProjectById_ValidId_ReturnsSuccessResponse() throws Exception {
//        when(projectService.getProjectById(1L)).thenReturn(project1);
//
//        mockMvc.perform(get("/projects/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project retrieved successfully"))
//                .andExpect(jsonPath("$.data.id").value(1))
//                .andExpect(jsonPath("$.data.title").value("Test Project 1"))
//                .andExpect(jsonPath("$.data.description").value("Test Description 1"));
//    }
//
//    @Test
//    @DisplayName("Should create project successfully")
//    void createProject_ValidData_ReturnsCreatedResponse() throws Exception {
//        doNothing().when(projectService).createProject(any(ProjectCreationDTO.class), anyString());
//
//        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
//            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
//
//            mockMvc.perform(post("/projects")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(createDTO)))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
//                    .andExpect(jsonPath("$.status").value("CREATED"))
//                    .andExpect(jsonPath("$.message").value("Project created successfully"))
//                    .andExpect(jsonPath("$.data").doesNotExist());
//
//            verify(projectService, times(1)).createProject(any(ProjectCreationDTO.class), eq("admin@example.com"));
//        }
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating project with invalid data")
//    void createProject_InvalidData_ReturnsBadRequest() throws Exception {
//        ProjectCreationDTO invalidDTO = new ProjectCreationDTO();
//        // Missing required fields
//
//        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
//            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
//
//            mockMvc.perform(post("/projects")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(invalidDTO)))
//                    .andExpect(status().isBadRequest());
//
//            verify(projectService, never()).createProject(any(ProjectCreationDTO.class), anyString());
//        }
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating project with null title")
//    void createProject_NullTitle_ReturnsBadRequest() throws Exception {
//        ProjectCreationDTO invalidDTO = ProjectCreationDTO.builder()
//                .title(null)
//                .description("Valid description")
//                .build();
//
//        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
//            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
//
//            mockMvc.perform(post("/projects")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(invalidDTO)))
//                    .andExpect(status().isBadRequest());
//
//            verify(projectService, never()).createProject(any(ProjectCreationDTO.class), anyString());
//        }
//    }
//
//    @Test
//    @DisplayName("Should return bad request when creating project with title exceeding max length")
//    void createProject_TitleTooLong_ReturnsBadRequest() throws Exception {
//        ProjectCreationDTO invalidDTO = ProjectCreationDTO.builder()
//                .title("a".repeat(101)) // Exceeds 100 character limit
//                .description("Valid description")
//                .build();
//
//        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
//            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
//
//            mockMvc.perform(post("/projects")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(invalidDTO)))
//                    .andExpect(status().isBadRequest());
//
//            verify(projectService, never()).createProject(any(ProjectCreationDTO.class), anyString());
//        }
//    }
//
//    @Test
//    @DisplayName("Should update project successfully")
//    void updateProject_ValidData_ReturnsSuccessResponse() throws Exception {
//        doNothing().when(projectService).updateProject(anyLong(), any(ProjectUpdateDTO.class));
//
//        mockMvc.perform(put("/projects/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project updated successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectUpdateDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when updating project with invalid data")
//    void updateProject_InvalidData_ReturnsBadRequest() throws Exception {
//        ProjectUpdateDTO invalidDTO = ProjectUpdateDTO.builder()
//                .title("a".repeat(101)) // Exceeds 100 character limit
//                .description("Valid description")
//                .build();
//
//        mockMvc.perform(put("/projects/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(projectService, never()).updateProject(anyLong(), any(ProjectUpdateDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should delete project successfully")
//    void deleteProject_ValidId_ReturnsSuccessResponse() throws Exception {
//        doNothing().when(projectService).deleteProject(anyLong());
//
//        mockMvc.perform(delete("/projects/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project deleted successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(projectService, times(1)).deleteProject(eq(1L));
//    }
//
//    @Test
//    @DisplayName("Should return project members with success response")
//    void getProjectMembers_ValidId_ReturnsSuccessResponse() throws Exception {
//        List<ProjectMemberDTO> members = List.of(member1, member2);
//
//        when(projectService.getProjectMembers(1L)).thenReturn(members);
//
//        mockMvc.perform(get("/projects/1/members"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project members retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].user_id").value(10))
//                .andExpect(jsonPath("$.data[0].role").value("ADMIN"))
//                .andExpect(jsonPath("$.data[1].user_id").value(20))
//                .andExpect(jsonPath("$.data[1].role").value("MEMBER"));
//    }
//
//    @Test
//    @DisplayName("Should add member to project successfully")
//    void addMemberToProject_ValidData_ReturnsCreatedResponse() throws Exception {
//        doNothing().when(projectService).addMemberToProject(anyLong(), any(AddMemberDTO.class));
//
//        mockMvc.perform(post("/projects/1/members")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(addMemberDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
//                .andExpect(jsonPath("$.status").value("CREATED"))
//                .andExpect(jsonPath("$.message").value("Member added to project successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(projectService, times(1)).addMemberToProject(eq(1L), any(AddMemberDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when adding member with invalid data")
//    void addMemberToProject_InvalidData_ReturnsBadRequest() throws Exception {
//        AddMemberDTO invalidDTO = new AddMemberDTO();
//        // Missing required fields
//
//        mockMvc.perform(post("/projects/1/members")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(projectService, never()).addMemberToProject(anyLong(), any(AddMemberDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should update member role successfully")
//    void updateMemberRole_ValidData_ReturnsSuccessResponse() throws Exception {
//        doNothing().when(projectService).updateMemberRole(anyLong(), anyLong(), any(UpdateMemberRoleDTO.class));
//
//        mockMvc.perform(put("/projects/1/members/10")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateMemberRoleDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Member role updated successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(projectService, times(1)).updateMemberRole(eq(1L), eq(10L), any(UpdateMemberRoleDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should return bad request when updating member role with invalid data")
//    void updateMemberRole_InvalidData_ReturnsBadRequest() throws Exception {
//        UpdateMemberRoleDTO invalidDTO = new UpdateMemberRoleDTO();
//        // Missing required role field
//
//        mockMvc.perform(put("/projects/1/members/10")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(projectService, never()).updateMemberRole(anyLong(), anyLong(), any(UpdateMemberRoleDTO.class));
//    }
//
//    @Test
//    @DisplayName("Should remove member from project successfully")
//    void removeMemberFromProject_ValidIds_ReturnsSuccessResponse() throws Exception {
//        doNothing().when(projectService).removeMemberFromProject(anyLong(), anyLong());
//
//        mockMvc.perform(delete("/projects/1/members/10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Member removed from project successfully"))
//                .andExpect(jsonPath("$.data").doesNotExist());
//
//        verify(projectService, times(1)).removeMemberFromProject(eq(1L), eq(10L));
//    }
//
//    @Test
//    @DisplayName("Should return project tasks with success response")
//    void getProjectTasks_ValidId_ReturnsSuccessResponse() throws Exception {
//        List<TaskResponseDTO> tasks = List.of(task1, task2);
//
//        when(taskService.getTasksByProjectId(1L)).thenReturn(tasks);
//
//        mockMvc.perform(get("/projects/1/tasks"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].id").value(1))
//                .andExpect(jsonPath("$.data[0].title").value("Task 1"))
//                .andExpect(jsonPath("$.data[1].id").value(2))
//                .andExpect(jsonPath("$.data[1].title").value("Task 2"));
//    }
//
//    @Test
//    @DisplayName("Should return project kanban board with success response")
//    void getProjectKanban_ValidId_ReturnsSuccessResponse() throws Exception {
//        List<TaskResponseDTO> kanbanData = List.of(task1, task2);
//
//        when(taskService.getKanbanBoard(1L)).thenReturn(kanbanData);
//
//        mockMvc.perform(get("/projects/1/kanban"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").value("Kanban board data retrieved successfully"))
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].id").value(1))
//                .andExpect(jsonPath("$.data[0].title").value("Task 1"))
//                .andExpect(jsonPath("$.data[1].id").value(2))
//                .andExpect(jsonPath("$.data[1].title").value("Task 2"));
//    }
//
//    @Test
//    @DisplayName("Should handle service exceptions gracefully")
//    void getAllProjects_ServiceException_ReturnsErrorResponse() throws Exception {
//        when(projectService.getAllProjects()).thenThrow(new RuntimeException("Service error"));
//
//        mockMvc.perform(get("/projects"))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for getProjectById")
//    void getProjectById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(get("/projects/invalid"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for updateProject")
//    void updateProject_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/projects/invalid")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle invalid path variable for deleteProject")
//    void deleteProject_InvalidPathVariable_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(delete("/projects/invalid"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle missing request body for createProject")
//    void createProject_MissingRequestBody_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(post("/projects")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle missing request body for updateProject")
//    void updateProject_MissingRequestBody_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/projects/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle malformed JSON for createProject")
//    void createProject_MalformedJSON_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(post("/projects")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle malformed JSON for updateProject")
//    void updateProject_MalformedJSON_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/projects/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle malformed JSON for addMemberToProject")
//    void addMemberToProject_MalformedJSON_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(post("/projects/1/members")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Should handle malformed JSON for updateMemberRole")
//    void updateMemberRole_MalformedJSON_ReturnsBadRequest() throws Exception {
//        mockMvc.perform(put("/projects/1/members/10")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }"))
//                .andExpect(status().isBadRequest());
//    }
//}

//---------------------------------------------------------------------------------

package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.authorization.ProjectAuthorizationService;
import edu.teamsync.teamsync.controller.ProjectController;
import edu.teamsync.teamsync.dto.projectDTO.*;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.service.ProjectService;
import edu.teamsync.teamsync.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProjectController.class)
class ProjectControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private ProjectAuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDTO sampleProject;
    private ProjectCreationDTO sampleCreationDTO;
    private ProjectUpdateDTO sampleUpdateDTO;
    private AddMemberDTO sampleAddMemberDTO;
    private UpdateMemberRoleDTO sampleUpdateRoleDTO;
    private List<ProjectMemberDTO> sampleMembers;
    private List<TaskResponseDTO> sampleTasks;

    @BeforeEach
    void setUp() {
        // Setup sample data
        sampleProject = ProjectDTO.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .createdBy(1L)
                .createdAt(ZonedDateTime.now())
                .members(Arrays.asList(
                        ProjectMemberDTO.builder()
                                .userId(1L)
                                .role("OWNER")
                                .joinedAt(ZonedDateTime.now())
                                .build()
                ))
                .build();

        sampleCreationDTO = ProjectCreationDTO.builder()
                .title("New Project")
                .description("New Description")
                .initialMembers(Arrays.asList(
                        InitialMemberDTO.builder()
                                .userId(2L)
                                .role("MEMBER")
                                .build()
                ))
                .build();

        sampleUpdateDTO = ProjectUpdateDTO.builder()
                .title("Updated Project")
                .description("Updated Description")
                .build();

        sampleAddMemberDTO = AddMemberDTO.builder()
                .userId(2L)
                .role("MEMBER")
                .build();

        sampleUpdateRoleDTO = UpdateMemberRoleDTO.builder()
                .role("ADMIN")
                .build();

        sampleMembers = Arrays.asList(
                ProjectMemberDTO.builder()
                        .userId(1L)
                        .role("OWNER")
                        .joinedAt(ZonedDateTime.now())
                        .build(),
                ProjectMemberDTO.builder()
                        .userId(2L)
                        .role("MEMBER")
                        .joinedAt(ZonedDateTime.now())
                        .build()
        );

        sampleTasks = Arrays.asList(
                TaskResponseDTO.builder()
                        .id(1L)
                        .title("Task 1")
                        .description("Task 1 Description")
                        .build(),
                TaskResponseDTO.builder()
                        .id(2L)
                        .title("Task 2")
                        .description("Task 2 Description")
                        .build()
        );
    }

    @Test
    @WithMockUser
    void getAllProjects_ShouldReturnAllProjects() throws Exception {
        // Given
        List<ProjectDTO> projects = Arrays.asList(sampleProject);
        when(projectService.getAllProjects()).thenReturn(projects);

        // When & Then
        mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Projects retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Test Project"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    @WithMockUser
    void getProjectById_ShouldReturnProject_WhenProjectExists() throws Exception {
        // Given
        Long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(sampleProject);

        // When & Then
        mockMvc.perform(get("/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Project"));

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    @WithMockUser
    void getProjectById_ShouldReturnNotFound_WhenProjectDoesNotExist() throws Exception {
        // Given
        Long projectId = 999L;
        when(projectService.getProjectById(projectId))
                .thenThrow(new NotFoundException("Project not found"));

        // When & Then
        mockMvc.perform(get("/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createProject_ShouldCreateProject_WhenValidData() throws Exception {
        // Given
        doNothing().when(projectService).createProject(any(ProjectCreationDTO.class), eq("test@example.com"));

        // When & Then
        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Project created successfully"));

        verify(projectService, times(1)).createProject(any(ProjectCreationDTO.class), eq("test@example.com"));
    }

    @Test
    @WithMockUser
    void createProject_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        ProjectCreationDTO invalidDTO = ProjectCreationDTO.builder()
                .title("") // Invalid: empty title
                .description(null) // Invalid: null description
                .build();

        // When & Then
        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any(ProjectCreationDTO.class), anyString());
    }

    @Test
    @WithMockUser
    void updateProject_ShouldUpdateProject_WhenValidData() throws Exception {
        // Given
        Long projectId = 1L;
        doNothing().when(projectService).updateProject(projectId, sampleUpdateDTO);

        // When & Then
        mockMvc.perform(put("/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project updated successfully"));

        verify(projectService, times(1)).updateProject(projectId, sampleUpdateDTO);
    }

    @Test
    @WithMockUser
    void deleteProject_ShouldDeleteProject_WhenAuthorized() throws Exception {
        // Given
        Long projectId = 1L;
        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(true);
        doNothing().when(projectService).deleteProject(projectId);

        // When & Then
        mockMvc.perform(delete("/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));

        verify(projectService, times(1)).deleteProject(projectId);
    }

//    @Test
//    @WithMockUser
//    void deleteProject_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
//        // Given
//        Long projectId = 1L;
//        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(false);
//
//        // When & Then
//        mockMvc.perform(delete("/projects/{id}", projectId)
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//
//        verify(projectService, never()).deleteProject(projectId);
//    }

    @Test
    @WithMockUser
    void getProjectMembers_ShouldReturnMembers() throws Exception {
        // Given
        Long projectId = 1L;
        when(projectService.getProjectMembers(projectId)).thenReturn(sampleMembers);

        // When & Then
        mockMvc.perform(get("/projects/{id}/members", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project members retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].user_id").value(1))  // Changed from userId to user_id
                .andExpect(jsonPath("$.data[0].role").value("OWNER"))
                .andExpect(jsonPath("$.data[1].user_id").value(2))  // Added second member check
                .andExpect(jsonPath("$.data[1].role").value("MEMBER"));

        verify(projectService, times(1)).getProjectMembers(projectId);
    }

    @Test
    @WithMockUser
    void addMemberToProject_ShouldAddMember_WhenAuthorized() throws Exception {
        // Given
        Long projectId = 1L;
        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(true);
        doNothing().when(projectService).addMemberToProject(projectId, sampleAddMemberDTO);

        // When & Then
        mockMvc.perform(post("/projects/{id}/members", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAddMemberDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Member added to project successfully"));

        verify(projectService, times(1)).addMemberToProject(projectId, sampleAddMemberDTO);
    }

//    @Test
//    @WithMockUser
//    void addMemberToProject_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
//        // Given
//        Long projectId = 1L;
//        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(false);
//
//        // When & Then
//        mockMvc.perform(post("/projects/{id}/members", projectId)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleAddMemberDTO)))
//                .andExpect(status().isForbidden());
//
//        verify(projectService, never()).addMemberToProject(anyLong(), any(AddMemberDTO.class));
//    }

    @Test
    @WithMockUser
    void addMemberToProject_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        Long projectId = 1L;
        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(true);
        AddMemberDTO invalidDTO = AddMemberDTO.builder()
                .userId(null) // Invalid: null userId
                .role(null) // Invalid: null role
                .build();

        // When & Then
        mockMvc.perform(post("/projects/{id}/members", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).addMemberToProject(anyLong(), any(AddMemberDTO.class));
    }

    @Test
    @WithMockUser
    void updateMemberRole_ShouldUpdateRole_WhenAuthorized() throws Exception {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(true);
        doNothing().when(projectService).updateMemberRole(projectId, userId, sampleUpdateRoleDTO);

        // When & Then
        mockMvc.perform(put("/projects/{projectId}/members/{userId}", projectId, userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUpdateRoleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Member role updated successfully"));

        verify(projectService, times(1)).updateMemberRole(projectId, userId, sampleUpdateRoleDTO);
    }

//    @Test
//    @WithMockUser
//    void updateMemberRole_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
//        // Given
//        Long projectId = 1L;
//        Long userId = 2L;
//        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(false);
//
//        // When & Then
//        mockMvc.perform(put("/projects/{projectId}/members/{userId}", projectId, userId)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleUpdateRoleDTO)))
//                .andExpect(status().isForbidden());
//
//        verify(projectService, never()).updateMemberRole(anyLong(), anyLong(), any(UpdateMemberRoleDTO.class));
//    }

    @Test
    @WithMockUser
    void removeMemberFromProject_ShouldRemoveMember_WhenAuthorized() throws Exception {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(true);
        doNothing().when(projectService).removeMemberFromProject(projectId, userId);

        // When & Then
        mockMvc.perform(delete("/projects/{projectId}/members/{userId}", projectId, userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Member removed from project successfully"));

        verify(projectService, times(1)).removeMemberFromProject(projectId, userId);
    }

//    @Test
//    @WithMockUser
//    void removeMemberFromProject_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
//        // Given
//        Long projectId = 1L;
//        Long userId = 2L;
//        when(authorizationService.isProjectAdminOrOwner(projectId)).thenReturn(false);
//
//        // When & Then
//        mockMvc.perform(delete("/projects/{projectId}/members/{userId}", projectId, userId)
//                        .with(csrf()))
//                .andExpect(status().isForbidden());
//
//        verify(projectService, never()).removeMemberFromProject(anyLong(), anyLong());
//    }

    @Test
    @WithMockUser
    void getProjectTasks_ShouldReturnTasks() throws Exception {
        // Given
        Long projectId = 1L;
        when(taskService.getTasksByProjectId(projectId)).thenReturn(sampleTasks);

        // When & Then
        mockMvc.perform(get("/projects/{id}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Task 1"));

        verify(taskService, times(1)).getTasksByProjectId(projectId);
    }

    @Test
    @WithMockUser
    void getProjectKanban_ShouldReturnKanbanData() throws Exception {
        // Given
        Long projectId = 1L;
        when(taskService.getKanbanBoard(projectId)).thenReturn(sampleTasks);

        // When & Then
        mockMvc.perform(get("/projects/{id}/kanban", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Kanban board data retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Task 1"));

        verify(taskService, times(1)).getKanbanBoard(projectId);
    }

    @Test
    @WithMockUser
    void getProjectTasks_ShouldReturnEmpty_WhenNoTasks() throws Exception {
        // Given
        Long projectId = 1L;
        when(taskService.getTasksByProjectId(projectId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/projects/{id}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Project tasks retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(taskService, times(1)).getTasksByProjectId(projectId);
    }

    @Test
    void getAllProjects_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).getAllProjects();
    }

    @Test
    void createProject_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCreationDTO)))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).createProject(any(ProjectCreationDTO.class), anyString());
    }
}

