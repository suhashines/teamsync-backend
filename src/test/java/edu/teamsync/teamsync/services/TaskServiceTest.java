package edu.teamsync.teamsync.services;
import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.entity.TaskStatusHistory;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.TaskMapper;
import edu.teamsync.teamsync.mapper.TaskStatusHistoryMapper;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.repository.TaskRepository;
import edu.teamsync.teamsync.repository.TaskStatusHistoryRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusHistoryRepository taskStatusHistoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskStatusHistoryMapper taskStatusHistoryMapper;

    @InjectMocks
    private TaskService taskService;

    private final Long taskId = 1L;
    private final Long projectId = 1L;
    private final Long userId = 1L;
    private final Long parentTaskId = 2L;

    private Tasks task;
    private Tasks parentTask;
    private Projects project;
    private Users user;
    private TaskCreationDTO taskCreationDTO;
    private TaskUpdateDTO taskUpdateDTO;
    private TaskResponseDTO taskResponseDTO;
    private TaskStatusHistory statusHistory;

    @BeforeEach
    void setUp() {
        project = Projects.builder().id(projectId).build();
        user = Users.builder().id(userId).email("test@example.com").build();

        parentTask = Tasks.builder()
                .id(parentTaskId)
                .title("Parent Task")
                .project(project)
                .status(Tasks.TaskStatus.todo)
                .deadline(ZonedDateTime.now().plusDays(10))
                .build();

        task = Tasks.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .project(project)
                .assignedTo(user)
                .assignedBy(user)
                .status(Tasks.TaskStatus.todo)
                .assignedAt(ZonedDateTime.now())
                .deadline(ZonedDateTime.now().plusDays(5))
                .parentTask(parentTask)
                .build();

        taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setTitle("New Task");
        taskCreationDTO.setDescription("New Description");
        taskCreationDTO.setProjectId(projectId);
        taskCreationDTO.setAssignedTo(userId);
        taskCreationDTO.setParentTaskId(parentTaskId);
        taskCreationDTO.setDeadline(ZonedDateTime.now().plusDays(3));

        taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setTitle("Updated Task");
        taskUpdateDTO.setDescription("Updated Description");
        taskUpdateDTO.setStatus("in_progress");
        taskUpdateDTO.setAssignedTo(userId);
        taskUpdateDTO.setProjectId(projectId);
        taskUpdateDTO.setParentTaskId(parentTaskId);

        taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(taskId);
        taskResponseDTO.setTitle("Test Task");
        taskResponseDTO.setDescription("Test Description");
        taskResponseDTO.setProjectId(projectId);
        taskResponseDTO.setAssignedTo(userId);
        taskResponseDTO.setStatus("TODO");

        statusHistory = TaskStatusHistory.builder()
                .id(1L)
                .task(task)
                .status(Tasks.TaskStatus.todo)
                .changedBy(user)
                .changedAt(ZonedDateTime.now())
                .comment("Task created")
                .build();
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findByIdWithDetails(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDTO);
        when(taskRepository.findSubtasksByParentTaskId(taskId)).thenReturn(Collections.emptyList());
        when(taskStatusHistoryRepository.findByTaskIdOrderByChangedAtDesc(taskId))
                .thenReturn(Collections.singletonList(statusHistory));
        when(taskStatusHistoryMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        TaskResponseDTO result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskRepository).findByIdWithDetails(taskId);
        verify(taskMapper).toDto(task);
    }

    @Test
    void getTaskById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(null));
    }

    @Test
    void getTaskById_NotFound() {
        when(taskRepository.findByIdWithDetails(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.getTaskById(taskId));
        verify(taskRepository).findByIdWithDetails(taskId);
    }

    @Test
    void getAllTasks_Success() {
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDTO);
        when(taskRepository.findSubtasksByParentTaskId(taskId)).thenReturn(Collections.emptyList());
        when(taskStatusHistoryRepository.findByTaskIdOrderByChangedAtDesc(taskId))
                .thenReturn(Collections.singletonList(statusHistory));
        when(taskStatusHistoryMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        List<TaskResponseDTO> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskId, result.get(0).getId());
        verify(taskRepository).findAll();
        verify(taskMapper).toDto(task);
    }

