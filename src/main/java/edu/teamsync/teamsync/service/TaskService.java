package edu.teamsync.teamsync.service;
import edu.teamsync.teamsync.authorization.ProjectAuthorizationService;
import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskStatusHistoryDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.entity.TaskStatusHistory;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.exception.http.UnauthorizedException;
import edu.teamsync.teamsync.mapper.TaskMapper;
import edu.teamsync.teamsync.mapper.TaskStatusHistoryMapper;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.repository.TaskRepository;
import edu.teamsync.teamsync.repository.TaskStatusHistoryRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository tasksRepository;
    private final TaskStatusHistoryRepository taskStatusHistoryRepository;
    private final ProjectRepository projectsRepository;
    private final UserRepository usersRepository;
    private final TaskMapper taskMapper;
    private final TaskStatusHistoryMapper statusHistoryMapper;
    private final UserService userService;
    private final ProjectAuthorizationService projectAuthorizationService;

    public TaskResponseDTO getTaskById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        Tasks task = tasksRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));

        return buildTaskResponseDto(task);
    }

    public List<TaskResponseDTO> getAllTasks() {
        List<Tasks> tasks = tasksRepository.findAll();
        return tasks.stream()
                .map(this::buildTaskResponseDto)
                .collect(Collectors.toList());
    }

    public void createTask(TaskCreationDTO createDto) {
        if (createDto == null) {
            throw new IllegalArgumentException("Task creation data cannot be null");
        }

        // Validate required fields
        if (createDto.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        if (createDto.getTitle() == null || createDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }

        Tasks task = taskMapper.toEntity(createDto);
        task.setAssignedAt(ZonedDateTime.now());

        // Set project (now guaranteed to be not null)
        Projects project = projectsRepository.findById(createDto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found with id: " + createDto.getProjectId()));
        task.setProject(project);

        // Set assigned to user if provided
        if (createDto.getAssignedTo() != null) {
            Users assignedUser = usersRepository.findById(createDto.getAssignedTo())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + createDto.getAssignedTo()));
            task.setAssignedTo(assignedUser);
            // Set the assignedBy to the same user for now (or get from security context)
            task.setAssignedBy(userService.getCurrentUser());
        }

        Tasks parentTask = null;
        // Set parent task if provided
        if (createDto.getParentTaskId() != null) {
            parentTask = tasksRepository.findById(createDto.getParentTaskId())
                    .orElseThrow(() -> new NotFoundException("Parent task not found with id: " + createDto.getParentTaskId()));

            // Validate that parent task belongs to the same project
            if (!parentTask.getProject().getId().equals(createDto.getProjectId())) {
                throw new IllegalArgumentException("Parent task must belong to the same project");
            }
            task.setParentTask(parentTask);
        }

        Tasks savedTask = tasksRepository.save(task);

        // Update parent deadlines recursively if this task has a parent and a deadline
        if (parentTask != null && savedTask.getDeadline() != null) {
            updateParentDeadlinesRecursively(parentTask, savedTask.getDeadline());
        }

        // Create initial status history entry
        createStatusHistoryEntry(savedTask, savedTask.getStatus(), "Task created");
    }

    private void updateParentDeadlinesRecursively(Tasks parentTask, ZonedDateTime newChildDeadline) {
        if (parentTask == null || newChildDeadline == null) {
            return;
        }

        boolean deadlineUpdated = false;

        // Update parent deadline if new child deadline is later
        if (parentTask.getDeadline() == null || newChildDeadline.isAfter(parentTask.getDeadline())) {
            parentTask.setDeadline(newChildDeadline);
            deadlineUpdated = true;
        }

        // Save the updated parent task if deadline was changed
        if (deadlineUpdated) {
            tasksRepository.save(parentTask);

            // Recursively update the parent of this parent task
            if (parentTask.getParentTask() != null) {
                updateParentDeadlinesRecursively(parentTask.getParentTask(), newChildDeadline);
            }
        }
    }

    public void updateTask(Long id, TaskUpdateDTO updateDto) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        if (updateDto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        Tasks existingTask = tasksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));

        // Validate title if provided
        if (updateDto.getTitle() != null && updateDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        // Check if status is changing to create history entry
        boolean statusChanged = updateDto.getStatus() != null &&
                !existingTask.getStatus().name().equals(updateDto.getStatus());
        Tasks.TaskStatus newStatus = null;

        if (statusChanged) {
            try {
                newStatus = Tasks.TaskStatus.valueOf(updateDto.getStatus());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid task status: " + updateDto.getStatus());
            }
        }

        // Update entity using MapStruct
        taskMapper.updateEntityFromDto(updateDto, existingTask);

        // Handle relationships manually (MapStruct doesn't handle these automatically)
        handleRelationshipUpdates(updateDto, existingTask);

        Tasks savedTask = tasksRepository.save(existingTask);

        // Create status history entry if status changed
        if (statusChanged && newStatus != null) {
            createStatusHistoryEntry(savedTask, newStatus, "Status updated");
        }

    }
    public void deleteTask(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        if (!tasksRepository.existsById(id)) {
            throw new NotFoundException("Task not found with id: " + id);
        }

        try {
            tasksRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        }
    }

    public List<TaskResponseDTO> getTasksByProjectId(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }

        // Verify project exists
        if (!projectsRepository.existsById(projectId)) {
            throw new NotFoundException("Project not found with id: " + projectId);
        }

        List<Tasks> tasks = tasksRepository.findByProjectIdWithDetails(projectId);
        return tasks.stream()
                .map(this::buildTaskResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getKanbanBoard(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }

        // Verify project exists
        if (!projectsRepository.existsById(projectId)) {
            throw new NotFoundException("Project not found with id: " + projectId);
        }

        List<Tasks>tasks= tasksRepository.findByProjectIdWithDetails(projectId);
        List<TaskResponseDTO> taskResponseDTOs=new ArrayList<>();
        for(Tasks task:tasks)
        {
            taskResponseDTOs.add(buildTaskResponseDto(task));
        }
        return taskResponseDTOs;
    }

    private void handleRelationshipUpdates(TaskUpdateDTO updateDto, Tasks existingTask) {
        // Update assigned to user if provided
        if (updateDto.getAssignedTo() != null) {
            Users assignedUser = usersRepository.findById(updateDto.getAssignedTo())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + updateDto.getAssignedTo()));
            existingTask.setAssignedTo(assignedUser);
        }

        // Update project if provided
        if (updateDto.getProjectId() != null) {
            Projects project = projectsRepository.findById(updateDto.getProjectId())
                    .orElseThrow(() -> new NotFoundException("Project not found with id: " + updateDto.getProjectId()));
            existingTask.setProject(project);
        }

        // Update parent task if provided
        if (updateDto.getParentTaskId() != null) {
            Tasks parentTask = tasksRepository.findById(updateDto.getParentTaskId())
                    .orElseThrow(() -> new NotFoundException("Parent task not found with id: " + updateDto.getParentTaskId()));

            // Validate that parent task belongs to the same project
            if (updateDto.getProjectId() != null && !parentTask.getProject().getId().equals(updateDto.getProjectId())) {
                throw new IllegalArgumentException("Parent task must belong to the same project");
            } else if (updateDto.getProjectId() == null && !parentTask.getProject().getId().equals(existingTask.getProject().getId())) {
                throw new IllegalArgumentException("Parent task must belong to the same project");
            }

            // Prevent circular reference
            if (parentTask.getId().equals(existingTask.getId())) {
                throw new IllegalArgumentException("Task cannot be its own parent");
            }

            existingTask.setParentTask(parentTask);
        }
    }

    private void createStatusHistoryEntry(Tasks task, Tasks.TaskStatus status, String comment) {
        // Get the user who made the change - for now use assignedBy, later replace with authenticated user
        Users changedByUser = task.getAssignedBy();

        // If assignedBy is null, try to use assignedTo, or throw an error
        if (changedByUser == null) {
            changedByUser = task.getAssignedTo();
        }

        // If still null, we need a valid user - you might want to create a system user or handle this differently
        if (changedByUser == null) {
            throw new IllegalStateException("Cannot create status history: no valid user found for changedBy");
        }

        try {
            TaskStatusHistory statusHistory = TaskStatusHistory.builder()
                    .task(task)
                    .status(status)
                    .changedBy(changedByUser)
                    .changedAt(ZonedDateTime.now())
                    .comment(comment)
                    .build();
            taskStatusHistoryRepository.save(statusHistory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create status history entry: " + e.getMessage(), e);
        }
    }

    private TaskResponseDTO buildTaskResponseDto(Tasks task) {

        TaskResponseDTO dto = taskMapper.toDto(task);
        // Set subtasks
        List<Long> subtaskIds = tasksRepository.findSubtasksByParentTaskId(task.getId())
                .stream()
                .map(Tasks::getId)
                .collect(Collectors.toList());
        dto.setSubtasks(subtaskIds);

        // Set status history
        List<TaskStatusHistory> statusHistoryList = taskStatusHistoryRepository
                .findByTaskIdOrderByChangedAtDesc(task.getId());
        dto.setStatusHistory(statusHistoryMapper.toDtoList(statusHistoryList));

        return dto;
    }

    /*checks if all the children of a task has their status as completed or not
     * @param task the task to check
     * @param incompleteChildren the list of incomplete children
     * @return true if all the children of a task has their status as completed, false otherwise
    */
    private boolean hasAllChildrenCompleted(Long parentTaskId){

        List<Tasks> children = tasksRepository.findSubtasksByParentTaskId(parentTaskId);

        for(Tasks child:children){
            if(!child.getStatus().equals(Tasks.TaskStatus.completed)){
               return false;
            }
        }
        return true ;
    }

    public SuccessResponse<TaskResponseDTO> updateTaskStatus(Long taskId,TaskStatusHistoryDTO dto){

        Tasks task = tasksRepository.findById(taskId)
        .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));


        if(Tasks.TaskStatus.valueOf(dto.getStatus()).equals(Tasks.TaskStatus.completed) && !projectAuthorizationService.canManageTask(taskId)
        ){
            throw new UnauthorizedException("You are not authorized to update the status as completed");
        }

        if(Tasks.TaskStatus.valueOf(dto.getStatus()).equals(Tasks.TaskStatus.completed) && !hasAllChildrenCompleted(taskId)){
            throw new IllegalArgumentException("All the children of the task must be completed before updating the status to completed");
        }

        if(task.getStatus().equals(Tasks.TaskStatus.completed) && !projectAuthorizationService.canManageTask(taskId)){
            throw new UnauthorizedException("You are not authorized to revert back a completed task");
        }

        task.setStatus(Tasks.TaskStatus.valueOf(dto.getStatus()));
        tasksRepository.save(task);

        TaskStatusHistory statusHistory = TaskStatusHistory.builder()
        .task(task)
        .status(task.getStatus())
        .changedBy(userService.getCurrentUser())
        .changedAt(ZonedDateTime.now())
        .comment(dto.getComment())
        .build();

        taskStatusHistoryRepository.save(statusHistory);

        return SuccessResponse.<TaskResponseDTO>builder()
        .message("Task status updated successfully")
        .data(taskMapper.toDto(task))
        .build();
    }

    public List<TaskResponseDTO> getUserInvolvedTasks() {

        Users currentUser = userService.getCurrentUser();
        List<Tasks> tasks = tasksRepository.findUserInvolvedTasks(currentUser.getId());
        return tasks.stream()
                .map(this::buildTaskResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getTasksAssignedToUser() {
        Users currentUser = userService.getCurrentUser();
        List<Tasks> tasks = tasksRepository.findTasksAssignedToUser(currentUser.getId());
        return tasks.stream()
                .map(this::buildTaskResponseDto)
                .collect(Collectors.toList());
    }
}