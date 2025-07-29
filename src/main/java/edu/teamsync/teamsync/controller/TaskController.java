package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskStatusHistoryDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService tasksService;

    @GetMapping("/user/involved")
    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getUserInvolvedTasks() {

        List<TaskResponseDTO> tasks = tasksService.getUserInvolvedTasks();

        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Current user tasks retrieved successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/assigned")
    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getTasksAssignedToUser() {
        List<TaskResponseDTO> tasks = tasksService.getTasksAssignedToUser();

        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Tasks assigned to user retrieved successfully")
                .data(tasks)
                .build();
        return ResponseEntity.ok(response);
    }
    

    @GetMapping(path = "/{id}")
    public ResponseEntity<SuccessResponse<TaskResponseDTO>> getTaskById(@PathVariable Long id) {
        TaskResponseDTO task = tasksService.getTaskById(id);

        SuccessResponse<TaskResponseDTO> response = SuccessResponse.<TaskResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Task retrieved successfully")
                .data(task)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getAllTasks() {
        List<TaskResponseDTO> tasks = tasksService.getAllTasks();

        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("All tasks retrieved successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<SuccessResponse<List<TaskResponseDTO>>> getTasksByProjectId(@PathVariable Long projectId) {
        List<TaskResponseDTO> tasks = tasksService.getTasksByProjectId(projectId);

        SuccessResponse<List<TaskResponseDTO>> response = SuccessResponse.<List<TaskResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Project tasks retrieved successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@projectAuthorizationService.isProjectAdminOrOwner(#createDto.projectId)")
    public ResponseEntity<SuccessResponse<Void>> createTask(@Valid @RequestBody TaskCreationDTO createDto) {

        tasksService.createTask(createDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Task created successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO updateDto) {
        tasksService.updateTask(id, updateDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Task updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@projectAuthorizationService.canManageTask(#id)")
    public ResponseEntity<SuccessResponse<Void>> deleteTask(@PathVariable Long id) {

        tasksService.deleteTask(id);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Task deleted successfully")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<SuccessResponse<TaskResponseDTO>> updateTaskStatus(@PathVariable Long id, @RequestBody TaskStatusHistoryDTO dto) {

        SuccessResponse<TaskResponseDTO> response = tasksService.updateTaskStatus(id, dto);

        return ResponseEntity.ok(response);
    }
}