@Test
void createTask_Success() {
    when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
    when(taskRepository.save(any(Tasks.class))).thenReturn(task);
    when(taskStatusHistoryRepository.save(any(TaskStatusHistory.class))).thenReturn(statusHistory);

    taskService.createTask(taskCreationDTO);

    verify(taskMapper).toEntity(taskCreationDTO);
    verify(projectRepository).findById(projectId);
    verify(userRepository).findById(userId);
    verify(taskRepository).findById(parentTaskId);
    verify(taskRepository, times(1)).save(any(Tasks.class)); // Change from times(2) to times(1)
    verify(taskStatusHistoryRepository).save(any(TaskStatusHistory.class));
}

    @Test
    void createTask_NullDto() {
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(null));
    }

    @Test
    void createTask_NullProjectId() {
        taskCreationDTO.setProjectId(null);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskCreationDTO));
    }

    @Test
    void createTask_EmptyTitle() {
        taskCreationDTO.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskCreationDTO));
    }

    @Test
    void createTask_NullTitle() {
        taskCreationDTO.setTitle(null);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskCreationDTO));
    }

    @Test
    void createTask_ProjectNotFound() {
        when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.createTask(taskCreationDTO));
        verify(projectRepository).findById(projectId);
    }

    @Test
    void createTask_UserNotFound() {
        when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.createTask(taskCreationDTO));
        verify(userRepository).findById(userId);
    }

    @Test
    void createTask_ParentTaskNotFound() {
        when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.createTask(taskCreationDTO));
        verify(taskRepository).findById(parentTaskId);
    }

    @Test
    void createTask_ParentTaskDifferentProject() {
        Projects differentProject = Projects.builder().id(999L).build();
        parentTask.setProject(differentProject);

        when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskCreationDTO));
    }

    @Test
    void createTask_WithoutAssignedUser() {
        taskCreationDTO.setAssignedTo(null);
        when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
        when(taskRepository.save(any(Tasks.class))).thenReturn(task);
        when(taskStatusHistoryRepository.save(any(TaskStatusHistory.class))).thenReturn(statusHistory);

        taskService.createTask(taskCreationDTO);

        verify(taskRepository, times(1)).save(any(Tasks.class)); // Change from times(2) to times(1)
        verify(taskStatusHistoryRepository).save(any(TaskStatusHistory.class));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void createTask_WithoutParentTask() {
        taskCreationDTO.setParentTaskId(null);
        when(taskMapper.toEntity(taskCreationDTO)).thenReturn(task);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Tasks.class))).thenReturn(task);
        when(taskStatusHistoryRepository.save(any(TaskStatusHistory.class))).thenReturn(statusHistory);

        taskService.createTask(taskCreationDTO);

        verify(taskRepository, times(1)).save(any(Tasks.class));
        verify(taskStatusHistoryRepository).save(any(TaskStatusHistory.class));
        verify(taskRepository, never()).findById(parentTaskId);
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
        when(taskRepository.save(any(Tasks.class))).thenReturn(task);
        when(taskStatusHistoryRepository.save(any(TaskStatusHistory.class))).thenReturn(statusHistory);
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        taskService.updateTask(taskId, taskUpdateDTO);

        verify(taskRepository).findById(taskId);
        verify(taskMapper).updateEntityFromDto(taskUpdateDTO, task);
        verify(taskRepository).save(task);
        verify(taskStatusHistoryRepository).save(any(TaskStatusHistory.class));
    }

    @Test
    void updateTask_NullId() {
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(null, taskUpdateDTO));
    }

    @Test
    void updateTask_NullDto() {
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, null));
    }

    @Test
    void updateTask_TaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
        verify(taskRepository).findById(taskId);
    }

    @Test
    void updateTask_EmptyTitle() {
        taskUpdateDTO.setTitle("");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
    }

    @Test
    void updateTask_InvalidStatus() {
        taskUpdateDTO.setStatus("INVALID_STATUS");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
    }

    @Test
    void updateTask_UserNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        assertThrows(NotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
        verify(userRepository).findById(userId);
    }

    @Test
    void updateTask_ProjectNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        assertThrows(NotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
        verify(projectRepository).findById(projectId);
    }

    @Test
    void updateTask_ParentTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.empty());
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        assertThrows(NotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
        verify(taskRepository).findById(parentTaskId);
    }

    @Test
    void updateTask_ParentTaskDifferentProject() {
        Projects differentProject = Projects.builder().id(999L).build();
        parentTask.setProject(differentProject);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
    }

    @Test
    void updateTask_CircularReference() {
        taskUpdateDTO.setParentTaskId(taskId); // Task trying to be its own parent
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));
    }

    @Test
    void updateTask_WithoutStatusChange() {
        taskUpdateDTO.setStatus(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
        when(taskRepository.save(any(Tasks.class))).thenReturn(task);
        doNothing().when(taskMapper).updateEntityFromDto(taskUpdateDTO, task);

        taskService.updateTask(taskId, taskUpdateDTO);

        verify(taskRepository).save(task);
        verify(taskStatusHistoryRepository, never()).save(any(TaskStatusHistory.class));
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        taskService.deleteTask(taskId);

        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTask_NullId() {
        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(null));
    }

    @Test
    void deleteTask_NotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.deleteTask(taskId));
        verify(taskRepository).existsById(taskId);
        verify(taskRepository, never()).deleteById(taskId);
    }

    @Test
    void deleteTask_Exception() {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(taskRepository).deleteById(taskId);

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskId));
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void getTasksByProjectId_Success() {
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.findByProjectIdWithDetails(projectId)).thenReturn(Collections.singletonList(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDTO);
        when(taskRepository.findSubtasksByParentTaskId(taskId)).thenReturn(Collections.emptyList());
        when(taskStatusHistoryRepository.findByTaskIdOrderByChangedAtDesc(taskId))
                .thenReturn(Collections.singletonList(statusHistory));
        when(taskStatusHistoryMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        List<TaskResponseDTO> result = taskService.getTasksByProjectId(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskId, result.get(0).getId());
        verify(projectRepository).existsById(projectId);
        verify(taskRepository).findByProjectIdWithDetails(projectId);
    }

    @Test
    void getTasksByProjectId_NullId() {
        assertThrows(IllegalArgumentException.class, () -> taskService.getTasksByProjectId(null));
    }

    @Test
    void getTasksByProjectId_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.getTasksByProjectId(projectId));
        verify(projectRepository).existsById(projectId);
    }

    @Test
    void getKanbanBoard_Success() {
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.findByProjectIdWithDetails(projectId)).thenReturn(Collections.singletonList(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDTO);
        when(taskRepository.findSubtasksByParentTaskId(taskId)).thenReturn(Collections.emptyList());
        when(taskStatusHistoryRepository.findByTaskIdOrderByChangedAtDesc(taskId))
                .thenReturn(Collections.singletonList(statusHistory));
        when(taskStatusHistoryMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        List<TaskResponseDTO> result = taskService.getKanbanBoard(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskId, result.get(0).getId());
        verify(projectRepository).existsById(projectId);
        verify(taskRepository).findByProjectIdWithDetails(projectId);
    }

    @Test
    void getKanbanBoard_NullId() {
        assertThrows(IllegalArgumentException.class, () -> taskService.getKanbanBoard(null));
    }

    @Test
    void getKanbanBoard_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.getKanbanBoard(projectId));
        verify(projectRepository).existsById(projectId);
    }
}
